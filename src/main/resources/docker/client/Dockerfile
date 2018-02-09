FROM ubuntu:16.04
EXPOSE 4321 4322 4323 4324 4327 4328 443

COPY conf/xtremweb.client.conf /xwhep/conf/
COPY lib /xwhep/lib
COPY bin /xwhep/bin/ 
WORKDIR /xwhep/bin
# remove non necessary scripts from /xwhep/bin
# remove all subfolders:
RUN find . -mindepth 1 -maxdepth 1 -type d -exec rm -rf {} +
# remove files that don't match xw* or xtr*
RUN find . -type f ! -name 'xw*' ! -name 'xtr*' -exec rm -rf {} +

RUN apt-get update && \
       export DEBIAN_FRONTEND=noninteractive && \
       apt-get install -y openjdk-8-jre zip unzip wget make curl iputils-ping

RUN echo "#!/bin/sh" > /tmp/xwstart.sh

RUN echo "if [ ! -z \$XWSERVERADDR ] ; then " >> /tmp/xwstart.sh && \
	echo "	echo \"\$XWSERVERADDR \$XWSERVERNAME\" >> /etc/hosts" >> /tmp/xwstart.sh && \
	echo "	sed -i "s/^DISPATCHERS=.*/DISPATCHERS=\$XWSERVERNAME/g" /xwhep/conf/xtremweb.client.conf " >> /tmp/xwstart.sh && \
	echo "fi" >> /tmp/xwstart.sh

# import the server certificate into the container keystore
RUN echo "keytool -import -alias localhost -file /xwhep/certificate/xwscheduler.pem -trustcacerts -keystore /etc/ssl/certs/java/cacerts -storepass changeit -v -noprompt " >> /tmp/xwstart.sh

# update the SSLKEYSTORE to point to the container's keystore
RUN echo "sed -i \"s/^SSLKEYSTORE=.*/SSLKEYSTORE=\/etc\/ssl\/certs\/java\/cacerts/g\" /xwhep/conf/xtremweb.client.conf " >> /tmp/xwstart.sh
RUN echo "sed -i \"s/^SSLKEYPASSWORD=.*/SSLKEYPASSWORD=changeit/g\" /xwhep/conf/xtremweb.client.conf " >> /tmp/xwstart.sh

RUN echo "cat /etc/hosts" >> /tmp/xwstart.sh
RUN chmod +x /tmp/xwstart.sh

WORKDIR /xwhep/bin
CMD bash -C '/tmp/xwstart.sh';'bash'
