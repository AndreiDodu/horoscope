package org.andreidodu.horoscope.repository.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.andreidodu.horoscope.entities.Forecast;
import org.andreidodu.horoscope.entities.ForecastSign;
import org.andreidodu.horoscope.entities.Sign;
import org.andreidodu.horoscope.repository.ForecastSignDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ForecastSignDaoImpl extends CommonDao implements ForecastSignDao {

	private static final Logger LOG = LoggerFactory.getLogger(ForecastSignDaoImpl.class);

	@Override
	public boolean thereAreRecordsForInterval(String sign, Date startDate, Date endDate, List<String> categories) {

		CriteriaBuilder builder = super.getSession().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);

		Root<ForecastSign> root = criteriaQuery.from(ForecastSign.class);
		criteriaQuery.where(builder.greaterThanOrEqualTo(root.get("forecastDate"), startDate));
		criteriaQuery.where(builder.lessThan(root.get("forecastDate"), endDate));
		if (categories != null && !categories.isEmpty()) {
			criteriaQuery.where(root.join("forecast").get("category").in(categories));
		}
		criteriaQuery.where(root.join("sign").get("signName").in(StringUtils.lowerCase(sign)));
		criteriaQuery.select(builder.count(root.get("id")));

		TypedQuery<Long> query = super.getSession().createQuery(criteriaQuery);
		Long numRecords = query.getSingleResult();
		LOG.debug("For {}-{} I found {} records", startDate, endDate, numRecords);
		if (categories != null && !categories.isEmpty()) {
			return numRecords == categories.size();
		}
		return numRecords > 0;
	}

	@Override
	public void generate(String sign, Date validityDate, Long... ids) {

		TypedQuery<Sign> querySign = super.getSession().createQuery("from Sign s where s.signName=:signName",
				Sign.class);
		querySign.setParameter("signName", sign);
		Sign s = querySign.getSingleResult();

		for (Long id : ids) {
			ForecastSign fs = new ForecastSign();
			TypedQuery<Forecast> queryForecast = super.getSession().createQuery("from Forecast f where f.id=:id",
					Forecast.class);
			queryForecast.setParameter("id", id);
			Forecast f = queryForecast.getSingleResult();
			fs.setForecast(f);
			fs.setSign(s);
			fs.setForecastDate(validityDate);
			super.getSession().save(fs);
		}

	}

	@Override
	public List<Forecast> retrieveRecordsForInterval(String sign, Date dateStart, Date dateEnd,
			List<String> categories) {
		CriteriaBuilder builder = super.getSession().getCriteriaBuilder();
		CriteriaQuery<Forecast> criteriaQuery = builder.createQuery(Forecast.class);

		Root<ForecastSign> root = criteriaQuery.from(ForecastSign.class);
		criteriaQuery.where(builder.greaterThanOrEqualTo(root.get("forecastDate"), dateStart));
		criteriaQuery.where(builder.lessThan(root.get("forecastDate"), dateEnd));
		if (categories != null && !categories.isEmpty()) {
			criteriaQuery.where(root.join("forecast").get("category").in(categories));
		}
		criteriaQuery.where(root.join("sign").get("signName").in(StringUtils.lowerCase(sign)));
		criteriaQuery.select(root.get("forecast"));

		TypedQuery<Forecast> query = super.getSession().createQuery(criteriaQuery);
		return query.getResultList();
	}

}
