<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
<?php
 echo "<meta http-equiv=\"Refresh\" content=\"300; URL=".$GLOBALS["XWROOTPATH"]."index.php?section=section3&subsection=serverStats\">\n";
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

  /*
  $tableNames = array ("apps", "hosts", "tasks", "works", "users");
  $tableTexts = array (" applications installed."," known workers."," managed tasks."," managed works."," users.");
  */
  $tableNames = array ("apps", "tasks", "users");
  $tableTexts = array (" applications installed."," managed tasks."," users.");


  if (!isset ($V_TableName)) {
    for ($i = 0; $i < count ($tableNames); $i++) {
      $nbRows [$i] = dbCountRows ($tableNames [$i]);
    }
  }

?>

<center>
<!--
<a href="./pics/lal.png">
<img src="pics/lal.png" alt="click to enlarge" width="40%" length="40%">
</a>
-->


<table width="92%" border="0">
 <tr>
  <td>

   <p align="center">
   <table>
<?php
  if (isset ($V_TableName)) {
?>
   <tr>
     <td>
        <form name="select1" action="javascript:void(0)" onsubmit="return false">
        <select name="filter" onchange="makeSelection();">
            <option value="" selected="selected">Select:</option>
            <option value="!0">All</option>
            <option value="0">None</option>
        </form>
     <td>
     <td>

<?php
       echo "<form method=\"post\" action=\"".$GLOBALS["XWROOTPATH"]."index.php?section=section3&subsection=serverStats\">\n";
?>

	  <input type="SUBMIT" value="  Back to stats main page  ">
       </form>
       <br>
     </td>
   </tr>
<?php
  }
?>
    <tr>
     <td>
<?php
  echo date ("F d, Y - H:i");
?>
     </td>
    </tr>
   </table>
   </p>

  </td>
 </tr>

 <tr>
  <td>
   <br>

   <p>
  </td>
 </tr>
</table>



<table width="92%" border="0">
 <tr>
  <td>

   <p align="center">
   <table cellspacing="1" border="1" bordercolor="lightblue" width="60%">
<?php

        /* a specific table has been selected */

  if (isset ($V_TableName)) {
    echo "<form name=\"sqlRows\">";
    dbDisplayTable ($V_TableName);
    echo "</form>";
  }
  else {

        /* no specific table selected */

    $contentClass = "CONTENT2";
    echo "<td class=\"".$contentClass."\">\n";
    echo "<p align=\"center\">\n";
    $connection = dbConnect ();
    $query = "select sum(nbjobs) from apps";
    $result = dbQuery ($connection, $query);
    $nbjobs= mysql_fetch_array($result);
    echo "<br><u><font size=\"+2\" color=\"lightgreen\">";
    echo $nbjobs[0]." jobs already executed";
    echo "</font></u><br><br>\n";
    echo "</p></td></tr>\n";
    echo "<tr>\n";
    echo "<td class=\"".$contentClass."\">\n";

    /* 
		 * Available giga hertz
		 * This should be simplyfied by the inner SQL query
		 * select sum(gigaTotal) from \
		 * (select cpuspeed*cpunb/1000 as gigaTotal from hosts group by name,ipaddr) as gigaTotal;
		 *
		 * But this is only understood by MySQL >= 4.1
		 * :(
		 */
							
    $queryGigaTotal = "select cpuspeed*cpunb/1000  as gigaTotal from hosts group by name,ipaddr";
    $resultGigaTotal = dbQuery ($connection, $queryGigaTotal);
		$gigaTotal = 0;
    while ($rowsGigaTotal = mysql_fetch_array($resultGigaTotal)) {
     	$gigaTotal += $rowsGigaTotal["gigaTotal"];
		}

    $queryGigaDispo = "select cpuspeed*cpunb/1000  as gigaDispo from hosts".
    $queryGigaDispo = $queryGigaDispo." where active='true' and available='true' and now()-lastAlive < 4500 and not isnull(lastAlive) group by name,ipaddr";
    $resultGigaDispo = dbQuery ($connection, $queryGigaDispo);
		$gigaDispo = 0;
    while ($rowsGigaDispo = mysql_fetch_array($resultGigaDispo)) {
    	$gigaDispo += $rowsGigaDispo["gigaDispo"];
		}

    $queryAvailableWorkers = "select count(*) from hosts group by name,ipaddr";
    $resultAvailableWorkers = dbQuery ($connection, $queryAvailableWorkers);
		$nbWorkers = 0;
    while ($rowsAvailableWorkers = mysql_fetch_array($resultAvailableWorkers)) {
    	$nbWorkers++;
		}

    $percentDispo = intval ($gigaDispo / $gigaTotal * 100);
    echo "<table width='100%'><tr><td height='25' bgcolor='green' width='".$percentDispo."%'>";
    if ($percentDispo >= 50)
      echo "<p class='".$contentClass."'>".$nbWorkers." workers ($percentDispo% ready).</p>";
    echo "</td>";

    $diffDispo=100-$percentDispo;
    if ($diffDispo > 0) {
      echo "<td width='".$diffDispo."%' bgcolor='red'>";
      if ($percentDispo < 50)
        echo $nbRows[$i].$tableTexts[$i]." ($percentDispo% ready).";
      echo "</td>";
    }
    echo "</td></tr></table></td></tr><tr>\n";

    for ($i = 0; $i < count($nbRows); $i++) {
      $contentClass = "CONTENT".(($i % 2) + 1);
      $contentClass = "CONTENT2";
      echo "<tr>\n";
      echo "<td class=\"".$contentClass."\">\n";

      echo "<p class=\"".$contentClass."\">\n";
      echo $nbRows[$i].$tableTexts[$i];
      echo "</p>\n";


      echo "</td>\n";

      if ((isprivileged ($V_Login, $V_Session)) && ($nbRows[$i] > 0)) {
        echo <<< EOF
        <td>
         <p valign="center">
EOF;
        echo "<form method=\"POST\" action=\"".$GLOBALS["XWROOTPATH"]."index.php\">\n";
        echo <<< EOF
          <input type="HIDDEN" name="section" value="section3">
          <input type="HIDDEN" name="subsection" value="serverStats">
	  <input type="HIDDEN" name="V_TableName" value="$V_TableName">
	  <input type="SUBMIT" value="  Edit  ">
         </form>
         </p>
        </td>
EOF;
      }
      echo "</tr>";
   }


               /* Tasks graphs*/
?>
      <table>
       <tr>
        <td align="center">
<br><br>
<br>
<div>
<u>Note</u>
<br>
Next stats are dynamically extracted and represent data still
in the server
<br>
(i.e. removed jobs are out of these statistics)
</div>
<br><br>

                <!-- Tasks graphs legend -->
      <tr >
        <td valign="center" align="left">
<h1>Tasks</h1>
         <table width="30%" >
          <tr valign="center">
           <th width="50pt">
             &nbsp;
           </th>
           <th bgcolor="white" colspan="2" align="center" valign="center">
            <u><font color="black"><h4>Legend</h4></font></u>
           </th>
          </tr>
          <tr>
           <td width="50pt">
             &nbsp;
           </td>
           <td>
             completed tasks
           </td>
           <td>
             <img src="pics/rainbow_blue.png"><br>
           </td>
          </tr>
          <tr>
           <td width="50pt">
             &nbsp;
           </td>
           <td>
             error tasks
           </td>
           <td>
             <img src="pics/rainbow_red.gif"><br>
           </td>
          </tr>
         </table>
        </td>
       </tr>
      </table>

<?php
    graphTaskLastWeek();
    graphTaskLastMonth();
    graphTaskLastYear();
?>
        </td>
      </tr>


       <!-- Hosts graphs -->
      <table>
       <tr>
        <td align="center">
                <!-- Hosts graphs legend -->
<hr width="100%" />
<h1>Hosts</h1>
         <table width="30%">
          <tr valign="center">
           <th width="50pt">
             &nbsp;
           </th>
           <th bgcolor="white" colspan="2" align="center" valign="center">
            <u><font color="black"><h4>Legend</h4></font></u>
           </th>
          </tr>
          <tr>
           <td width="50pt">
             &nbsp;
           </td>
           <td>
             computing resource
           </td>
           <td>
             <img src="pics/rainbow_blue.png"><br>
           </td>
          </tr>
         </table>
        </td>
       </tr>
      </table>
<?php

/*
    if (graphHostLastHour () == 0)
      $displayLegend = 1;
    echo "<br>";
    if (graphHostLastDay () == 0)
      $displayLegend = 1;
    echo "<br>";
*/
    graphHostLastWeek();
    graphHostLastMonth();
    graphHostLastYear();

?>
        </td>
      </tr>


<?php

   }
?>
    </table>
    </p>
  </td>
 </tr>
</table>


</center>
</blockquote>
</body>
</html>

