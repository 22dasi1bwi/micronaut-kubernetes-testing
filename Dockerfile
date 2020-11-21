FROM openjdk:11-jdk-slim-stretch

#Create non-root user
RUN useradd appuser --gid=100 --uid=1337 -m

COPY build/libs/demo-0.1-all.jar demo.jar

USER appuser

ENV JAVA_OPTS="-Djdk.tls.client.protocols=TLSv1.2 -XshowSettings:vm -XX:MaxRAMPercentage=75"
# If the datadog agent is present, start it as well, otherwise don't.
CMD java $JAVA_OPTS -jar demo.jar
