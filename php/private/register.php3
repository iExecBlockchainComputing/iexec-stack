<?php

/*
 * File   : register.php
 * Date   : May 26th, 2003
 * Author : Oleg Lodygensky
 * Email  : lodygens@lal.in2p3.fr
 */

include "../php/file/error.php";
include "../php/config/library.php";
include "../php/db/library.php";
include "../php/session/library.php";

$err = 0;

$msg = "";


$V_Email = $HTTP_POST_VARS["V_Email"];
$V_Login = $HTTP_POST_VARS["V_Login"];
$V_Password = $HTTP_POST_VARS["V_Password"];
$V_Nom = $HTTP_POST_VARS["V_Nom"];
$V_Prenom = $HTTP_POST_VARS["V_Prenom"];
$V_Country = $HTTP_POST_VARS["V_Country"];
$V_Team = $HTTP_POST_VARS["V_Team"];
$V_Rights = $HTTP_POST_VARS["V_Rights"];

if (empty($V_Login) == 1)
{
  $V_Login = "";
}
if ($V_Login == "")
{
  $err = 1;
  $msg = $msg."Invalid login!<br>";
}

if (empty($V_Email) == 1)
{
  $V_Email = "";
}
if ($V_Email == "")
{
  $err = 1;
  $msg = $msg."Invalid e-mail!<br>";
}

if (empty($V_Password) == 1)
{
  $V_Password = "";
}
if ($V_Password == "")
{
  $V_Password = "password invalid";
}

if (empty($V_Nom) == 1)
{
  $V_Nom = "";
}

if (empty($V_Prenom) == 1)
{
  $V_Prenom = "";
}

if (empty($V_Country) == 1)
{
  $V_Pays = "";
}

if (empty($V_Rights) == 1)
{
  $V_Rights = 0;
}

if ($err == 1)
{
  displayMessage("Donnees manquantes", $msg, "/XtremWeb/private/register.html");
  return;
}


$V_Password = codePassword($V_Password);

$connection = dbConnect ();
if ($connection == 0)
{
  $msg = "System error!";
  displayMessage("Can't connect to database", $msg, "/XtremWeb/private/register.html");
  return;
}

$request = "SELECT code FROM users WHERE login = '". $V_Email ."'";
$resultat = dbQuery ($connection, $request); 
$nb_enr = mysql_num_rows($resultat);

if ($nb_enr>0)
{
  $msg = "Email address \"".$V_Email."\" allready used";
  displayMessage("Creation error", $msg, "/XtremWeb/private/register.html");
  return;
}

$request = "INSERT INTO users (email,login,password,fname,lname,team,country,rights)";
$request = $request."VALUES ( '";
$request = $request. $V_Email ."', '". $V_Login. "', '". $V_Password. "', '";
$request = $request. $V_Prenom . "', '" . $V_Nom ."', '";
$request = $request. $V_Team ."', '" . $V_Country ."', ".$V_Rights.")";


$resultat = dbQuery($connection, $request); 

include "index.html";
?>

<!--
<html>
<head>
<link rel="stylesheet" href="/css/xtremweb.css" type="text/css">
<title>
XtremWeb new account
</title>
</head>
<body>
<center>
User account successfully created.<BR>
<BR><A HREF="/XtremWeb/private/alpha.php?LETTER=A">Users list</A><BR><BR>
</center>
</body>
</html>
-->
