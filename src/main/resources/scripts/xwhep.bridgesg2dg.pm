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
# File     : xwhep.bridgesg2dg.pm
# Version  : 0.1
# Created  : Oct 30th, 2006
# Modified : Nov 16th, 2010
# Author   : Oleg Lodygensky (lodygens _a_t_ lal dot in2p3 dot fr)
#            CNRS - IN2P3 - LAL
# Purpose  : this script binds XtremWeb-HEP as Globus scheduler
#            this script is called by Globus GateKeeper
#            so that EGEE can gain access to XtremWeb-HEP ressources
# License  : GPL V2
# Web      : http://www.xtremweb-hep.org
#
#
# Installation : this script must be installed in a glite UI
#                in the directory
#                /opt/globus/lib/perl/Globus/GRAM/JobManager
#
# Usage    :
#            $> voms-proxy-init --voms vo.lal.in2p3.fr
#
#            $> glite-wms-job-submit -a -e https://grid25.lal.in2p3.fr:7443/glite_wms_wmproxy_server -r xw2.lal.in2p3.fr:2119/jobmanager-xw-lal WhoAmI.jdl
#
# Following is deprecated (kept for history, if someone someday writes a book on me :D )
#
#            $> globus-job-run xw2.lal.in2p3.fr/jobmanager-fork /usr/bin/whoami
#            $> globus-job-run xw2.lal.in2p3.fr/jobmanager-pbs /usr/bin/whoami
#            $> globus-job-run xw2.lal.in2p3.fr/jobmanager-pbs -q lal /usr/bin/whoami
#            $> cd ~/globus/
#            $> edg-job-submit -r xw2.lal.in2p3.fr:2119/jobmanager-pbs-lal WhoAmI.jdl
#            $> edg-job-status https://grid09.lal.in2p3.fr:9000/wG_tQ7qIbS8dl81jiqbIxQ
#
#            $> mkdir ~/JobOutput
#            $> cd ~/JobOutput/
#            $> edg-job-get-output https://grid09.lal.in2p3.fr:9000/wG_tQ7qIbS8dl81jiqbIxQ
#            $> cd lodygens_wG_tQ7qIbS8dl81jiqbIxQ/
#            $> more std.out
#
#

use Globus::GRAM::Error;
use Globus::GRAM::JobState;
use Globus::GRAM::JobManager;
use Globus::Core::Paths;

use XML::Parser;
use XML::Dumper;
use Data::Dumper;


use Config;
use Cwd;
use File::Copy;

# NOTE: This package name must match the name of the .pm file!!
package Globus::GRAM::JobManager::xwhep;

@ISA = qw(Globus::GRAM::JobManager);


############################################################
#
# variable definitions
#
############################################################
my ($JAVA_HOME, $JAVA_BINDIR, $XWROOTDIR, $XWBINDIR, $XWCFGDIR, $xwsubmit, $xwstat, $xwresult, $xwdelete);
my %workAttributes;



############################################################
#
# This sets variable values
#
############################################################
BEGIN
{
    $JAVA_HOME = "/usr/java/jdk1.6.0_07" || $ENV{JAVA_HOME} || $ENV{JAVA_INSTALL_PATH};
    $JAVA_BINDIR = $JAVA_HOME."/bin";

    $XWROOTDIR = "/opt/xwhep-bridge-7.0.0";
    $XWBINDIR  = "$XWROOTDIR/bin";
    $XWCFGDIR  = "$XWROOTDIR/conf";

    $xwsubmit  = "$XWBINDIR/xwsubmit";
    $xwstat    = "$XWBINDIR/xwstatus";
    $xwresult  = "$XWBINDIR/xwresult";
    $xwdelete  = "$XWBINDIR/xwrm";
}


############################################################
#
# This is the constructor
#
############################################################
sub new
{
    my $proto = shift;
    my $class = ref($proto) || $proto;
    my $self = $class->SUPER::new(@_);
    my $log_dir;

    if(! exists($ENV{GLOBUS_SPOOL_DIR}))
    {
        $log_dir = $Globus::Core::Paths::tmpdir;
    }
    else
    {
        $log_dir = $ENV{GLOBUS_SPOOL_DIR};
    }

#    $self->{xtremweb_logfile} = "$log_dir/gram_xtremweb_log."
#        . $self->{JobDescription}->uniq_id();

    bless $self, $class;
    return $self;
}


############################################################
#
# This submits new job to XtremWeb
# This retreives job definition from Globus::GRAM::JobDescription
#
############################################################
sub submit
{
    my $self = shift;
    my $description = $self->{JobDescription};
    my $tag = $description->cache_tag() || $ENV{GLOBUS_GRAM_JOB_CONTACT};
    my $status;
    my $pbs_job_script;
    my $pbs_job_script_name;
    my $errfile = '';
    my $job_id;
    my $rsh_env;
    my $script_url;
    my @arguments;
    my $email_when = '';
    my $cache_pgm = "$Globus::Core::Paths::bindir/globus-gass-cache";
    my %library_vars;
    my $command = $xwsubmit;
    my $result;

    $self->append_path(\%ENV, 'JAVA_HOME', $JAVA_HOME);

    $self->nfssync( $description->executable() )
        unless $description->executable() eq '';
    $self->nfssync( $description->stdin() )
        unless $description->stdin() eq '';

    open( DESC, '> /tmp/xwhep-submit.job-description.txt');

    print DESC "#\n";
    print DESC "# **********************#\n";
    print DESC "#    XtremWeb submit    #\n";
    print DESC "# **********************#\n";
    print DESC "#\n";
    print DESC "# Date : ", `date`,"\n";
    print DESC "#\n";
    print DESC "# log file = ", $self->{xtremweb_logfile}, "\n";
    print DESC "# JAVA_BINDIR       = ", $JAVA_BINDIR, "\n";
    print DESC "# JAVA_HOME         = ", $JAVA_HOME, "\n";
    print DESC "# JAVA_INSTALL_PATH = ", $JAVA_INSTALL_PATH, "\n";
    print DESC "# email      = ", $description->email_address(), "\n";
    print DESC "# queue      = ", $description->queue(), "\n";
    print DESC "# project    = ", $description->project(), "\n";
    print DESC "# host count = ", $description->host_count(), "\n";
    print DESC "# directory  = ", $description->directory(), "\n";
    print DESC "# executable = ", $description->executable(), "\n";


    my $zipErr = "/tmp/ziperr.txt";
    `rm -f  $zipErr`;

    my $zipEnv = "";
    if( -d $description->directory())
    {
        $zipEnv="/tmp/toto.zip";
        `rm -f  $zipEnv`;
        my $workingDir=$description->directory();

        my $currentDir;
        chomp($currentDir = `pwd`);
        print DESC "# current dir  = $currentDir\n";
        chdir($workingDir);
        my $currentDir2;
        chomp($currentDir2 = `pwd`);
        print DESC "# current dir2  = $currentDir2\n";

        my $CMD="/usr/bin/zip -r  $zipEnv $workingDir/* >> $zipErr 2>&1";
        print DESC "# zipping working dir command = $CMD\n";
        chomp($result = `$CMD`);
        my $RC = $?;

        chdir($currentDir);
        my $currentDir3;
        chomp($currentDir3 = `pwd`);
        print DESC "# current dir3  = $currentDir3\n";

        print DESC "# zipping working dir return code = $RC\n";
        print DESC "# zipping working dir result      = $result\n";

    }
    if( -f $description->executable())
    {
        if($zipEnv eq "")
        {
            $zipEnv="/tmp/toto.zip";
            `rm -f  $zipEnv`;
        }

        my $executable=$description->executable();
        chomp($result = `file $executable`);
        my $RC0 = $?;

        print DESC "# file return code = $RC0\n";
        print DESC "# file result      = $result\n";

        my $executable=$description->executable();
        my $executableBasename;
        chomp($executableBasename = `basename $executable`);

        my $CMD="/usr/bin/zip -j $zipEnv $executable >> $zipErr 2>&1";
        print DESC "# zipping executable command  = $CMD\n";

        chomp($result = `$CMD`);
        my $RC = $?;

        chdir($currentDir);

        print DESC "# zipping executable return code = $RC\n";
        print DESC "# zipping executable result      = $result\n";

        #
        # glite executable is always a bash script
        #
        $command = "$command bash $executableBasename";
    }


#    @arguments = $description->arguments();
#    foreach(@arguments)
#    {
#        if(ref($_))
#        {
#            $self->log("# Xtremweb submit argument error");
#            return Globus::GRAM::Error::RSL_ARGUMENTS;
#        }
#        print DESC "#  __arguments__  = ", $_, "\n";
#        $command = $command." ".$_;
#    }

    if(($description->stdin() ne "") && ($description->stdin() ne "/dev/null")) {

        if($zipEnv eq "")
        {
            $zipEnv="/tmp/toto.zip";
            `rm -f  $zipEnv`;
        }

        print DESC "#  stdin  = \"", $description->stdin(), "\"\n";

        my $stdin=$description->stdin();

        my $CMD="/usr/bin/zip -j $zipEnv $stdin >> $zipErr 2>&1";
        print DESC "# zipping executable command  = $CMD\n";

        chomp($result = `$CMD`);
        my $RC = $?;

        chdir($currentDir);

        print DESC "# zipping stdin return code = $RC\n";
        print DESC "# zipping stdin result      = $result\n";
    }
    else {
        print DESC "#  stdin not defined (or set to /dev/null)\n";
    }
    print DESC "# stdout     = ", $description->stdout(), "\n";
    print DESC "# stderr     = ", $description->stderr(), "\n";

    if($zipEnv ne "")
    {
        $command = "$command --xwenv $zipEnv";
    }

    print DESC "\n\n";
    print DESC "# command = $command\n";
    print DESC "\n\n";

    chomp($result = `$command`);
    my $return_code = $?;

    print DESC "# command return code = $return_code\n";
    print DESC "# command result = $result\n";

#    return Globus::GRAM::Error::JOBTYPE_NOT_SUPPORTED;

    if($zipEnv ne "")
    {
#        `rm -f  $zipEnv`;
    }

    if($return_code == 0)
    {
        print DESC "# successful submit, setting state to PENDING\n";
        
        chomp($job_id = `echo $result | cut -d ' ' -f 1`);
        
        print DESC "# job_id = $job_id\n";
        close( DESC );
        
        #system("$cache_pgm -cleanup-url $script_url");
        
        $self->log("# Xtremweb submit successful, setting state to PENDING");
        
        return {JOB_ID => $job_id,
                JOB_STATE => Globus::GRAM::JobState::PENDING };
    }
    #system("$cache_pgm -cleanup-url $tag/pbs_job_script.$$");
		
    $self->log("# Xtremweb submit unsuccessful");
    close( DESC );
    
    return Globus::GRAM::Error::INVALID_SCRIPT_REPLY;
}


############################################################
#
# This method retreives job status from XtremWeb
# This retreives job definition from Globus::GRAM::JobDescription
# This uses JobDescription->jobid() as the XtremWeb UID
#
############################################################
sub poll
{
    my $self = shift;
    my $description = $self->{JobDescription};
    my $job_id = $description->jobid();
    my $state;
    my $status_line;
    my $exit_code;
		
    $self->log("polling job $job_id");
		
    open( DESC, "> /tmp/xwhep-status.job-description-$job_id.txt");

    print DESC "#\n";
    print DESC "# **********************#\n";
    print DESC "#    XtremWeb status    #\n";
    print DESC "# **********************#\n";
    print DESC "#\n";
    print DESC "# Date : ", `date`,"\n";
    print DESC "#\n";
    print DESC "# JAVA_BINDIR       = ", $JAVA_BINDIR, "\n";
    print DESC "# JAVA_HOME         = ", $JAVA_HOME, "\n";
    print DESC "# JAVA_INSTALL_PATH = ", $JAVA_INSTALL_PATH, "\n";
    print DESC "# email      = ", $description->email_address(), "\n";
    print DESC "# queue      = ", $description->queue(), "\n";
    print DESC "# project    = ", $description->project(), "\n";
    print DESC "# host count = ", $description->host_count(), "\n";
    print DESC "# directory  = ", $description->directory(), "\n";
    print DESC "# job_id    = ", $job_id, "\n";
    print DESC "# stdin     = ", $description->stdin(), "\n";
    print DESC "# stdout     = ", $description->stdout(), "\n";
    print DESC "# stderr     = ", $description->stderr(), "\n";

    my $result;
    $result = (grep($job_id, $self->pipe_out_cmd($xwstat, '--xwformat xml ', $job_id)))[1];
    # get the exit code of the qstat command.  for info search $CHILD_ERROR
    # in perlvar documentation.
    $exit_code = $? >> 8;
		
    print DESC "# result       = ", $result, "\n";
    print DESC "# exit_code    = ", $exit_code, "\n";


    my $parser = new XML::Parser;

    $parser->setHandlers( Start => \&xml_startElement,
                          End => \&xml_endElement,
                          Char => \&xml_characterData,
                          Default => \&xml_default);
    
    if($exit_code == 5)
    {
        $self->log("xwstat unknown UID");
        print DESC "# unknown UID \n";
        close( DESC);
        $state = Globus::GRAM::JobState::JOB_CANCEL_FAILED;
        return {JOB_STATE => $state};
    }

    my $data = $parser->parse($result);
    print DESC "# workAttributes[UID]    = ".$workAttributes{'UID'}."\n";
    print DESC "# workAttributes[STATUS] = ".$workAttributes{'STATUS'}."\n";
    
    
    if($workAttributes{'STATUS'} eq "PENDING")
    {
        $state = Globus::GRAM::JobState::PENDING;
        print DESC "# set state  = PENDING \n";
    }
    elsif($workAttributes{'STATUS'} eq "WAITING")
    {
        $state = Globus::GRAM::JobState::PENDING;
        print DESC "# set state  = PENDING \n";
    }
    elsif($workAttributes{'STATUS'} eq "ERROR")
    {
        $state = Globus::GRAM::JobState::JOB_EXECUTION_FAILED;
        print DESC "# set state  = SUSPENDED \n";
    }
    elsif($workAttributes{'STATUS'} eq "RUNNING")
    {
        $state = Globus::GRAM::JobState::ACTIVE;
        print DESC "# set state  = ACTIVE \n";
    }
    elsif($workAttributes{'STATUS'} eq "COMPLETED")
    {
#        $self->log("qstat rc is 153 == Unknown Job ID == DONE");
#        $state = Globus::GRAM::JobState::DONE;
#				$self->nfssync( $description->stdout() )
#						if $description->stdout() ne '';
#				$self->nfssync( $description->stderr() )
#						if $description->stderr() ne '';
        if($description->stdout() ne '') {
            my $currentDir;
            chomp($currentDir = `pwd`);
            print DESC "# current dir  = $currentDir\n";
            chdir("/tmp/");
            my $currentDir2;
            chomp($currentDir2 = `pwd`);
            print DESC "# current dir2  = $currentDir2\n";
        
            my $CMD="$xwresult $job_id";
            print DESC "# xwresult command = $CMD\n";
            chomp($result = `$CMD`);
            my $RC = $?;
            print DESC "# xwresult return code = $RC\n";

            if($RC == 0) {
                chdir($currentDir);

                my $dirName=$job_id;
                $dirName=~s/:/_/g;
                $dirName="/tmp/bash/$dirName";
                print DESC "# dirName = $dirName\n";

                my $srcFile = "$dirName/stdout.out";
#                print DESC "# copy(",$srcFile,",", $description->stdout(),")\n";
#                copy($srcFile, $description->stdout())
#                    or print DESC "# $srcFile cannot be copied.\n";

                open(OUPUT, '>> ',$description->stdout());
                open (INPUT, $srcFile);
                my $ligne;
                while ($ligne = <INPUT>) {
                    
                    chop ($ligne);
                    print OUTPUT $ligne;
                }
                close INPUT;
                close OUTPUT;
            }

            $self->nfssync( $description->stdout() );
        }

        $state = Globus::GRAM::JobState::DONE;
        print DESC "# set state  = DONE \n";
    }
    else
    {
        # This else is reached by an unknown response from xtremweb.
        $self->log("xwstat returned an unknown response.  Telling JM to ignore this poll");
        print DESC "# set state  UNKNOWN \n";
        close( DESC);
        return {};
    }
    
    print DESC "# poll done... \n";
    close( DESC);
    return {JOB_STATE => $state};
}


############################################################
#
# This method cancels job from XtremWeb
# This retreives job definition from Globus::GRAM::JobDescription
# This uses JobDescription->jobid() as the XtremWeb UID
#
############################################################
sub cancel
{
    my $self = shift;
    my $description = $self->{JobDescription};
    my $job_id = $description->jobid();
		
    $self->log("cancel job $job_id");

    open( DESC, "> /tmp/xwhep-remove.job-description-$job_id.txt");

    print DESC "#\n";
    print DESC "# **********************#\n";
    print DESC "#    XtremWeb remove    #\n";
    print DESC "# **********************#\n";
    print DESC "# Date : ", `date`,"\n";
    print DESC "#\n";
    print DESC "#\n";
    print DESC "# JAVA_BINDIR       = ", $JAVA_BINDIR, "\n";
    print DESC "# JAVA_HOME         = ", $JAVA_HOME, "\n";
    print DESC "# JAVA_INSTALL_PATH = ", $JAVA_INSTALL_PATH, "\n";
    print DESC "# email      = ", $description->email_address(), "\n";
    print DESC "# queue      = ", $description->queue(), "\n";
    print DESC "# project    = ", $description->project(), "\n";
    print DESC "# host count = ", $description->host_count(), "\n";
    print DESC "# directory  = ", $description->directory(), "\n";
    print DESC "# job_id    = ", $job_id, "\n";

    close (DESC);

    my $result;
    $result = (grep($job_id, $self->pipe_out_cmd($xwdelete, $job_id)))[1];
    if($? == 0)
    {
        return { JOB_STATE => Globus::GRAM::JobState::FAILED }
    }
    return Globus::GRAM::Error::JOB_CANCEL_FAILED();
}


############################################################
#
# This is the XML parser start element handler
#
############################################################
sub xml_startElement {
    my( $parseinst, $element, %attrs ) = @_;

#		print "* startElement element      = ".$element."\n";

    if ($element ne "WORK") {
#				print "* startElement not work, exiting \n";
        return;
    }
    %workAttributes = %attrs;

#		print "* startElement attrs[UID]       = ".$attrs{'UID'}."\n";
#		print "* startElement attrs[ISSERVICE] = ".$attrs{'ISSERVICE'}."\n";
		
}

############################################################
#
# This is the XML parser end element handler
#
############################################################
sub xml_endElement {
    my( $parseinst, $element ) = @_;
#		print "*   endElement element      = ".$element."\n";
}

############################################################
#
# This is the XML parser character handler
#
############################################################
sub xml_characterData {
    my( $parseinst, $data ) = @_;
    if (($tag eq "title") || ($tag eq "summary")) {
        $data =~ s/\n|\t//g;
#				print "$data";
    }
}


############################################################
#
# This is the XML parser default handler
#
############################################################
sub xml_default {
    my( $parseinst, $data ) = @_;
    # you could do something here
}


1;
