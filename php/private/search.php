<?php

/*
 * File   : search.php
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
  displayMessage("Probleme syst&egrave;me", $msg, "/session/alpha.php?LETTER=A");
  return;
}

$request = "SELECT * FROM users WHERE UCASE(login) LIKE \"".$HTTP_POST_VARS["V_Email"]."%\"";
$resultat = dbQuery($connection, $request); 
if (mysql_num_rows($resultat)) {
?>

<html>
<head>
<link rel="stylesheet" href="/css/xtremweb.css" type="text/css">
<title>
XtremWeb : account management
</title>
</head>

<body>
<center>


<BR>
<a href ="/session/search.htm">Autre recherche</a><br>
<BR><BR><HR WIDTH="50%"><BR>

<TABLE CELLSPACING=1 BORDER=2>

<?php

   while ($user = mysql_fetch_array($resultat))
   {
     dbDisplayUser ($user);

     echo  "<FORM ACTION=\"./editer.php3\" METHOD=\"POST\">\n";
     echo "<INPUT TYPE=\"HIDDEN\" NAME=\"V_Code\" VALUE=\"".$user["code"]."\">\n";
     echo  "  <TR><TD COLSPAN=\"2\" ALIGN=\"CENTER\"><INPUT TYPE=\"SUBMIT\" VALUE=\"Editer\"></TD></TR>\n";
     echo  "</FORM>\n";

     echo  "<FORM ACTION=\"./password.php\" METHOD=\"POST\">\n";
     echo "<INPUT TYPE=\"HIDDEN\" NAME=\"V_Code\" VALUE=\"".$user["code"]."\">\n";
     echo  "  <TR><TD COLSPAN=\"2\" ALIGN=\"CENTER\"><INPUT TYPE=\"SUBMIT\" VALUE=\"Changer le mot de passe\"></TD></TR>\n";
     echo  "</FORM>\n";

   }

?>


</table>

<?php

    }
else {
  echo  "<H1 ALIGN=\"CENTER\">Aucun r&eacute;sultat trouv&eacute;</H1>\n";
  echo  "<BR><BR><A HREF=\"./search.html\">Autre recherche</A>";
}

?>

<hr width="50%">
<a href ="/session/alpha.php?LETTER=A">Liste des abonnes</a><br>
<a href ="/session/search.htm">Autre recherche</a><br>
</center>
</body>
</html>
