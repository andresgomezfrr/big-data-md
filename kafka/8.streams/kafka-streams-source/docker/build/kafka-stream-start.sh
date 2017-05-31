#!/usr/bin/env bash

java -cp /opt/kafka-streams/conf:/opt/kafka-streams/app/kafka-streams-selfcontained.jar ${MAIN_CLASS} ${APP_ID} ${KAFKA_BOOTSTRAPER}