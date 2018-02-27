FROM ubuntu:16.04
EXPOSE 4321 4322 4323 4324 4327 4328 443

COPY docker/xtremweb.client.conf /xwhep/conf/
COPY lib /xwhep/lib
COPY bin /xwhep/bin/ 
WORKDIR /xwhep/bin
RUN mkdir -p /xwhep/certificate

# remove non necessary scripts from /xwhep/bin
# remove all subfolders:
RUN find . -mindepth 1 -maxdepth 1 -type d -exec rm -rf {} +

# remove files that don't match xw* or xtr*
RUN find . -type f ! -name 'xw*' ! -name 'xtr*' -exec rm -rf {} +

# Add the script that will set up the configuration in the container
ADD docker/client/start_client.sh /xwhep/start_client.sh

RUN apt-get update && \
       export DEBIAN_FRONTEND=noninteractive && \
       apt-get install -y openjdk-8-jre zip unzip wget openssl curl iputils-ping

RUN chmod +x /xwhep/start_client.sh

WORKDIR /xwhep/bin
CMD bash -C '/xwhep/start_client.sh';'bash'
