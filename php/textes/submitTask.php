<!doctype HTML public "-//w3c//dTD HTML 4.0 TRansitional//en">
<HTML>
<HEAD>
</HEAD>

<BODY>

<br><br>
<blockquote>
<p class="TITRE">

April 11th, 2006:
<h1>
Submission must be done using the XtremWeb client
</h1>

</p>

<?php

	return;

  include "php/config/library.php";
  include "php/db/library.php";
  include "php/session/library.php";


  /*
   * Test variables
   */

  if (isset($_COOKIE["sessionLogin"]))
    $V_Login = $_COOKIE["sessionLogin"];
  if (isset($_COOKIE["sessionFName"]))
    $V_FName = $_COOKIE["sessionFName"];
  if (isset($_COOKIE["sessionLName"]))
    $V_LName = $_COOKIE["sessionLName"];
  if (isset($_COOKIE["sessionID"]))
    $V_Session = $_COOKIE["sessionID"];

  if (!isset ($V_FName))
    $V_FName = "";
  if (!isset ($V_LName))
    $V_LName = "";

  if ($V_LName != "")
    echo "$V_FName $V_LName";
  else if (isset ($V_Login))
    echo "$V_Login";

?>

Job submission</P>

<HR WIDTH="100%">


<?php


  /*
   * If variable indice set, display application php page
   */

  if (isset ($_POST["indice"]) || isset ($_POST["V_AppUID"])) {
    include "textes/appPage.php";
    return;
  }

?>


<table WIDTH="92%" border ="0">
 <tr>
  <td>

   <p ALIGN="center">
   <table CELLSPACING="0" WIDTH="60%">

<?php

  /*
   * No specific table selected; display all table summary
   */

  echo "<form method=\"post\" name=\"sqlRows\" action=\"".$GLOBALS["XWROOTPATH"]."index.php\">\n";
  echo <<< EOF
  <input type="HIDDEN" name="section" value="section2">
  <input type="HIDDEN" name="subsection" value="submitTask">
  <input type="HIDDEN" name="V_Action" value="submitTask">
EOF;

    if (dbDisplayTableApps () != 0)
       echo "<font color=\"red\">Error</font>\n";

    echo "<center><br><input type=\"SUBMIT\" name=\"V_SUBMIT\" value=\"Choose application\"></center>\n";
    echo "</form>\n";
//  }

?>

    </table>
    </P>
  </td>
 </tr>
</table>


</blockquote>
</body>
</html>
