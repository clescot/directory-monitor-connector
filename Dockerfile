FROM confluentinc/cp-kafka-connect:4.0.0
ARG JAR_FILE
COPY target/${JAR_FILE} /etc/kafka-connect/jars/kafka-connect-directory-connector.jar