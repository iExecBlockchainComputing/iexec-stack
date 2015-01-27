<?php

/*
 * File   : password.php
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

$request = "SELECT * FROM users WHERE code='".$HTTP_POST_VARS["V_Code"]."'";
$resultat = dbQuery($connection, $request); 
if (mysql_num_rows($resultat)) {
?>

<html>
<head>
<link rel="stylesheet" href="/css/xtremweb.css" type="text/css">
<title>
XtremWeb users management
</title>
</head>

<body>
<center>


<TABLE CELLSPACING=1 BORDER=2>

<?php

   while ($user = mysql_fetch_array($resultat))
   {
     echo  "<FORM ACTION=\"./validpass.php\" METHOD=\"POST\">\n";

     dbDisplayUser ($user);

     echo  "  <TR><TD COLSPAN=\"2\" ALIGN=\"CENTER\">&nbsp;</TD></TR>\n";

     echo  "  <TR><TD><font size=\"+2\" color=\"red\">Nouveau mot de passe&nbsp;</font></TD><TD><INPUT TYPE=\"TEXT\" NAME=\"V_Password\" VALUE=\"\"></TD></TR>\n";

     echo  "  <TR><TD COLSPAN=\"2\" ALIGN=\"CENTER\">&nbsp;</TD></TR>\n";

     echo  "  <TR><TD COLSPAN=\"2\" ALIGN=\"CENTER\"><INPUT TYPE=\"SUBMIT\" VALUE=\"Valider\"></TD></TR>\n";
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

<br><hr>
<br><a href="/session/alpha.php?LETTER=A">Liste des abonnes</A>
</center>
</body>
</html>
