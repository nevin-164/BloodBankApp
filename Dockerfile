# Use Tomcat 9 with Java 11
FROM tomcat:9.0-jdk11

# Remove default ROOT app
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy your WAR into Tomcat (adjust name if needed)
COPY target/BloodBankApp.war /usr/local/tomcat/webapps/ROOT.war

# Expose Render's port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
