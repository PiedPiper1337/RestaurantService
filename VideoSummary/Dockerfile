#java docker image pulls from ubuntu 14:04
FROM java:latest
MAINTAINER Brian Zhao version 0.1

#https://stackoverflow.com/questions/7739645/install-mysql-on-ubuntu-without-password-prompt
ENV DEBIAN_FRONTEND noninteractive
ENV DISPLAY :10

RUN apt-get update && \
    apt-get install -y wget && \
    apt-get install -y unzip && \
    apt-get install -y Xvfb && \
    apt-get install -y mysql-server-5.5 && \
    apt-get install -y apt-transport-https && \
    apt-get -y upgrade

#https://askubuntu.com/questions/79280/how-to-install-chrome-browser-properly-via-command-line
#https://askubuntu.com/questions/510056/how-to-install-google-chrome
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    sh -c 'echo "deb https://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list' && \
    apt-get update && \
    apt-get install -y google-chrome-stable

COPY VidSumMySQLSetup.sh /root/VidSumMySQLSetup.sh
COPY entrypoint.sh /root/entrypoint.sh
WORKDIR /root
RUN /bin/bash VidSumMySQLSetup.sh

COPY target/universal/videosummary-1.0-SNAPSHOT.zip /root/videosummary-1.0-SNAPSHOT.zip
RUN unzip videosummary-1.0-SNAPSHOT.zip && rm *.zip
COPY chromedriverLinux /root/videosummary-1.0-SNAPSHOT
COPY chromedriverLinux /root/videosummary-1.0-SNAPSHOT/bin

EXPOSE 80
CMD ["/bin/bash", "/root/entrypoint.sh"]