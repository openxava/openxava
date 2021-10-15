FROM openjdk:8-jdk AS BUILDER

ENV DEBIAN_FRONTEND=noninteractive \
	LANG=C.UTF-8 \
	LC_ALL=C.UTF-8 \
	ANT_VERSION=1.10.11 \
    ANT_MIRROR=https://downloads.apache.org \
	ANT_HOME=/opt/ant

RUN apt-get update && apt-get install -y --no-install-recommends wget \
    && wget --no-check-certificate --no-cookies ${ANT_MIRROR}/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz \
    && tar -zvxf apache-ant-${ANT_VERSION}-bin.tar.gz -C /opt/ \
    && ln -sf /opt/apache-ant-${ANT_VERSION} /opt/ant \
    && rm -f apache-ant-${ANT_VERSION}-bin.tar.gz* \
    && update-alternatives --install /usr/bin/ant ant /opt/ant/bin/ant 1000 \
    && rm -rf /var/lib/apt/lists/*

ARG MAVEN_VERSION=3.5.2
ARG USER_HOME_DIR="/root"
# set as env vars to be reused inside dockerfile and app instance....
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

RUN mkdir -p /usr/share/maven && \
curl -fsSL http://www-eu.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz | tar -xzC /usr/share/maven --strip-components=1 && \
ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

RUN mkdir -p /tmp/app
WORKDIR /tmp/app
COPY . /tmp/app
RUN mvn dependency:copy-dependencies -DoutputDirectory=web/WEB-INF/lib -DexcludeTransitive=true
RUN curl https://github.com/openxava/openxava/blob/master/Addons/web/WEB-INF/lib/addons.jar -o web/WEB-INF/lib/addons.jar
RUN curl https://github.com/openxava/openxava/blob/master/OpenXava/web/WEB-INF/lib/openxava.jar -o web/WEB-INF/lib/openxava.jar
RUN curl https://github.com/openxava/openxava/blob/master/OpenXava/web/WEB-INF/lib/ox-jdbc-adapters.jar -o web/WEB-INF/lib/ox-jdbc-adapters.jar
RUN ant createWar

FROM openjdk:8-jdk

ENV TOMCAT_MAJOR 9
ENV TOMCAT_VERSION 9.0.26

RUN apt-get update
RUN apt-get install -y wget
RUN apt-get install -y curl

RUN mkdir /usr/local/tomcat
RUN wget https://archive.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz -O /tmp/tomcat.tar.gz
RUN cd /tmp && tar xvfz tomcat.tar.gz
RUN cp -Rv /tmp/apache-tomcat-$TOMCAT_VERSION/* /usr/local/tomcat/

RUN rm -rf /usr/local/tomcat/webapps/ROOT
RUN rm -rf /usr/local/tomcat/webapps/docs
RUN rm -rf /usr/local/tomcat/webapps/examples
RUN rm -rf /usr/local/tomcat/webapps/host-manager
RUN rm -rf /usr/local/tomcat/webapps/manager
ADD DockerfileEntrypoint.sh DockerfileEntrypoint.sh
RUN chmod 744 DockerfileEntrypoint.sh

COPY --from=BUILDER  /tmp/app/dist/  /app/target

# override defaul entrypoint
ENTRYPOINT ["/DockerfileEntrypoint.sh"]
