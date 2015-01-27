<?php

/*
 * File   : login.php
 * Date   : May 26th, 2003
 * Author : Oleg Lodygensky
 * Email  : lodygens@lal.in2p3.fr
 */

  include "./php/file/error.php";
  include "./php/config/library.php";
  include "./php/db/library.php";
  include "./php/session/library.php";

  if (! isset ($V_Login))
    if (isset ($_POST["V_Login"]))
      $V_Login = $_POST["V_Login"];

  if (! isset($V_Password))
    if (isset ($_POST["V_Password"]))
      $V_Password = $_POST["V_Password"];

  $V_Session = "";
  if (isset($V_Password) && isset($V_Login))
    $V_Session = sessionOpen ($V_Login, $V_Password, $V_FName, $V_LName);

  if ($V_Session != "") {

    $V_Password = codePassword($V_Password);

    setcookie("sessionID",$V_Session, time () + 3600);  // validity : 1 hour 
    setcookie("sessionLogin",$V_Login, time () + 3600);  // validity : 1 hour 
    setcookie("sessionPasswd", $V_Password, time () + 3600);  // validity : 1 hour 
    setcookie("sessionFName",$V_FName, time () + 3600);  // validity : 1 hour 
    setcookie("sessionLName",$V_LName, time () + 3600);  // validity : 1 hour 

/*
 * prepare client so that user can immediatly work with the platform
 */
/*
    if (!file_exists ("download/".$V_Login)) {
      mkdir ("download/".$V_Login, 0770) || die ("can't create directories");
    }
    if (!file_exists ("download/".$V_Login."/data")) {
      mkdir ("download/".$V_Login."/data", 0770) || die ("can't create subdirectories");
    }

    $jarFile = "xtremweb.jar";
    copy ("download/".$jarFile, "download/".$V_Login."/".$jarFile) || die ("can't create jar file");
    copy ("download/data/config.defaults", "download/".$V_Login."/data/config.defaults") || die ("can't create data");

    $xwrcFile = $V_Login."/data/config.defaults";
    if (file_exists ("download/".$xwrcFile))
      unlink ("download/".$xwrcFile);

    $fp = fopen ("download/".$xwrcFile, "w");
    fwrite ($fp, "# worker default config\n\n");

    fwrite ($fp, "# default dispatcher, try localhost\n");
    fwrite ($fp, "dispatcher.host=".configGetDispatcherHost ()."\n");
    fwrite ($fp, "# Login and password to connect to the server (required)\n");
    fwrite ($fp, "login=".$V_Login."\n");
    fwrite ($fp, "password=".$V_Passwd."\n");
    fwrite ($fp, "\n");
    fwrite ($fp, "update.restartCommand=java -jar $jarFile\n");
    fwrite ($fp, "\n");
    fwrite ($fp, "\n");
    fwrite ($fp, "# worker activation\n");
    fwrite ($fp, "# name of the class used as an activator\n");
    fwrite ($fp, "activator.class=xtremweb.worker.AlwaysActive\n");
    fwrite ($fp, "\n");
    fwrite ($fp, "# sandboxing\n");
    fwrite ($fp, "sandox.enable=false\n");
    fwrite ($fp, "\n");
    fwrite ($fp, "# tracer\n");
    fwrite ($fp, "tracer.enable=false\n");
    fwrite ($fp, "\n");
    fwrite ($fp, "#### log4j config\n");
    fwrite ($fp, "#\n");
    fwrite ($fp, "\n");
    fwrite ($fp, "# by default send all the logs to standard output\n");
    fwrite ($fp, "# Output levels are : debug, info, warn, error\n");
    fwrite ($fp, " log4j.rootLogger=info, CONSOLE\n");
    fwrite ($fp, " log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender\n");
    fwrite ($fp, " log4j.appender.CONSOLE.target=System.out\n");
    fwrite ($fp, " log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout\n");
    fwrite ($fp, " log4j.appender.CONSOLE.layout.ConversionPattern=%d{dd/MM/yy HH:mm} [%9c] (%t) %5p %m%n\n");
    fclose ($fp);

    $execCmd = "download/make-standalone-worker.sh -v -d ".$V_Login." -f ".$jarFile;

    exec("$execCmd 2>&1", $output, $execReturn);

    if ($execReturn != 0) {

      foreach($output as $outputline){
        echo("$outputline<br>");
      }
      echo "<html><body><br><br>";
      echo "<p class=\"SOUSTITRE\">An error occured!!!<br>Please contact the administrator</p></body></htm>";
    }
		else {
*/

			echo <<< EOF
				<html>
				<head>
				<META HTTP-EQUIV="Refresh" CONTENT="0; URL=./index.php">
				</head>
				</html>
EOF;
//		}

  }
  else
  {
?>

<html>
<head>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<title>
XtremWeb Desktop Grid user connection
</title>
</head>

<body onload="document.login_form.V_Login.focus();">


<BR><BR><BR>

<?php
 echo "<FORM METHOD=\"POST\" ACTION=\"".$GLOBALS["XWROOTPATH"]."index.php\" NAME=\"login_form\">\n";
?>

   <INPUT TYPE="HIDDEN" NAME="section" VALUE="section2">
   <INPUT TYPE="HIDDEN" NAME="subsection" VALUE="login">
<TABLE WIDTH="50%">
 <TR>
  <TD WIDTH="50%">
   Login :
  </TD>
  <TD>
   <INPUT TYPE="TEXT" NAME="V_Login">
  </TD>
 </TR>
 <TR> 
  <TD>
   Password :
  </TD>
  <TD>
   <INPUT TYPE="PASSWORD" NAME="V_Password">
  </TD>
 </TR>
 <TR>
  <TD COLSPAN="2" ALIGN="center">
   <INPUT TYPE="SUBMIT" VALUE="Validez">
  </TD>
 </TR>
</TABLE>
<BR>
</FORM>

<?php
  }
?>

</body>
</html>
