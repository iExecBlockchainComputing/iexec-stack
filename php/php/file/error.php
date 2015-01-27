<?php


 /* ------------------------------------------ */
 Function displayMessage ($title, $msg, $urlBack)
 /* Purpose : insert a header if necessary     */
 /*           create text file if not present  */
 /*           insert a header if file is empty */
 /* Returns : 0 if no header inserted          */
 /*           1 otherwise                      */
 /* ------------------------------------------ */
 {
echo <<< EOF
<html>
<header>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">

<title>
echo $title
</title>
</header>
<body>
<br>
<br>
<h2><center>
echo $msg
</center></h2>
<hr><br><br>
Click here to : 
<a href=$urlBack>continue</a><br><br>
</body>
</html>
EOF;

   exit (-1);
 }



?>
