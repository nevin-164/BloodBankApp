# Use official Tomcat image (lightweight JDK 17 or 21 version)
FROM tomcat:10.1-jdk17

# Set environment variable so Railway knows which port to use
ENV PORT=8080

# Remove default Tomcat ROOT app to avoid conflicts
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy your WAR or compiled app into Tomcat webapps folder
# (Assumes you have target/BloodBankApp.war after building with Maven or manually zipping)
COPY target/BloodBankApp.war /usr/local/tomcat/webapps/ROOT.war

# Expose the port (Railway maps this automatically)
EXPOSE 8080

# Start Tomcat when container runs
CMD ["catalina.sh", "run"]
