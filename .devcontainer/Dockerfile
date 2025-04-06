# Utiliser une image officielle OpenJDK (ici version 17)
FROM eclipse-temurin:17-jdk

# Mettre à jour et installer SQLite (utile pour tester la base en ligne de commande si besoin)
RUN apt-get update && apt-get install -y sqlite3

# Créer un répertoire pour les bibliothèques supplémentaires
RUN mkdir -p /libs

# Télécharger le driver JDBC SQLite (version 3.49.1.0) et le JAR SLF4J API (version 1.7.36)
ADD https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.49.1.0/sqlite-jdbc-3.49.1.0.jar /libs/sqlite-jdbc.jar
ADD https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar /libs/slf4j-api.jar

# Définir le répertoire de travail pour que le code compilé se retrouve à la racine
WORKDIR /workspace
