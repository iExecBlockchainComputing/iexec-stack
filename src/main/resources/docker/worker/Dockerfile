FROM ubuntu:16.04
EXPOSE 4321 4322 4323 4324 4327 4328 443

ADD conf/xtremweb.worker.conf /xwhep/conf/
RUN mkdir -p /xwhep/bin
RUN mkdir -p /xwhep/certificate
ADD bin/xtremweb.worker /xwhep/bin
ADD bin/xtremweb /xwhep/bin
ADD bin/xtremwebconf.sh /xwhep/bin
ADD lib /xwhep/lib

WORKDIR /xwhep

RUN apt-get update && \ 
       export DEBIAN_FRONTEND=noninteractive && \
       apt-get install -y openjdk-8-jre zip unzip wget curl openssl apt-transport-https ca-certificates software-properties-common 

# install docker
RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
RUN apt-get update
RUN apt-get install -y docker-ce

# check that the server name has been defined
RUN echo "#!/bin/sh" > /xwhep/xwstart.sh && \
	echo "if [ -z \$SCHEDULER_DOMAIN ] ; then " >> /xwhep/xwstart.sh && \
	echo "	echo \" The domain name of the scheduler is missing \"; exit 1" >> /xwhep/xwstart.sh && \
	echo "fi" >> /xwhep/xwstart.sh

# Modify etc hosts if an ip is given in parameters
RUN echo "if [ ! -z \$SCHEDULER_IP ] ; then " >> /xwhep/xwstart.sh && \
    echo "	echo \"\$SCHEDULER_IP \$SCHEDULER_DOMAIN\" >> /etc/hosts" >> /xwhep/xwstart.sh && \
	echo "fi" >> /xwhep/xwstart.sh

# download the certificate from the server
RUN echo "  echo \"Downloading certificate from scheduler...\" " >> /xwhep/xwstart.sh && \
	echo "  echo -n | openssl s_client -connect \$SCHEDULER_DOMAIN:443 | sed -ne \"/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p\" > /xwhep/certificate/xwscheduler.crt " >> /xwhep/xwstart.sh && \
	echo "  echo \"Converting downloaded certificate to x509 DER...\" " >> /xwhep/xwstart.sh && \
	echo "  openssl x509 -outform der -in /xwhep/certificate/xwscheduler.crt -out /xwhep/certificate/xwscheduler.pem " >> /xwhep/xwstart.sh && \
    echo "	sed -i "s/^DISPATCHERS=.*/DISPATCHERS=\$SCHEDULER_DOMAIN/g" /xwhep/conf/xtremweb.worker.conf " >> /xwhep/xwstart.sh

# import the server certificate into the container keystore
RUN echo "keytool -import -alias localhost -file /xwhep/certificate/xwscheduler.pem -trustcacerts -keystore /etc/ssl/certs/java/cacerts -storepass changeit -v -noprompt " >> /xwhep/xwstart.sh

# update the SSLKEYSTORE to point to the container's keystore
RUN echo "sed -i \"s/^SSLKEYSTORE=.*/SSLKEYSTORE=\/etc\/ssl\/certs\/java\/cacerts/g\" /xwhep/conf/xtremweb.worker.conf " >> /xwhep/xwstart.sh  && \
    echo "sed -i \"s/^SSLKEYPASSWORD=.*/SSLKEYPASSWORD=changeit/g\" /xwhep/conf/xtremweb.worker.conf " >> /xwhep/xwstart.sh

# ADD defined SHAREDAPPS variable if defined, otherwise use a default value  
RUN echo "if [ ! -z \$SHAREDAPPS ] ; then " >> /xwhep/xwstart.sh && \
    echo "	sed -i \"s/^#SHAREDAPPS=.*/SHAREDAPPS=\$SHAREDAPPS/g\" /xwhep/conf/xtremweb.worker.conf " >> /xwhep/xwstart.sh && \
	echo "else sed -i \"s/^#SHAREDAPPS=.*/SHAREDAPPS=docker/g\" /xwhep/conf/xtremweb.worker.conf " >> /xwhep/xwstart.sh && \
	echo "fi" >> /xwhep/xwstart.sh

# Add SHAREDPACKAGES only if it is given in parameters
RUN echo "if [ ! -z \$SHAREDPACKAGES ] ; then " >> /xwhep/xwstart.sh && \
    echo "	sed -i \"s/^#SHAREDPACKAGES=.*/SHAREDPACKAGES=\$SHAREDPACKAGES/g\" /xwhep/conf/xtremweb.worker.conf " >> /xwhep/xwstart.sh && \
	echo "fi" >> /xwhep/xwstart.sh

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
