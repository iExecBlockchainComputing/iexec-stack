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

%define	xwsrv	xwhep-server
%define	name	%{xwsrv}-conf
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
Requires: %{xwsrv}-%{version}
Provides: %{name}-%{version}
BuildArch: noarch
Distribution: whatever


BuildRoot: %{_builddir}/%{name}-%{version}
# rpm -q --whatprovides /etc/rc.d/init.d/httpd
# AutoReqProv: no

%define XWSERVER xtremweb.server
%define XWGANGLIA xtremweb.ganglia
%define XW_INSTALLDIR /opt/%{xwsrv}-%{version}
%define XW_BINDIR %{XW_INSTALLDIR}/bin
%define XW_CONFDIR %{XW_INSTALLDIR}/conf
%define XW_KEYDIR %{XW_INSTALLDIR}/keystore
%define SYSLOGIN @SYSTEMUSER@
%define LOG /var/log/xwhep-server.log



%description
XWHEP is a Distributed Computing Plateform. It allows to set up
Global and Peer-to-Peer applications. The software is composed of
a server, a worker (the computing element) and a client (the user
interface element) parts.
This package installs the configuration of the server element that ensures the platform integrity.

%prep
echo "[`date`] [%{name}] RPM prep nothing to do" >> %{LOG} 2>&1

%setup
touch %{LOG}
echo "[`date`] [%{name}] RPM setup" >> %{LOG} 2>&1
echo "[`date`] [%{name}] SETUP _tmppath = "%{_tmppath} >> %{LOG} 2>&1
echo "[`date`] [%{name}] SETUP _builddir = "%{_builddir} >> %{LOG} 2>&1
mkdir -p %{XW_INSTALLDIR} >> %{LOG}  2>&1 
cp -rf %{_builddir}/%{name}-%{version}/* %{XW_INSTALLDIR} >> %{LOG}  2>&1 

%pre
# this is done when installing the RPM
echo "[`date`] [%{name}] RPM pre nothing to do" >> %{LOG} 2>&1


%install
# this is done when creating the RPM
echo "[%{name}] RPM install  XW_INSTALLDIR = %{XW_INSTALLDIR} nothing to do" >> %{LOG} 2>&1


%post
# this is done when installing the RPM

touch %{LOG}
echo "[`date`] [%{name}] RPM post" >> %{LOG} 2>&1

if [ "X%{SYSLOGIN}" != "X" ] ; then 
	echo "[`date`] [%{name}] add login %{SYSLOGIN}" >> %{LOG} 2>&1
    chown -R %{SYSLOGIN}.%{SYSLOGIN} %{XW_INSTALLDIR} >> %{LOG}  2>&1 
else 
    echo "[`date`] [%{name}] SYSLOGIN variable is not set; this package will run as root; this is not a good idea" >> %{LOG} 2>&1
fi

/sbin/service %{XWSERVER} restart >> %{LOG}  2>&1 
/sbin/service %{XWGANGLIA} restart >> %{LOG}  2>&1 

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

	/sbin/service %{XWSERVER} stop >> %{LOG}  2>&1 
	/sbin/service %{XWGANGLIA} stop >> %{LOG}  2>&1 
else
	echo "[`date`] [%{name}] RPM preun does nothing" >> %{LOG} 2>&1
fi
echo "[`date`] [%{name}] RPM preun done" >> %{LOG} 2>&1


%files
%defattr(-,root,root)
%{XW_KEYDIR}/
%config %{XW_CONFDIR}/


%changelog
