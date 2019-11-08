FROM openjdk:8

RUN useradd henninb
RUN mkdir -p /opt/raspi_finance_convert/bin /opt/raspi_finance_convert/ssl /opt/raspi_finance_convert/logs /opt/raspi_finance_convert/json_in
RUN chown -R henninb /opt/raspi_finance_convert/*

COPY ./build/libs/raspi_finance_convert.jar /opt/raspi_finance_convert/bin/raspi_finance_convert.jar
WORKDIR /opt/raspi_finance_convert/bin
#CMD ["java", "-jar" "/opt/raspi_finance_convert/raspi_finance_convert.jar"]

#RUN echo "172.17.0.1 hornsup" | tee -a /etc/hosts
#RUN ping $(ip route|awk '/default/ { print $3 }')
#hornsup:9092
USER henninb

CMD java -jar raspi_finance_convert.jar
