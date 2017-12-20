#!/usr/bin/perl


#
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
#
#


#
# File    : xtremweb.tests.pl
# Author  : Oleg Lodygens (lodygens at lal.in2p3.fr)
# Date    : Aout 20th, 2008
# Purpose : this tests the XWHEP DesktopGrid platform
#
#

use strict;
use English;
use IO::Handle;
use IO::Socket;
use File::Basename;
use File::stat;
use DirHandle;
use integer;
use DBI;
use XML::Simple;
use Data::Dumper;
use UNIVERSAL 'isa';

#
# trap CTRL+C
#
use sigtrap 'handler' => \&ctrlcHandler, 'INT';

#
# this controls program execution
#
my $continuer = 1;

# Synchronize the Perl IOs
autoflush STDIN 1;
autoflush STDERR 1;
autoflush STDOUT 1;

#
# default values
#
my $startTime = time;
my $currentTime = time;
my $scriptName = `basename $0`;
chomp ($scriptName);
my $SAVDIR = `pwd`;
my $ROOTDIR = `dirname $0`;
`cd $ROOTDIR`;
`cd ..`;
$ROOTDIR=`pwd`;
`cd $SAVDIR`;
chomp ($ROOTDIR);

my $HEAD="";
my $PPC="ppc";
my $INTEL="ix86";
my $INTEL64="x86_64";
my $LINUX="linux";
my $WIN32="win32";
my $MACOSX="macosx";

my $appbasename="apptest";
my $appdir="$ROOTDIR/$appbasename";
my $bindir="$ROOTDIR/bin";
my $keystoredir="$ROOTDIR/keystore";
my $workerkeys="$keystoredir/xwhepworker.keys";
my $clientkeys="$keystoredir/xwhepclient.keys";
my $confdir="$ROOTDIR/conf";
my $libdir="$ROOTDIR/lib";
my $clientconffile="$confdir/xtremweb.client.conf";
my $workerconffile="$confdir/xtremweb.worker.conf";
my $RANDOM = randomInteger();
my $confsavfile="$confdir/xtremweb.client.conf.$RANDOM";
$RANDOM = randomInteger();

my $group1appbasename;
my $privapp1basename;


#my $WORKERCLASS="xtremweb.worker.Worker";
my $WORKERCLASS="xtremweb.common.HTTPLauncher";
my $JARFILE="$libdir/xtremweb.jar";

my $JAVAWORKER="java -Djavax.net.ssl.trustStore=$workerkeys -cp $JARFILE $WORKERCLASS ";

my $userstdlevel="STANDARD_USER";
my $userworkerlevel="WORKER_USER";

# group1worker
$RANDOM = randomInteger();
my $group1workerconffile="$confdir/group1worker.conf.$RANDOM";
$RANDOM = randomInteger();
my $group1workerlogin="group1worker.$RANDOM";
my $group1workerpassword=$group1workerlogin;
my $group1workeremail=$group1workerlogin;

# user1
my $user1clientconffile="$confdir/user1.client.conf.$RANDOM";
my $user1workerconffile="$confdir/user1.worker.conf.$RANDOM";
$RANDOM = randomInteger();
my $user1login="user1.$RANDOM";
my $user1password=$user1login;
my $user1email=$user1login;
#user2
my $user2clientconffile="$confdir/user2.client.conf.$RANDOM";
my $user2workerconffile="$confdir/user2.worker.conf.$RANDOM";
$RANDOM = randomInteger();
my $user2login="user2.$RANDOM";
my $user2password=$user2login;
my $user2email=$user2login;
# user11
my $user11clientconffile="$confdir/user11.client.conf.$RANDOM";
my $user11workerconffile="$confdir/user11.worker.conf.$RANDOM";
$RANDOM = randomInteger();
my $user11login="user11.$RANDOM";
my $user11password=$user11login;
my $user11email=$user11login;
#user12
my $user12clientconffile="$confdir/user12.client.conf.$RANDOM";
my $user12workerconffile="$confdir/user12.worker.conf.$RANDOM";
$RANDOM = randomInteger();
my $user12login="user12.$RANDOM";
my $user12password=$user12login;
my $user12email=$user12login;
# user21
my $user21clientconffile="$confdir/user21.client.conf.$RANDOM";
my $user21workerconffile="$confdir/user21.worker.conf.$RANDOM";
$RANDOM = randomInteger();
my $user21login="user21.$RANDOM";
my $user21password=$user21login;
my $user21email=$user21login;
#user22
my $user22clientconffile="$confdir/user22.client.conf.$RANDOM";
my $user22workerconffile="$confdir/user22.worker.conf.$RANDOM";
$RANDOM = randomInteger();
my $user22login="user22.$RANDOM";
my $user22password=$user22login;
my $user22email=$user22login;


my $xwapps="$bindir/xwapps";
my $xwdatas="$bindir/xwdatas";
my $xwusers="$bindir/xwusers";
my $xwusergroups="$bindir/xwusergroups";
my $xwrm="$bindir/xwrm";
my $xwchmod="$bindir/xwchmod";
my $xwsendapp="$bindir/xwsendapp";
my $xwsenddata="$bindir/xwsenddata";
my $xwsenduser="$bindir/xwsenduser";
my $xwsendusergroup="$bindir/xwsendusergroup";
my $xwsubmit="$bindir/xwsubmit";
my $xwworks="$bindir/xwworks";


my $pubworkerpid;
my $priv1workerpid;
my $group1workerpid;
$RANDOM = randomInteger();
my $pubworkerout="/tmp/workerpub.$RANDOM";
$RANDOM = randomInteger();
my $priv1workerout="/tmp/workerpriv1.$RANDOM";
$RANDOM = randomInteger();
$RANDOM = randomInteger();
my $group1workerout="/tmp/workergroup1.$RANDOM";
$RANDOM = randomInteger();
my $group1label="group1.$RANDOM";
my $admingroup1login="admin1.$RANDOM";
my $admin1clientconffile="$confdir/admin1.client.conf.$RANDOM";
$RANDOM = randomInteger();
my $admin2clientconffile="$confdir/admin2.client.conf.$RANDOM";

my $admingroup1uid;
my $group1uid;
my $group1workeruid;
my $user1uid;
my $user2uid;
my $user11uid;
my $user12uid;
my $user21uid;
my $user22uid;
my $appuid;
my $group1appuid;
my $privapp1uid;
my $linuxix86binuid;
my $win32ix86binuid;
my $macosxix86binuid;
my $macosx8664binuid;
my $macosxppcbinuid;

my $pubjobuser1uid;
my $pubjobuser2uid;
my $pubjobuser11uid;
my $pubjobuser12uid;

my $privjobuser1uid;

my $groupjobuser11uid;
my $groupjobuser12uid;

my $exectime = "";


#
# debug levels
#
my $DEBUG = 0;
my $INFO  = 1;
my $WARN  = 2;
my $ERROR = 3;
my $debug = $INFO;

my $sleepDelay = 60;

#
# this is called whenever user hits CTRL+C
#
sub ctrlcHandler {
    cleanup ("Caught SIGINT", 1);
}


#
# This prints out usage and exits
#
sub usage {
    print "Usage : $scriptName [-h] [-t] [-p] [-d level] [-d sleep]\n";
    print "\t -h         : gets this help\n";
    print "\t -t         : times connections\n";
    print "\t -d level   : sets debug level\n";
    print "\t -s sleep   : sets the number of seconds to sleep (default : 60s)\n";
    print "\t -alljobs   : runs all tests\n";
    print "\t           => insert usergroups, users, public/group/private applications and jobs\n";
    print "\t           => start public/group/private workers\n";
    print "\t -nojob     : inserts no job\n";
    print "\t           => insert usergroups, users, public/group/private applications and jobs\n";
    print "\t           => start no workers\n";
    print "\t -pub       : starts public workers\n";
    print "\t           => public jobs must be run\n";
    print "\t -nopub     : don't start public workers\n";
    print "\t           => public jobs may not run (this is normal)\n";
    print "\t -pubonly   : start public workers only\n";
    print "\t           => private and group jobs are not run (this is normal)\n";
    print "\t -group     : start group workers\n";
    print "\t           => group jobs must be run\n";
    print "\t -grouponly : start group workers only\n";
    print "\t           => private and public jobs may not run (this is normal)\n";
    print "\t -nogroup   : don't start group workers\n";
    print "\t           => group jobs may not run (this is normal)\n";
    print "\t -priv      : start private workers\n";
    print "\t           => private jobs are run\n";
    print "\t -privonly  : start private workers only\n";
    print "\t           => group and public jobs may not run (this is normal)\n";
    print "\t -nopriv    : don't start private workers\n";
    print "\t           => private jobs are not run (this is normal)\n";
    exit 1;
}


#
# This returns a random integer < 10K
#
sub randomInteger {
    return int(rand(10000));
}
#
# This gets date and time
# @return `date "+%m/%d/%y %H:%M:%S"`
sub dateAndTime {
    my $d;
    chomp($d=`date "+%m/%d/%y %H:%M:%S"`);
    return $d;
}
#
# This prints the script name, the current date and the provided message
# @param msg to print
#
sub printTimed {
    print "$HEAD $scriptName ".dateAndTime()." @_\n";
}
#
# This prints debugs
# @param msg to print
#
sub debugLevel {
    return "debug" if ($debug <= $DEBUG);
    return "info"  if ($debug <= $INFO);
    return "warn"  if ($debug <= $WARN);
    return "error" if ($debug <= $ERROR);
}
#
# This prints debugs
# @param msg to print
#
sub debug {
    printTimed "[DEBUG] : @_" if ($debug <= $DEBUG);
}


#
# This prints infos
# @param msg to print
#
sub info {
    printTimed "[INFO] : @_" if ($debug <= $INFO);
}


#
# This prints warnings
# @param msg to print
#
sub warn {
    printTimed "WARN : @_" if ($debug <= $WARN);
}


#
# This prints errors
# @param msg to print
#
sub error {
    printTimed "ERROR : @_" if ($debug <= $ERROR);
}
#
# This prints fatal errors
# This stops the script immediatly
# @param msg to print
#
sub fatal {
    cleanup ("FATAL : @_", 1);
}


#
# this prints 1st header
# @param msg to print
#
sub header1 {
    $HEAD = "";
    print "** @_\n";
}
#
# this prints 2nd level header
# @param msg to print
#
sub header2 {
    $HEAD = "   ";
    print " ** @_\n";
}

#
# params : PID
#
sub killchild {
    my $pid = shift;
    my $sig = 9;
    kill $sig, $pid;
}

#
# This removes temp files and datas
# This stops the script immediatly
#
sub cleanup {
    my $msg=shift;
    my $RC=shift;

    if ($debug <= $DEBUG) {
	header1 ("Cleaning up : ".dateAndTime()."  ($msg) : don't clean on DEBUG mode");
	exit $RC;
    }
    header1 ("Cleaning up : ".dateAndTime()."  ($msg)");
    header2("Cleaning DB");

    `$xwrm $privjobuser1uid`  if ($privjobuser1uid ne "");
    `$xwrm $pubjobuser1uid`   if ($pubjobuser1uid ne "");
    `$xwrm $pubjobuser2uid`   if ($pubjobuser2uid ne "");
    `$xwrm $pubjobuser11uid`  if ($pubjobuser11uid ne "");
    `$xwrm $pubjobuser12uid`  if ($pubjobuser12uid ne "");
    `$xwrm $groupjobuser11uid` if ($groupjobuser11uid ne "");
    `$xwrm $groupjobuser12uid` if ($groupjobuser12uid ne "");
    `$xwrm $user1uid`         if ($user1uid ne "");
    `$xwrm $user2uid`         if ($user2uid ne "");
    `$xwrm $user11uid`        if ($user11uid ne "");
    `$xwrm $user12uid`        if ($user12uid ne "");
    `$xwrm $user21uid`        if ($user21uid ne "");
    `$xwrm $user22uid`        if ($user22uid ne "");
    `$xwrm $group1workeruid`  if ($group1workeruid ne "");
    `$xwrm $group1uid`        if ($group1uid ne "");
    `$xwrm $admingroup1uid`   if ($group1uid ne "");
    `$xwrm $appuid`           if ($appuid ne "");
    `$xwrm $group1appuid`     if ($group1appuid ne "");
    `$xwrm $privapp1uid`      if ($privapp1uid ne "");
    `$xwrm $linuxix86binuid`  if ($linuxix86binuid ne "");
    `$xwrm $win32ix86binuid`  if ($win32ix86binuid ne "");
    `$xwrm $macosxix86binuid` if ($macosxix86binuid ne "");
    `$xwrm $macosx8664binuid` if ($macosx8664binuid ne "");
    `$xwrm $macosxppcbinuid ` if ($macosxppcbinuid ne "");

    header2("Cleaning FS");

    `mv $confsavfile $clientconffile` if ($clientconffile ne "");
    `rm -f $group1workerconffile`     if ($group1workerconffile ne "");
    `rm -f $admin1clientconffile`     if ($user1clientconffile ne "");
    `rm -f $admin2clientconffile`     if ($user1clientconffile ne "");
    `rm -f $user1clientconffile`      if ($user1clientconffile ne "");
    `rm -f $user2clientconffile`      if ($user2clientconffile ne "");
    `rm -f $user11clientconffile`     if ($user11clientconffile ne "");
    `rm -f $user12clientconffile`     if ($user12clientconffile ne "");
    `rm -f $user21clientconffile`     if ($user21clientconffile ne "");
    `rm -f $user22clientconffile`     if ($user22clientconffile ne "");
    `rm -f $user1workerconffile`      if ($user1workerconffile ne "");
    `rm -f $user2workerconffile`      if ($user2workerconffile ne "");
    `rm -f $user11workerconffile`     if ($user11workerconffile ne "");
    `rm -f $user12workerconffile`     if ($user12workerconffile ne "");
    `rm -f $user21workerconffile`     if ($user21workerconffile ne "");
    `rm -f $user22workerconffile`     if ($user22workerconffile ne "");

    `rm -f $pubworkerout`         if ($pubworkerout ne "");
    `rm -f $group1workerout`      if ($group1workerout ne "");
    `rm -f $priv1workerout`       if ($priv1workerout ne "");

    header2("Stopping workers");

    &killchild($pubworkerpid)    if ($pubworkerpid ne "");
    &killchild($group1workerpid) if($group1workerpid ne "");
    &killchild($priv1workerpid)  if($priv1workerpid ne "");

    exit $RC;
}

#
# This sends a command to XWHEP server
# On error this stops the script immediatly
# @param msg to display
# @param errormsg to display on error
# @return the stdout of the command 
#
sub sendCommand {
    my $msg=shift;
    my $errormsg=shift;

    header2 ($msg);

    my $CMD="@_";
    $CMD="$exectime @_";

    debug ("Executing $CMD");

    my $ret;
    chomp($ret=`$CMD`);

    my $RC = $?;

    if ($RC != 0) {
	cleanup ($errormsg, $RC);
    }
    return $ret;
}


#
# This adds an application
# @param config file
# @param name
# @param cpu
# @param os
# @param data UID
#
sub addApp {
    my $cfg = shift;
    my $name = shift;
    my $cpu=shift;
    my $os=shift;
    my $datauid=shift;

    return sendCommand ("Adding/updating application $name",
			"Can't add/update application $name",
			$xwsendapp, "--xwconfig ", $cfg,
			$name, "deployable", $cpu, $os, $datauid);
}

#
# This adds a data
# @param config file
# @param name
# @param cpu
# @param os
# @param file
# @return 0 on success, 1 otherwise
#
sub addData {
    my $cfg = shift;
    my $name = shift;
    my $cpu=shift;
    my $os=shift;
    my $file=shift;
    if ( -f $file ) {
	return sendCommand ("Adding   data $name",
			    "Can't add data $name",
			    $xwsenddata, "--xwconfig ", $cfg,
			    $name, $cpu, $os, $file);
    }
    return "";
}



# ============================================== #
#                  Main                          #
# ============================================== #


#
# parse command line args
#
my $arg;

my $privjobs=1;
my $groupjobs=1;
my $pubjobs=1;

while ($arg = shift) {
    if (($arg eq '-h') || ($arg eq '--help')) {
        usage;
    }
    if ($arg eq '-t') {
        $exectime = "time";
    }
    if ($arg eq '-d') {
        $debug = shift;
    }
    if ($arg eq '-s') {
        $sleepDelay = shift;
    }
    if ($arg eq '-nojob') {
	$privjobs  = 0;
	$groupjobs = 0;
	$pubjobs   = 0;
    }
    if ($arg eq '-all') {
	$privjobs  = 1;
	$groupjobs = 1;
	$pubjobs   = 1;
    }
    if ($arg eq '-priv') {
	$privjobs  = 1;
    }
    if ($arg eq '-privonly') {
	$privjobs  = 1;
	$groupjobs = 0;
	$pubjobs   = 0;
    }
    if ($arg eq '-nopriv') {
	$privjobs  = 0;
    }
    if ($arg eq '-group') {
	$groupjobs  = 1;
    }
    if ($arg eq '-grouponly') {
	$privjobs  = 0;
	$groupjobs = 1;
	$pubjobs   = 0;
    }
    if ($arg eq '-nogroup') {
	$groupjobs  = 0;
    }
    if ($arg eq '-pub') {
	$pubjobs  = 1;
    }
    if ($arg eq '-pubonly') {
	$privjobs  = 0;
	$groupjobs = 0;
	$pubjobs   = 1;
    }
    if ($arg eq '-nopub') {
	$pubjobs  = 0;
    }
}


#
# prepare config file : set logger level to "warn"
#

print<<EOF;

*******************************************************
* This script aims to test the platform
* This :
*   - inserts new temporary users
*   - inserts new temporary usergroups
*   - inserts new temporary public, group and private applications
*   - submits jobs for these new users and applications
*   - launches workers to execute jobs
*
* All temporary items inserted by this script are
* erased at the end (or when you hit CTRL + C)
*******************************************************

Please, hit any key.
-------------------

EOF

<STDIN>;


if( ! -f $clientconffile) {
    fatal ("$clientconffile unreachable");
}

`cp $clientconffile $confsavfile`;
`rm -f $clientconffile`;

my $result;
`cat $confsavfile | grep "^logger.level=" > /dev/null  2>&1`;
if ( $? == 0 ) {
    `cat $confsavfile | sed "s/^logger\.level.*/logger\.level=warn/g" > $clientconffile`;
}
else {
    `cat $confsavfile > $clientconffile`;
    `echo "logger.level=warn" >> $clientconffile`;
}


header1 ("Starting : ".dateAndTime());

#
# add apptest applications and its binaries as datas
#
my $binarynotfound=1;

header1 ("Inserting public datas");

#
# add linux ix86 binary
#
$RANDOM = randomInteger();
my $cpu=$INTEL;
my $os=$LINUX;
my $name="$os-$cpu.$RANDOM";
my $file="$appdir/$appbasename-$os-$cpu";

$linuxix86binuid = addData ($clientconffile, $name, $cpu, $os, $file);
$binarynotfound=0 if($linuxix86binuid ne "");

#
# add win32 ix86 binary
#
$RANDOM = randomInteger();
my $cpu=$INTEL;
my $os=$WIN32;
my $name="$os-$cpu.$RANDOM";
my $file="$appdir/$appbasename-$os-$cpu";

$win32ix86binuid = addData ($clientconffile, $name, $cpu, $os, $file);
if ($binarynotfound == 1) {
    $binarynotfound=0 if($win32ix86binuid ne "");    
}

#
# add macosx ix86 binary
#
$RANDOM = randomInteger();
my $cpu=$INTEL;
my $os=$MACOSX;
my $name="$os-$cpu.$RANDOM";
my $file="$appdir/$appbasename-$os-$cpu";

$macosxix86binuid = addData ($clientconffile, $name, $cpu, $os, $file);
if ($binarynotfound == 1) {
    $binarynotfound=0 if($macosxix86binuid ne "");    
}


#
# add macosx x86_64 binary
#
$RANDOM = randomInteger();
my $cpu=$INTEL64;
my $os=$MACOSX;
my $name="$os-$cpu.$RANDOM";
my $file="$appdir/$appbasename-$os-$cpu";

$macosx8664binuid = addData ($clientconffile, $name, $cpu, $os, $file);
if ($binarynotfound == 1) {
    $binarynotfound=0 if($macosx8664binuid ne "");    
}


#
# add macosx ppc binary
#
$RANDOM = randomInteger();
my $cpu=$PPC;
my $os=$MACOSX;
my $name="$os-$cpu.$RANDOM";
my $file="$appdir/$appbasename-$os-$cpu";

$macosxppcbinuid = addData ($clientconffile, $name, $cpu, $os, $file);
if ($binarynotfound == 1) {
    $binarynotfound=0 if($macosxppcbinuid ne "");    
}
#
# Exit if no binaries found 
#
if ( $binarynotfound == 1 ) {
    fatal "Can't find any binary for apptest";
}



#
# add public application with these binaries
#
header1 ("Inserting public application");
$RANDOM = randomInteger();
$appbasename="$appbasename.$RANDOM";

$appuid=addApp ($clientconffile, $appbasename, $INTEL,   $LINUX,  $linuxix86binuid)  if ($linuxix86binuid  ne "");
$appuid=addApp ($clientconffile, $appbasename, $INTEL,   $WIN32,  $win32ix86binuid)  if ($win32ix86binuid  ne "");
$appuid=addApp ($clientconffile, $appbasename, $INTEL,   $MACOSX, $macosxix86binuid) if ($macosxix86binuid ne "");
$appuid=addApp ($clientconffile, $appbasename, $INTEL64, $MACOSX, $macosx8664binuid) if ($macosx8664binuid ne "");
$appuid=addApp ($clientconffile, $appbasename, $PPC,     $MACOSX, $macosxppcbinuid)  if ($macosxppcbinuid  ne "");
fatal("Can't insert pub  application $appbasename")       if($appuid eq "");


#
# add  group1
#
header1 "Inserting $group1label";
$group1uid = sendCommand ("Adding $group1label",
			  "Can't add $group1label",
			  $xwsendusergroup,
			  $group1label, $admingroup1login, $admingroup1login, $admingroup1login);
fatal("Can't insert usergroup $group1label") if($group1uid eq "");
open (USERCONF, "> $admin1clientconffile");
print USERCONF $group1uid;
close(USERCONF);
print "group1uid = " . $group1uid;
$group1uid=`cat $admin1clientconffile | head -1`;
print "group1uid = " . $group1uid;

#
# add  "STANDARD_USER" user1 in no group
#

header1 "Inserting non privileged $user1login in no group";

my $config = sendCommand ("Adding  $user1login in no group",
			  "Can't add  $user1login in no group",
			  $xwsenduser,
			  $user1login, $user1password, $user1email, $userstdlevel);
open (USERCONF, "> $user1clientconffile");
print USERCONF $config;
close(USERCONF);
my $clientText="client";
`sed "s/client/worker/g" $user1clientconffile | sed "s/CLIENT/worker/g" | sed "s/logger\.level=.*/logger\.level=debug/g" > $user1workerconffile`;
my $launchurl=`grep 'launcher\.url' $workerconffile`;
chomp($launchurl);
open (WORKERCONF, ">> $user1workerconffile");
print USEWORKERCONF "\n$launchurl\n";
close (WORKERCONF);
#`echo ""  >> $user1workerconffile`;
#`echo $launchurl  >> $user1workerconffile`;
#`echo ""  >> $user1workerconffile`;
$user1uid=`cat  $user1clientconffile | grep user.uid | cut -d '=' -f 2`;
fatal("Can't insert user1 in no group") if($user1uid eq "");

#
# add a  privrate application for user1
#

header1 "Inserting a private application for $user1login";
$privapp1basename="app.$user1login";

$privapp1uid=addApp ($user1clientconffile, $privapp1basename, $INTEL,   $LINUX,  $linuxix86binuid)  if ($linuxix86binuid  ne "");
$privapp1uid=addApp ($user1clientconffile, $privapp1basename, $INTEL,   $WIN32,  $win32ix86binuid)  if ($win32ix86binuid  ne "");
$privapp1uid=addApp ($user1clientconffile, $privapp1basename, $INTEL,   $MACOSX, $macosxix86binuid) if ($macosxix86binuid ne "");
$privapp1uid=addApp ($user1clientconffile, $privapp1basename, $INTEL64, $MACOSX, $macosx8664binuid) if ($macosx8664binuid ne "");
$privapp1uid=addApp ($user1clientconffile, $privapp1basename, $PPC,     $MACOSX, $macosxppcbinuid)  if ($macosxppcbinuid  ne "");
fatal("Can't insert priv application $privapp1basename")  if($privapp1uid eq "");

#
# add  "STANDARD_USER" user2 in no group
#

header1 "Inserting non privileged $user2login in no group";

$config = sendCommand ("Adding $user2login in no group",
		       "Can't add $user2login in no group",
		       $xwsenduser,
		       $user2login, $user2password, $user2email, $userstdlevel);
open (USERCONF, "> $user2clientconffile");
print USERCONF $config;
close(USERCONF);
`sed "s/client/worker/g" $user2clientconffile | sed "s/CLIENT/worker/g" | sed "s/logger\.level=.*/logger\.level=debug/g" > $user2workerconffile`;

open (WORKERCONF, ">> $user2workerconffile");
print USEWORKERCONF "\n$launchurl\n";
close (WORKERCONF);
#`echo ""  >> $user2workerconffile`;
#`echo $launchurl  >> $user2workerconffile`;
#`echo ""  >> $user2workerconffile`;

#`grep 'launcher\.url' $workerconffile  >> $user2workerconffile`;

$user2uid=`cat  $user2clientconffile | grep user.uid | cut -d '=' -f 2`;
fatal("Can't insert user2 in no group") if($user2uid eq "");

#
# add  "WORKER_USER" user in group1
#
header1 "Inserting group worker $group1workerlogin in $group1label";
$config = sendCommand ("Adding $group1workerlogin in $group1label",
		       "Can't add  $group1workerlogin in $group1label",
		       $xwsenduser,
		       $group1workerlogin, $group1workerpassword, $group1workeremail, $userworkerlevel, $group1uid);
open (USERCONF, "> $group1workerconffile");
print USERCONF $config;
close(USERCONF);
`mv $group1workerconffile /tmp/toto`;
`sed "s/client/worker/g" /tmp/toto | sed "s/CLIENT/worker/g" | sed "s/logger\.level=.*/logger\.level=debug/g" > $group1workerconffile`;

open (WORKERCONF, ">> $group1workerconffile");
print WORKERCONF "\n$launchurl\n";
close (WORKERCONF);
#`echo ""  >> $group1workerconffile`;
#`echo $launchurl  >> $group1workerconffile`;
#`echo ""  >> $group1workerconffile`;
#`grep 'launcher\.url' $workerconffile  >> $group1workerconffile`;

$group1workeruid=`cat  $group1workerconffile | grep user\.uid | cut -d '=' -f 2`;
fatal("Can't insert group worker1 in group1") if($group1workeruid eq "");

#
# add  "STANDARD_USER" user11 in group1
#

header1 "Inserting non privileged $user11login in $group1label";

my $user11conf = sendCommand ("Adding  $user11login in $group1label",
			    "Can't add  user11login in $group1label",
			    $xwsenduser,
			    $user11login, $user11password, $user11email, $userstdlevel, $group1uid);
open (USERCONF, "> $user11clientconffile");
print USERCONF $user11conf;
close(USERCONF);
`sed "s/client/worker/g" $user11clientconffile | sed "s/CLIENT/worker/g" | sed "s/logger\.level=.*/logger\.level=debug/g" > $user11workerconffile`;

open (WORKERCONF, ">> $user11workerconffile");
print WORKERCONF "\n$launchurl\n";
close (WORKERCONF);
#`echo ""  >> $user11workerconffile`;
#`echo $launchurl  >> $user11workerconffile`;
#`echo ""  >> $user11workerconffile`;
#`grep 'launcher\.url' $workerconffile  >> $user11workerconffile`;

$user11uid=`cat  $user11clientconffile | grep user.uid | cut -d '=' -f 2`;
fatal("Can't insert user user11 in group1") if($user11uid eq "");

#
# add a group application for group1
#
header1 "Inserting a group application $user11login ($group1label)";
$RANDOM = randomInteger();
$group1appbasename="group1app.$RANDOM";

$group1appuid=addApp ($user11clientconffile, $group1appbasename, $INTEL,   $LINUX,  $linuxix86binuid)  if ($linuxix86binuid  ne "");
$group1appuid=addApp ($user11clientconffile, $group1appbasename, $INTEL,   $WIN32,  $win32ix86binuid)  if ($win32ix86binuid  ne "");
$group1appuid=addApp ($user11clientconffile, $group1appbasename, $INTEL,   $MACOSX, $macosxix86binuid) if ($macosxix86binuid ne "");
$group1appuid=addApp ($user11clientconffile, $group1appbasename, $INTEL64, $MACOSX, $macosx8664binuid) if ($macosx8664binuid ne "");
$group1appuid=addApp ($user11clientconffile, $group1appbasename, $PPC,     $MACOSX, $macosxppcbinuid)  if ($macosxppcbinuid  ne "");

sendCommand ("xwchmod 0x750 $group1appbasename",
	     "Can't xwchmod 0x750 $group1appbasename ($group1appuid)",
	     $xwchmod, "0x750", $group1appuid);

#
# add  "STANDARD_USER" user12 in group1
#

header1 "Inserting non privileged $user12login in $group1label";

my $user12conf = sendCommand ("Adding $user12login in $group1label",
			    "Can't add  $user12login in $group1label",
			    $xwsenduser,
			    $user12login, $user12password, $user12email, $userstdlevel, $group1uid);
open (USERCONF, "> $user12clientconffile");
print USERCONF $user12conf;
close(USERCONF);
`sed "s/client/worker/g" $user12clientconffile | sed "s/CLIENT/worker/g" | sed "s/logger\.level=.*/logger\.level=debug/g" > $user12workerconffile`;

open (WORKERCONF, ">> $user12workerconffile");
print WORKERCONF "\n$launchurl\n";
close (WORKERCONF);
#`echo ""  >> $user12workerconffile`;
#`echo $launchurl  >> $user12workerconffile`;
#`echo ""  >> $user12workerconffile`;
#`grep 'launcher\.url' $workerconffile  >> $user12workerconffile`;

$user12uid=`cat  $user12clientconffile | grep user.uid | cut -d '=' -f 2`;
fatal("Can't insert user12 in group1") if($user12uid eq "");

#
# Submit jobs for user1
#
my $label;
header1 "Submit jobs as user1 ($user1login) from no group";
$label="pub$user1login";
$pubjobuser1uid=sendCommand ("Submit public job \"$label\" for $appbasename",
			     "Can't submit public job \"$label\" for $appbasename",
			     $xwsubmit, "--xwconfig ", $user1clientconffile, $appbasename,
			     "--xwlabel", $label);
$label="priv$user1login";
$privjobuser1uid=sendCommand ("Submit priv job \"$label\" for $privapp1basename",
			      "Can't submit priv job \"$label\" for $privapp1basename",
			      $xwsubmit, "--xwconfig ", $user1clientconffile, $privapp1basename,
			      "--xwlabel", $label);
#
# Submit jobs for user2
#
header1 "Submit jobs as user2 ($user2login) from no group";
$label="pub$user2login";
$pubjobuser2uid=sendCommand ("Submit public job \"$label\" for $appbasename",
			     "Can't submit public job \"$label\" for $appbasename",
			     $xwsubmit, "--xwconfig ", $user2clientconffile, $appbasename,
			     "--xwlabel", $label);

#
# Submit jobs for user11
#
header1 "Submit jobs as user11 ($user11login) from group1 ($group1label)";
my $label="pub$user11login";
$pubjobuser11uid=sendCommand ("Submit public job \"$label\" for $appbasename",
			      "Can't submit public job \"$label\" for $appbasename",
			      $xwsubmit, "--xwconfig ", $user11clientconffile, $appbasename,
			      "--xwlabel", $label);
$label="group$user11login";
$groupjobuser11uid=sendCommand ("Submit group job \"$label\" for $group1appbasename",
				"Can't submit group job \"$label\" for $group1appbasename",
				$xwsubmit, "--xwconfig ", $user11clientconffile, $group1appbasename,
				"--xwlabel", $label);

#
# Submit jobs for user12
#
header1 "Submit jobs as user12 $user12login from group1 ($group1label)";
$label="pub$user12login";
$pubjobuser12uid=sendCommand ("Submit public job \"$label\" for $appbasename",
			      "Can't submit public job \"$label\" for $appbasename",
			      $xwsubmit, "--xwconfig ", $user12clientconffile, $appbasename,
			      "--xwlabel", $label);
$label="group$user12login";
$groupjobuser12uid=sendCommand ("Submit group job \"$label\" for $group1appbasename",
				"Can't submit group job \"$label\" for $group1appbasename",
				$xwsubmit, "--xwconfig ", $user12clientconffile, $group1appbasename,
				"--xwlabel", $label);

  
print<<EOF;

*******************************************************
* We have successfully inserted usergroups, users,
* as well as public, group and private applications.
* We also have submitted jobs for all these applications.
*
* We will now monitor jobs and wait for their completion.
*
* Let first start public worker. A public worker can
* compute any public job. Hence we will see public jobs
* beeing completed, but group and private jobs will not
* be computed by any public worker.
*******************************************************

EOF


header1 "Starting public worker";
$pubworkerpid = fork;
fatal( "Can't exec public worker") if ($pubworkerpid eq undef);
if ($pubworkerpid == 0) {
    `$JAVAWORKER --xwconfig $workerconffile > $pubworkerout  2>&1`;
    &warn("Public worker has finished");
    exit 0;
}

my $totaljobs = 4;
my $completions = $totaljobs;
while($completions > 0) {
    header1 "Monitoring public jobs as admin";

    my $cmdResult = sendCommand ("Retreiving public jobs",
				 "Can't retreive public jobs",
				 $xwworks, "--xwformat xml",
				 $pubjobuser1uid,
				 $pubjobuser2uid,
				 $pubjobuser11uid,
				 $pubjobuser12uid);

    &debug($cmdResult);

    if ($cmdResult ne "" ) {
	
	my $works = XMLin($cmdResult);
	
	my @worksArray = ();
	if(isa($works->{work}, "ARRAY")) {
	    @worksArray = @{$works->{work}};
	}
	else {
	    @worksArray = $works->{work};
	};

	foreach my $work (@worksArray) {
	    &info($work->{uid}." (".$work->{label}.") : ".$work->{status});
	    $completions-- if($work->{status} eq "COMPLETED");
	    $completions-- if($work->{status} eq "ERROR");
	}
    }

    if($completions > 0) {
	`date`;
	header1 "[".dateAndTime()."] sleeping ".$sleepDelay."s";
	sleep $sleepDelay;
    }
}

&killchild($pubworkerpid) if ($pubworkerpid ne "");
$pubworkerpid="";

print<<EOF;

*******************************************************
* Wow! We have successfully completed $totaljobs public jobs.
*
* We will now monitor groups jobs for $group1label and wait for their completion.
*
* Let start a group worker for group "$group1label" and see
* group jobs beeing completed. Of course private jobs and jobs
* not in the group will not be computed by any group worker.
*******************************************************

EOF

header1 "Starting group worker for group $group1label";
$group1workerpid = fork;
fatal( "Can't start group worker 1") if ($group1workerpid eq undef);
if ($group1workerpid == 0) {
    `$JAVAWORKER --xwconfig $group1workerconffile > $group1workerout  2>&1`;
    &warn("Group worker 1 has finished");
    exit 0;
}

#
# Waiting for jobs completion
#
$totaljobs = 2;
$completions = $totaljobs;
while($completions > 0) {
    header1 "Monitoring group jobs as admin";

    my $cmdResult = sendCommand ("Retreiving group jobs",
				 "Can't retreive group jobs",
				 $xwworks, "--xwformat xml",
				 $groupjobuser11uid,
				 $groupjobuser12uid);

    &debug($cmdResult);

    if ($cmdResult ne "" ) {
	
	my $works = XMLin($cmdResult);
	
	my @worksArray = ();
	if(isa($works->{work}, "ARRAY")) {
	    @worksArray = @{$works->{work}};
	}
	else {
	    @worksArray = $works->{work};
	};

#
# Loop on retreived works 
#    
	foreach my $work (@worksArray) {
	    &info($work->{uid}." (".$work->{label}.") : ".$work->{status});
	    $completions-- if($work->{status} eq "COMPLETED");
	    $completions-- if($work->{status} eq "ERROR");
	}
    }

    if($completions > 0) {
	`date`;
	header1 "[".dateAndTime()."] sleeping ".$sleepDelay."s";
	sleep $sleepDelay;
    }
}

&killchild($group1workerpid) if ($group1workerpid ne "");
$group1workerpid="";


print<<EOF;

*******************************************************
* Wow! We have successfully completed $totaljobs group jobs.
*
* We will now monitor private jobs for $user1login and wait for their completion.
*
* Finally let start a private worker for user $user1login
* so that its private jobs referring its private application can be computed
*******************************************************

EOF

header1 "Starting private worker for user $user1login";
$priv1workerpid = fork;
fatal( "Can't exec priv worker 1") if ($priv1workerpid eq undef);
if ($priv1workerpid == 0) {
    `$JAVAWORKER --xwconfig $user1workerconffile > $priv1workerout  2>&1`;
    info("Private worker 1 has finished");
    exit 0;
}

#
# Waiting for jobs completion
#
$totaljobs = 1;
$completions = $totaljobs;
while($completions > 0) {
    header1 "Monitoring private jobs as admin";

    my $cmdResult = sendCommand ("Retreiving private jobs",
				 "Can't retreive provate jobs",
				 $xwworks, "--xwformat xml",
				 $privjobuser1uid);

    &debug($cmdResult);

    if ($cmdResult ne "" ) {
	
	my $works = XMLin($cmdResult);
	
	my @worksArray = ();
	if(isa($works->{work}, "ARRAY")) {
	    @worksArray = @{$works->{work}};
	}
	else {
	    @worksArray = $works->{work};
	};

#
# Loop on retreived works 
#    
	foreach my $work (@worksArray) {
	    &info($work->{uid}." (".$work->{label}.") : ".$work->{status});
	    $completions-- if($work->{status} eq "COMPLETED");
	    $completions-- if($work->{status} eq "ERROR");
	}
    }

    if($completions > 0) {
	`date`;
	header1 "[".dateAndTime()."] sleeping ".$sleepDelay."s";
	sleep $sleepDelay;
    }
}

&killchild($priv1workerpid) if ($priv1workerpid ne "");
$priv1workerpid="";

print<<EOF;

*******************************************************
*
* Wow! We have successfully completed $totaljobs private jobs.
*
*******************************************************

*******************************************************
*
* That is all folks
*
*******************************************************

EOF

#
# Clean up
#
cleanup ("Ending : " . dateAndTime(), 0);


#
# End of file
#
