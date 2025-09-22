# Use official Tomcat 9 image with JDK 17
FROM tomcat:9.0-jdk17

# Set port for Railway deployment
ENV PORT=8080

# Remove default Tomcat ROOT app to avoid conflicts
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy your WAR file from target folder to ROOT.war in Tomcat
COPY target/BloodBankApp.war /usr/local/tomcat/webapps/ROOT.war

# Expose the port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
