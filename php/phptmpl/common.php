<?php

/*
  common.php

  Author: Geraud P. Krawezik (gk@lri.fr)
*/

if (!defined("_common_php_")) {
  define ("_common_php_", true);

  /* Several useful functions, used everywhere */
  
  // Reads a text file ans returns its content
  function fileToString($url) {
    if ($fd = fopen($url, "r")) {
      $contents = fread($fd, filesize($url));
      fclose($fd);
    } else {
      $contents = "INTERNAL ERROR: Could not open ".$url;
    }
    return $contents;
  }
  
  // Eval a PURE PHP program and returns the output from it into a String
  // $arguments contains an array of variables and their values
  function programToString($url, $arguments) {
    
    if (is_file($url) && is_readable($url)) {
      
      // Now we take care of the arguments
      $argumentsCode = "";
      if ($arguments !== false) {
	while (list($variable, $value) = each($arguments))
	  $argumentsCode .= "\$".$variable." = \"".$value."\";";
      }

      /* This is for PHP 4.0 */
      ob_start();
      eval($argumentsCode.POST_AND_GET_VARIABLES);
      include($url);
      $contents = ob_get_contents();
      ob_end_clean();
    } else {
      $contents = "INTERNAL ERROR: Could not open ".$urlArguments;
    }
    
    return $contents;
  }
}
?>