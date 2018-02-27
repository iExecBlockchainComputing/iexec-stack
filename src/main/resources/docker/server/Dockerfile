FROM ubuntu:16.04
EXPOSE 4321 4322 4323 443

COPY conf/xtremweb.server.conf /xwhep/conf/
COPY conf/xwconfigure.values /xwhep/conf/
COPY lib /xwhep/lib
COPY bin /xwhep/bin/
COPY keystore/xwhepserver.keys /xwhep/keystore/xwhepserver.keys
COPY keystore/xwscheduler.pem /xwhep/keystore/xwscheduler.pem

# Add the script that will set up the configuration in the container
ADD docker/server/start_server.sh /xwhep/start_server.sh

WORKDIR /xwhep

RUN apt-get update
RUN export DEBIAN_FRONTEND=noninteractive && \
       apt-get install -y openjdk-8-jre zip unzip wget make iputils-ping

RUN chmod +x /xwhep/start_server.sh

ENTRYPOINT [ "/xwhep/start_server.sh" ]


