</html>
<head>
<title>essai remove bad char</title>
</head>

<body>
 
<center>

<?php
 if (isset($V_Search)) {
   echo "V_Search = ".$V_Search."<BR>";
   $V_Search = str_replace(",", " ", $V_Search);
   $V_Search = str_replace(";", " ", $V_Search);
   $V_Search = str_replace(".", " ", $V_Search);
   $V_Search = str_replace(":", " ", $V_Search);
   $V_Search = str_replace("!", " ", $V_Search);
   $V_Search = str_replace("?", " ", $V_Search);
   $V_Search = str_replace("/", " ", $V_Search);
   $V_Search = str_replace("<", " ", $V_Search);
   $V_Search = str_replace(">", " ", $V_Search);
   $V_Search = str_replace("%", " ", $V_Search);
   $V_Search = str_replace("$", " ", $V_Search);
   $V_Search = str_replace("(", " ", $V_Search);
   $V_Search = str_replace(")", " ", $V_Search);
   $V_Search = str_replace("'", " ", $V_Search);
   $V_Search = str_replace("\"", " ", $V_Search);
   $V_Search = str_replace("\\", " ", $V_Search);
   echo "V_Search = ".$V_Search."<BR>";
 }
?>

<form action="./recherche.php" method=Get>
  <INPUT TYPE="text" SIZE="30" NAME="V_Search"><br>
  <INPUT TYPE="submit">
</form>

</center>

</body>
</html>
