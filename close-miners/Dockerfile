FROM iexechub/ubuntu:16.04

ADD closeMiners /home/closeMiners

WORKDIR /home

RUN chmod +x /home/closeMiners

ENTRYPOINT [ "/home/closeMiners" ]
