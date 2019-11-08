FROM openjdk:8

RUN useradd henninb
RUN mkdir -p /opt/raspi_finance_convert/bin
RUN mkdir -p /opt/raspi_finance_convert/ssl
RUN mkdir -p /opt/raspi_finance_convert/logs
RUN mkdir -p /opt/raspi_finance_convert/json_in
RUN chown -R henninb /opt/raspi_finance_convert/*

COPY ./build/libs/raspi_finance_convert.jar /opt/raspi_finance_convert/bin/raspi_finance_convert.jar
WORKDIR /opt/raspi_finance_convert/bin

USER henninb

CMD java -jar raspi_finance_convert.jar
