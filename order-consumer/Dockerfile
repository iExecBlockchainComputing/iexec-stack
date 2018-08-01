FROM iexechub/ubuntu:16.04

RUN mkdir -p /home/sdk-env

WORKDIR /home

RUN apt-get install -y curl jq

RUN curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.33.2/install.sh | bash
RUN /bin/bash -c "source ~/.nvm/nvm.sh && nvm install v8.6.0 && npm -g install iexec"

ADD consumeOrders /home/consumeOrders

RUN chmod +x /home/consumeOrders

ENTRYPOINT [ "/home/consumeOrders" ]

# docker run -t -v $(pwd):/home/sdk-env -e CHAIN_NAME='rinkeby' -e CATEGORY='5' -e WORKERPOOL_OWNER='0x68ba0fC5E52e540778069113507507A012362cfD' -e PERIOD='60' --rm --name order-consumer iexechub/iexec-order-consumer