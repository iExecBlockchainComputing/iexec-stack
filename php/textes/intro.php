<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
</head>

<body>

<BR><BR>
<BLOCKQUOTE>


<P CLASS="TITRE">Welcome

<?php


  if (isset($_COOKIE["sessionLogin"]))
    $V_Login = $_COOKIE["sessionLogin"];
  if (isset($_COOKIE["sessionFName"]))
    $V_FName = $_COOKIE["sessionFName"];
  if (isset($_COOKIE["sessionLName"]))
    $V_LName = $_COOKIE["sessionLName"];

  if (!isset ($V_FName))
    $V_FName = "";
  if (!isset ($V_LName))
    $V_LName = "";

  if ($V_LName != "")
    echo ", $V_FName $V_LName";
  else if (isset ($V_Login))
    echo ", $V_Login";
?>

</P>

<hr WIDTH="100%">



<table WIDTH="92%" border="0">
 <tr>
  <td>
   <p CLASS="TITRE">Background</p>
   <p> This server is intended to deploy a Desktop Grid, using XtremWeb as middleware.</p>

   <p align="center">
   <table>
    <tr>
     <td>
      <img src="pics/construct.gif">
     </td>
     <td>
      These pages are under contruction...
     </td>
    </tr>
   </table>
   </p>

  </td>
 </tr>

 <tr>
  <td>
   <br>

   <p>
  </td>
 </tr>
</table>

<br>&nbsp;
</BLOCKQUOTE>
</body>
</html>
