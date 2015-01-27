<?php
/*
  SubsectionClass.php

  Author: Geraud P. Krawezik (gk@lri.fr)
*/

if(!defined("_SubsectionClass_php_")) {
  define ("_SubsectionClass_php_", true);
  
  Class Subsection {
    var $name;
    var $title;
    var $visible;
    var $index;
    var $linkToContents;  // Just a link, as this can refer to a :
                          // - HTML file
                          // - PHP Program
                          // - String

    function Subsection($_name, $_title, $_linkToContents) {
      $this->name           = $_name;
      $this->title          = $_title;
      $this->linkToContents = $_linkToContents;
    }

    function setVisible($_visibility) {
      $this->visible = $_visibility;
    }

    function getContents() {
      // Let's test if we have a file of the 'linkToContents' name
      $ltc = $this->linkToContents;
      // We get the programs in the form of 'program_name.php?var1=val1&var2=val2...'
      if (ereg("([^?]*)(.*)",$ltc, $args)) {
	$cf = $args[1];
	if ($args[2] == "")
	  $arguments = false;
	else {
	  $varval = split("&",substr($args[2],1));
	  foreach($varval as $myVar) {
	    list($vari, $val) = split("=",$myVar);
	    $arguments[$vari]=urldecode($val);
	  }
	}
      } else {
	$cf = $ltc;
	$arguments = false;
      }

      if (file_exists($cf)) { // cf = contents file
	// If we have got something else than .php or .php3,
	//  we consider the file as HTML or pure text
	//  (same thing for the browser of course...)
	if ( (substr($cf, -4) == ".php") || (substr($cf, -5) == ".php3")) {
	  return programToString($cf, $arguments);
	} else {
	  return fileToString($cf);
	}	  
      } else
	return $ltc;
    }

  }

}

?>
