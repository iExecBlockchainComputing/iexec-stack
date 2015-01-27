<?php

/*
  PHPTMPL.php

  version: 1.0.2

  Author: Geraud P. Krawezik (gk@lri.fr)
*/

// This is the version number needed for the PHPTMPL scripts
define("PHP_VERSION_MINIMAL",403);

if(!defined("_PHPTMPL_php_")) {
  define("_PHPTMPL_php_", true);

  require("./phptmpl/SiteClass.php");
  require("./phptmpl/SectionClass.php");
  require("./phptmpl/SubsectionClass.php");
  require("./phptmpl/common.php");
  require("./phptmpl/error.php");

  // We start by checking if the current version of PHP on the webserver is correct
  $PHPVERSION = split("\.", PHP_VERSION);
  if(intval($PHPVERSION[0]*100+$PHPVERSION[1]*10+$PHPVERSION[2]) < PHP_VERSION_MINIMAL) {
    Error("INCORRECT PHP VERSION: ".PHP_VERSION."<br>Needed: at least ".PHP_VERSION_MINIMAL);
  }


  /*
    Now we take care of the arguments, POST and GET
    The POST variables are predominant: if a POST variable redefines
      one var already defined by GET, then the new value will be taken
  */

  if (isset($inputVariables)) unset($inputVariables);
  // GET'd variables
  if (isset($HTTP_GET_VARS)) {
    while (list($name, $value) = each($HTTP_GET_VARS)) {
      if (is_array($value))
	while (list($key,$val) = each($value))
	  $inputVariables[$name."[".$key."]"] = $val;
      else
	$inputVariables[$name] = $value;
    }
  }

  // POST'd variables
  if (isset($HTTP_POST_VARS)) {
    while (list($name, $value) = each($HTTP_POST_VARS)) {
      //      $inputVariables[$name] = $value;
     if (is_array($value))
       while (list($key,$val) = each($value))
	 $inputVariables[$name."[".$key."]"] = $val;
      else
	$inputVariables[$name] = $value;
    }
  }

  // POST'd files
  if (isset($HTTP_POST_FILES)) {
    while (list($name,$value) = each($HTTP_POST_FILES)) {
      $inputVariables[$name]         = $value["tmp_name"];
      $inputVariables[$name."_name"] = $value["name"];
      $inputVariables[$name."_size"] = $value["size"];
      $inputVariables[$name."_type"] = $value["type"];
    }
  }

  // The following is used so that pages calling themsleves through a 
  //  form will be really called...
  /*  if ((!isset($inputVariables["section"])) || (!isset($inputVariables["subsection"]))) {
    $referer_string = $HTTP_SERVER_VARS["HTTP_REFERER"];
    //echo "toto=".$referer_string;
    $referer_elts   = parse_url($referer_string);
    $referer_query  = $referer_elts["query"];
    $referer_query_elts = explode("&",$referer_query);
    while (list($key,$val) = each($referer_query_elts)) {
      if ((substr($val,0,8) == "section=") || (substr($val,0,11) == "subsection="))
	parse_str($val);
    }
    if (isset($section) && (!isset($inputVariables["section"])))
      $inputVariables["section"]    = $section;
    if (isset($subsection) && (!isset($inputVariables["subsection"])))
      $inputVariables["subsection"] = $subsection;
      }*/
  

  // Check if the section and subsection variables exist.
  // If not, just create them
  if (!isset($inputVariables["section"]))
    $inputVariables["section"] = "";
  
  if (!isset($inputVariables["subsection"]))
    $inputVariables["subsection"] = "";
  

  $myPostAndGet = "";

  // We assign the arguments variables to their values
  // If $value exists as a constant, then it needs to be passed
  //  as this constant. Otherwise it is passed as a string.
  //  this is necessary as the user may use keywords that would produce
  //  an error when eval'd
  while (list($name, $value) = each($inputVariables)) {
    if (!defined($value))
      $myPostAndGet .= "\$".$name." = \"".$value."\";";
    else
      $myPostAndGet .= "\$".$name." = ".$value.";";
  }
  // The following evaluation is done so that the user can
  // use the passed variables into his index.php file
  eval($myPostAndGet);

  define("POST_AND_GET_VARIABLES", $myPostAndGet);

  // The following constants are defined as they will be used by SiteClass.php
  // This is the only way I have found to pass them 'cleanly' 
  define("SECTION_DISPLAYED", $inputVariables["section"]);
  define("SUBSECTION_DISPLAYED", $inputVariables["subsection"]);

}

?>
