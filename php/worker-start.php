<!--
Author  : Oleg Lodygensky
Date    : 15 Septembre 2008
Purpose : this is a documentation page for the worker
          this specifically aims to easily get access to statitics page  (see line 98)
-->

<html>

<head>

<title>XWHEP 3.0.0</title>

</head>

<body>

<h1 align="right">XWHEP 3.0.0</H1>

<hr align="center" width="50%"/>

<p>
This page contains some informations on XWHEP worker.
<br />
To get more complete informations about XWHEP, please refer to the <a href="http://dghep.lal.in2p3.fr/spip.php?rubrique16">XWHEP site</a>.
</p>

<hr align="center" width="50%"/><br />

<p>
<h2>Table of contents</h2>
<ul>
<li> <a href="#INSTALL">What have been installed in your machine</a>&nbsp;;
<li> <a href="#UNINSTALL">How to uninstall the XWHEP worker from your machine</a>&nbsp;;
<li> <a href="#STATS">How to get your machine desktop grid statistics</a>.
</ul>
</p>

<hr align="center" width="50%"/>
<p>
<a name="INSTALL" />
<h2>What have been installed in your machine.</h2>
</p>

<p>
The XWHEP worker is installed as service, launched everytime your machine starts.
</p>

<h3>Linux</h3>
<blockquote>
The package is in <b>/opt/XWHEP-worker-3.0.0</b>.
<br />
The service is started from <b>/etc/init.d</b>
</blockquote>

<h3>Mac OS X</h3>
<blockquote>
The package is in <b>/Applications/xtremweb.worker</b>.
<br />
The service is started from <b>/Lirary/StartupItems/xtremweb.worker</b>
</blockquote>

<h3>Windows</h3>
<blockquote>
The package is in <b>\Program Files\xtremweb worker\</b> this page.
</blockquote>



<hr align="center" width="50%"/>

<p>
<a name="UNINSTALL" />
<h2>How to uninstall the XWHEP worker from your machine.</h2>
</p>

<p>
<h3>Mac OS X</h3>
<blockquote>
You can run /Applications/xtremweb.worker/xtremweb.uninstall.pkg and follow the instructions.
</blockquote>

<h3>Windows</h3>
<blockquote>
Please use the standard &quot;add/remove application&quot; in the configuration panel.
</blockquote>
</p>

<hr align="center" width="50%"/>

<p>
<a name="STATS" />
<h2>How to get your desktop grid involvement.</h2>
</p>

<p>
<?php
echo "Please connect to the <a href=\"http://dghep.lal.in2p3.fr/ganglia/?c=XWHEP&h=".$_SERVER["REMOTE_HOST"]."_".$_SERVER["REMOTE_ADDR"]."\">statistics site</a>.<br />";
?>

You can also connect to the <a href="http://xtremweb.lal.in2p3.fr/ganglia">statistics main page</a>.</p>


<p>&nbsp;<br /><br /><br /></p>

</body>
</html>

