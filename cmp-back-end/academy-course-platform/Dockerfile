FROM maven:3 AS build

COPY ./ /app/
WORKDIR /app
RUN mvn clean install

FROM tomcat:9

RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY --from=build /app/academy-course-platform-webapp/target/academy-course-platform-webapp-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war