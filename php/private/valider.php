<?php

/*
 * File   : valider.php
 * Date   : 03/09/2002
 * Author : Oleg Lodygensky
 * Email  : lodygens@lal.in2p3.fr
 */

include "../php/file/error.php";
include "../php/config/library.php";
include "../php/db/library.php";


/*
 * Determine XtremWeb Root Directory
 */

$XWURL = (parse_url($_SERVER['REQUEST_URI']));
$XWURLS = split ("/", $XWURL[path]);
$XWROOTPATH="";
for ($i = 0; $i < count ($XWURLS) - 1; $i++)
  $XWROOTPATH = $XWROOTPATH.$XWURLS[$i]."/";



$connection = dbConnect ();
if ($connection == 0)
{
  $msg = "Probl&egrave;me syst&egrave;me.<BR>Un mail a ete envoye au webmaster";
  displayMessage("Probleme syst&egrave;me", $msg, "/session/register.html");
  return;
}

$request = "UPDATE users SET login='".$HTTP_POST_VARS["V_Login"]."',";
$request = $request." email='".$HTTP_POST_VARS["V_Email"]."',";
$request = $request." lname='".$HTTP_POST_VARS["V_Nom"]."',";
$request = $request." fname='".$HTTP_POST_VARS["V_Prenom"]."',";
$request = $request." country='".$HTTP_POST_VARS["V_Country"]."',";
$request = $request." team='".$HTTP_POST_VARS["V_Team"]."',";
$request = $request." rights=".$HTTP_POST_VARS["V_Rights"];
$request = $request." WHERE code=".$HTTP_POST_VARS["V_Code"];

echo "request = $request<br><br>";

$resultat = dbQuery($connection, $request); 
$request = "SELECT * FROM users WHERE code = ".$HTTP_POST_VARS["V_Code"];
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


<TABLE CELLSPACING=1 BORDER=2>

 <FORM ACTION="./editer.php3" METHOD="POST">

<?php

   while ($user = mysql_fetch_array($resultat)) { 
     dbDisplayUser ($user);

     echo  "  <TR><TD COLSPAN=\"2\" ALIGN=\"CENTER\"><INPUT TYPE=\"SUBMIT\" VALUE=\"Editer\"></TD></TR>\n";

   }

?>

</form>

</table>

<?php

    }
else {
  echo  "<H1 ALIGN=\"CENTER\">Aucun r&eacute;sultat trouv&eacute;</H1>\n";
  echo  "<BR><BR><A HREF=\"./search.html\">Autre recherche</A>";
}


	echo "<HR WIDTH=\"50%\">\n";
	echo "<A HREF=\"".$GLOBALS["XWROOTPATH"]."private/alpha.php?LETTER=A\">Liste des abonnes</A>\n";
?>
  </CENTER>

</center>
</body>
</html>
