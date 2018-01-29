FROM ubuntu:16.04
EXPOSE 4321 4322 4323 4324 4327 4328 443

ADD conf/xtremweb.worker.conf /xwhep/conf/
RUN mkdir -p /xwhep/bin
ADD bin/xtremweb.worker /xwhep/bin
ADD bin/xtremweb /xwhep/bin
ADD bin/xtremwebconf.sh /xwhep/bin
ADD lib /xwhep/lib

WORKDIR /xwhep

RUN apt-get update

RUN export DEBIAN_FRONTEND=noninteractive && \
       apt-get install -y openjdk-8-jre zip unzip wget make curl docker.io iputils-ping

RUN echo "#!/bin/sh" > /tmp/xwstart.sh

RUN echo "if [ ! -z \$XWSERVERADDR ] ; then " >> /tmp/xwstart.sh
RUN echo "	echo \"\$XWSERVERADDR \$XWSERVERNAME\" >> /etc/hosts" >> /tmp/xwstart.sh
RUN echo "	sed -i "s/^DISPATCHERS=.*/DISPATCHERS=\$XWSERVERNAME/g" /xwhep/conf/xtremweb.worker.conf " >> /tmp/xwstart.sh
RUN echo "fi" >> /tmp/xwstart.sh

# import the server certificate into the container keystore
RUN echo "keytool -import -alias localhost -file /xwhep/certificate/xwhepcert.pem -trustcacerts -keystore /etc/ssl/certs/java/cacerts -storepass changeit -v -noprompt " >> /tmp/xwstart.sh

# update the SSLKEYSTORE to point to the container's keystore
RUN echo "sed -i \"s/^SSLKEYSTORE=.*/SSLKEYSTORE=\/etc\/ssl\/certs\/java\/cacerts/g\" /xwhep/conf/xtremweb.worker.conf " >> /tmp/xwstart.sh
RUN echo "sed -i \"s/^SSLKEYPASSWORD=.*/SSLKEYPASSWORD=changeit/g\" /xwhep/conf/xtremweb.worker.conf " >> /tmp/xwstart.sh

# update the SHAREDAPPS variable to docker  
RUN echo "sed -i \"s/^#SHAREDAPPS=.*/SHAREDAPPS=docker/g\" /xwhep/conf/xtremweb.worker.conf " >> /tmp/xwstart.sh

# choose the login/password to run worker or vworker:
RUN echo "if [ ! -z \$LOGIN ] ; then " >> /tmp/xwstart.sh
RUN echo "	sed -i \"s/^LOGIN=.*/LOGIN=\$LOGIN/g\" /xwhep/conf/xtremweb.worker.conf " >> /tmp/xwstart.sh
RUN echo "	sed -i \"s/^PASSWORD=.*/PASSWORD=\$PASSWORD/g\" /xwhep/conf/xtremweb.worker.conf " >> /tmp/xwstart.sh
RUN echo "fi" >> /tmp/xwstart.sh

RUN echo "cat /etc/hosts" >> /tmp/xwstart.sh
RUN echo "/xwhep/bin/xtremweb.worker console" >> /tmp/xwstart.sh

RUN chmod +x /tmp/xwstart.sh

ENTRYPOINT [ "/tmp/xwstart.sh" ]
