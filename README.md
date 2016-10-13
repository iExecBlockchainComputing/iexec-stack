# xtremweb-hep
Data driven volunteer cloud

### Status
[![Build Status](https://travis-ci.org/lodygens/xtremweb-hep.svg?branch=master)](https://travis-ci.org/lodygens/xtremweb-hep)

Quick start
===========

To start your XWHEP server, launch an Ubuntu16 instance and run the following commands (don't forget to set your own variables on xwconfigure.values) :

    sudo apt-get -y update
    sudo apt-get -y upgrade
    sudo apt-get install -y openjdk-8-jdk mysql-client mysql-server git make ant gcc openvpn zip uuid
    sudo service mysql start
    git clone https://github.com/lodygens/xtremweb-hep.git
    cd xtremweb-hep/build/
    export JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64"
    make clean && make
    cd dist/xwhep-10.4.0/

    cat << EOF > conf/xwconfigure.values

    XWUSER='xwhep'
    DBVENDOR='mysql'
    DBENGINE='InnoDB'
    DBHOST=''
    DBADMINLOGIN='root'
    DBADMINPASSWORD='a_password'
    DBNAME='xtremweb'
    DBUSERLOGIN='xwuser'
    DBUSERPASSWORD='xwuserp'
    XWADMINLOGIN='admin'
    XWADMINPASSWORD='adminp'
    XWWORKERLOGIN='worker'
    XWWORKERPASSWORD='workerp'
    XWSERVER='your_IP'
    CERTCN='your_IP'
    CERTOU=''
    CERTO=''
    CERTL=''
    CERTC='fr'
    SSLKEYPASSPHRASE='your_passphrase'
    SSLKEYSERVERPASSWORD='serverp'
    SSLKEYWORKERPASSWORD='workerp'
    SSLKEYCLIENTPASSWORD='clientp'
    USERCERTDIR=''
    XWUPGRADEURL='http://your_IP/XWHEP/download/xtremweb.jar'

    EOF


    ./bin/xwconfigure --yes

    # You must retreive the content of /et/hostname
    # On EC2, it should be something like ip-172-xxx-yyy-zzz
    #
    # You must edit /etc/hosts and make the line like
    # 127.0.0.1 localhost ip-172-xxx-yyy-zzz

    ./bin/xtremweb.server console -DHOMEDIR=homedir/ -DLOGGERLEVEL=DEBUG


Depending on your configuration, in your `conf/xtremweb.server.conf` file, you may need to :
* remove or comment `LAUNCHERURL` variable .
* change the `HTTPSPORT` value


Finally you can access to https://YOUR_IP:HTTPSPORT
