#SET OF COMMANDS YOU MAY NEED TO COMPILE AND RUN APPLICATION AND AUXILIARY SOFTWARE

#Setting JAVA_HOME:
export JAVA_HOME="/usr/lib/jvm/java-11-openjdk"
#Maven clean:
mvn clean

#DOCKER
#get the latest postgres official:
sudo docker pull postgres

#run the POSTGRES server in container:
sudo docker run -d --name postgresCont -p 5432:5432-e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres postgres


#make a CONNECTION WITH POSTGRES in terminal:
sudo psql -h localhost -U postgres
#create database within the postgres session we made above:
create database pet_shelter_bot;