FROM ubuntu:16.04
EXPOSE 4321 4322 4323 443

COPY conf/xtremweb.server.conf /xwhep/conf/
COPY lib /xwhep/lib
COPY bin /xwhep/bin/
COPY keystore/xwhepserver.keys /xwhep/keystore/xwhepserver.keys
WORKDIR /xwhep

RUN apt-get update
RUN export DEBIAN_FRONTEND=noninteractive && \
       apt-get install -y openjdk-8-jre zip unzip wget make iputils-ping

#
# -1- don't renice in container
# -2- we must remove LAUNCHERURL since Apache is not installed
# 
RUN sed -i "s/^V_NICE=.*//g" /xwhep/bin/xtremwebconf.sh
RUN sed -i "s/LAUNCHER.*//g" /xwhep/conf/xtremweb.server.conf

#
# Entry point script
# 
RUN echo "#!/bin/sh" > /tmp/xwstart.sh
RUN echo "/xwhep/bin/xtremweb.server console" >> /tmp/xwstart.sh
RUN chmod +x /tmp/xwstart.sh

ENTRYPOINT [ "/tmp/xwstart.sh" ]

