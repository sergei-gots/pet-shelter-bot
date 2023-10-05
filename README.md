# pet-shelter-bot😼
<u>Pet Shelter Telegram bot</u>
<br>
<br>
Study team work.
<br>authors: Anton Tsarjov, Sergei Gots, Valerii Zavolskii
<br>
Telegram bot is a pet shelter aggreagtor.
It provides automated interaction between pet shelter and pet adopters, or potential pet adopters and also
provides interaction between adopters and volunteers who are able to answer to adopter's questions within telegram dialogs.

## How to build and launch

This is a spring-boot based maven project described with <b>pom.xml</b>
<br>
As database engine there is postgresQL data base server used.
Server is described in <b>resources/application.properties</b>
Name of the database is <b>pet_shelter_bot</b>
Database objects are described with <b>liquibase</b>-log available in <b>resources/db</b>

<u>Command to launch<u>:

mvn package 

java -jar target/pet-shelter-bot-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --spring.datasource.username=<DB_USERNAME> --spring.datasource.password=<DB_PASSWORD> --telegram.bot.token=<YOUR_BOT_TELEGRAM_TOKEN>
<br><br>
 where <br>
    <li>DB_USERNAME, DB_PASSWORD -  username/password to your Postgres DB Server
    <li>YOUR_TELEGRAM_BOT_TOKEN - a token you've got with Telegram @BotFather bot 
<br><br>
After a successfull launch you can interact with the bot in Telegram.
<br>There are two different roles/scenarios for users.
Users can have role either of a <b>Vounteers</b> or an <b>Adopter</b>
<br>If the user is listed in the database as a shelter's volunteer 
then they have within the bot a notification desk about requests for dialogs 
and can participate dialogs answering questions asked by adopters.
<br>Both adopters and volunteers are able to terminate their dialog session.
<br>
<br>Also there is API provided for shelter's employees who
can manage list of volunteeers, asign adopters for pets,
review reports from adopters about their pets etc.
<br>To interact with API there is swagger-UI provided available on:
<br>
<br> http://localhost:8080/shelter/swagger-ui/index.html#/


