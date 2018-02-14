FROM ubuntu:16.04
EXPOSE 4321 4322 4323 4324 4327 4328 443

COPY conf/xtremweb.client.conf /xwhep/conf/
COPY lib /xwhep/lib
COPY bin /xwhep/bin/ 
WORKDIR /xwhep/bin
RUN mkdir -p /xwhep/certificate
# remove non necessary scripts from /xwhep/bin
# remove all subfolders:
RUN find . -mindepth 1 -maxdepth 1 -type d -exec rm -rf {} +
# remove files that don't match xw* or xtr*
RUN find . -type f ! -name 'xw*' ! -name 'xtr*' -exec rm -rf {} +

RUN apt-get update && \
       export DEBIAN_FRONTEND=noninteractive && \
       apt-get install -y openjdk-8-jre zip unzip wget openssl curl iputils-ping

RUN echo "#!/bin/sh" > /tmp/xwstart.sh

# check that the server name has been defined
RUN echo "if [ -z \$SCHEDULER_DOMAIN ] ; then " >> /tmp/xwstart.sh && \
	echo "	echo \" The domain name of the scheduler is missing \"; exit 1" >> /tmp/xwstart.sh && \
	echo "fi" >> /tmp/xwstart.sh

# Modify etc hosts if an ip is given in parameters
RUN echo "if [ ! -z \$SCHEDULER_IP ] ; then " >> /tmp/xwstart.sh && \
    echo "	echo \"\$SCHEDULER_IP \$SCHEDULER_DOMAIN\" >> /etc/hosts" >> /tmp/xwstart.sh && \
	echo "fi" >> /tmp/xwstart.sh

# download the certificate from the server
RUN echo "  echo \"Downloading certificate from scheduler...\" " >> /tmp/xwstart.sh && \
	echo "  echo -n | openssl s_client -connect \$SCHEDULER_DOMAIN:443 | sed -ne \"/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p\" > /xwhep/certificate/xwscheduler.crt " >> /tmp/xwstart.sh && \
	echo "  echo \"Converting downloaded certificate to x509 DER...\" " >> /tmp/xwstart.sh && \
	echo "  openssl x509 -outform der -in /xwhep/certificate/xwscheduler.crt -out /xwhep/certificate/xwscheduler.pem " >> /tmp/xwstart.sh

RUN echo "	sed -i "s/^DISPATCHERS=.*/DISPATCHERS=\$SCHEDULER_DOMAIN/g" /xwhep/conf/xtremweb.client.conf " >> /tmp/xwstart.sh

# import the server certificate into the container keystore
RUN echo "keytool -import -alias localhost -file /xwhep/certificate/xwscheduler.pem -trustcacerts -keystore /etc/ssl/certs/java/cacerts -storepass changeit -v -noprompt " >> /tmp/xwstart.sh

# update the SSLKEYSTORE to point to the container's keystore
RUN echo "sed -i \"s/^SSLKEYSTORE=.*/SSLKEYSTORE=\/etc\/ssl\/certs\/java\/cacerts/g\" /xwhep/conf/xtremweb.client.conf " >> /tmp/xwstart.sh
RUN echo "sed -i \"s/^SSLKEYPASSWORD=.*/SSLKEYPASSWORD=changeit/g\" /xwhep/conf/xtremweb.client.conf " >> /tmp/xwstart.sh

RUN echo "cat /etc/hosts" >> /tmp/xwstart.sh
RUN chmod +x /tmp/xwstart.sh

WORKDIR /xwhep/bin
CMD bash -C '/tmp/xwstart.sh';'bash'
