<?php

/*
 * File   : logout.php
 * Date   : May 26th, 2003
 * Author : Oleg Lodygensky
 * Email  : lodygens@lal.in2p3.fr
 */


  include "./php/config/library.php";

  if (isset ($_COOKIE["sessionLogin"])) {
    $V_Login = $_COOKIE["sessionLogin"];
    configDelDir ("download/".$V_Login);		
  }
  setcookie("sessionID","", -1); // cancel cookie
  setcookie("sessionLogin","", -1); // cancel cookie
  setcookie("sessionPasswd","", -1); // cancel cookie
  setcookie("sessionFName","", -1); // cancel cookie
  setcookie("sessionLName","", -1); // cancel cookie
?>

<html>
<head>
<META HTTP-EQUIV="Refresh" CONTENT="0; URL=./index.php">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<title>
XtremWeb Desktop Grid user deconnection
</title>
</head>

<body>
</body>
</html>
