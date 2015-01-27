<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
<?php
  if (!isset ($WORKER))
    echo "<meta http-equiv=\"Refresh\" content=\"300; URL=".$GLOBALS["XWROOTPATH"]."index.php?section=section3&subsection=workerStats\">\n";
  else
    echo "<meta http-equiv=\"Refresh\" content=\"300; URL=".$GLOBALS["XWROOTPATH"]."index.php?section=section3&subsection=workerStats&WORKER=".$WORKER."\">\n";
?>

</head>

<body>

<script language="JavaScript" type="text/javascript" src="./scripts/xtremweb.js">
</script>

<br><br>
<blockquote>
<p class="TITRE">Grid statistics</p>
<HR width="100%">


<?php
  include "php/config/library.php";
  include "php/db/library.php";
  include "php/session/library.php";
  include "php/html_graph/html_graphs.php3";
  include "php/html_graph/library.php";

  if (isset($_COOKIE["V_TableName"]))
    $V_TableName = $_COOKIE["V_TableName"];

  if (isset($_COOKIE["sessionLogin"]))
    $V_Login = $_COOKIE["sessionLogin"];
  if (isset($_COOKIE["sessionFName"]))
    $V_FName = $_COOKIE["sessionFName"];
  if (isset($_COOKIE["sessionLName"]))
    $V_LName = $_COOKIE["sessionLName"];
  if (isset($_COOKIE["sessionID"]))
    $V_Session = $_COOKIE["sessionID"];

?>

<center>

<a name="JOBWORKER">

<?php
  if (!isset ($WORKER)) {
?>
<br>
<div>
<u>Note</u>
<br>
Next stats are dynamically extracted and represent data still
in the server
<br>
(i.e. removed jobs are out of these statistics)
</div>
<br><br><br>

<?php
  }
?>

<table width="92%" border="0">
 <tr>
  <td align="center" >
<?php
  if (!isset ($WORKER)) {
?>
    <h3><a href="#WORKERDAY">Job/worker/day</a></h3>
<?php
  }
  else {
?>
    <h3><a href="index.php?section=section3&subsection=workerStats">Back to workers stats</a></h3>
<?php
  }
?>
  </td>
  <td align="center">
<?php
  if (!isset ($WORKER)) {
?>
    <h3><a href="#DAYWORKER">Job/day/worker</a></h3>
<?php
  }
?>
  </td>
 </tr>
</table>
<br>

<table width="92%" border="0">
 <tr>
  <td>

   <p align="center">
   <table border="0" cellspacing="0" width="100%">
<?php
  if (!isset ($WORKER)) {
?>
    <tr>
    <th align="center" valign="center" bgcolor="#ffffff" colspan="4">
    <font color="black">
      <h2>Jobs/workers&nbsp;
<input type="submit" value="*"
onClick="myRef = window.open('./textes/help.html','XtremWeb Help',
'left=20,top=20,width=600,height=300,toolbar=1,resizable=0');
myRef.focus()">
</h2>
      <h6>COMPLETED (RUNNING)</h6>
      <br>
    </font>
    </th>
    </tr>
<?php
  }
?>

<?php

    $connection = dbConnect ();

    if (isset ($WORKER)) {
      $queryWorker = "select uid,name,lastAlive,available,active,cputype,cpunb,cpuspeed,os,now()-lastAlive as delai from hosts where uid='".$WORKER."'";
      $resultWorker = dbQuery ($connection, $queryWorker);
      $rowsWorker= mysql_fetch_array($resultWorker);

      $couleur = "#16e82a";
      if ($rowsWorker["available"] == "false")
        $couleur = "#b48622";
      if ($rowsWorker["active"] == "false")
        $couleur = "gray";
      if (($rowsWorker["delai"] == "") || ($rowsWorker["delai"] > 4500))
        $couleur = "red";
?>
<input type="submit" value="Color conventions"
onClick="myRef = window.open('./textes/help.html','XtremWeb Help',
'left=20,top=20,width=600,height=300,toolbar=1,resizable=0');
myRef.focus()">
<br><br>
<?php
      echo "<tr><td colspan=\"4\"><center><font color=\"".$couleur."\">";
      echo $rowsWorker["name"]."&nbsp;:&nbsp;";
      echo "<i>".$rowsWorker["os"].";&nbsp;".$rowsWorker["cputype"].";&nbsp;".$rowsWorker["cpuspeed"]."Mhz&nbsp;<br>";
      echo "</i><u>Ping</u>&nbsp;:&nbsp;<i>".$rowsWorker["lastAlive"]."</i>";


      echo "<br><br></font></center></td></tr>";
      echo "<tr><td align=\"center\"><u>Application</u></td><td align=\"center\"><u>Start date</u></td><td align=\"center\"><u>Completion date</u></td><td align=\"center\"><u>Status</u></td></tr>\n";

      $queryWorker = "select apps.name as app,tasks.laststartdate as sdate,tasks.removaldate as cdate,tasks.status as status from hosts,tasks,apps";
      $queryWorker = $queryWorker." where tasks.host=hosts.uid and hosts.uid='".$WORKER."'";
      $queryWorker = $queryWorker." and tasks.app=apps.uid order by tasks.laststartdate";

      $resultWorker = dbQuery ($connection, $queryWorker);
      $i = 0;
      while ($rowsWorker= mysql_fetch_array($resultWorker)) {
        echo "<tr>\n";
        echo "<td>".$rowsWorker["app"]."</td>\n";
        echo "<td>".$rowsWorker["sdate"]."</td>\n";
        if (isset ($rowsWorker["cdate"]))
          echo "<td>".$rowsWorker["cdate"]."</td>\n";
        else
          echo "<td align=\"center\"><i>n/a</i></td>\n";
        echo "<td>".$rowsWorker["status"]."</td>\n";
        echo "</tr>\n";
      }

      echo "</table></code></html>";
      echo "<br><br><center><h3><a href=\"index.php?section=section3&subsection=workerStats\">Back to workers stats</a></h3></center>\n";

      return;

    } // end of if (isset($WORKER))


/*
    $queryHost = "select *,now()-lastAlive as delai from hosts having delai<=4500";
    $queryHost = "select *,now()-max(lastAlive) as delai from hosts group by name,ipaddr";
*/
    $queryHost = "select *,unix_timestamp(now())-unix_timestamp(max(lastAlive)) as delai from hosts group by name,ipaddr";
    $queryHost = $queryHost." order by name";

    $resultHost = dbQuery ($connection, $queryHost);
    $i = 0;
    while ($rowsHost= mysql_fetch_array($resultHost)) {
      echo "<tr>\n";

      $queryRunning = "select count(*) from hosts,tasks";
/*
      $queryRunning = $queryRunning." where tasks.status='RUNNING' and tasks.host=hosts.uid and hosts.name='".$rowsHost["name"]."'";
*/
      $queryRunning = $queryRunning." where tasks.status='RUNNING' and tasks.host=hosts.uid and hosts.name='".$rowsHost["name"]."'";
      $resultRunning = dbQuery ($connection, $queryRunning);
      $rowsRunning = mysql_fetch_array($resultRunning);

/*
      $queryCompleted = "select count(*) from hosts,tasks";
    $queryCompleted = $queryCompleted." where tasks.status='COMPLETED' and tasks.host=hosts.uid and hosts.name='".$rowsHost["name"]."'";

      $resultCompleted = dbQuery ($connection, $queryCompleted);
      $rowsCompleted = mysql_fetch_array($resultCompleted);
*/

/*
      if(($rowsCompleted["count(*)"] < 1) &&
         ($rowsRunning["count(*)"]   < 1) &&
         ($rowsHost["delai"] > 4500)) {
        continue;
      }
*/

      $contentClass = "CONTENT".(($i % 2) + 1);
      echo "<td class=\"".$contentClass."\" width=\"15%\">\n<p>\n";
/*
      echo $rowsCompleted["count(*)"]."&nbsp;&nbsp;(".($rowsRunning["count(*)"] == "" ? 0 : $rowsRunning["count(*)"]).")&nbsp;";
*/
      echo ($rowsHost["nbJobs"] == "" ? 0 : $rowsHost["nbJobs"])."&nbsp;&nbsp;(".($rowsRunning["count(*)"] == "" ? 0 : $rowsRunning["count(*)"]).")&nbsp;";
      echo "</p></td>\n";

      echo "<td class=\"".$contentClass."\">\n<p>\n";

      $couleur[$rowsHost["name"]] = "#16e82a";
      if ($rowsHost["available"] == "false")
        $couleur[$rowsHost["name"]] = "#b48622";
      if ($rowsHost["active"] == "false")
        $couleur[$rowsHost["name"]] = "gray";
      if (($rowsHost["delai"] == "") || ($rowsHost["delai"] > 4500))
        $couleur[$rowsHost["name"]] = "red";

      echo "<a href=\"#".$rowsHost["name"]."\" style=\"color:".$couleur[$rowsHost["name"]]."\">".$rowsHost["name"]."</a></p></td>\n";

      echo "<td class=\"".$contentClass."\" width=\"5%\">\n<p>\n";
      echo "<a href=\"index.php?section=section3&subsection=workerStats&WORKER=".$rowsHost["uid"]."\" style=\"color:".$couleur[$rowsHost["name"]]."\">details</a>&nbsp;&nbsp;</p></td>\n";

      echo "<td class=\"".$contentClass."\" width=\"50%\">\n<p>\n";
      echo "<font size=\"3pt\"><i>".$rowsHost["os"]."&nbsp;; ".$rowsHost["cpunb"]." CPUs&nbsp;; ".$rowsHost["cpuspeed"]."Mhz&nbsp;\n";
      if (substr($rowsHost["ipaddr"], 0, 7) == "134.158") {
        if(substr($rowsHost["ipaddr"], 0, 10) == "134.158.92")
          echo "<a href=\"http://ipnweb.in2p3.fr/\" target=\"labs\">IPN</a>";
        if(substr($rowsHost["ipaddr"], 0, 11) == "134.158.121")
          echo "<a href=\"http://clrwww.in2p3.fr/\" target=\"labs\">LPC - Clermont Ferrand</a>";
        if((substr($rowsHost["ipaddr"], 8, 2) == "88") ||
           (substr($rowsHost["ipaddr"], 8, 2) == "89") ||
           (substr($rowsHost["ipaddr"], 8, 2) == "90") ||
           (substr($rowsHost["ipaddr"], 8, 2) == "91"))
          echo "<a href=\"http://www.lal.in2p3.fr\" target=\"labs\">LAL</a>";
      }
      else if (substr($rowsHost["ipaddr"], 0, 9) == "129.175.7")
        echo "<a href=\"http://www.lri.fr\">LRI</a>";
      else if (substr($rowsHost["ipaddr"], 0, 6) == "82.226")
        echo "<a href=\"http://www.proxad.net\">proxad.net</a>";
      else if (substr($rowsHost["ipaddr"], 0, 5) == "81.66")
        echo "<a href=\"http://www.noos.fr\">noos.fr</a>";
      else if (substr($rowsHost["ipaddr"], 0, 7) == "192.168")
        echo "private network";

      echo "</i></font></p></td>\n";

      echo "</p>\n";
      echo "</td>\n";

      echo "</tr>\n";

      $i++;
   }

?>

   </table>

<?php
  if (isset ($WORKER))
    return;
?>


   <br><br>

<a name="DAYWORKER">
<table width="92%" border="0">
 <tr>
  <td align="center" >
    <h3><a href="#">Job/worker</a></h3>
  </td>
  <td align="center">
    <h3><a href="#WORKERDAY">Job/worker/day</a></h3>
  </td>
 </tr>
</table>
<br>

   <table cellspacing="0" width="80%">
    <tr>
    <th align="center" valign="center" bgcolor="#ffffff" colspan="2">
    <font color="black">
    <h2>Jobs/day/worker
<input type="submit" value="*"
onClick="myRef = window.open('./textes/help.html','XtremWeb Help',
'left=20,top=20,width=600,height=300,toolbar=1,resizable=0');
myRef.focus()">
</h2>
    </font>
    </th>
    </tr>

<?php
    $connection = dbConnect ();
    $query = "select count(*),left(tasks.startdate,10),hosts.name as host,tasks.status from tasks,hosts where not isnull(host) and not isnull(startdate) and status='COMPLETED' and tasks.host=hosts.uid group by left(startdate,10),host order by left(startdate,10),host";
    $result = dbQuery ($connection, $query);
    $i = 0;
    $date="";

    while ($rows= mysql_fetch_array($result)) {
      echo "<tr>\n";

      if ($date != $rows["left(tasks.startdate,10)"]) {
        $date = $rows["left(tasks.startdate,10)"];
        echo "<tr>";
        echo "<td align=\"center\" valign=\"center\" bgcolor=\"#ffffff\" colspan=\"2\">\n";
        echo "<font color=\"black\"><h2>".$date."</h2></font>\n";
        echo "</td>\n";
        echo "</tr>\n";
      }

      $contentClass = "CONTENT".(($i % 2) + 1);
      echo "<td class=\"".$contentClass."\">\n";
      echo "<p class=\"".$contentClass."\">\n";

      echo $rows["host"];
      echo "</p>\n";
      echo "</td>\n";

      $contentClass = "CONTENT".(($i % 2) + 1);
      echo "<td class=\"".$contentClass."\">\n";
      echo "<p class=\"".$contentClass."\">\n";
      echo $rows["count(*)"];
      echo "</p>\n";
      echo "</td>\n";

?>
      </tr>

<?php

      $i++;
   }

?>
    </table>

   <br><br>

<a name="WORKERDAY">
<table width="92%" border="0">
 <tr>
  <td align="center" >
    <h3><a href="#">Job/worker</a></h3>
  </td>
  <td align="center">
    <h3><a href="#DAYWORKER">Job/day/worker</a></h3>
  </td>
 </tr>
</table>
<br>

   <table cellspacing="0" width="80%">
    <tr>
    <th align="center" valign="center" bgcolor="#ffffff" colspan="2">
    <font color="black">
    <h2>Jobs/worker/day
<input type="submit" value="*"
onClick="myRef = window.open('./textes/help.html','XtremWeb Help',
'left=20,top=20,width=600,height=300,toolbar=1,resizable=0');
myRef.focus()">
</h2>
    </font>
    </th>
    </tr>

<?php
    $connection = dbConnect ();
    $query = "select count(*),left(tasks.startdate,10),hosts.name as
host,tasks.status from tasks,hosts where not isnull(host) and not isnull(startdate) and status='COMPLETED' and tasks.host=hosts.uid group by host,left(startdate,10) order by host";
    $result = dbQuery ($connection, $query);
    $i = 0;
    $worker="";

    while ($rows= mysql_fetch_array($result)) {
      echo "<tr>\n";

      if ($worker != $rows["host"]) {
        $worker = $rows["host"];
        echo "<tr>";

        echo "<td align=\"center\" valign=\"center\" bgcolor=\"#ffffff\" colspan=\"2\">\n";
        echo "<a name=\"".$worker."\"></a>\n";
        echo "<font color=\"".$couleur[$rows["host"]]."\"><h2>".$worker."&nbsp;&nbsp;<a href=\"#\"><font color=\"black\">top</font></a></h2></font>\n";
        echo "</td>\n";
        echo "</tr>\n";
      }

      $contentClass = "CONTENT".(($i % 2) + 1);
      echo "<td class=\"".$contentClass."\">\n";
      echo "<p class=\"".$contentClass."\">\n";
      echo $rows["left(tasks.startdate,10)"];
      echo "</p>\n";
      echo "</td>\n";

      $contentClass = "CONTENT".(($i % 2) + 1);
      echo "<td class=\"".$contentClass."\">\n";
      echo "<p class=\"".$contentClass."\">\n";
      echo $rows["count(*)"];
      echo "</p>\n";
      echo "</td>\n";

?>
      </tr>

<?php

      $i++;
   }

?>
    </table>
    </p>
  </td>
 </tr>
</table>

<table width="92%" border="0">
 <tr>
  <td align="center" >
    <h3><a href="#">Job/worker</a></h3>
  </td>
  <td align="center">
    <h3><a href="#DAYWORKER">Job/day/worker</a></h3>
  </td>
  <td align="center">
    <h3><a href="#WORKERDAY">Job/worker/day</a></h3>
  </td>
 </tr>
</table>
<br>


</center>
</blockquote>
</body>
</html>

