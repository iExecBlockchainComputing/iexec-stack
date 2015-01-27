<?php

/*
 * File   : view.php
 * Date   : May 26th, 2003
 * Author : Oleg Lodygensky
 * Email  : lodygens@lal.in2p3.fr
 */


/*
 * Determine XtremWeb Root Directory
 */

$XWURL = (parse_url($_SERVER['REQUEST_URI']));
$XWURLS = split ("/", $XWURL[path]);
$XWROOTPATH="";
for ($i = 0; $i < count ($XWURLS) - 1; $i++)
  $XWROOTPATH = $XWROOTPATH.$XWURLS[$i]."/";



if (!isset($V_Order))
  $V_Order = "";
if (!isset($V_Sort))
  $V_Sort = "ASC";
if (!isset($LETTER))
  $LETTER = "";

$conditions = "FROM users WHERE UCASE(login) LIKE \"".$LETTER."%\"";
if (isset ($V_Request))
  $conditions = $conditions."AND ".$V_Request;

if ($V_Order != "")
  $conditions = $conditions." ORDER BY ".$V_Order." ".$V_Sort;

$request = "SELECT COUNT(*) ".$conditions;
$resultat = dbQuery($connection, $request); 
$users = mysql_num_rows($resultat);
$users = mysql_fetch_array($resultat);

$request = "SELECT * ".$conditions;
$resultat = dbQuery($connection, $request); 
if ($resultat != 0)
  $nb_enr = mysql_num_rows($resultat);
?>

<html>
<head>
<link rel="stylesheet" href="/css/xtremweb.css" type="text/css">
<title>
XtremWeb : account management
</title>
</head>

<body>
<CENTER>
<BR><HR WIDTH="90%"><BR>
<TABLE WIDTH="90%">
 <TR>
  <TD ALIGN="CENTER">
   <A HREF="./search.html">Search user</A>
  </TD>
  <TD ALIGN="CENTER">
   <A HREF="./register.html">Add user</A>
  </TD>
 </TR>
 <TR>
</TABLE>

<BR><HR WIDTH="90%"><BR><BR><BR><BR>

<TABLE WIDTH="100%" CELLSPACING=1 BORDER=2>
 <TR>
  <TH COLSPAN="12" ALIGN="CENTER">
  <H2>
  <FONT COLOR="black">
<?php
   if ($LETTER != "")
     echo "Users starting with &quot;".$LETTER."&quot;";
   else
     echo "All users";
  echo " (".$users[0]."&nbsp;users)";
?>
  </H2>
  </FONT>
  </TH>
 </TR>
 <TR>

<?php

  $entete = array ("code","email","login","fname","lname","team","country","rights");

  for ($i = 0; $i < count ($entete); $i++) {
    echo "<TH>\n";
    echo "<a href=\"./alpha.php";
    
    if ($LETTER  != "")
      echo "?LETTER=".$LETTER."&V_Order=$entete[$i]";
    else
      echo "?V_Order=CODE";
    if (isset ($V_Request))
      echo "&V_Request=".$V_Request;
    if ($V_Sort == "DESC")
      echo "&V_Sort=ASC";
    else
      echo "&V_Sort=DESC";

    echo "\">$entete[$i]</a>\n";

    if ($V_Order==$entete[$i]) {
      if ($V_Sort == "DESC")
        echo "<IMG SRC=\"".$XWROOTPATH."pics/Descendant.gif\">";
      else
        echo "<IMG SRC=\"".$XWROOTPATH."pics/Ascendant.gif\">";
    }
  echo "</TH>\n";
  }
?>
  </TH>

 </TR>

<?php

if ($resultat != 0)
 while ($user = mysql_fetch_array($resultat)) { 
  echo " <TR ALIGN=\"CENTER\" VALIGN=\"CENTER\">\n";

  echo  "  <TD>".$user["code"]."</TD>\n";
  echo  "  <TD>".$user["email"]."</TD>\n";
  echo  "  <TD>".$user["login"]."</TD>\n";
  echo  "  <TD>".$user["fname"]."</TD>\n";
  echo  "  <TD>".$user["lname"]."</TD>\n";
  echo  "  <TD>".$user["team"]."</TD>\n";
  echo  "  <TD>".$user["country"]."</TD>\n";
  echo  "  <TD>".$user["rights"]."</TD>\n";

  echo  "  <TD>\n";
  echo  "   <FORM ACTION=\"./editer.php3\" METHOD=\"POST\">\n";
  echo  "    <INPUT TYPE=\"HIDDEN\" NAME=\"V_Code\" VALUE=\"".$user["code"]."\">\n";
  echo  "    <INPUT TYPE=\"SUBMIT\" VALUE=\"Editer\">\n";
  echo  "   </FORM>\n";
  echo  "  </TD>\n";

  echo "   </FONT>\n";
  echo " </TR>\n";
}
?>

</TABLE>

<BR><HR WIDTH="90%"><BR>

<TABLE WIDTH="90%">
 <TR>
  <TD ALIGN="CENTER">
   <A HREF="./search.html">Search user</A>
  </TD>
  <TD ALIGN="CENTER">
   <A HREF="./register.html">Add user</A>
  </TD>
 </TR>
 <TR>
</TABLE>
</CENTER>
</body>
</html>
