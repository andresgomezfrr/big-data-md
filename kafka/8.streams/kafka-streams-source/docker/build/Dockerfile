FROM java:openjdk-8-jre

MAINTAINER andresgomezfrr@gmail.com

RUN apt-get update
RUN mkdir -p /opt/kafka-streams/app; mkdir -p /opt/kafka-streams/bin; mkdir -p /opt/kafka-streams/conf
RUN mkdir -p /var/log/kafka-streams

ADD kafka-stream-start.sh /opt/kafka-streams/bin
ADD kafka-streams-selfcontained.jar /opt/kafka-streams/app
ADD log4j.xml /opt/kafka-streams/conf

CMD /opt/kafka-streams/bin/kafka-stream-start.sh