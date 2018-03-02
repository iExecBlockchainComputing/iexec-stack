FROM perl:5.20

RUN cpanm install DBI
RUN cpanm install DBD::mysql

RUN mkdir -p /iexec

COPY xtremweb.gmond.pl /iexec
WORKDIR /iexec

EXPOSE 8649

CMD [ "perl", "./xtremweb.gmond.pl" ]
