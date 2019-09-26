FROM openjdk:8

RUN useradd henninb
RUN mkdir -p /opt/raspi_finance/bin /opt/raspi_finance/ssl /opt/raspi_finance/logs /opt/raspi_finance/json_in
RUN chown -R henninb /opt/raspi_finance/*

COPY ./build/libs/raspi_finance_convert.jar /opt/raspi_finance/bin/raspi_finance.jar
WORKDIR /opt/raspi_finance/bin
#CMD ["java", "-jar" "/opt/raspi_finance/raspi_finance.jar"]

#RUN echo "172.17.0.1 hornsup" | tee -a /etc/hosts
#RUN ping $(ip route|awk '/default/ { print $3 }')
#hornsup:9092
USER henninb

CMD java -jar raspi_finance.jar
