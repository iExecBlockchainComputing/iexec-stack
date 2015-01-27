<?php

/*
 * File   : validpass.php
 * Date   : 03/09/2002
 * Author : Oleg Lodygensky
 * Email  : lodygens@lal.in2p3.fr
 */

include "../php/file/error.php";
include "../php/config/library.php";
include "../php/db/library.php";
include "../php/session/library.php";
include "../php/mailer/mailer.php";



/*
 * Determine XtremWeb Root Directory
 */

$XWURL = (parse_url($_SERVER['REQUEST_URI']));
$XWURLS = split ("/", $XWURL[path]);
$XWROOTPATH="";
for ($i = 0; $i < count ($XWURLS) - 1; $i++)
  $XWROOTPATH = $XWROOTPATH.$XWURLS[$i]."/";



function make_seed() {
    list($usec, $sec) = explode(' ', microtime());
    return (float) $sec + ((float) $usec * 100000);
}
srand(make_seed());

$connection = dbConnect ();
if ($connection == 0)
{
  $msg = "Can't connect to database.<BR>";
  displayMessage("System error", $msg, $XWROOTPATH);
  return;
}

$V_Password = "";

for ($j=1; $j<=6; $j++) {
  $symb = chr(rand(48,90));

  if (($symb >= ':') && ($symb <= '@'))
    $symb = chr(rand(97,122));

  $V_Password .= $symb;
}

/*
echo "Password = \"".$V_Password."\"<BR>\n";
*/

$V_Code   = $HTTP_POST_VARS["V_Code"];
$V_Email  = $HTTP_POST_VARS["V_Email"];
$V_Nom    = $HTTP_POST_VARS["V_Nom"];
$V_Prenom = $HTTP_POST_VARS["V_Prenom"];

$request = "UPDATE users SET password='".codePassword($V_Password)."'";
$request = $request." WHERE code=".$HTTP_POST_VARS["V_Code"];
/*
echo "request = $request<br><br>";
*/
$resultat = dbQuery($connection, $request); 

$request = "SELECT * FROM users WHERE code = ".$HTTP_POST_VARS["V_Code"];
$resultat = dbQuery($connection, $request); 

if (mysql_num_rows($resultat)) {

  $message = $V_Prenom." ".$V_Nom.", \n";
  $message = $message."Your passwd is now : ".$V_Password."\n\n";
  $message = $message."Enjoy,\n";
  $message = $message."Paris XI University Grid Team.\n";

  $webMaster = configGetWebMasterAddress ();

  $retour = envoiMail ($V_Nom, $webMaster, $webMaster,
                       "Paris XI University Grid (copy)",
                       $message,
                       "");

  $retour = envoiMail ($V_Nom, $webMaster, $V_Email,
                       "Paris XI University Grid",
                       $message,
                       "");

    switch ($retour)
    {
  case 0 :
    $msg = "Can't send mail to \"".$V_Email."\"<BR>\n";
    displayMessage("System error", $msg, $XWROOTPATH);
     break;
    case 1 : 
    case 2 : 
     break;
   }

?>

<html>
<head>
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

     echo  "  <TR><TD COLSPAN=\"2\" ALIGN=\"CENTER\"><INPUT TYPE=\"SUBMIT\" VALUE=\"Edit\"></TD></TR>\n";

   }

?>

</form>

</table>

<?php

    }
else {
  echo  "<H1 ALIGN=\"CENTER\">No record found</H1>\n";
  echo  "<BR><BR><A HREF=\"".$XWROOTPATH.">Back to users list</A>";
}

	echo "<HR WIDTH=\"50%\">\n";
	echo "<A HREF=\"".$XWROOTPATH."\">Back to users list</A>\n";
?>

      </CENTER>

</center>
</body>
</html>
