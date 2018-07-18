#FROM ubuntu:16.04
FROM ubuntu-16.04-updated

ADD publishOrders /home/publishOrders

WORKDIR /home

RUN apt-get update && export DEBIAN_FRONTEND=noninteractive && apt-get install -y curl mysql-client

RUN chmod +x /home/publishOrders

ENTRYPOINT [ "/home/publishOrders" ]


# docker run -t -e DBUSER='' -e DBPASS='' -e ADMINLOGIN='' -e ADMINPASSWORD='' -e TRUST='10' -e PUBLISH_PERIOD='1' -e CATEGORY='5' PRICE='30000000000' --name order-publisher iexechub/iexec-order-publisher