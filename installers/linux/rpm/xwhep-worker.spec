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

%define	name	xwhep-worker
%define version	@XWVERSION@
%define release 1

Summary: XtremWeb-HEP (XWHEP) is a platform for Global and Peer-to-Peer Computing, based on XtremWeb
Name: %{name}
Version: %{version}
Release: %{release}
License: GPL
Group: Applications/Distributed Computing
Source: %{name}-%{version}.tar.gz
URL: http://www.xtremweb-hep.org
Vendor: Laboratoire de l Accelerateur Lineaire (http://www.lal.in2p3.fr)
Packager: Oleg Lodygensky <lodygens@lal.in2p3.fr>
#Requires: j2sdk
Provides: %{name}-%{version}
BuildArch: noarch
Distribution: whatever


BuildRoot: %{_builddir}/%{name}-%{version}
# rpm -q --whatprovides /etc/rc.d/init.d/httpd
# AutoReqProv: no

%define XWSCRIPT xtremweb
%define XWJAR %{XWSCRIPT}.jar
%define XWCONFIG xtremwebconf.sh
%define XWWORKER xtremweb.worker
%define XWWORKERCONF xtremweb.worker.conf
%define XWMONITOR xtremweb.monitor
%define XWMONITOR_PL %{XWMONITOR}.pl
%define XW_INSTALLDIR /opt/%{name}-%{version}
%define XW_BINDIR %{XW_INSTALLDIR}/bin
%define XW_KEYDIR %{XW_INSTALLDIR}/keystore
%define XW_CONFDIR %{XW_INSTALLDIR}/conf
%define XW_DOCDIR %{XW_INSTALLDIR}/doc
%define XW_LIBDIR %{XW_INSTALLDIR}/lib
%define XWWORKER_PATH %{XW_BINDIR}/%{XWWORKER}
%define XWMONITOR_PATH %{XW_BINDIR}/%{XWMONITOR}
%define INITD /etc/init.d
%define INITDWORKER %{INITD}/%{XWWORKER}
%define INITDMONITOR %{INITD}/%{XWMONITOR}
%define SYSLOGIN @SYSTEMUSER@
%define LOG /var/log/xwhep-worker.log
%define VBHL /usr/bin/vboxheadless
%define VBAPPNAME virtualbox
%define USRBIN /usr/bin
%define XWCREATEVDI %{USRBIN}/xwcreatevdi
%define CREATEVDI %{XW_BINDIR}/createvdi
%define SUDOERS /etc/sudoers
%define SUDOERSENTRY "%{SYSLOGIN} ALL=NOPASSWD: %{XWCREATEVDI}"

%description
XWHEP is a Distributed Computing Plateform. It allows to set up
Global and Peer-to-Peer applications. The software is composed of
a server, a worker (the computing element) and a client (the user
interface element) parts.
This package installs the computing element.

%prep
touch %{LOG}
echo "[`date`] [%{name}] RPM prep nothing to do" >> %{LOG} 2>&1


%setup
touch %{LOG}

echo "[`date`] [%{name}] RPM setup" >> %{LOG} 2>&1
echo "[`date`] [%{name}] _tmppath = "%{_tmppath} >> %{LOG}  2>&1
echo "[`date`] [%{name}] _builddir = "%{_builddir} >> %{LOG}  2>&1 

rm -Rf %{XW_INSTALLDIR}
mkdir -p %{XW_INSTALLDIR}
cp -rf %{_builddir}/%{name}-%{version}/* %{XW_INSTALLDIR}

echo "[`date`] [%{name}] RPM setup done" >> %{LOG} 2>&1


%pre
touch %{LOG}
echo "[`date`] [%{name}] RPM pre nothing to do" >> %{LOG} 2>&1



%install
touch %{LOG}
echo  "RPM install" >> %{LOG} 2>&1
# this is done when creating the RPM
echo "[`date`] [%{name}] INSTALL XW_INSTALLDIR = %{XW_INSTALLDIR}" >> %{LOG} 2>&1


%post
# this is done when installing the RPM

touch %{LOG}
echo "[`date`] [%{name}] RPM post" >> %{LOG} 2>&1

rm -f %{INITDWORKER} >> %{LOG}  2>&1 
ln -s %{XWWORKER_PATH} %{INITDWORKER} >> %{LOG}  2>&1 
rm -f %{INITDMONITOR} >> %{LOG}  2>&1 
ln -s %{XWMONITOR_PATH} %{INITDMONITOR} >> %{LOG}  2>&1 

rm -f %{XWCREATEVDI} >> %{LOG}  2>&1 
ln -s %{CREATEVDI} %{XWCREATEVDI} >> %{LOG}  2>&1 

if [ -f %{SUDOERS} ] ; then
	cp -f %{SUDOERS} /etc/sudoers-without-xwhep
	echo %{SUDOERSENTRY} >> %{SUDOERS}
	chmod 440 %{SUDOERS}
fi

rm -Rf /tmp/xtremweb.worker* >> %{LOG}  2>&1 
rm -Rf /tmp/xtremweb.monitor* >> %{LOG}  2>&1 
rm -Rf /tmp/PortMapper.jni* >> %{LOG}  2>&1 
rm -f /tmp/XwTracer.jni* >> %{LOG}  2>&1 
rm -f /tmp/XWUtil.jni* >> %{LOG}  2>&1 
rm -Rf /tmp/XW.WORKER* >> %{LOG}  2>&1 

if [ "X%{SYSLOGIN}" != "X" ] ; then 
    /usr/sbin/groupadd %{SYSLOGIN} >> %{LOG}  2>&1 
    /usr/sbin/useradd %{SYSLOGIN} -d /home/%{SYSLOGIN} -s /bin/bash -g %{SYSLOGIN} -m >> %{LOG}  2>&1 
    chown -R %{SYSLOGIN}.%{SYSLOGIN} %{XW_INSTALLDIR} >> %{LOG}  2>&1 
    chown -R %{SYSLOGIN}.%{SYSLOGIN} %{XWCREATEVDI} >> %{LOG}  2>&1 
else
    echo "[`date`] [%{name}] SYSLOGIN variable is not set; this package will run as root; this is not a good idea" >> %{LOG} 2>&1
fi


#
# share VirtualBox, if available
#
if [ -x %{VBHL} ] ; then 
	echo "[`date`] [%{name}] sharedapps=%{VBAPPNAME}" >> %{XW_CONFDIR}/%{XWWORKERCONF}
fi

/sbin/service %{XWWORKER} stop >> %{LOG}  2>&1 
/sbin/chkconfig --del %{XWWORKER} >> %{LOG}  2>&1 
/sbin/chkconfig --add %{XWWORKER} >> %{LOG}  2>&1 
/sbin/service %{XWWORKER} start >> %{LOG}  2>&1 
/sbin/service %{XWMONITOR} stop >> %{LOG}  2>&1 
/sbin/chkconfig --del %{XWMONITOR} >> %{LOG}  2>&1 
/sbin/chkconfig --add %{XWMONITOR} >> %{LOG}  2>&1 
/sbin/service %{XWMONITOR} start >> %{LOG}  2>&1 

echo "[`date`] [%{name}] RPM post done" >> %{LOG} 2>&1



%clean
# this is called at the end of the rpmbuild
touch %{LOG}
echo "[`date`] [%{name}] RPM clean" >> %{LOG} 2>&1

rm -Rf %{XW_INSTALLDIR}/opt >> %{LOG}  2>&1 
[ -d ${XW_BINDIR} ] || rm -Rf %{XW_INSTALLDIR} >> %{LOG}  2>&1

echo "[`date`] [%{name}] RPM clean done" >> %{LOG} 2>&1


###########################################################
# package uninstallation
###########################################################
# The RPM upgrading first installs new version, then remove older one
# Scripts have one argument (known as $1) containing count the number of versions of the package that are installed
# This argument is as follow
#  first installation, $1 == 1
#  upgrade             $1 >  1
#  remove              $1 == 0
# See:
# http://docs.fedoraproject.org/en-US/Fedora_Draft_Documentation/0.1/html/RPM_Guide/ch09s04s05.html
###########################################################

%preun
touch %{LOG}
echo "[`date`] [%{name}] RPM preun ($1)" >> %{LOG} 2>&1

if [ $1 = 0 ] ; then 
	echo "[`date`] [%{name}] RPM preun is unistalling" >> %{LOG} 2>&1

	/sbin/service %{XWWORKER} stop >> %{LOG}  2>&1 
	/sbin/service %{XWMONITOR} stop >> %{LOG}  2>&1 
	/sbin/chkconfig --del %{XWWORKER} >> %{LOG}  2>&1 
	/sbin/chkconfig --del %{XWMONITOR} >> %{LOG}  2>&1 

	rm -Rf %{XW_INSTALLDIR} >> %{LOG}  2>&1 
	ls -l %{INITDWORKER} | grep %{version} 2>&1
	[ $? -eq 0 ] && rm -f %{INITDWORKER} >> %{LOG}  2>&1 
	ls -l %{INITDMONITOR} | grep %{version} 2>&1
	[ $? -eq 0 ] && rm -f %{INITDMONITOR} >> %{LOG}  2>&1 

	rm -f %{XWCREATEVDI} >> %{LOG}  2>&1

	cat %{SUDOERS} | grep -v %{SYSLOGIN} > /tmp/xwsudoers
	mv -f /tmp/xwsudoers %{SUDOERS}
	chmod 440 %{SUDOERS}

	rm -Rf /tmp/xtremweb.worker* >> %{LOG}  2>&1 
	rm -Rf /tmp/PortMapper.jni* >> %{LOG}  2>&1 
	rm -f /tmp/XwTracer.jni* >> %{LOG}  2>&1 
	rm -f /tmp/XWUtil.jni* >> %{LOG}  2>&1 
	rm -Rf /tmp/XW.WORKER* >> %{LOG}  2>&1 
else
	echo "[`date`] [%{name}] RPM preun does nothing" >> %{LOG} 2>&1
fi

echo "[`date`] [%{name}] RPM preun done" >> %{LOG} 2>&1


%files
%defattr(-,root,root)
%{XW_INSTALLDIR}/AUTHORS
%{XW_INSTALLDIR}/ChangeLog
%{XW_INSTALLDIR}/INSTALL
%{XW_INSTALLDIR}/License
%{XW_INSTALLDIR}/License.bouncycastle
%{XW_INSTALLDIR}/License.smartsockets-1.4
%{XW_BINDIR}/xtremwebconf.sh
%{XW_BINDIR}/xtremweb
%{XW_BINDIR}/xtremweb.worker
%{XW_BINDIR}/createvdi
%{XW_BINDIR}/xtremweb.monitor
%{XW_BINDIR}/xtremweb.monitor.pl
%{XW_BINDIR}/createvdi
%{XW_KEYDIR}/xwhepworker.keys
%config %{XW_CONFDIR}/xtremweb.worker.conf
#%doc %{XW_DOCDIR}/
%{XW_LIBDIR}/


%changelog
