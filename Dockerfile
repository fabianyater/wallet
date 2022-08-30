FROM openjdk:8-alpine
ADD target/wallet-0.0.1-SNAPSHOT.jar /usr/share/wallet-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/wallet-0.0.1-SNAPSHOT.jar"]