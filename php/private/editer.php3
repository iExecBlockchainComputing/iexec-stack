<?php

/*
 * File   : editer.php
 * Date   : May 26th, 2003
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

$connection = dbConnect();
if ($connection == 0)
{
  $msg = "System error.";
  displayMessage("Cant connect to database", $msg, $XWROOTPATH."register.html");
  return;
}

?>

<html>
<head>
<title>
XtremWeb Desktop Grid user management
</title>
</head>

<body>
<center>
<A HREF NAME="ABONNE">


<TABLE>
 <TR>
  <TD>
<?php
   echo "<FORM ACTION=\"".$XWROOTPATH."alpha.php\">\n";
?>
     <INPUT TYPE="SUBMIT" VALUE="Back to users list">
   </FORM>
  </TD>
 </TR>
</TABLE>

<BR>
<H3>User informations</H3>
<TABLE CELLSPACING=1 BORDER=2>


<?php

$request = "SELECT * FROM users WHERE code = ".$HTTP_POST_VARS["V_Code"];
$resultat = dbQuery($connection, $request); 
$nb_enr = mysql_num_rows($resultat);

while ($user = mysql_fetch_array($resultat)) { 

  $userCode = $user["code"];
  echo "<FORM ACTION=\"./valider.php\" METHOD=\"POST\">\n";

  dbEditUser ($user);

  $V_Email = $user["login"];
  $V_Nom = $user["lname"];
  $V_Prenom = $user["fname"];
  $V_Team = $user["team"];
  $V_Pays = $user["country"];
  $V_Rights = $user["rights"];

  echo  "  <TR><TD COLSPAN=\"2\" ALIGN=\"CENTER\"><INPUT TYPE=\"SUBMIT\" VALUE=\"Validate\"></TD></TR>\n";

  echo "</form>\n";

  echo  "<FORM ACTION=\"./validpass.php\" METHOD=\"POST\">\n";
  echo "<INPUT TYPE=\"HIDDEN\" NAME=\"V_Code\" VALUE=\"".$user["code"]."\">\n";
  echo "<INPUT TYPE=\"HIDDEN\" NAME=\"V_Nom\" VALUE=\"".$user["lname"]."\">\n";
  echo "<INPUT TYPE=\"HIDDEN\" NAME=\"V_Prenom\" VALUE=\"".$user["fname"]."\">\n";
  echo "<INPUT TYPE=\"HIDDEN\" NAME=\"V_Email\" VALUE=\"".$user["email"]."\">\n";
  echo  "  <TR><TD COLSPAN=\"2\" ALIGN=\"CENTER\"><INPUT TYPE=\"SUBMIT\" VALUE=\"Change password\"></TD></TR>\n";
  echo  "</FORM>\n";

  echo "<TR><TD COLSPAN=\"2\">&nbsp;&nbsp;</TD></TR>\n";
  echo  "<FORM ACTION=\"./supprimer.php\" METHOD=\"POST\">\n";
  echo "<INPUT TYPE=\"HIDDEN\" NAME=\"V_Code\" VALUE=\"".$user["code"]."\">\n";
  echo  "  <TR><TD COLSPAN=\"2\" ALIGN=\"CENTER\">\n";
  echo  "  <FONT COLOR=\"red\"><B>\n";
  echo  "  *******************************<BR>\n";
  echo  "  Attention!!<BR>\n";
  echo  "  </B></FONT>\n";
  echo  "  <INPUT TYPE=\"SUBMIT\" VALUE=\"Remove this account\">\n";
  echo  "  <FONT COLOR=\"red\"><B>\n";
  echo  "  <BR>Attention!!<BR>\n";
  echo  "  *******************************<BR>\n";
  echo  "  </B></FONT>\n";
  echo  "  </TD></TR>\n";
  echo  "</FORM>\n";
}
?>


<TABLE>
 <TR>
  <TD>
   <BR>
<?php
   echo "<FORM ACTION=\"".$XWROOTPATH."alpha.php\">\n";
?>
     <INPUT TYPE="SUBMIT" VALUE="Back to users list">
   </FORM>
  </TD>
 </TR>
</TABLE>

</CENTER>
</body>
</html>
