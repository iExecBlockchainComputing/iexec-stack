# Copyrights     : CNRS
# Author         : Oleg Lodygensky
# Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
# Web            : http://www.xtremweb-hep.org
# 
#      This file is part of XtremWeb-HEP.
#
#    XtremWeb-HEP is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    XtremWeb-HEP is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.

%define _unpackaged_files_terminate_build 0

Summary: XtremWeb-HEP (XWHEP) is a platform for Global and Peer-to-Peer Computing, based on XtremWeb
Name: xtremweb-hep
Version: @XWVERSION@
Release: 1

Copyright: GPL
Group: Applications/Distributed Computing
Source: %{name}-%{version}.tar.gz
URL: http://www.xtremweb-hep.org
Vendor: Laboratoire de l Accelerateur Lineaire (http://www.lal.in2p3.fr)
Packager: Oleg Lodygensky <lodygens@lal.in2p3.fr>, Gilles Fedak <fedak@lri.fr>
#Requires: j2sdk
Provides: %{name}-%{version}-sources

#BuildRoot: /var/tmp/%{name}-buildroot
# rpm -q --whatprovides /etc/rc.d/init.d/httpd

%define SYSLOGIN @SYSTEMUSER@

%description
Xtremweb-HEP is a Distributed Computing Plateform. It allows to set up
    Global and Peer-to-Peer applications. The software is composed of
    a server part and a worker part. A demonstration based on the
    PovRay software shows how to plug a legacy application in the
    Xtremweb framework.

%prep
%setup


%build
./configure --prefix=/usr/local --with-www-dir=/var/www/html/%{name} \
    --sysconfdir=/etc --disable-keys --disable-installworker  
make

%install
make install
#FIXME
cp /usr/src/redhat/BUILD/%{name}-%{version}/XtremPov_demo.tar.gz /var/www/html/%{name}
(cd /var/www/html/%{name} && tar zxvf XtremPov_demo.tar.gz)
 
%clean
rm -rf /var/www/html/%{name} /usr/local/share/%{name} \
    /usr/local/bin/rc.xtremweb.server /usr/local/bin/xtremweb.server \
    /usr/local/bin/xtremweb.worker /usr/local/bin/xwgendb\
    /usr/local/bin/xwgenkeys /usr/local/bin/xwgenworker\
    /usr/local/bin/java-config /usr/local/bin/xtremweb.monitor \
    /usr/local/bin/xtremweb.client \
    /usr/local/lib/libXwUtil.a /usr/local/lib/libXwUtil.la\
    /usr/local/lib/libXwUtil.so /usr/local/lib/libXwUtil.so.0\
    /usr/local/lib/libXwUtil.so.0.0.0\
    /usr/local/lib/libXwTracer.a /usr/local/lib/libXwTracer.la\
    /usr/local/lib/libXwTracer.so /usr/local/lib/libXwTracer.so.0\
    /usr/local/lib/libXwTracer.so.0.0.0\
    /etc/xw.conf /etc/xwrc.sample

%files
%defattr(-,root,root)
/usr/local/bin/java-config
/usr/local/bin/rc.xtremweb.server
/usr/local/bin/xtremweb.server
/usr/local/bin/xtremweb.worker
/usr/local/bin/xwgendb
/usr/local/bin/xwgenkeys
/usr/local/bin/xwgenworker
/usr/local/bin/xtremweb.monitor
/usr/local/bin/xtremweb.client
/usr/local/lib/libXwUtil.a
/usr/local/lib/libXwUtil.la
/usr/local/lib/libXwUtil.so
/usr/local/lib/libXwUtil.so.0
/usr/local/lib/libXwUtil.so.0.0.0
/usr/local/lib/libXwTracer.a
/usr/local/lib/libXwTracer.la
/usr/local/lib/libXwTracer.so
/usr/local/lib/libXwTracer.so.0
/usr/local/lib/libXwTracer.so.0.0.0
/usr/local/share/%{name}
/etc/xw.conf
/etc/xwrc
/var/www/html/%{name}/

%post
/etc/rc.d/init.d/httpd start
/etc/rc.d/init.d/mysqld start
rm -f /tmp/xtremweb_install.sh
cat > /tmp/xtremweb_install.sh <<EOF 
ldconfig
adduser -c "%{name} server" -d /var/www/html/%{name} -g apache -s /bin/sh xtremweb  -M
chown -R xtremweb.apache /var/www/html/%{name}
chmod -R g+w /var/www/html/%{name}/db
/usr/local/bin/xwgenkeys
/usr/local/bin/xwgenworker
/usr/local/bin/xwgendb
ln -s /usr/local/bin/rc.xtremweb.server /etc/rc.d/init.d/
/etc/rc.d/init.d/rc.xtremweb.server start
mysql --user=xtremweb --password=xwpassword < /var/www/html/%{name}/demo/povray.sql
EOF
sh /tmp/xtremweb_install.sh

%preun

/etc/rc.d/init.d/rc.xtremweb.server stop
rm -rf /var/www/html/%{name}/download
rm -rf /var/www/html/%{name}/db
rm -rf /usr/local/share/%{name}/keys
rm -f /etc/rc.d/init.d/rc.xtremweb.server
/etc/rc.d/init.d/mysqld start
 
%postun
mysqladmin -f drop xtremweb
mysql mysql -e " delete from user where User='xtremweb'"
userdel xtremweb

%changelog
