FROM ubuntu:16.04
EXPOSE 4321 4322 4323 4324 4327 4328 443
ENV XWVERSION "11.5.0"

ADD conf /xwhep/conf
ADD bin /xwhep/bin
ADD lib /xwhep/lib
ADD keystore /xwhep/keystore

WORKDIR /xwhep

RUN apt-get update

RUN export DEBIAN_FRONTEND=noninteractive && \
       apt-get install -y openjdk-8-jre zip unzip wget make curl docker.io

RUN echo "#!/bin/sh" > /tmp/xwstart.sh

RUN echo "if [ ! -z \$XWSERVERADDR ] ; then " >> /tmp/xwstart.sh
RUN echo "	echo \"\$XWSERVERADDR \$XWSERVERNAME\" >> /etc/hosts" >> /tmp/xwstart.sh
RUN echo "	sed -i "s/^DISPATCHERS=.*/DISPATCHERS=\$XWSERVERNAME/g" /xwhep/conf/xtremweb.worker.conf " >> /tmp/xwstart.sh
RUN echo "fi" >> /tmp/xwstart.sh
RUN echo "cat /etc/hosts" >> /tmp/xwstart.sh
RUN echo "/xwhep/bin/xtremweb.worker console" >> /tmp/xwstart.sh

RUN chmod +x /tmp/xwstart.sh

ENTRYPOINT [ "/tmp/xwstart.sh" ]



# server

#ADD conf /xwhep/conf
#ADD bin /xwhep/bin
#ADD lib /xwhep/lib
#ADD keystore /xwhep/keystore

#WORKDIR /xwhep

#
# DEBIAN_FRONTEND=noninteractive bypasses user input.
# Hence, mysql-server installation will not ask
# for the new mysql admin password;
# and mysql admin password will be empty
#
#RUN apt-get update
#RUN export DEBIAN_FRONTEND=noninteractive && \
#       apt-get install -y openjdk-8-jre mysql-server mysql-client \
#       zip unzip wget make

#
# -1- don't renice in container
# -2- we must remove LAUNCHERURL since Apache is not installed
# 

#RUN sed -i "s/^V_NICE=.*//g" /xwhep/bin/xtremwebconf.sh
#RUN sed -i "s/LAUNCHER.*//g" /xwhep/conf/xtremweb.server.conf

#
# Entry point script
# 
#RUN echo "#!/bin/sh" > /tmp/xwstart.sh
#RUN echo "service mysql start && mysql -u root --password=root < /xwhep/bin/db-maintenance/xwhep-core-tables-create-tables.sql" >> /tmp/xwstart.sh
#RUN echo "/xwhep/bin/xtremweb.server console" >> /tmp/xwstart.sh
#RUN chmod +x /tmp/xwstart.sh

#ENTRYPOINT [ "/tmp/xwstart.sh" ]

