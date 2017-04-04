FROM maven:3-jdk-8-alpine

RUN mkdir -p /opt/app
WORKDIR /opt/app

ADD pom.xml /opt/app
ADD zorka-agent/pom.xml /opt/app/zorka-agent/
ADD zorka-common/pom.xml /opt/app/zorka-common/
ADD zorka-common-test/pom.xml /opt/app/zorka-common-test/
ADD zorka-core/pom.xml /opt/app/zorka-core/
ADD zorka-core-test/pom.xml /opt/app/zorka-core-test/
ADD zorka-dist/pom.xml /opt/app/zorka-dist/
ADD zorka-viewer/pom.xml /opt/app/zorka-viewer/

RUN mvn -pl '!zorka-dist' install

ADD . /opt/app
RUN mvn clean install

RUN ls -l zorka-dist/target/

RUN mv zorka-dist/target/output/netuitive-zorka-*? /opt/zorka

WORKDIR /opt/zorka
RUN rm -rf /opt/app

ENV JAVA_API_KEY changeme
ENV JAVA_APPLICATION_NAME My Application
ENV HTTP_VAR https
ENV API_URL api.app.netuitive.com

CMD sed -i -e "s/netuitive.api.key\ =\ <Your Java Data Source Api Key>/netuitive.api.key = ${JAVA_API_KEY}/g" /opt/zorka/zorka.properties && \
    echo "Configuring JAVA API KEY with $JAVA_API_KEY" && \
    sed -i -e "s/zorka.application\ =\ My Application/zorka.application = ${JAVA_APPLICATION_NAME}/g" /opt/zorka/zorka.properties && \
    echo "Configuring JAVA APPLICATION NAME with $JAVA_APPLICATION_NAME" && \
    sed -i -e "s/https/${HTTP_VAR}/g" /opt/zorka/zorka.properties && \
    echo "Configuring HTTP VAR with $HTTP_VAR" && \
    sed -i -e "s/api\.app\.netuitive\.com/${API_URL}/g" /opt/zorka/zorka.properties && \
    echo "Configuring API URL with $API_URL" && \
    sed -i -e "s/scripts\ =\ jvm\.bsh/scripts\ =\ jvm\.bsh,calculator\.bsh/g" /opt/zorka/zorka.properties && \
    java -javaagent:/opt/zorka/netuitive.jar=/opt/zorka -jar zorka-core-test.jar
