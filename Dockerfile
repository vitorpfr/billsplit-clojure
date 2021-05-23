FROM clojure:latest

COPY . /app
WORKDIR /app

RUN ["lein", "uberjar"]

CMD ["java", "-jar", "target/billsplit-clojure-0.1.0-SNAPSHOT-standalone.jar"]

EXPOSE 3030
