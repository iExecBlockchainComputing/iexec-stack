#!/bin/bash

# create the config file for gmetad in the container: /etc/ganglia/gmetad.conf
sudo sh -c "cat > /etc/ganglia/gmetad.conf <<EOL
data_source \"XtremWeb-HEP\" 60 $GMOND_CONTAINER_NAME:8694 
setuid_username \"ganglia\"
# RRDCached_address available in Ganglia 3.7.0+
#rrdcached_address 127.0.0.1:9998
case_sensitive_hostnames 0
EOL"

# Command from the official Dockerfile
/etc/init.d/rrdcached start & /etc/init.d/gmetad start & /usr/sbin/apache2ctl -D FOREGROUND
