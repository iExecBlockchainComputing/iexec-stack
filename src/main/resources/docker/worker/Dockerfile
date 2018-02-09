FROM ubuntu:16.04
EXPOSE 4321 4322 4323 4324 4327 4328 443

ADD conf/xtremweb.worker.conf /xwhep/conf/
RUN mkdir -p /xwhep/bin
ADD bin/xtremweb.worker /xwhep/bin
ADD bin/xtremweb /xwhep/bin
ADD bin/xtremwebconf.sh /xwhep/bin
ADD lib /xwhep/lib

WORKDIR /xwhep

RUN apt-get update && \ 
       export DEBIAN_FRONTEND=noninteractive && \
       apt-get install -y openjdk-8-jre zip unzip wget make gcc curl docker.io iputils-ping

# install openSSL to be able to download certificates 
RUN wget http://www.openssl.org/source/openssl-1.0.2g.tar.gz && \
		tar -xvzf openssl-1.0.2g.tar.gz && \
		cd openssl-1.0.2g && \
		./config --prefix=/usr/ && \
		make && \
		make install

RUN echo "#!/bin/sh" > /xwhep/xwstart.sh && \
    echo "if [ ! -z \$XWSERVERADDR ] ; then " >> /xwhep/xwstart.sh && \
    echo "	echo \"\$XWSERVERADDR \$XWSERVERNAME\" >> /etc/hosts" >> /xwhep/xwstart.sh && \
    echo "	sed -i "s/^DISPATCHERS=.*/DISPATCHERS=\$XWSERVERNAME/g" /xwhep/conf/xtremweb.worker.conf " >> /xwhep/xwstart.sh && \
    echo "fi" >> /xwhep/xwstart.sh

# import the server certificate into the container keystore
RUN echo "keytool -import -alias localhost -file /xwhep/certificate/xwscheduler.pem -trustcacerts -keystore /etc/ssl/certs/java/cacerts -storepass changeit -v -noprompt " >> /xwhep/xwstart.sh

# update the SSLKEYSTORE to point to the container's keystore
RUN echo "sed -i \"s/^SSLKEYSTORE=.*/SSLKEYSTORE=\/etc\/ssl\/certs\/java\/cacerts/g\" /xwhep/conf/xtremweb.worker.conf " >> /xwhep/xwstart.sh  && \
    echo "sed -i \"s/^SSLKEYPASSWORD=.*/SSLKEYPASSWORD=changeit/g\" /xwhep/conf/xtremweb.worker.conf " >> /xwhep/xwstart.sh

# update the SHAREDAPPS variable to docker  
RUN echo "sed -i \"s/^#SHAREDAPPS=.*/SHAREDAPPS=docker/g\" /xwhep/conf/xtremweb.worker.conf " >> /xwhep/xwstart.sh

# choose the login/password to run worker or vworker:
RUN echo "if [ ! -z \$LOGIN ] ; then " >> /xwhep/xwstart.sh  && \
    echo "	sed -i \"s/^PASSWORD=.*/PASSWORD=\$PASSWORD/g\" /xwhep/conf/xtremweb.worker.conf " >> /xwhep/xwstart.sh  && \
    echo "	sed -i \"s/^LOGIN=.*/LOGIN=\$LOGIN/g\" /xwhep/conf/xtremweb.worker.conf " >> /xwhep/xwstart.sh  && \
    echo "fi" >> /xwhep/xwstart.sh && \
	echo "if [ ! -z \$LOGGERLEVEL ] ; then " >> /xwhep/xwstart.sh  && \
    echo "	sed -i \"s/^LOGGERLEVEL=.*/LOGGERLEVEL=\$LOGGERLEVEL/g\" /xwhep/conf/xtremweb.worker.conf " >> /xwhep/xwstart.sh  && \
    echo "fi" >> /xwhep/xwstart.sh && \
    echo "cat /etc/hosts" >> /xwhep/xwstart.sh  && \
    echo "/xwhep/bin/xtremweb.worker console" >> /xwhep/xwstart.sh

RUN chmod +x /xwhep/xwstart.sh

ENTRYPOINT [ "/xwhep/xwstart.sh" ]
