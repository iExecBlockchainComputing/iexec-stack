FROM ubuntu:16.04
EXPOSE 4321 4322 4323 4324 4327 4328 443

ADD conf/xtremweb.worker.conf /xwhep/conf/
RUN mkdir -p /xwhep/bin
RUN mkdir -p /xwhep/certificate
ADD bin/xtremweb.worker /xwhep/bin
ADD bin/xtremweb /xwhep/bin
ADD bin/xtremwebconf.sh /xwhep/bin
ADD lib /xwhep/lib

# Add the script that will set up the configuration in the container
ADD docker/worker/start_worker.sh /xwhep/start_worker.sh

WORKDIR /xwhep

RUN apt-get update && \ 
       export DEBIAN_FRONTEND=noninteractive && \
       apt-get install -y openjdk-8-jre zip unzip wget curl openssl apt-transport-https ca-certificates software-properties-common 

# install docker
RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
RUN apt-get update
RUN apt-get install -y docker-ce

RUN chmod +x /xwhep/start_worker.sh

ENTRYPOINT [ "/xwhep/start_worker.sh" ]
