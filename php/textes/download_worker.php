<?php

/*
 * File   : download_worker.php
 * Date   : 03/08/2003
 * Author : Oleg Lodygensky
 * Email  : lodygens@lal.in2p3.fr
 */



  include "./php/config/library.php";

  if (!isset($V_Login)) {
    if (isset($_COOKIE["sessionLogin"]))
      $V_Login = $_COOKIE["sessionLogin"];
  }
  if (!isset($V_Passwd)) {
    if (isset($_COOKIE["sessionPasswd"]))
      $V_Passwd = $_COOKIE["sessionPasswd"];
  }

?>


<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
  <title>Download XtremWeb worker</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <meta name="GENERATOR" content="Mozilla/4.79 [en] (X11; U; SunOS 5.8 sun4u) [Netscape]">
</head>
<body>


<br>

<blockquote><i><font face="Times New Roman,Times">
<p class="TITRE">
Download worker
</p>
 <center>
<hr width="100%"></blockquote>


<table width="77%" nosave >
<tr nosave>
<td width="10%" nosave></td>

<td width="90%" align="center">
<?php
  if (!isset ($V_Version)) {
?>
  <table width="100%" cellspacing="5" cellpadding="15">
   <tr>
    <TH COLSPAN="3" ALIGN="LEFT">
      <p class="SOUSTITRE">Please, select a file</p>
      <BR>
    </TH>
   </tr>
   <tr>
    <td width="10%">
    </td>
    <td>

<?php
    if (file_exists ("download/XWWorker-Linux.jar")) {
     echo "<form method=\"POST\" action=\"".$GLOBALS["XWROOTPATH"]."index.php\" name=\"login_form\">\n";
?>

      <input type="HIDDEN" name="section" value="section2">
      <input type="HIDDEN" name="subsection" value="downloadworker">
      <input type="HIDDEN" name="V_Version" value="Linux">
      <input type="SUBMIT" value="Linux">
     </form>

<?php
    }
    else {
      echo "<i><b>Linux</b> version not installed in this server</i><br>\n";
    }
?>

    </td>
    <td>

<?php
    if (file_exists ("download/XWWorker-Win32-ix86.jar")) {
     echo "<form method=\"POST\" action=\"".$GLOBALS["XWROOTPATH"]."index.php\" name=\"login_form\">\n";
?>

      <input type="HIDDEN" name="section" value="section2">
      <input type="HIDDEN" name="subsection" value="downloadworker">
      <input type="HIDDEN" name="V_Version" value="Win32-ix86">
      <input type="SUBMIT" value="Win32 ix86">
     </form>

<?php
    }
    else {
      echo "<i><b>Win32-ix86</b> version not installed in this server</i><br>\n";
    }
?>

    </td>
   </tr>
   <tr>
    <td width="10%">
    </td>
    <td>

<?php
    if (file_exists ("download/XWWorker-Solaris.jar")) {
     echo "<form method=\"POST\" action=\"".$GLOBALS["XWROOTPATH"]."index.php\" name=\"login_form\">\n";
?>

      <input type="HIDDEN" name="section" value="section2">
      <input type="HIDDEN" name="subsection" value="downloadworker">
      <input type="HIDDEN" name="V_Version" value="Solaris">
      <input type="SUBMIT" value="Solaris">
     </form>

<?php
    }
    else {
      echo "<i><b>Solaris</b> version not installed in this server</i><br>\n";
    }
?>

    </td>
    <td>

<?php
    if (file_exists ("download/xtremweb.worker-macosx-$XWVERSION.tgz.jar")) {
	    echo "<a href=\"download/xtremweb.worker-macosx-$XWVERSION.tgz\">worker-macosx-$XWVERSION.tgz</a>\n";
    }
    else {
      echo "<i><b>Mac OS-X</b> version not installed in this server</i><br>\n";
    }
?>

    </tr>
  </table>


<?php

  }
?>

</td>
</tr>
</table>
</body>
</html>

