<?php

/*
  error.php

  Author: Geraud P. Krawezik (gk@lri.fr)
*/

if (!defined("_error_php_")) {
  define ("_error_php_", true);

  function Warning($error, $line="") {
    $myWarning  = "<!--\n";
    $myWarning .= "PHPHTML WARNING: ".$error."\n";
    if ($line != "")
      $myWarning .= "Arguments were: ".$line."\n";
    $myWarning .= "-->\n";

    if (!defined(WARNINGS_LIST))
      define("WARNINGS_LIST", $myWarning);
    else
      define("WARNINGS_LIST", WARNINGS_LIST.$myWarning);
  }

  function Error($error, $line = "") {
    echo "<html>\n";
    echo "<body bgcolor=#ffffff>\n";
    echo "<img src=./phptmpl/PHPTMPL_error.jpg><br>\n";
    echo "<b>PHPTMPL ERROR: </b>".$error."<br>\n";
    if ($line != "") {
      echo "Arguments were: ".$line."<br>\n";
    }
    die("");
  }
  
}

?>
