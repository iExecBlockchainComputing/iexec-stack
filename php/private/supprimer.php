<?php

/*
 * File   : supprimer.php
 * Date   : 03/09/2002
 * Author : Oleg Lodygensky
 * Email  : lodygens@lal.in2p3.fr
 */

include "../php/file/error.php";
include "../php/config/library.php";
include "../php/db/library.php";


$connection = dbConnect ();
if ($connection == 0)
{
  $msg = "Probl&egrave;me syst&egrave;me.<BR>Un mail a ete envoye au webmaster";
  displayMessage("Probleme syst&egrave;me", $msg, "/session/register.html");
  return;
}

$request = "DELETE FROM users ";
$request = $request." WHERE code=".$HTTP_POST_VARS["V_Code"];
$resultat = dbQuery($connection, $request); 

include "index.html";
?>
<!--
<html>
<head>
<link rel="stylesheet" href="/css/xtremweb.css" type="text/css">
<title>
XtremWeb : account management
</title>
</head>

<body>
<center>


	<HR WIDTH="50%">
	  <A HREF="/session/alpha.php?LETTER=A">Liste des abonnes</A>
      </CENTER>

</center>
</body>
</html>
-->
