### Run the project
1. ```docker-compose up```
2. ```gradle bootRun```

### DB initialization
For postgres, the spring.datasource.url in application properties needs to point to a live db. Although there are  spring.jpa.hibernate.ddl-auto properties to create the DB on startup, these seem to be run after the connection is made, and this wont work for initialization. Therefor, the docker-compose script points to a folder with SQL files (bigspender/src/main/resources/sql) that are run on startup to create the DB. Hibernate takes care of the rest.
   
