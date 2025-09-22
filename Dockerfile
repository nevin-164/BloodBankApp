# Use official Tomcat 9 with JDK 17 (matches your app)
FROM tomcat:9.0-jdk17

# Set environment variable so Railway knows which port to use
ENV PORT=8080

# Remove default Tomcat ROOT app to avoid conflicts
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy your WAR file into Tomcat's webapps folder as ROOT.war
# Make sure 'target/BloodBankApp.war' exists in the build context
COPY target/BloodBankApp.war /usr/local/tomcat/webapps/ROOT.war

# Expose the port (Railway automatically maps this)
EXPOSE 8080

# Start Tomcat when container runs
CMD ["catalina.sh", "run"]
