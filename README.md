# horoscope
A complete horoscope web service that I written, which will be integrated with alexa. The application provides an REST API.
I used Spring Boot, liquibase, Hibernate and MySQL.

In order to run it, make a maven clean install and then run as Spring Boot application. 

Then call the endpoint with postman to retrieve some informations: http://localhost:8080/horoscope/sign/${YOUR_SIGN}

[For further information, see my blog](http://dodu.it/?page_id=516)

P.S. don't forget to install lombok in your eclipse
