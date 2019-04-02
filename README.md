# RiskEngine
###### Mercury Code Challenge.

Considerations
1) Used Java Spring boot as preferred choice as the task was to create a linux microservice. I have experience in both Java and C# but since last 10 months, I have been working on creating microservices in C#. With Java I don't have experience creating microservices from scratch as I have mostly worked on matured or legacy application in enterprise environment when working for the Java team in my first rotation. Wanted to challenge myself which was another reason to choose Java.
2) Used H2 as in-memory DB to to speedup the dev process. Changing to Oracle/Postgres would only require driver and url change in application settings.
3) Used Ehcache for caching mechanism, the system stores Currency account data as it is used mostly during the lifecycle of all transaction. The cache.xml has all the settings of the cache and we currently store 300 records in the accounts cache but later can be increased with config change.
4) As the task was required to be completed within a set amount of time, no unit tests were written, though I totally understand this is the wrong approach and some would argue that it speeds up the process but I wanted to finish this quickly and solely relied on extensive debugging and Postman tool for post and get requests.
5) The database is loaded from data.sql which contains all the data which was provided in the excel file.
6) The database can be accessed at http://localhost:8080/h2 username sa password 
7) The naming of the classes and variables can be improved.
8) There are places where code could have been abstracted example in Producer where creation of templates could have been hidden.
9) There are places where interfaces could have been extracted, example for controllers, there could have been interfaces so that we can add different implementations .i.e Kafka implementation/Rest Api Calls directly to services

Tests
1) Tested the application using Postman tool and debugging.
2) Used collection in Postman to add 1138 different requests for Withdrawal using a csv fine (Can be found under resources by the name data-article.csv). The tool allowed to also execute and control iterations. This was especially beneficial in testing the behaviour of the cache as the task suggested to use only 300 records in memory. 
3) If I had to write this again, I would have added tests to all the services, would have mocked different dependencies to test individual methods without relying on other logic. Would have created integration tests to test the API's.

Extra things added
1) Spring boot Actuator to get the health statistics of the application. Doing a get call on /actuator/health shows if the service is UP (Can add significant importance for microservice environment)
2) Added Lombok for auto generation of getter/setter constructor etc. Chose to use it in the end as I wanted to finish this quickly. Some of the classes need consistency as some use Lombok and some don't.
3) Authentication added, the service can only be accessed by using credentials (Can be found in app settings). Default user name admin, password admin