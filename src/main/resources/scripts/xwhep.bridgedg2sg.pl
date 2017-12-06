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
# File    : xwhep.bridgedg2sg.pl
# Author  : Oleg Lodygens (lodygens at lal.in2p3.fr)
# Date    : Septembre 16th, 2008
# Purpose : this bridges the XWHEP DesktopGrid platform with SG
#           so that XWHEP can use SG ressources using "Pilot Jobs".
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
my $scriptDir = `dirname $0`;
chomp ($scriptDir);
&debug("scriptDir = ".$scriptDir);
my $PPC="ppc";
my $INTEL="ix86";
my $LINUX="linux";
my $WIN32="win32";
my $MACOSX="macosx";


my $currentDir=`pwd`;
chomp ($currentDir);
&debug("currentDir = ".$currentDir);
#`cd $scriptDir`;
my $ROOTDIR=`cd $scriptDir && pwd`;
chomp ($ROOTDIR);
&debug("ROOTDIR = ".$ROOTDIR);
$ROOTDIR=$ROOTDIR."/..";
`cd $currentDir`;
#my $ROOTDIR = `dirname $0`;
chomp ($scriptName);
chomp ($ROOTDIR);
##$ROOTDIR = `pwd` if ($ROOTDIR eq ".");
#chomp ($ROOTDIR);
#$ROOTDIR="$ROOTDIR/..";
&debug("ROOTDIR = ".$ROOTDIR);

my $bindir="$ROOTDIR/bin";
my $confdir="$ROOTDIR/conf";
my $libdir="$ROOTDIR/lib";
my $keydir="$ROOTDIR/keystore";
my $workerkeys="$keydir/xwhepworker.keys";

my $RANDOM = randomInteger();

my $WORKERCLASS="xtremweb.worker.Worker";
my $JARFILENAME="xtremweb.jar";
my $JARFILE="$libdir/$JARFILENAME";
my $JAVA="java -Djavax.net.ssl.trustStore=$workerkeys -cp $JARFILE $WORKERCLASS ";

#
# XWHEP scripts
#
my $xwapps="$bindir/xwapps";
my $xwalive="$bindir/xwalive";
my $xwdatas="$bindir/xwdatas";
my $xwdownload="$bindir/xwdownload";
my $xwusers="$bindir/xwusers";
my $xwusergroups="$bindir/xwusergroups";
my $xwrm="$bindir/xwrm";
my $xwclean="$bindir/xwclean";
my $xwchmod="$bindir/xwchmod";
my $xwsendwork="$bindir/xwsendwork";
my $xwsendapp="$bindir/xwsendapp";
my $xwsenddata="$bindir/xwsenddata";
my $xwsenduser="$bindir/xwsenduser";
my $xwsendusergroup="$bindir/xwsendusergroup";
my $xwsubmit="$bindir/xwsubmit";
my $xwworks="$bindir/xwworks";


#
# Debug scripts
#
my $dbgsubmit="echo https://localhost/`uuidgen` && echo ";
my $dbgstatus="echo 'Current Status : Done' && echo";
my $dbgcancel="echo Cancel";
my $dbgoutput="echo Output";

#
# EGEE scripts
#
my $egeesubmit="glite-wms-job-submit";
my $egeestatus="glite-wms-job-status";
my $egeecancel="glite-wms-job-cancel --noint";
my $egeeoutput="glite-wms-job-output";

#
# SG scripts
#
my $sgsubmit=$egeesubmit;
my $sgstatus=$egeestatus;
my $sgcancel=$egeecancel;
my $sgoutput=$egeeoutput;

#
# DB variables
#
my $dbusage = 1;
my $DBUSER='@DBUSER@';
my $dbpassword='@DBPASSWORD@';
my $DBHOST='@DBHOST@';
my $DBNAME='@DBNAME@';
my $dbHandler;

#
# Files
#
my $workerCfg;
my $jdlFileName;
my $shFileName;
my $tmpFileName;
my $xmlFileName;
my $userProxyFile;

#
# This hashtable stores SG job IDs
#
my %pilotJobs = ();
#
# This hashtable stores XWHEP jobs
#
my %xwhepJobs = ();


#
# debug levels
#
my $DEBUG = 0;
my $INFO  = 1;
my $WARN  = 2;
my $ERROR = 3;
my $debug = $INFO;



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
    print "Usage : $scriptName [-h] [-d level] [-db] [-dbu user] [-dbp password] [-dbn dbname] [-dbh dbhost]\n";
    print "\t -h            : gets this help\n";
    print "\t -d  0|1|2|3   : sets debug level\n";
    print "\t -db           : use the DB and not the XWHEP server\n";
    print "\t -dbu user     : sets the DB user to connect to DB server\n";
    print "\t -dbp passowrd : sets the DB password to connect to DB server\n";
    print "\t -dbh host     : sets the DB host to connect to DB server\n";
    print "\t -dbn name     : sets the DB name to connect to DB server\n";
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
    my $text = "$scriptName ".dateAndTime()." @_\n";
    print $text;
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
    my $text = "** @_";
    printTimed $text;
}
#
# this prints 2nd level header
# @param msg to print
#
sub header2 {
    my $text = "**** @_";
    printTimed $text;
}


#
# This removes temp files and datas
# This stops the script immediatly
#
sub deleteFiles {

#    return if ($debug <= $DEBUG);

    `mv -f $workerCfg   $workerCfg-last`     if( -f $workerCfg );
    `mv -f $shFileName  $shFileName-last`    if( -f $shFileName );
    `mv -f $jdlFileName $jdlFileName-last`   if( -f $jdlFileName );
    `mv -f $tmpFileName $tmpFileName-last`   if( -f $tmpFileName );
    `mv -f $xmlFileName $xmlFileName-last`   if( -f $xmlFileName );
    `rm -f $userProxyFile`;
}
#
# This removes temp files and datas
# This stops the script immediatly
# @param : a String to display as message
# @param : this script return code
#
sub cleanup {
    my $msg=shift;
    my $RC=shift;
    
    if($dbusage != 0) {
	if($dbHandler) {
	    $dbHandler->disconnect;
	}
    }
    
    header1 ("Cleaning up : ".dateAndTime()."  ($msg)");
    &deleteFiles();
    `rm -f $JARFILENAME`;
    
#    foreach my $job (keys %pilotJobs) {
#	&info("Clean up removes : ".$job." / ".$pilotJobs{$job}->{xwhepJob}->{uid});
#	$userProxyFile=$pilotJobs{$job}->{userproxy};
#	my $CMD = "bash -c \"(export X509_USER_PROXY='$userProxyFile' && $sgcancel $job)\"";
#	&debug("Executing ".$CMD);
#	`$CMD`;
#	`rm -f $userProxyFile`;
#    }
    
    exit $RC;
}

#
# This sends a command to XWHEP server
# On error this stops the script immediatly
# @param main message always displayed
# @param errormsg displayed on error
# @return the stdout of the command 
#
sub sendCommand {
    my $msg=shift;
    my $errormsg=shift;
    
    header2 ($msg);
    
    my $CMD="@_";
    $CMD="@_";
    
    debug ("Executing $CMD");
    
    my $ret;
    chomp($ret=`$CMD`);
    
    my $RC = $?;
    
    &debug ("RC = ".$RC);
    
    if ($RC != 0) {
	error ($errormsg." (RC = $RC)");
	$ret = "";
    }
    return $ret;
}

#
# This writes an XWHEP work description to XML file
# @param the XML file name
# @param the work to write to file
#
sub workToXMLFile {
    my $fileName = shift;
    my $xwhepWork = shift;
    my $XML;
    `rm -f $fileName`;
    &debug("XML = ".$fileName);
    open (XML, "> ".$fileName);
    print XML "<work ";
    print XML " uid='".$xwhepWork->{uid}."'"                         if($xwhepWork->{uid} ne "" );
    print XML " userproxy='".$xwhepWork->{userproxy}."'"             if($xwhepWork->{userproxy} ne "" );
    print XML " sessionuid='".$xwhepWork->{sessionuid}."'"           if($xwhepWork->{sessionuid} ne "" );
    print XML " groupuid='".$xwhepWork->{groupuid}."'"               if($xwhepWork->{groupuid} ne "" );
    print XML " expectedhostuid='".$xwhepWork->{expectedhostuid}."'" if($xwhepWork->{expectedhostuid} ne "" );
    print XML " accessrights='".$xwhepWork->{accessrights}."'"       if($xwhepWork->{accessrights} ne "" );
    print XML " isservice='".$xwhepWork->{isservice}."'"             if($xwhepWork->{isservice} ne "" );
    print XML " isdeleted='".$xwhepWork->{isdeleted}."'"             if($xwhepWork->{isdeleted} ne "" );
    print XML " label='".$xwhepWork->{label}."'"                     if($xwhepWork->{label} ne "" );
    print XML " appuid='".$xwhepWork->{appuid}."'"                   if($xwhepWork->{appuid} ne "" );
    print XML " owneruid='".$xwhepWork->{owneruid}."'"               if($xwhepWork->{owneruid} ne "" );
    print XML " status='".$xwhepWork->{status}."'"                   if($xwhepWork->{status} ne "" );
    print XML " returncode='".$xwhepWork->{returncode}."'"           if($xwhepWork->{returncode} ne "" );
    print XML " server='".$xwhepWork->{server}."'"                   if($xwhepWork->{server} ne "" );
    print XML " cmdline='".$xwhepWork->{cmdline}."'"                 if($xwhepWork->{cmdline} ne "" );
    print XML " stdinuri='".$xwhepWork->{stdinuri}."'"               if($xwhepWork->{stdinuri} ne "" );
    print XML " dirinuri='".$xwhepWork->{dirinuri}."'"               if($xwhepWork->{dirinuri} ne "" );
    print XML " resulturi='".$xwhepWork->{resulturi}."'"             if($xwhepWork->{resulturi} ne "" );
    print XML " arrivaldate='".$xwhepWork->{arrivaldate}."'"         if($xwhepWork->{arrivaldate} ne "" );
    print XML " completeddate='".$xwhepWork->{completeddate}."'"     if($xwhepWork->{completeddate} ne "" );
    print XML " error_msg='".substr($xwhepWork->{error_msg}, 0, 255)."'" if($xwhepWork->{error_msg} ne "" );
    print XML " sendtoclient='".$xwhepWork->{sendtoclient}."'"       if($xwhepWork->{sendtoclient} ne "" );
    print XML " local='".$xwhepWork->{local}."'"                     if($xwhepWork->{local} ne "" );
    print XML " active='".$xwhepWork->{active}."'"                   if($xwhepWork->{active} ne "" );
    print XML " replicated='".$xwhepWork->{replicated}."'"           if($xwhepWork->{replicated} ne "" );
    print XML " maxretry='".$xwhepWork->{maxretry}."'"               if($xwhepWork->{maxretry} ne "" );
    print XML " minmemory='".$xwhepWork->{minmemory}."'"             if($xwhepWork->{minmemory} ne "" );
    print XML " mincpuspeed='".$xwhepWork->{mincpuspeed}."'"         if($xwhepWork->{mincpuspeed} ne "" );
    print XML " />";
    close(XML);
}

#
# This removes CR LF, ', " from param
# @param the string to clean
# @return the cleaned string
#
sub cleanString {
    my $str = shift;
    $str =~ s/\r\n|\n/./g;
    $str =~ s/\'//g;
    $str =~ s/\"//g;
    return $str;
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
    if ($arg eq '-d') {
	$debug = shift;
    }
    if ($arg eq '-dbg') {
	$sgsubmit=$dbgsubmit;
	$sgstatus=$dbgstatus;
	$sgcancel=$dbgcancel;
	$sgoutput=$dbgoutput;
    }
    if ($arg eq '-db') {
	$dbusage = 1;
    }
    if ($arg eq '-dbu') {
	$DBUSER = shift;
    }
    if ($arg eq '-dbp') {
	$dbpassword = shift;
    }
    if ($arg eq '-dbn') {
	$DBNAME = shift;
    }
    if ($arg eq '-dbh') {
	$DBHOST = shift;
    }
}

&info("sgSubmit = ".$sgsubmit);
&info("sgStatus = ".$sgstatus);
&info("sgCancel = ".$sgcancel);
&info("sgOutput = ".$sgoutput);
#
# retreiving config files
#
my $CFG;
opendir (CFG, $confdir) or die "Can't retreive config files";
my @fichiers = readdir (CFG);
closedir (CFG);

#
# this hashtable stores config files for each server found
#
my %servers = ();
my $serverfound = 0;

#
# loop on all config files
#
foreach my $entree (@fichiers){

    unless ($entree =~ /xtremweb.client.conf/ ) {
	&info ("not a client conf file : ".$entree);
	next;
    }
    &debug ("client conf file : ".$entree);
    my $delimiteur = "=";
    my $server =`grep -E '^[[:space:]]*dispatcher.servers[[:space:]]*=' $confdir/$entree`;
    if($? != 0) {
	$server =`grep -E '^[[:space:]]*dispatcher.servers[[:space:]]*:' $confdir/$entree`;
	$delimiteur = ":";
    }
    if($server eq "") {
	&warn($entree." is not used : it has no dispatcher.servers");
	next;
    }
    my @splitarray = split($delimiteur, $server);
    $server = $splitarray[1];
    chomp($server);
    
    $delimiteur = "=";
    my $keystore =`grep -E '^[[:space:]]*SSLKeyStore[[:space:]]*=' $confdir/$entree`;
    if($? != 0) {
	$keystore =`grep -E '^[[:space:]]*SSLKeyStore[[:space:]]*:' $confdir/$entree`;
	$delimiteur = ":";
    }
    if($keystore eq "") {
	&warn($entree." is not used : it has no keyStore");
	next;
    }
    @splitarray = split($delimiteur, $keystore);
    $keystore = $splitarray[1];
    chomp($keystore);
    if( ! -f $keystore) {
	&warn($entree." is not used : can't find keyStore file ".$keystore);
	next;
    }
    
    $delimiteur = "=";
    my $login =`grep -E '^[[:space:]]*login[[:space:]]*=' $confdir/$entree`;
    if($? != 0) {
	$login =`grep -E '^[[:space:]]*login[[:space:]]*:' $confdir/$entree`;
	$delimiteur = ":";
    }
    if($login eq "") {
	&warn($entree." is not used : it has no login");
	next;
    }
    @splitarray = split($delimiteur, $login);
    $login = $splitarray[1];
    chomp($login);
    
    $delimiteur = "=";
    my $password =`grep -E '^[[:space:]]*password[[:space:]]*=' $confdir/$entree`;
    if($? != 0) {
	$password =`grep -E '^[[:space:]]*password[[:space:]]*:' $confdir/$entree`;
	$delimiteur = ":";
    }
    if($password eq "") {
	&warn($entree." is not used : it has no password");
	next;
    }
    @splitarray = split($delimiteur, $password);
    $password = $splitarray[1];
    chomp($password);
    
    &debug($entree." : S = ".$server.", K = ".$keystore.", L = ".$login.", P = ".$password);
    
    my $enreg = {};
    $enreg->{clientconf} = $confdir."/".$entree;
    $enreg->{server} = $server;
    $enreg->{keystore} = $keystore;
    $enreg->{login} = $login;
    $enreg->{password} = $password;
    
    $servers{$server}->{configs} = $enreg;
    
    $serverfound++;
}

if($serverfound < 1) {
    &fatal("No config file found");
}

&info("Found ".$serverfound." config files");

&debug("cp $JARFILE ./$JARFILENAME");
`cp $JARFILE ./$JARFILENAME`;
if($? != 0) {
    &fatal("Can't find $JARFILE");
}

#
# main loop
#
while(1) {

#
# Connect to MySQL
#
    if($dbusage != 0) {
	&debug("Connecting to DB : $DBHOST:$DBNAME");
	$dbHandler = DBI->connect("DBI:mysql:$DBNAME:$DBHOST", $DBUSER, $dbpassword)
	    or die "Couldn't connect to database: " . DBI->errstr;
    }

#
# loop on servers
#
    foreach my $server (keys %servers) {

	my $clientConfFile = $servers{$server}->{configs}->{clientconf};

#
# Testing connection
#
	header1 "Testing ".$servers{$server}->{configs}->{server};
	`wget http://$servers{$server}->{configs}->{server}:4321 --timeout=5 --tries=1 -o /dev/null -O /dev/null`;
	if($? != 0) {
	    &error("Connection error : bypassing ".$servers{$server}->{configs}->{server});
	    next;
	}
	header1 "Signaling to ".$servers{$server}->{configs}->{server};

	`grep -E '^[[:space:]]*uid[[:space:]]*=' $clientConfFile`;
	if($? != 0) {
	    my $hostuid=`uuidgen`;
	    if($? != 0) {
		&warn("Can't generate host uid : this will lead to duplicate this host entry in server DB :(");
	    }
	    else {
		chomp($hostuid);
		&info("Generating new host UID ($hostuid) in $clientConfFile");
		`echo "uid=$hostuid" >> $clientConfFile`;
	    }
	}

#
# send alive signal for monitoring purposes
#
	sendCommand ("Sending alive to ".$servers{$server}->{configs}->{server},
		     "Can't send alive signal",
		     $xwalive, "--xwconfig", $clientConfFile);
#
#  cleaning XWHEP cache
#
#	sendCommand ("Cleaning cache",
#		     "Can't clean cache",
#		     $xwclean, "--xwconfig", $clientConfFile);
	
#
# retreive works
#
	my $cmdWorks = "";
	if($dbusage == 0) {
	    header1 "Retreiving Jobs from ".$servers{$server}->{configs}->{server};
	    $cmdWorks = sendCommand ("Retreiving jobs",
				     "Can't retreive jobs",
				     $xwworks, "--xwconfig", $clientConfFile,
				     "--xwformat xml");
	}
	else {
	    &debug("Retreiving works from DB");
	    my $reqWorks = $dbHandler->prepare("select uid,userproxy,status,owneruid from works where not isnull(userproxy) and status='PENDING' and isdeleted='false'")
		or die "Couldn't prepare statement: " . $dbHandler->errstr;

	    $reqWorks->execute()
		or die "Couldn't execute statement: " . $reqWorks->errstr;

	    $cmdWorks = "<?xml version=\"1.0\"?>\n<get>\n";

	    my $atleastone = 0;

	    while (my $work = $reqWorks->fetchrow_hashref) {

		$atleastone = 1;

		$cmdWorks = $cmdWorks."<work uid='".$work->{uid}."'";
		if ($work->{userproxy} ne "" ) {
		    $cmdWorks = $cmdWorks." userproxy='".$work->{userproxy}."'";
		}
		if ($work->{owneruid} ne "" ) {
		    $cmdWorks = $cmdWorks." owneruid='".$work->{owneruid}."'";
		}
		else {
		    &error($work->{uid}." has no OWNERUID ???");
		    next;
		}
		if ($work->{status} ne "" ) {
		    $cmdWorks = $cmdWorks." status='".$work->{status}."'";
		}
		else {
		    &error($work->{uid}." has no STATUS ???");
		    next;
		}
		$cmdWorks = $cmdWorks." />\n";
	    }

	    $cmdWorks = $cmdWorks."</get>\n";

	    $cmdWorks = "" if ($atleastone < 1);
	}
	
	&debug($cmdWorks);
	
	if ($cmdWorks eq "" ) {
	    next;
	}
	
	my $works = XMLin($cmdWorks);

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

	    if ($xwhepJobs{$work->{uid}}) {
		&debug("Work ".$work->{uid}." has already been submitted to SG : ".$xwhepJobs{$work->{uid}}->{sgJobId});
		next;
	    }
	    
	    if ($work->{status} ne "PENDING" ) {
		next;
	    }
	    
	    if ($work->{userproxy} eq "" ) {
#		&debug("Work ".$work->{uid}." has no X509 user proxy");
		next;
	    }
	    
	    &debug("Work ".$work->{uid}.", ".$work->{status});
	    
#
# retreive work owner
#
	    my $cmdUser = "";
	    
	    if($dbusage == 0) {
		$cmdUser = sendCommand("Retreiving user informations",
				       "Can't retreive user informations",
				       $xwusers, "--xwconfig", $clientConfFile,
				       $work->{owneruid},
				       "--xwformat xml");

	    }
	    else {
		&debug("Retreiving users from DB : select uid,login,password from users where uid='".$work->{owneruid}."' and isdeleted='false'");
		my $reqUser = $dbHandler->prepare("select uid,login,password from users where uid='".$work->{owneruid}."' and isdeleted='false'")
		    or die "Couldn't prepare statement: " . $dbHandler->errstr;
		
		$reqUser->execute()
		    or die "Couldn't execute statement: " . $reqUser->errstr;
		
		$cmdUser = "<?xml version=\"1.0\"?>\n<get>\n";
		
		my $atleastone = 0;

		while (my $user = $reqUser->fetchrow_hashref) {

		    $atleastone = 1;

		    $cmdUser = $cmdUser."<user uid='".$user->{uid}."'";

		    if ($user->{login} ne "" ) {
			$cmdUser = $cmdUser." login='".$user->{login}."'";
		    }
		    if ($user->{password} ne "" ) {
			$cmdUser = $cmdUser." password='".$user->{password}."'";
		    }
		    $cmdUser = $cmdUser." />\n";
		}

		$cmdUser = $cmdUser."</get>\n";

		$cmdUser = "" if ($atleastone < 1);
	    }

	    &debug($cmdUser);

	    if ($cmdUser eq "" ) {
		&error("cmdUser == \"\" ?!?!");
		next;
	    }
	    
	    my $users = XMLin($cmdUser);
	    if($users == undef ) {
		&error("users is undef ?!?!");
		next;
	    }
	    
	    my $user = $users->{user};
	    if($user == undef ) {
		&error("user is undef ?!?!");
		next;
	    }
	    if($user->{uid} eq "") {
		&error("user.uid is null ?!?!");
		next;
	    }
	    if($user->{login} eq "") {
		&error("user.login is null ?!?!");
		next;
	    }
	    if($user->{password} eq "") {
		&error("user.password is null ?!?!");
		next;
	    }
	    
	    &debug("Work owner login = ".$user->{login});
	    
#
# retreive X509 proxy
#
	    my $cmdData = sendCommand ("Downloading X509 proxy",
				       "Can't download X509 proxy",
				       $xwdownload, , "--xwconfig", $clientConfFile,
				       $work->{userproxy},
				       "--xwformat xml");
	    if($dbusage == 1) {

		&debug("Retreiving proxy from DB");
		my $lastindex = rindex($work->{userproxy}, "/");
		my $proxyuidstr = substr($work->{userproxy}, $lastindex+1);
		debug("lastindex = ".$lastindex);
		debug("substr = ".$proxyuidstr);
		my $reqDataString = "select uid,owneruid,name from datas where uid='".$proxyuidstr."' and isdeleted='false'";
		my $reqDatas = $dbHandler->prepare($reqDataString)
		    or die "Couldn't prepare statement: " . $dbHandler->errstr;

		$reqDatas->execute()
		    or die "Couldn't execute statement: " . $reqDatas->errstr;

		$cmdData = "<?xml version=\"1.0\"?>\n<get>\n";

		my $atleastone = 0;

		while (my $data = $reqDatas->fetchrow_hashref) {

		    $atleastone = 1;

		    $cmdData = $cmdData."<data uid='".$data->{uid}."'";
		    if ($data->{owneruid} ne "" ) {
			$cmdData = $cmdData." owneruid='".$data->{owneruid}."'";
		    }
		    else {
			&error($data->{uid}." has no OWNERUID ???");
			next;
		    }
		    if ($data->{name} ne "" ) {
			$cmdData = $cmdData." name='".$data->{name}."'";
		    }
		    $cmdData = $cmdData." />\n";
		}

		$cmdData = $cmdData."</get>\n";

		$cmdData = "" if ($atleastone < 1);
	    }

	    if ($cmdData eq "" ) {
		&error("cmdData == \"\" ?!?!");
		next;
	    }
	    
	    my $datas = XMLin($cmdData);
	    if($datas == undef ) {
		&error("datas is undef ?!?!");
		next;
	    }

	    my $dataErrorMsg = "";
	    my $data = $datas->{data};
	    if($data == undef ) {
		$dataErrorMsg = "Can't get X509 proxy : download error (access rights ?)";
	    }
	    if($data->{uid} eq "") {
		$dataErrorMsg = "Can't get X509 proxy : data.uid is undef ?!?!";
	    }
	    if($data->{owneruid} ne $work->{owneruid}) {
		$dataErrorMsg = "Can't get X509 proxy : work.owner != proxy.owner (".$data->{owneruid}." != ".$work->{owneruid}.")";
	    }
	    
	    if($dataErrorMsg ne "") {
		$work->{status} = "ERROR";
		$work->{error_msg} = $dataErrorMsg;
		$xmlFileName="$work->{uid}.xml";
		&workToXMLFile($xmlFileName, $work);
		sendCommand ("Cancelling job",
			     "Can't cancel jobs",
			     $xwsendwork, "--xwconfig", $clientConfFile,
			     "--xwxml $xmlFileName",
			     "--xwformat xml");

#		`rm -f $xmlFileName`;
		&deleteFiles();
		next;
	    }

	    $_ = $data->{name};
	    my ($filename, $fileext) =  /^(.*)\.(.*)/;
	    $filename = $data->{name} if($fileext  eq "");
	    $filename = "_".$filename if($filename ne "");
	    
	    my $proxyFileName = `ls $data->{uid}*`;
	    chomp($proxyFileName);
	    $userProxyFile = $proxyFileName;
	    &debug("proxyFileName = ".$proxyFileName);
	    `chmod 600 $proxyFileName`;
	    if($? != 0) {
		&error("X509 proxy download error ".$data->{uri});
		&deleteFiles();
		next;
	    }
	    
	    &debug("X509 proxy = ".$data->{uri});

#
# submit pilot job
#

	    &debug("SSLKeyStore=".$servers{$server}->{configs}->{keystore});
	    my $keystoreBaseName = basename($servers{$server}->{configs}->{keystore});
	    `cp $servers{$server}->{configs}->{keystore} $keystoreBaseName`;
#	    my $datehour = `date "+%Y%m%d%H%M%S"`;
	    my $datehour = "";
#	    chomp($datehour);
	    $workerCfg = $servers{$server}->{configs}->{server}."_worker.conf".$datehour;
	    &debug("workerCfg = ".$workerCfg);
	    my $CFGOUT;
	    open (CFGOUT,  "> ".$workerCfg);
	    print CFGOUT "xtremweb.role=worker\n";
	    print CFGOUT "dispatcher.servers=".$servers{$server}->{configs}->{server}."\n";
	    print CFGOUT "SSLKeyStore=".$keystoreBaseName."\n";
	    print CFGOUT "login=".$user->{login}."\n";
	    print CFGOUT "password=".$user->{password}."\n";
#	    print CFGOUT "cert.uri=".$data->{uri}."\n";
	    print CFGOUT "server.http=false\n";
	    print CFGOUT "pilotjob=true\n";
	    print CFGOUT "workpool.size=1\n";
	    print CFGOUT "noopTimeout=600\n";
	    print CFGOUT "computing.jobs=1\n";
	    print CFGOUT "logger.level=debug\n";
	    print CFGOUT "incomingconnections=true\n";	    
	    print CFGOUT "sharedapps=VirtualBox\n";	    
	    close(CFGOUT);

	    my $SH;
	    $shFileName="xwhep$datehour.sh";
	    &debug("SH = ".$shFileName);
	    open (SH, "> ".$shFileName);
	    print SH "#!/bin/sh\n";
	    print SH "unset X509_USER_PROXY && java -Dxtremweb.cache=/tmp -Djava.library.path=/tmp -Djavax.net.ssl.trustStore=$keystoreBaseName -cp $JARFILENAME xtremweb.worker.Worker --xwconfig $workerCfg\n";
	    close(SH);

	    my $JDL;
	    $jdlFileName="xwhep$datehour.jdl";
	    &debug("JDL = ".$jdlFileName);
	    open (JDL, "> ".$jdlFileName);
	    print JDL "[\n";
	    print JDL "Executable    = \"$shFileName\"; \n";
	    print JDL "Arguments     = \"\"; \n";
	    print JDL "StdOutput     = \"std.out\"; \n";
	    print JDL "StdError      = \"std.err\"; \n";
	    print JDL "InputSandbox  = {\"$shFileName\",\"$JARFILENAME\",\"$workerCfg\", \"$keystoreBaseName\"}; \n";
	    print JDL "OutputSandbox = {\"std.out\",\"std.err\"}; \n";
	    print JDL "RetryCount    = 0; \n";
#
# next ensures we use EGI resources having VirtualBox installed
#
	    print JDL "Requirements = Regexp(\".*grid36.*lal.in2p3.fr.*\",other.GlueCEUniqueID);\n";
	    print JDL "] \n";
	    close(JDL);

#	    &debug("mv $proxyFileName $ROOTDIR/$proxyFileName");
	    $userProxyFile=$ROOTDIR."/$proxyFileName"; 
	    `mv $proxyFileName $userProxyFile`;
	    `chmod 600 $userProxyFile`;
	    &debug("userProxyFile = ".$userProxyFile);
	    my $soumission;
	    $tmpFileName="xwhep$datehour.tmp";
	    &header2("Submitting pilot job for ".$work->{uid});
	    my $CMD = "bash -c \"(export X509_USER_PROXY='$userProxyFile' && $sgsubmit -a $jdlFileName && echo SOUMIS) > $tmpFileName 2>&1\"";
	    &debug("Executing ".$CMD);
	    `$CMD`;
	    my $RC = $?;
	    chomp($soumission=`bash -c "cat $tmpFileName"`);

	    if($RC == 0) {
		chomp(my $sgJobId=`cat $tmpFileName | grep -E "^https"`);
#		&debug("Submitted ID = ".$sgJobId);
		if (!$pilotJobs{$sgJobId}) {
		    $pilotJobs{$sgJobId} = {};
		}

		$pilotJobs{$sgJobId}->{sgJobId} = $sgJobId;
		$pilotJobs{$sgJobId}->{xwhepJob} = $work;
		$pilotJobs{$sgJobId}->{userproxy} = $userProxyFile;

		if (!$xwhepJobs{$work->{uid}}) {
		    $xwhepJobs{$work->{uid}} = {};
		}
		$xwhepJobs{$work->{uid}}->{sgJobId} = $sgJobId;
		&info("Pilot job submitted ".$sgJobId." / ".$work->{uid});

	    }
	    else {
		$soumission = &cleanString($soumission);
#		&debug("soumission = ".$soumission);

		&warn("Can't submit pilot job for ".$work->{uid}." : ".$soumission);
		&warn("Cancelling userproxy for ".$work->{uid});
#
# Jumy  16th 2009 : We don't cancel job
# March 16th 2010 : we cancel its proxy since there is something wrong with it
#
		$work->{status} = "PENDING";
		$work->{error_msg} = $soumission;
#		$work->{returncode} = $RC;

		if( $dbusage != 0) {
		    my $cancelProxy = $dbHandler->prepare("update works set userproxy=NULL,error_msg='Bridge cancelled proxy : ".$soumission."' where uid='".$work->{uid}."'")
			or die "Couldn't prepare statement: " . $dbHandler->errstr;
		    $cancelProxy->execute()
			or die "Couldn't execute statement: " . $cancelProxy->errstr;
		}
		else {
		    $xmlFileName="$work->{uid}.xml";
		    &workToXMLFile($xmlFileName, $work);
		    sendCommand ("Cancelling job proxy ",
				 "Can't cancel job proxy",
				 $xwsendwork, "--xwconfig", $clientConfFile,
				 "--xwxml $xmlFileName",
				 "--xwformat xml");

		}
	    }


	    &info("");

	    &deleteFiles();
	}

#
# Check SG jobs status
#
	foreach my $job (keys %pilotJobs) {

	    if ($job eq "" ) {
		&info("Getting pilot job status : job is empty ?!?!");		
		next;
	    }

	    my $sgJobStatus;
	    my $work = $pilotJobs{$job}->{xwhepJob};

	    `rm -f $tmpFileName`;

	    if($work == undef ) {
		&info("Getting pilot job status : work is undef ?!?!");		
		next;
	    }
	    if ($work->{uid} == undef ) {
		&info("Getting pilot job status : work->{uid} is undef ?!?!");		
		next;
	    }
	    if ($work->{uid} eq "" ) {
		&info("Getting pilot job status : work->{uid} is empty ?!?!");		
		next;
	    }


	    &info("Getting pilot job status (".$job." / ".$work->{uid}.")");

	    $userProxyFile=$pilotJobs{$job}->{userproxy};
	    if( ! -f $userProxyFile ) {
		&info("Getting status : must download X509 proxy ".$pilotJobs{$job}->{userproxy});
		my $clientConfFile = $servers{$server}->{configs}->{clientconf};

#
# retreive X509 proxy
#
		my $cmdData = sendCommand ("Downloading X509 proxy",
					   "Can't download X509 proxy",
					   $xwdownload, , "--xwconfig", $clientConfFile,
					   $work->{userproxy},
					   "--xwformat xml");
		if($dbusage == 1) {

		    &debug("Retreiving proxy from DB");
		    my $lastindex = rindex($work->{userproxy}, "/");
		    my $proxyuidstr = substr($work->{userproxy}, $lastindex+1);
		    debug("lastindex = ".$lastindex);
		    debug("substr = ".$proxyuidstr);
		    my $reqDataString = "select uid,owneruid,name from datas where uid='".$proxyuidstr."' and isdeleted='false'";
		    my $reqDatas = $dbHandler->prepare($reqDataString)
			or die "Couldn't prepare statement: " . $dbHandler->errstr;

		    $reqDatas->execute()
			or die "Couldn't execute statement: " . $reqDatas->errstr;

		    $cmdData = "<?xml version=\"1.0\"?>\n<get>\n";

		    my $atleastone = 0;

		    while (my $data = $reqDatas->fetchrow_hashref) {

			$atleastone = 1;

			$cmdData = $cmdData."<data uid='".$data->{uid}."'";
			if ($data->{owneruid} ne "" ) {
			    $cmdData = $cmdData." owneruid='".$data->{owneruid}."'";
			}
			else {
			    &error($data->{uid}." has no OWNERUID ???");
			    next;
			}
			if ($data->{name} ne "" ) {
			    $cmdData = $cmdData." name='".$data->{name}."'";
			}
			$cmdData = $cmdData." />\n";
		    }

		    $cmdData = $cmdData."</get>\n";

		    $cmdData = "" if ($atleastone < 1);
		}

		if ($cmdData eq "" ) {
		    &error("cmdData == \"\" ?!?!");
		    next;
		}
	    
		my $datas = XMLin($cmdData);
		if($datas == undef ) {
		    &error("datas is undef ?!?!");
		    next;
		}

		my $dataErrorMsg = "";
		my $data = $datas->{data};
		if($data == undef ) {
		    $dataErrorMsg = "Can't get X509 proxy : download error (access rights ?)";
		}
		if($data->{uid} eq "") {
		    $dataErrorMsg = "Can't get X509 proxy : data.uid is undef ?!?!";
		}
		if($data->{owneruid} ne $work->{owneruid}) {
		    $dataErrorMsg = "Can't get X509 proxy : work.owner != proxy.owner (".$data->{owneruid}." != ".$work->{owneruid}.")";
		}
	    
		if($dataErrorMsg ne "") {
		    $work->{status} = "ERROR";
		    $work->{error_msg} = $dataErrorMsg;
		    $xmlFileName="$work->{uid}.xml";
		    &workToXMLFile($xmlFileName, $work);
		    sendCommand ("Cancelling job",
				 "Can't cancel jobs",
				 $xwsendwork, "--xwconfig", $clientConfFile,
				 "--xwxml $xmlFileName",
				 "--xwformat xml");
		    
#		`rm -f $xmlFileName`;
		    &deleteFiles();
		    next;
		}

		$_ = $data->{name};
		my ($filename, $fileext) =  /^(.*)\.(.*)/;
		$filename = $data->{name} if($fileext  eq "");
		$filename = "_".$filename if($filename ne "");
		
		my $proxyFileName = `ls $data->{uid}*`;
		chomp($proxyFileName);
		$userProxyFile = $proxyFileName;
		&debug("proxyFileName = ".$proxyFileName);
		`chmod 600 $proxyFileName`;
		if($? != 0) {
		    &error("X509 proxy download error ".$data->{uri});
		    &deleteFiles();
		    next;
		}
	    
		&debug("X509 proxy = ".$data->{uri});
	    }

	    &debug("userProxyFile = ".$userProxyFile);
	    my $datehour = "";
#	    my $datehour = `date "+%Y%m%d%H%M%S"`;
#	    chomp($datehour);
	    my $tmpFileName="xwhep$datehour.tmp";
	    my $CMD = "bash -c \"(export X509_USER_PROXY='$userProxyFile' && $sgstatus $job) > $tmpFileName 2>&1\"";
	    &debug("Executing ".$CMD);
	    `$CMD`;
	    my $RC = $?;
	    if($RC != 0) {
		&error("$sgstatus error : ".$RC);
		$sgJobStatus = "Pilot Job Aborted : cannot retreive pilot job status RC = ".$RC;
	    }
	    else {
		chomp($sgJobStatus=`cat $tmpFileName | grep -E "^Current Status" | cut -d ":" -f 2 | sed "s/^[[:space:]]*//g"`);
	    }

	    $sgJobStatus = &cleanString($sgJobStatus);
	    &info("Pilot job status (".$job." / ".$work->{uid}.") : ".$sgJobStatus);
#
# 3 decembre 2010 : update job so that the user can see SG status
#
	    $work->{error_msg} = $sgJobStatus;
	    if( $dbusage != 0) {
		my $updatework = $dbHandler->prepare("update works set error_msg='Pilot job status (".$job.") : ".$sgJobStatus."' where uid='".$work->{uid}."'")
		    or die "Couldn't prepare statement: " . $dbHandler->errstr;
		$updatework->execute()
		    or die "Couldn't execute statement: " . $updatework->errstr;
	    }
	    else {
		$xmlFileName="$work->{uid}.xml";
		&workToXMLFile($xmlFileName, $work);
		sendCommand ("Updating job",
			     "Can't update job",
			     $xwsendwork, "--xwconfig", $clientConfFile,
			     "--xwxml $xmlFileName");
	    }

#
# If Pilot Job failed, cancel XWHEP job
#
#	    if(($sgJobStatus =~ "Aborted") ||
#	       ($sgJobStatus =~ "Done")) {


	    if($sgJobStatus =~ "Aborted") {

#
# 16 juillet 2009 : We don't cancel job
#
#		$work->{status} = "ERROR";
#		$work->{error_msg} = $sgJobStatus;
#		$xmlFileName="$work->{uid}.xml";
#		&workToXMLFile($xmlFileName, $work);
#		sendCommand ("Cancelling job",
#			     "Can't cancel jobs",
#			     $xwsendwork, "--xwconfig", $clientConfFile,
#			     "--xwxml $xmlFileName");

		$userProxyFile = $pilotJobs{$job}->{userproxy};
		&debug("userProxyFile = ".$userProxyFile);
		my $CMD = "bash -c \"(export X509_USER_PROXY='$userProxyFile' && $sgcancel $job)\"";
		&debug("Executing ".$CMD);
		`$CMD`;

		&info("Pilot job removed (".$job." / ".$work->{uid}.") : ".$sgJobStatus);

		$userProxyFile=$pilotJobs{$job}->{userproxy};
		`rm -f $userProxyFile`;
		undef $pilotJobs{$job}->{userproxy};
		undef $pilotJobs{$job};
		undef $xwhepJobs{$pilotJobs{$job}->{xwhepJob}->{uid}};
	    }
	    else {
#
# If Pilot Job is not successfully completed, it is certainly "ready", "schedulled" or "running"
# When do nothing then
#
		if(($sgJobStatus =~ "Success") ||
		   ($sgJobStatus =~ "Done")) {
#
# If Pilot Job successed, remove it and check that XWHEP job has correctly been computed by Pilot Job
#
		    $userProxyFile = $pilotJobs{$job}->{userproxy};
		    &debug("userProxyFile = ".$userProxyFile);
		    if( ! -f $userProxyFile ) {
			&error("Can't call $sgoutput : proxy not found ".$userProxyFile);
			&deleteFiles();
			next;
		    }
		    my $CMD = "bash -c \"(export X509_USER_PROXY='$userProxyFile' && cd ~/JobOutput ; $sgoutput $job)\"";
		    &debug("Executing ".$CMD);
		    `$CMD`;
		    if( $? != 0) {
			&error("Can't get SG output");
			&deleteFiles();
			next;
		    }
#		    if ($debug > $DEBUG) {
			my $CMD = "bash -c \"mkdir -p ~/JobOutput && cd ~/JobOutput && rm -Rf * \"";
			&debug("Executing ".$CMD);
			`$CMD`;
#		    }

# 16 juillet 2009 : We don't cancel job

#		    my $cmdWorks = sendCommand ("Retreiving jobs",
#						"Can't retreive jobs",
#						$xwworks, "--xwconfig", $clientConfFile,
#						$pilotJobs{$job}->{xwhepJob}->{uid});
#		    if ($cmdWorks eq "" ) {
#			&error("Can't retreive ".$pilotJobs{$job}->{xwhepJob}->{uid});
#		    }
#		    else {
#			my $works = XMLin($cmdWorks);

#			my @worksArray = ();
#			if(isa($works->{work}, "ARRAY")) {
#			    @worksArray = @{$works->{work}};
#			}
#			else {
#			    @worksArray = $works->{work};
#			};
#			foreach my $work (@worksArray) {
#			    &debug("XWJob ".$work->{uid}." status ".$work->{status});
#			    if($work->{status} ne "COMPLETED") {
#

#
# If XWHEP job has not been correctly computed, cancel it
#
#				$work->{status} = "ERROR";
#				$work->{error_msg} = "Pilot Job has not been able to compute job for any reason (certainly no binary available for hosting environnement)";
#				$xmlFileName="$work->{uid}.xml";
#				`rm -f $xmlFileName`;
#				&workToXMLFile($xmlFileName, $work);
#				sendCommand ("Cancelling job",
#					     "Can't cancel jobs",
#					     $xwsendwork, "--xwconfig", $clientConfFile,
#					     "--xwxml $xmlFileName");
#				`rm -f $xmlFileName`;
#			    }
#			}
#		    }

		    $userProxyFile=$pilotJobs{$job}->{userproxy};
		    `rm -f $userProxyFile`;
		    undef $pilotJobs{$job}->{userproxy};
		    undef $pilotJobs{$job};
		    undef $xwhepJobs{$pilotJobs{$job}->{xwhepJob}->{uid}};

#		    &deleteFiles();

		}
	    }

	    &deleteFiles();

	}
    }

    if($dbusage != 0) {
	$dbHandler->disconnect;
    }

    header2 "Sleeping 5mn";
    sleep 300;
}


#
# Clean up
#
cleanup ("Ending : " . dateAndTime(), 0);


#
# End of file
#
