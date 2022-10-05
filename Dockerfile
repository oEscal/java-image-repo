FROM tomcat:8.5.82-jre11 


RUN rm -rf $CATALINA_HOME/webapps/ROOT

RUN sed -i 's/redirectPort=\"8443\"/redirectPort=\"8443\" server=\"Apache Tomcat\/8.5.82-jre11\"/g' $CATALINA_HOME/conf/server.xml

ENV _JAVA_OPTIONS="-Djava.net.preferIPv4Stack=true"

#https://www.baeldung.com/tomcat-root-application
COPY ROOT.xml $CATALINA_HOME/conf/Catalina/localhost/ROOT.xml

COPY target/example.war $CATALINA_HOME/deploy/example.war

# RUN useradd appuser
# RUN chown -R appuser $CATALINA_HOME
# USER appuser
