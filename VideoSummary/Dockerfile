#java docker image pulls from ubuntu 14:04
FROM java:latest
MAINTAINER Brian Zhao

#https://stackoverflow.com/questions/7739645/install-mysql-on-ubuntu-without-password-prompt
ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update && \
    apt-get install -y unzip && \
    apt-get install -y mysql-server-5.5

COPY VidSumMySQLSetup.sh /root/VidSumMySQLSetup.sh
COPY entrypoint.sh /root/entrypoint.sh
WORKDIR /root
RUN /bin/bash VidSumMySQLSetup.sh

COPY target/universal/videosummary-1.0-SNAPSHOT.zip /root/videosummary-1.0-SNAPSHOT.zip
RUN unzip videosummary-1.0-SNAPSHOT.zip && rm *.zip

EXPOSE 80
CMD ["/bin/bash", "/root/entrypoint.sh"]