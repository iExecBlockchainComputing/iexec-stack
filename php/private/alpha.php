<HTML>
<HEAD>
<TITLE>
XtremWeb Desktop Grid management pages
</TITLE>
</HEAD>
<BODY>

<?php

include "../php/file/error.php";
include "../php/config/library.php";
include "../php/db/library.php";

if (!isset($LETTER)) $LETTER=$_GET["LETTER"];
if (!isset($LETTER)) $LETTER=$_POST["LETTER"];
if (!isset($V_Order)) $V_Order=$_GET["V_Order"];
if (!isset($V_Order)) $V_Order=$_POST["V_Order"];


/*
 * Determine XtremWeb Root Directory
 */

$XWURL = (parse_url($_SERVER['REQUEST_URI']));
$XWURLS = split ("/", $XWURL[path]);
$XWROOTPATH="";
for ($i = 0; $i < count ($XWURLS) - 1; $i++)
  $XWROOTPATH = $XWROOTPATH.$XWURLS[$i]."/";



if (isset($V_Order) == 0)
  $V_Order = "";

$connection = dbConnect ();
if ($connection == 0)
{
  $msg = "System error.<BR>";
  displayMessage("Can't connect to database", $msg, $XWROOTPATH."private/alpha.php?LETTER=A");
  return;
}

?>


<TABLE WIDTH="100%">
<TR>
<TH COLSPAN="2" WIDTH="10%" ALIGN="CENTER">
There's actually&nbsp;
<?php
$request = "SELECT COUNT(*) FROM users";
$resultat = dbQuery($connection, $request); 
$nb_enr = mysql_num_rows($resultat);
$users= mysql_fetch_array($resultat);
  echo $users[0]."&nbsp;users.";
?>

</TH>
</TR>
<TR VALIGN="TOP">
<TD WIDTH="10%" ALIGN="CENTER">
<BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR>

<?php
  echo "<A HREF=\"alpha.php?V_Order=".$V_Order;
  if (isset ($V_Request))
    echo "&V_Request=".$V_Request;
  echo "\">All</A><BR>\n";

  for ($i = ord("A"); $i <= ord("Z"); $i++) {

    $letter = chr($i);

    echo "<A HREF=\"alpha.php?LETTER=".$letter;
    echo "&V_Order=".$V_Order;

    if (isset ($V_Request))
      echo "&V_Request=".$V_Request;

    echo "\">".$letter."</A>&nbsp;(";

    $request = "SELECT COUNT(*) FROM users WHERE UCASE(login) LIKE \"".$letter."%\"";
    $resultat = dbQuery($connection, $request); 
    $nb_enr = mysql_num_rows($resultat);
    $users= mysql_fetch_array($resultat);

    echo "$users[0]&nbsp;)<BR>\n";

  }

?>

</TD>
<TD>


<?php

include "view.php";


?>


</TD>
</TR>
</TABLE>
</BODY>
</HTML>
