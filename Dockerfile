FROM confluentinc/cp-kafka-connect:3.3.0
COPY target/${JAR_FILE} /etc/kafka-connect/jars/kafka-connect-directory-connector.jar