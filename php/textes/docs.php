<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
   <meta name="GENERATOR" content="Mozilla/4.73 [en] (X11; U; SunOS 5.7 sun4u) [Netscape]">
</head>


<?php
if (!isset($V_Session)) {
  if (isset($_COOKIE["sessionID"]))
    $V_Session = $_COOKIE["sessionID"];
}
?>


<body>

<br>

<blockquote>
<p class="TITRE">Documentations
</p>
</blockquote>

<br><br>

<blockquote>
<blockquote>

This web site is the Web visible part of an XtremWeb installation.
<br><br>

This page is not intended to explain and detail the
<a href="http://www.xtremweb.net">XtremWeb</a> platform, but to present
how to use this current Global Computing platform installation.<br>
<br>
For further informations, please refer to the <a href="http://www.xtremweb.net">XtremWeb</a>
Web site.
<br><br>

<center><hr width="50%"></center>
</blockquote>
</blockquote>

<br><br>
<a name="TOC"></a>

<blockquote>
Table of contents
<blockquote>
<ol class="withroman">
<li><a href="#INTRO"><b>Introduction</b></a>
<br>
<li><a href="#REGISTER"><b>Registering</b></a>
<br>
<li><a href="#LOGIN"><b>Logging in and out</b></a>

<br>
<li><a href="#DOWNLOAD"><b>Downloads</b></a>
<br>
<li><a href="#CLIENTUSAGE"><b>Client usage</b></a>
</ol>

</blockquote>
</blockquote>

<br><br>

<ol>

<hr width="80%">
<a name="INTRO"></a>
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="40%">
&nbsp;
</td>
<td align="center" width="20%">
  <a href="#TOC"><b>Table of contents</b></a>
</td>
<td align="center" width="40%">
<a href="#REGISTER"><b>Next : Registering</b></a>
</td>
</tr>
</table>
</div>

<br><br>

<!-- nummering chapters -->
<ol class="withroman">


<li><p class="TITRE">Introduction</p>

<br><br>


XtremWeb is a research project belonging to light weight systems.
Its a Free Open Source and non-profit software platform to explore
scientific issues and applications of Global Computing,
Peer to Peer distributed systems. 
<br><br>

Global Computing and Peer-to Peer systems are the enabling concepts/technologies
toward Global Operating Systems also called Internet Scale Operating Systems.
Like the other Distributed System Platforms, an XW platform uses a) remote
resources (PCs, workstations, servers) connected to Internet or b) a pool of
resources (PCs, workstations, servers) inside a LAN. 
Participants of an XW platform cooperate by providing their CPU idle time. 
<br><br>

The XtremWeb software platform allows to setup and run Distributed System projects.
Such projects must be based on a community of participants. For example, XtremWeb
platforms allow a High School, a University,  ... or a Company to setup and run
a Global Computing or Peer to Peer distributed system for either a specific
application or a range of applications. 
<br><br><br>

This Web site has been installed by any authorized party from the
<a href="http://www.xtremweb.net">XtremWeb package</a> to take benefits of
Global Computing&nbsp;; it is intended to allow jobs management in 
the platform and, more generally, to get involved with the system.<br>

<br><br>

<a name="REGISTER"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="40%">
  <a href="#INTRO"><b>Previous : Introduction</b></a>
</td>
<td align="center" width="20%">
  <a href="#TOC"><b>Table of contents</b></a>
</td>
<td align="center" width="40%">
  <a href="#LOGIN"><b>Next : Logging in and out</b></a>
</td></tr>
</table>
</div>

<br><br>

<li><p class="TITRE">Registering</p>

<br><br>

For security reason, any user of an installed <i>XtremWeb</i> platform must be registered.<br>
<br>
To do so, a <a href="./index.php?section=section2&subsection=register">dedicated page</a>
is available in each site, where you could provide some informations that are sent
to the webmaster.<br>
<br>
It is up to that last to validate your request and send you the
password you will need to go <a href="#LOGIN">further</a>.

<br><br>

<a name="LOGIN"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="40%">
<a href="#REGISTER"><b>Previous : Registering</b></a>
</td><td align="center" width="20%">
<a href="#TOC"><b>Table of contents</b></a>


</td><td align="center" width="40%">
<a href="#DOWNLOAD"><b>Next : Downloads</b></a>


</td></tr>
</table>
</div>

<br><br>

<li><p class="TITRE">Logging in and out</p>

<br><br>

As soon as your registration has been validated, you can 
<a href="./index.php?section=section2&subsection=login">log in </a>
with your own password and <a href="#WORKER">start working</a>.<br>
<br>
You will not forget to

<?php
if (isset ($V_Session)) {
?>

<a href="./index.php?section=section2&subsection=logout">log out</a>,

<?php
}
else
  echo " log out ";
?>

as you finish your work.<br>
<br>
Note&nbsp;: a login is only valid for one hour&nbsp;; after this delay, you will have to re-login.


<br><br>


<a name="DOWNLOAD"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="40%">
<a href="#LOGIN"><b>Previous : Logging in and out</b></a>
</td><td align="center" width="20%">
<a href="#TOC"><b>Table of contents</b></a>
</td><td align="center" width="40%">
<a href="#GETWORKER"><b>Next : Downloading the worker</b></a>
</td></tr>
</table>
</div>

<br><br>

<li><p class="TITRE">Downloads</p>

<br>
XtremWeb is a three tier Desktop Grid platform&nbsp;:
<br><br>
<ol>
<li> the worker is the computing part, spread among the network, installed on
volunteer PCs. As so, you can <a href="#GETWORKER"><b>download the worker</b></a>&nbsp;;
<li> the client is the user interface part, spread among the network, installed on
authorized PCs. As so, you can <a href="#GETCLIENT"><b>download the client</b></a>.
<li> in order to connect to the server, you need to <a href="#GETXWRC"><b>download your config file</b></a>. It should be stored into the directory <code>&lt;$HOME/.xtremweb/&gt;</code> and named <code>&quot;xwrc&quot;</code>.<br>
Its full pathname should then be <code>&lt;$HOME/.xtremweb/xwrc&gt;</code>.
</ol>

<br><br>


<a name="GETWORKER"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="10%">
<a href="#DOWNLOAD"><b>Up : Downloads</b></a>
</td>
<td align="center" width="35%">
<a href="#DOWNLOAD"><b>Previous : Downloading</b></a>
</td>
<td align="center" width="20%">
<a href="#TOC"><b>Table of contents</b></a>
</td>
<td align="center" width="35%">
<a href="#GETCLIENT"><b>Next : Downloading the client</b></a>
</td></tr>
</table>
</div>


<br><br>

<!-- nummering sub sections -->
<ol>

<li><p class="SOUSTITRE">Downloading the worker</p>

<br><br>

The worker part of the platform is the computing one.<br>
You are invited to download and install this component in order
to improve this platform power.<br>

<?php
if (isset ($V_Session)) {
?>

To do so, please, 
<a href="./index.php?section=section2&subsection=downloadworker">download the worker</a>.

<?php
} else {
?>

To download the client, you must <a href="./index.php?section=section2&subsection=login">log in<a> first.

<?php
}
?>

<br><br>
<br><br>

<a name="GETCLIENT"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="10%">
<a href="#DOWNLOAD"><b>Up : Downloads</b></a>
</td>
<td align="center" width="35%">
<a href="#GETWORKER"><b>Previous : Downloading the worker</b></a>
</td><td align="center" width="20%">
<a href="#TOC"><b>Table of contents</b></a>
</td>
<td align="center" width="35%">
<a href="#GETXWRC"><b>Next : Downloading the client configuration file</b></a>
</td></tr>
</table>
</div>


<br><br>

<li><p class="SOUSTITRE">Downloading the client</p>

<br><br>

The client part of the platform aims to help jobs management.<br>
You are invited to download and use this component to submit jobs,
get their status, retrieve their results etc.
<br>

<?php
if (isset ($V_Session)) {
?>

To do so, please,
<a href="./index.php?section=section2&subsection=downloadclient">download the client</a>.

<?php
} else {
?>

To download the client, you must <a href="./index.php?section=section2&subsection=login">log in<a> first.

<?php
}
?>

<br><br>
<br><br>

<a name="GETXWRC"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="10%">
<a href="#DOWNLOAD"><b>Up : Downloads</b></a>
</td>
<td align="center" width="35%">
<a href="#GETCLIENT"><b>Previous : Downloading the client</b></a>
</td><td align="center" width="20%">
<a href="#TOC"><b>Table of contents</b></a>
</td>
<td align="center" width="35%">
<a href="#CLIENTUSAGE"><b>Next : Using the client</b></a>
</td></tr>
</table>
</div>


<br><br>

<li><p class="SOUSTITRE">Downloading the client configuration file</p>

<br><br>

The client needs some parameters provided by a configuration file. Please refer
to <a href="http://www.lri.fr/~fedak/XtremWeb/index.php?section=section3&subsection=documentations">the XtremWeb documentation</a>.
<br><br>

<?php
if (isset ($V_Session)) {
?>

But this server knows the configuration you need to connect to itself.<br>
It then provides it and you just have to 
<a href="./index.php?section=section2&subsection=downloadclient">download your configuration file</a>.

<?php
} else {
?>

To download your configuration file, you must <a href="./index.php?section=section2&subsection=login">log in<a> first.

<?php
}
?>

<br><br>
<br><br>


<!-- nummering sub sections -->
</ol>


<br><br>

<a name="CLIENTUSAGE"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="40%">
<a href="#GETXWRC"><b>Previous : Downloading the client configuration file</b></a>
</td><td align="center" width="20%">
<a href="#TOC"><b>Table of contents</b></a>
</td><td align="center" width="40%">
<a href="#CLIENTPARAMS"><b>Next : Commons parameters</b></a>
</td></tr>
</table>
</div>


<br><br>

<li><p class="TITRE">Using the client</p>

<br><br>


The client can fully manage the platform.<br>
Some actions require XtremWeb administrator privileges and are not detailed here&nbsp; (please refer to <a href="http://www.lri.fr/~fedak/XtremWeb/index.php?section=section3&subsection=documentations">the XtremWeb documentation</a>)&nbsp;:
<br><br>
<ul>
<li> add/edit/remove applications to the platform&nbsp;;
<li> add/edit/remove users.
</ul>

<br><br>

Next sections detail non privileged actions to&nbsp;:

<blockquote>


<br>
<ol>
<li><a href="#CLIENTPARAMS"><b>provide common parameters to the client</b></a>
<br>
<li><a href="#APPS"><b>retrieve installed applications</b></a>
<br>
<li><a href="#WORKERS"><b>retrieve workers list</b></a>
<br>
<li><a href="#SUBMIT"><b>submit jobs</b></a>
<br>
<li><a href="#STATUS"><b>retrieve jobs status</b></a>
<br>
<li><a href="#RESULTS"><b>retrieve jobs results</b></a>
<br>
<li><a href="#CANCEL"><b>cancel jobs</b></a>
</ol>


</blockquote>

<br><br>

<!-- nummering sub sections -->
<ol>

<a name="CLIENTPARAMS"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="10%">
<a href="#CLIENTUSAGE"><b>Up : client usage</b></a>
</td>
<td align="center" width="35%">
<a href="#CLIENTUSAGE"><b>Previous : Using the client</b></a>
</td><td align="center" width="20%">
<a href="#TOC"><b>Table of contents</b></a>
</td><td align="center" width="35%">
<a href="#APPS"><b>Next : Installed applications</b></a>
</td></tr>
</table>
</div>

<br><br>

<li><p class="SOUSTITRE">Providing common parameters to the client</p>

<br><br>


This section describes common parameters that apply to <b>all</b> client actions&nbsp;:
<br><br>

<ul>
<li>--xwconfig &lt;configFile&gt; to provide a config file.<br>
    Default is&nbsp;<code>$HOME/.xtremweb/xwrc</code>.
    If such a file exists, this <i>--xwconfig</i> option is not necessary.
<li>--xwhelp to display the client helps&nbsp;;
<li>--xwformat [ html | csv | xml ] to specify the output format (default is raw text).
</ul>

<br><br>
Example&nbsp;:<br>
<blockquote>
<code>
xwclient --xwconfig &lt;myConfigFile&gt
</code>
</blockquote>


<br><br>

<a name="APPS"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="10%">
<a href="#CLIENTUSAGE"><b>Up : client usage</b></a>
</td>
<td align="center" width="35%">
<a href="#CLIENTPARAMS"><b>Previous : Common parameters</b></a>
</td><td align="center" width="20%">
<a href="#TOC"><b>Table of contents</b></a>
</td><td align="center" width="35%">
<a href="#WORKERS"><b>Next : Workers list</b></a>
</td></tr>
</table>
</div>

<br><br>

<li><p class="SOUSTITRE">Retrieving installed applications</p>

<br><br>


Any application to must be installed on the server before jobs can be dispatch
through the platform. This can only be done by XtremWeb users with enough privileges
(please refer to <a href="http://www.lri.fr/~fedak/XtremWeb/index.php?section=section3&subsection=documentations">the XtremWeb documentation</a>).


<br><br><br>

<ul>
<li>Using the client application&nbsp;:

<br><br>
The client expects the parameter <code>--xwapps</code> to list installed applications on server side.
<br><br>
Examples&nbsp;:<br>
<blockquote>
<code>
xwclient --xwapps
<br>
xwclient --xwconfig &lt;configFile&gt --xwapps
</code>
</blockquote>

<br>

<li>Using the <code>xwapps</code> script&nbsp;:<br>
<br>
The <code>xwapps</code> encapsulates the needed arguments for you.
<br><br>
Examples&nbsp;:<br>
<blockquote>
<code>
xwapps
<br>
xwapps --xwconfig &lt;configFile&gt
</code>
</blockquote>


<br><br>
<li>Using the web site&nbsp;:
<br><br>
<blockquote>

<?php
if (isset ($V_Session)) {
?>

You can see the installed application on the 
<a href="./index.php?section=section2&subsection=submitTask">
submission page</a>.

<?php
} else {
?>

To list installed applications, you must <a href="./index.php?section=section2&subsection=login">log in<a> first.

<?php
}
?>

</blockquote>

</ul>


<br><br>

<a name="WORKERS"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="10%">
<a href="#CLIENTUSAGE"><b>Up : client usage</b></a>
</td>
<td align="center" width="35%">
<a href="#APPS"><b>Previous : Installed applications</b></a>
</td><td align="center" width="20%">
<a href="#TOC"><b>Table of contents</b></a>
</td><td align="center" width="35%">
<a href="#SUBMIT"><b>Next : Submitting jobs</b></a>
</td></tr>
</table>
</div>

<br><br>

<li><p class="SOUSTITRE">Retrieving workers list</p>

<br><br>


Any worker that has at least been connected once is registered on server side.
<br>
The client can retrieve informations relative to workers.
<br><br><br>

<ul>
<li>Using the client application&nbsp;:

<br><br>
The client expects the parameter <code>--xwworkers</code> to retrieve known workers.
<br><br>
Examples&nbsp;:<br>
<blockquote>
<code>
xwclient --xwworkers
<br>
xwclient --xwconfig &lt;configFile&gt --xwworkers
</code>
</blockquote>

<br><br>

<li>Using the <code>xwworkers</code> script&nbsp;:<br>
<br>
The <code>xwworkers</code> encapsulates the needed arguments for you.
<br><br>
Examples&nbsp;:<br>
<blockquote>
<code>
xwworkers
<br>
xwworkers --xwconfig &lt;configFile&gt
</code>
</blockquote>
<br>


<li>Using the web site&nbsp;:
<br><br>
<blockquote>
You can retrieve the known workers on the 
<a href="./index.php?section=section3&subsection=workerStats">
workers stats page</a>.
</blockquote>

</ul>

<br><br>

<a name="SUBMIT"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="10%">
<a href="#CLIENTUSAGE"><b>Up : client usage</b></a>
</td>
<td align="center" width="35%">
<a href="#WORKERS"><b>Previous : Workers list</b></a>
</td><td align="center" width="20%">
<a href="#TOC"><b>Table of contents</b></a>
</td><td align="center" width="35%">
<a href="#STATUS"><b>Next : Jobs status</b></a>
</td></tr>
</table>
</div>

<br><br>

<li><p class="SOUSTITRE">Submitting jobs</p>

<?php
  include "storage.php";
?>
<br><br>

<ul>
<li>Using the client application&nbsp;:
<br><br>

<blockquote>

Job submission can only refer <a href="#APPS">to installed applications</a> 
(i.e. you can only run a job for applications that are already installed on server).
<br><br>
The client expects the parameters <code>--xwsubmit <i>appName</i></code> to submit a new
job for the installed application which is registered on server side with its name as <i>appName</i>.
<br><br>
Three kinds of information can be provided to jobs&nbsp;<i>(let suppose the application 
&quot;ls&quot;is installed on server side)</i>&nbsp;:
<br>

</blockquote>

<center>
<table bgcolor="black">
<tr>
<td>
<br>
The server keeps jobs until the user explicitly <a href="#CANCEL">remove</a> them.
<br>
It is then the user responsability to ensure its jobs don't fill full the server.
<br><br>
</td>
</tr>
</table>
</center>

<br><br>

<center>
<table width="100%">
<tr>
<td class="CONTENT2" align="center">#</td>
<td width="15%" class="CONTENT2" align="center">information types</td>
<td width="60%" class="CONTENT2" align="center">using a shell</td>
<td width="25%" class="CONTENT2" align="center">purpose</td>
</tr>
<tr>
<td class="CONTENT2" align="center">a</td>
<td width="15%" class="CONTENT1" align="center">
arguments
</td>
<td width="60%" class="CONTENT1" align="center">
<code> ls <b><u>-l</u></b></code>
</td>
<td width="25%" class="CONTENT1" align="center">
command line arguments interpreted by the applications
</td>
</tr>

<tr>
<td class="CONTENT2" align="center">b</td>
<td width="15%" class="CONTENT1" align="center">
standard input
</td>
<td width="60%" class="CONTENT1" align="center">
<code> ls <b><u>&lt; txtFile</u></b></code>
</td>
<td width="25%" class="CONTENT1" align="center">
standard input replaces the keyboard and is interpreted
by the application
</td>
</tr>

<tr>
<td class="CONTENT2" align="center">c</td>
<td width="15%" class="CONTENT1" align="center">
environment
</td>
<td width="60%" class="CONTENT1" align="center">
<code> ( <b><u>cd aDirectory</u></b> &amp;&amp; ls  </code> ) 
</td>
<td width="25%" class="CONTENT1" align="center">
the environment is the directory structure from
which the application is run
</td>
</tr>

<tr>
<td class="CONTENT2" align="center">d</td>
<td width="15%" class="CONTENT1" align="center">
all together
</td>
<td width="60%" class="CONTENT1" align="center">
<code> ( <b><u>cd aDirectory</u></b> &amp;&amp; ls  <b><u>-l &lt; txtFile</u></b> </code> ) 
</td>
<td width="25%" class="CONTENT1" align="center">
&nbsp;
</td>
</tr>

<tr>
<td class="CONTENT2" align="center"></td>
<td width="15%" class="CONTENT2" align="center">information types</td>
<td width="60%" class="CONTENT2" align="center">using the XtremWeb client</td>
<td width="25%" class="CONTENT2" align="center">purpose</td>
</tr>
<tr>
<td class="CONTENT2" align="center">a</td>
<td width="15%" class="CONTENT1" align="center">
arguments
</td>
<td width="60%" class="CONTENT1" align="center">
<code> xwclient --xwsubmit ls <b><u>-l</u></b></code>
</td>
<td width="25%" class="CONTENT1" align="center">
command line arguments interpreted by the applications
</td>
</tr>

<tr>
<td class="CONTENT2" align="center">b</td>
<td width="15%" class="CONTENT1" align="center">
standard input
</td>
<td width="60%" class="CONTENT1" align="center">
<code> xwclient --xwsubmit ls <b><u>&lt; txtFile</u></b></code>
</td>
<td width="25%" class="CONTENT1" align="center">
standard input replaces the keyboard and is interpreted
by the application
</td>
</tr>

<tr>
<td class="CONTENT2" align="center">c</td>
<td width="15%" class="CONTENT1" align="center">
environment
</td>
<td width="60%" class="CONTENT1" align="center">
<code> xwclient --xwsubmit ls <b><u>--xwenv aDirectory</u></b></code> 
</td>
<td width="25%" class="CONTENT1" align="center">
the environment is the directory structure from
which the application is run
</td>
</tr>

<tr>
<td class="CONTENT2" align="center">d</td>
<td width="15%" class="CONTENT1" align="center">
all together
</td>
<td width="60%" class="CONTENT1" align="center">
<code> xwclient --xwsubmit ls <b><u>-l --xwenv aDirectory &lt; txtFile</u></b></code> 
</td>
<td width="25%" class="CONTENT1" align="center">
&nbsp;
</td>
</tr>

</table>
</center>

<blockquote>

<br><br>

</blockquote>

<br>

<li>Using the <code>xwsubmit</code> script&nbsp;:<br>
<br>
The <code>xwsubmit</code> encapsulates the needed arguments for you.
<br><br>
Examples&nbsp;:<br>
<blockquote>
<code>
xwsubmit etc.
<br>
xwsubmit --xwconfig &lt;configFile&gt etc.
</code>
</blockquote>

<br>
<li>Writing a dedicated script&nbsp;:<br>
<br>
<a href="./textes/randomshowers.sh">
Here is a shell script example to encapsulate job submission.
</a>
<br><p align="center">(right click to <i>save as</i> this file)</p>
<br><br>


<li>Using the web site&nbsp;:
<br><br>
<blockquote>

<?php
if (isset ($V_Session)) {
?>

A <a href="./index.php?section=section2&subsection=submitTask">dedicated page</a>
helps to submit job.<br>
You first see the list of installed application in this server among which you have
to choose the one you want to execute.<br>
Accordingly to your needs, you have to fill the form shown in the next page&nbsp;; please,
read the provided examples carefully.<br>
<br>
Click the <code>Submit</code> button to finally submit your job.

<?php
} else {
?>

To submit jobs, you must <a href="./index.php?section=section2&subsection=login">log in<a> first.

<?php
}
?>

</blockquote>

</ul>

<br><br>

<a name="STATUS"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="10%">
<a href="#CLIENTUSAGE"><b>Up : client usage</b></a>
</td>
<td align="center" width="35%">
<a href="#SUBMIT"><b>Previous : Submitting jobs</b></a>
</td><td align="center" width="20%">
<a href="#TOC"><b>Table of contents</b></a>
</td><td align="center" width="35%">
<a href="#RESULTS"><b>Next : Jobs results</b></a>
</td></tr>
</table>
</div>

<br><br>

<li><p class="SOUSTITRE">Retrieving jobs status</p>

<?php
  include "storage.php";
?>
<br><br>

<ul>
<li>Using the client application&nbsp;:
<br><br>

<blockquote>


The XtremWeb platform gives every object an unique identifier (<code>UID</code>).
<br>

Every submitted job has then an <code>UID</code> which is expected for any job manipulation.

<br><br>

The client expects the parameters <code>--xwstatus [<i>UID UID ...</i>]</code> to retrieve
user's jobs status
<br><br>
<center>
<table bgcolor="#f47a07">
<tr>
<td>
<br>
Provided <code>UIDs</code> determine the jobs to retrieve status for.
<br>
Requesting status with no <code>UID</code> retrieve all user's jobs status.
<br><br>
</td>
</tr>
</table>
</center>

<br><br>
</blockquote>
Examples&nbsp;:<br>
<blockquote>
<code>
xwclient --xwstatus
<br>
xwclient --xwconfig &lt;configFile&gt --xwstatus
</code>
</blockquote>

</blockquote>
<br><br>

<li>Using the <code>xwstatus</code> script&nbsp;:<br>
<br>
The <code>xwstatus</code> encapsulates the needed arguments for you.
<br><br>
Examples&nbsp;:<br>
<blockquote>
<code>
xwstatus
<br>
xwstatus --xwconfig &lt;configFile&gt
</code>
</blockquote>
<br>


<li>Using the web site&nbsp;:
<br><br>
<blockquote>

<?php
if (isset ($V_Session)) {
?>

A <a href="./index.php?section=section3&subsection=userStats">dedicated page</a>
helps to retrieve job status.<br>

<?php
} else {
?>

To retrieve your jobs status , you must <a href="./index.php?section=section2&subsection=login">log in<a> first.

<?php
}
?>

</blockquote>

</ul>

<br><br>

<a name="RESULTS"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="10%">
<a href="#CLIENTUSAGE"><b>Up : client usage</b></a>
</td>
<td align="center" width="35%">
<a href="#STATUS"><b>Previous : Jobs status</b></a>
</td><td align="center" width="20%">
<a href="#TOC"><b>Table of contents</b></a>
</td><td align="center" width="35%">
<a href="#CANCEL"><b>Next : Cancelling jobs</b></a>
</td></tr>
</table>
</div>

<br><br>

<li><p class="SOUSTITRE">Retrieving jobs results</p>

<?php
  include "storage.php";
?>

<br><br>

<ul>
<li>Using the client application&nbsp;:
<br><br>

The XtremWeb platform gives every object an unique identifier (<code>UID</code>).
<br>

Every submitted job has then an <code>UID</code> which is expected for any job manipulation.

<br><br>

The client expects the parameters <code>--xwresult [<i>UID UID ...</i>]</code> to retrieve
user's jobs results.
<br><br>
<center>
<table bgcolor="#f47a07">
<tr>
<td>
<br>
Provided <code>UIDs</code> determine the jobs to retrieve results for.
<br>
Requesting results with no <code>UID</code> retrieve all user's jobs results.
<br><br>
</td>
</tr>
</table>
</center>

<br><br>

Examples&nbsp;:<br>
<blockquote>
<code>
xwclient --xwresult
<br>
xwclient --xwconfig &lt;configFile&gt --xwresult
</code>
</blockquote>

</blockquote>
<br><br>

<li>Using the <code>xwresult</code> script&nbsp;:<br>
<br>
The <code>xwresult</code> encapsulates the needed arguments for you.
<br><br>
Examples&nbsp;:<br>
<blockquote>
<code>
xwresult
<br>
xwresult --xwconfig &lt;configFile&gt
</code>
</blockquote>
<br>


<li>Using the web site&nbsp;:
<br><br>
<blockquote>

<?php
if (isset ($V_Session)) {
?>

A <a href="./index.php?section=section3&subsection=userStats">dedicated page</a>
helps to retrieve job results.<br>

<?php
} else {
?>

To retrieve your jobs results , you must <a href="./index.php?section=section2&subsection=login">log in<a> first.

<?php
}
?>

</blockquote>

</ul>

<br><br>

<a name="CANCEL"></a>
<hr width="80%">
<div align="center">
<table width="80%" cellspacing="10">
<tr>
<td align="center" width="10%">
<a href="#CLIENTUSAGE"><b>Up : client usage</b></a>
</td>
<td align="center" width="35%">
<a href="#RESULTS"><b>Previous : Jobs results</b></a>
</td><td align="center" width="20%">
<a href="#TOC"><b>Table of contents</b></a>
</td>
<td align="center" width="35%">
&nbsp;
</td>
</tr>
</table>
</div>

<br><br>

<li><p class="SOUSTITRE">Cancelling jobs</p>

<?php
  include "storage.php";
?>
<br><br>

<ul>
<li>Using the client application&nbsp;:
<br><br>
The XtremWeb platform gives every object an unique identifier (<code>UID</code>).
<br>

Every submitted job has then an <code>UID</code> which is expected for any job manipulation.

<br><br>

The client expects the parameters <code>--xwrm [<i>UID UID ...</i>]</code> to delete 
user's jobs from the platform.
<br><br>


<center>
<table bgcolor="#f47a07">
<tr>
<td>
<br>
Provided <code>UIDs</code> determine the jobs to delete.
<br>
Requesting to delete jobs with no <code>UID</code> <span style="color:red">delete <b><u>all</b></u></span> user's jobs.
<br><br>
</td>
</tr>
</table>
</center>

<br><br>

Example&nbsp;:<br>
<blockquote>
<code>
xwclient --xwrm
<br>
xwclient --xwconfig &lt;configFile&gt --xwrm
</code>
</blockquote>

</blockquote>
<br><br>

<li>Using the <code>xwrm</code> script&nbsp;:<br>
<br>
The <code>xwrm</code> encapsulates the needed arguments for you.
<br><br>
Examples&nbsp;:<br>
<blockquote>
<code>
xwrm
<br>
xwrm --xwconfig &lt;configFile&gt
</code>
</blockquote>
<br><br>


<li>Using the web site&nbsp;:
<br><br>
<blockquote>

<?php
if (isset ($V_Session)) {
?>

A <a href="./index.php?section=section3&subsection=userStats">dedicated page</a>
helps to cancel jobs.<br>

<?php
} else {
?>

To cancel your jobs , you must <a href="./index.php?section=section2&subsection=login">log in<a> first.

<?php
}
?>

</blockquote>

</ul>

<br><br>


<!-- nummering sub sections -->
</ol>

<!-- nummering chapters -->
</ol>

<br><br><br>
<hr width="80%">
<br><br><br>
<div align="center"><a href="#"><b>Top</b></a></div>

<HR SIZE=1>
<BR>  
<FONT SIZE="-1">
This document was written
by <I>Oleg Lodygensky</I> on <I>July, 1st  2004</I>


</ol>

</body>
</html>
