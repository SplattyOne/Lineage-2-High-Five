FROM ubuntu:xenial

RUN apt-get update \
    && apt-get install -y curl \
    && curl -O https://ftp.zhirov.website/program/Games/Lineage_2/server/Docker/x86-64/jre-8u311-linux-x64.tar.gz \
    && tar xfz jre-8u311-linux-x64.tar.gz -C /opt/ \
    && rm jre-8u311-linux-x64.tar.gz

COPY ./first-team/la2server/loginserver /opt/login/loginserver
COPY ./first-team/la2server/serverslibs /opt/login/serverslibs

ENV JAVA_HOME /opt/jre1.8.0_311
ENV PATH $PATH:$JAVA_HOME/bin

WORKDIR /opt/login/loginserver
# CMD ["/bin/bash", "StartAuthServer.sh"]
CMD ["/bin/bash", "AuthServer_loop.sh"]
# ENTRYPOINT ["tail", "-f", "/dev/null"]
