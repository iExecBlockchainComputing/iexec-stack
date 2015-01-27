<?php

/*
  SiteClass.php

  Author: Geraud P. Krawezik (gk@lri.fr)
*/

if(!defined("_SiteClass_php_")) {
  define ("_SiteClass_php_", true);

  require("./phptmpl/SectionClass.php");
  require("./phptmpl/SubsectionClass.php");
  require("./phptmpl/common.php");
  require("./phptmpl/error.php");

  Class Site {
    var $title;              // The title as displayed in the browser header
    var $name;               // The internal name, used for example in generic structures
    var $sections;           // The sections included inside the site
    var $url;                // The complete URL of the site
    var $hiddenParameters;   // section and subsection passed through post? (or get?)
    var $templatePage;       // The starting template, describing the whole page layout
    var $templatesDir;       // The directory where templates are situated

    var $lastsection_index;  // Only visible ones !!!
    var $displayedSection;   // The section currently being shown to the user
    var $displayedSubsection;// The subsection currently being shown

    var $user_variables;    // The variables set inside index.php

    function Site($_name, $_title, $_url, $_hidden_params = true, $_tmpl_page = "{PAGE}.html", $_tmpl_dir = "./templates") {
      $this->name                = $_name;
      $this->title               = $_title;
      $this->url                 = $_url;
      $this->hiddenParameters    = $_hidden_params;
      $this->templatePage        = $_tmpl_page;
      $this->templatesDir        = $_tmpl_dir;

      $this->lastsection_index   = 0;
      $this->displayedSection    = SECTION_DISPLAYED;
      $this->displayedSubsection = SUBSECTION_DISPLAYED;

      // We verify that $this->templatesDir exists and is accessible
      if (!is_dir($this->templatesDir))
	Error("The templates directory: ".$this->templatesDir." does not exist");
      if (!is_readable($this->templatesDir))
	Error("The templates directory: ".$this->templatesDir." cannot be read");

      // Same thing for $this->tmpl_page
      if (!is_file($this->templatesDir."/".$this->templatePage))
	Error("The page layout template file: ".$this->templatesDir."/".$this->templatePage." does not exist");
      if (!is_readable($this->templatesDir."/".$this->templatePage))
	Error("The page layout template file: ".$this->templatesDir."/".$this->templatePage." cannot be read");

    }
    
    function addVariable($_name, $_value) {
      
      $this->user_variables .= "\$".$_name."=".$_value.";";

    }

    /*
      The two following functions are used to add Section's to the Site
    */

    function add($_new_section) {
      if (isset($this->sections[$_new_section->name])) {
	Warning("Trying to add an already existing section: ".$_new_section->name." (IGNORED)");
	return ;
      }
      $_new_section->visible = true;
      $_new_section->index   = ++$this->lastsection_index;
      $this->sections[$_new_section->name] = $_new_section;
    }

    function addInvisible($_new_section) {
      if (isset($this->sections[$_new_section->name])) {
	Warning("Trying to add an already existing section: ".$_new_section->name." (IGNORED)");
	return ;
      }
      $_new_section->visible = false;
      $_new_section->index   = -1;
      $this->sections[$_new_section->name] = $_new_section;
    }


    /*
      This is the function used to display the webpage currently accessed
    */

    function toString() {

      // We check if the site is empty or contains an empty section
      if (!isset($this->sections))
	Error("Site ".$this->name." does not contain any sections");
      foreach($this->sections as $mySection)
	if (!isset($mySection->subsections))
	  Error("Section ".$mySection->name." does not contain any subsections");


      // We take care of the arguments of the displayed area
      if (!isset($this->sections[$this->displayedSection])) {
	foreach($this->sections as $mySection) {
	  if ($mySection->visible) {
	    $this->displayedSection = $mySection->name;
	    break;
	  }
	}
      }

      if (!isset($this->sections[$this->displayedSection]->subsections[$this->displayedSubsection])) {
	foreach($this->sections[$this->displayedSection]->subsections as $mySubsection) {
	  if ($mySubsection->visible) {
	    $this->displayedSubsection = $mySubsection->name;
	    break;
	  }
	}
      }

      $page_template_file = $this->templatesDir."/".$this->templatePage;
      $page_template      = fileToString($page_template_file);

      $s = $this->stringFromTemplate($page_template, 0, $this->displayedSection, $this->displayedSubsection); 

      // If the user has decided to hide the variables section and subsection
      // After having evaluated all the templates, it' s time to add the <form>
      // used to pass $section and $subsection
      // It is placed just before <\body>
      if ($this->hiddenParameters) {
	$form  = "<form name=\"PHPTMPLFORM\" method=\"post\" action=\"".$this->url."\">\n";
	$form .= "  <input type=\"hidden\" name=\"section\" value=\"".$this->displayedSection."\">\n";
	$form .= "  <input type=\"hidden\" name=\"subsection\" value=\"".$this->displayedSubsection."\">\n";
	$form .= "</form>\n";
	
	if (ereg("<[bB][oO][dD][yY][^>]*>", $s, $bodyTag)) {
	  $s = str_replace($bodyTag[0], $bodyTag[0]."\n".$form, $s);
	}
	else {
	  Error("&lt;body&gt; found");
	}
      }

      echo $s;

      // We display the copyright at the begining fo the generated HTML
      echo "<!--\n".fileToString("./phptmpl/COPYRIGHT")."\n-->\n";

      // Then we display the Warnings if there are some]
      if (defined(WARNINGS_LIST))
	echo WARNINGS_LIST;

    }  // End of toString()


    //
    // Probably the most important function...
    //

    function stringFromTemplate($_template, $_start, $currentSection, $currentSubsection) {

      $s = $_template;

      // If we can find some more {***} expressions, then time to evaluate them!
      while (getFirstTmplCall($s, $_start, $pattern)) {

	if (is_file($this->templatesDir."/".$pattern.".html")) {
	  /*
	    if {PATTERN}.html is a template file, open it and evaluate...
	  */
	  //$t = fileToString($this->templatesDir."/".$pattern.".html");
	  $t = programToString($this->templatesDir."/".$pattern.".html",false);
	  $ex = $this->stringFromTemplate($t, 0, $currentSection, $currentSubsection);
	  $re = true;	
	} else {
	  /*
	    If the {PATTERN} is not referring to any template file
	  */
	  $pat = substr($pattern, 1, strlen($pattern)-2);

	  // Let's start with the SITE level functions
	  if ($pat == "SITE-NAME") {
	    $ex = $this->name;
	    $re = true;
	  } else if ($pat == "SITE-TITLE") {
	    $ex = $this->title;
	    $re = true;
	  } else if ($pat == "SITE-URL") {
	    $ex = $this->url;
	    $re = true;
	  } else if ($pat == "SITE-NBSECTIONS") {
	    $ex = $this->lastsection_index;
	    $re = true;
	    // Now the SECTION level ones
	  } else if ($pat == "SECTION-NAME") {
	    $ex = $currentSection;
	    $re = true;
	  } else if ($pat == "SECTION-TITLE") {
	    $ex = $this->sections[$currentSection]->title;
	    $re = true;
	  } else if ($pat == "SECTION-NBSUBSECTIONS") {
	    $ex = $this->sections[$currentSection]->lastsubsection_index;
	    $re = true;
	  } else if ($pat == "SECTION-INDEX") {
	    if (isset($this->sections[$currentSection]->index))
	      $ex = $this->sections[$currentSection]->index;
	    else
	      $ex = "";
	    $re = true;
	  } else if ($pat == "SECTION-DISPLAYED") {
	    $ex = $this->displayedSection;
	    $re = true;
	  } else if (substr($pat,0,18) == "SECTION-SUBSECTION") {
	    if (ereg("\[(.*)\]", substr($pat,18), $argssection_subsection)) {
	      $index = $this->stringFromTemplate($argssection_subsection[1],0,$currentSection,$currentSubsection);
	      $idx = $index + 0; // Yes +0,  because we want to evaluate the string inside index
	      if ($idx <= $this->sections[$currentSection]->lastsubsection_index) {
		$ex = "".$idx;
		foreach($this->sections[$currentSection]->subsections as $mySubsection) {
		  if ($mySubsection->index == $idx) {
		    $ex = $mySubsection->name;
		    break;
		  }
		}
	      } else {
		$ex = "Incorrect index for SECTION-SUBSECTION[INDEX]: ".$idx;
	      }
	    } else
	      $ex = "Bad argument for SECTION-SUBSECTION[INDEX]".substr($pat,18);
	    $re = true;
	    // Now the SUBSECTION level
	  } else if ($pat == "SUBSECTION-NAME") {
	    $ex = $currentSubsection;
	    $re = true;
	  } else if ($pat == "SUBSECTION-TITLE") {
	    $ex = $this->sections[$currentSection]->subsections[$currentSubsection]->title;
	    $re = true;
	  } else if ($pat == "SUBSECTION-INDEX") {
	    if (isset($this->sections[$currentSection]->subsections[$currentSubsection]->index))
	      $ex = $this->sections[$currentSection]->subsections[$currentSubsection]->index;
	    else
	      $ex = "";
	    $re = true;
	  } else if ($pat == "SUBSECTION-DISPLAYED") {
	    $ex = $this->displayedSubsection;
	    $re = true;
	  } else if ($pat == "SUBSECTION-CONTENTS") {
	    $ex = $this->stringFromTemplate($this->sections[$currentSection]->subsections[$currentSubsection]->getContents(), 0, $currentSection, $currentSubsection);
	    //$ex = $this->stringFromTemplate($this->sections[$this->displayedSection]->subsections[$this->displayedSubsection]->getContents(), 0, $currentSection, $currentSubsection);
	    $re = true;
	  } else if (substr($pat,0,6) == "FORALL") {            // FORALL(TYPE,ACTION)
	    if (getArguments(substr($pat,6), $args)) {          // Checks the arguments
	      if ($args[0] == "SECTION") {
		$ex = "";
		foreach($this->sections as $mySection)
		  if ($mySection->visible)
		    $ex .= $this->stringFromTemplate($args[1], 0, $mySection->name, "");
	      } else if ($args[0] == "SUBSECTION") {
		$ex = "";
		foreach($this->sections[$currentSection]->subsections as $mySubsection)
		  if ($mySubsection->visible)
		    $ex .= $this->stringFromTemplate($args[1], 0, $currentSection, $mySubsection->name);
	      } else
		$ex = "Illegal first argument for FORALL: ".$args[0]."<br>";
	    } else
	      $ex = "Illegal arguments for FORALL: ".$pat."<br>";
	    $re = true;
	  } else if (substr($pat,0,6) == "LINKTO") {              // LINKTO(SECTION,SUBSECTION)
	    if (ereg("\((.*)\,(.*)\)",substr($pat,6), $args2)) {  // Checks the arguments
	      $args2[1] = $this->stringFromTemplate($args2[1], 0, $currentSection, $currentSubsection);
	      $args2[2] = $this->stringFromTemplate($args2[2], 0, $currentSection, $currentSubsection);
	      $ex = $this->createLink($args2[1], $args2[2]);
	    } else {
	      $ex = "Illegal arguments for LINKTO: ".$pattern."<br>";
	    }
	    $re = true;
	  } else if (substr($pat,0,6) == "ACTION") {
	    // TODO: Verify the arguments
	    if (ereg("\((.*)\,(.*)\)",substr($pat,6), $args2)) {
	      $args2[1] = $this->stringFromTemplate($args2[1], 0, $currentSection, $currentSubsection);
	      $args2[2] = $this->stringFromTemplate($args2[2], 0, $currentSection, $currentSubsection);
	      $ex  = "<input type=\"hidden\" name=\"section\" value=\"".$args2[1]."\">\n";
	      $ex .= "<input type=\"hidden\" name=\"subsection\" value=\"".$args2[2]."\">\n";
	    } else {
	      $ex = "Illegal arguments for ACTION: ".$pattern."<br>";
	    }
	    $re = true;
	  } else if (substr($pat,0,10) == "IFTHENELSE") {           // IFTHENELSE(TEST,ACTION1,ACTION2)
	    if (getArguments(substr($pat,10), $args)) {
	      $args[0] = $this->stringFromTemplate($args[0], 0, $currentSection, $currentSubsection);

	      // TODO: explode the test into argument to test its validity and add several "" where necessary. For example if a variable is not set, we should find "" instead of nothing which will make an error.
	      // Also, we might catch any errors and reply false when they are found
	      $test = "\$b = (".$args[0].");";
	      eval($test);
	      if ($b)
		$ex = $this->stringFromTemplate($args[1], 0, $currentSection, $currentSubsection);
	      else
		$ex = $this->stringFromTemplate($args[2], 0, $currentSection, $currentSubsection);
	    } else
	      $ex = "Illegal arguments for IFTHENELSE";
	    $re = true;
	  } else if (substr($pat,0,6) == "IFTHEN") {                // IFTHEN(TEST,ACTION)
	    if (getArguments(substr($pat, 6),$args)) {     // Checks the arguments
	      $args[0] = $this->stringFromTemplate($args[0], 0, $currentSection, $currentSubsection);
	      $test = "\$b = (".$args[0].");";
	      //$ex = $test;
	      eval($test); 
	       if ($b)
		 $ex = $this->stringFromTemplate($args[1], 0, $currentSection, $currentSubsection);
	       else
		 $ex = "";
	    } else
	      $ex = "Illegal arguments for IFTHEN";
	    $re = true;
	  } else if(substr($pat,0,4) == "EVAL") {      // EVAL(MATHEMATICAL_EXPRESSION)
	    if (ereg("\((.*)\)", substr($pat,4), $args)) {
	      $args[1] = $this->stringFromTemplate($args[1], 0, $currentSection, $currentSubsection);
	      $expr = "\$ex=(".$args[1].");";
	      eval($expr);
	    } else
	      $ex = "Illegal arguments for EVAL";
	    $re = true;
	  } else if(substr($pat,0,3) == "VAR") {       // VAR(VARIABLE_NAME)
	    if (getArguments(substr($pat, 3),$args)) {     // Checks the arguments
	      // if (isset($HTTP_GET_VARS[$args[0]])) {
	      $to_eval = POST_AND_GET_VARIABLES.$this->user_variables."\$ex=\$".$args[0].";";
	      eval($to_eval);
	    } else
	      $ex = "Illegal arguments for VAR";
	    $re = true;
	  } else {
	    $re = false;
	  }
	}
	
	$idx = strpos($s, $pattern, $_start);
	$len = strlen($pattern);

	if ($re) {
	  $_start = $idx + strlen($ex);
	  $s = substr_replace($s, $ex, $idx, $len);  // Replaces ONLY the pointed {PATTERN}
	} else {
	  $_start = $idx + $len;
	}

      } // End of While

      return $s;
    } // End of stringFromTemplate

    //
    // This function creates a link to a section and subsection
    // Used when {LINKTO(section, subsection)} is found in a template
    //

    function createLink($_section, $_subsection) {
      // We check if the section and subsection exist...
      if (!$this->checkSectionSubsection($_section, $_subsection)) {
	Warning("Incorrect section/subsection inside LINKTO\nsection= ".$_section."\nsubsection = ".$_subsection);

	if (!isset($this->sections[$_section])) {
	  foreach($this->sections as $mySection) {
	    if ($mySection->visible) {
	      $_section = $mySection->name;
	      break;
	    }
	  }
	}
	if (!isset($this->sections[$_section]->subsections[$_subsection])) {
	  foreach($this->sections[$_section]->subsections as $mySubsection) {
	    if ($mySubsection->visible) {
	      $_subsection = $mySubsection->name;
	      break;
	    }
	  }
	}
      }

      if ($this->hiddenParameters) {
	$l  = "javascript: ";
	$l .= "document.PHPTMPLFORM.section.value='".urlencode($_section)."'; ";
	$l .= "document.PHPTMPLFORM.subsection.value='".urlencode($_subsection)."'; ";      
	$l .= "document.PHPTMPLFORM.submit();";
      } else
	$l = $this->url."?section=".urlencode($_section)."&subsection=".urlencode($_subsection);
      return $l;
    }

    //
    // This function checks if:
    // - $_section exists
    // - $_subsection is a subsection of $_section
    //
 
    function checkSectionSubsection($_section, $_subsection) {
      if (!isset($this->sections[$_section]))
	return false;
      else if (!isset($this->sections[$_section]->subsections[$_subsection]))
	return false;
      else 
	return true;
    }

  }


  /*
    This function gets the first {TMPL} in a document,
    even if several of them are nested! (eg: {FORALL(SECTION,{NAME})} )
  */
  function getFirstTmplCall($_template, $_start, &$_pattern) {

    // We start by cutting the first part of the _template
    $_template = substr($_template, $_start);

    $obi = strpos($_template, "{"); // Opening Brace Index
  
    if ($obi === false) {
      $_pattern = "";
      return false;
    }
    
    $s = substr($_template, $obi+1);
    
    $obn = 1; //Opening Brace Number

    for ($i=0; $i<strlen($s); $i++) {
      if ($s[$i] == "{")
	$obn++;
      if ($s[$i] == "}") {
	$obn--;
	if ($obn == 0) { // OK, let's get out of there
	  $_pattern = "{".substr($s, 0, $i)."}";
	  return true;
	}
      }
    }
    
    $_pattern = substr($s, 0, strlen($s));
    return false;
    
  } // End of getFirstTmplCall

  /*
    This function gets all the variables from a function of the form (*,*,...)
    The arguments are returned in $_args
    Returns false if the $_list is incorrect
  */
  function getArguments($_list, &$_args) {
    if (($_list[0] != "(") || ($_list[strlen($_list)-1] != ")"))
      return false;
    $nbargs = 0;
    $list = substr($_list,1,strlen($_list)-2);
    $myArg = "";
    $obn = 0; // Opening braces      { number
    $okn = 0; // Opening brackets    [ number
    $opn = 0; // Opening parenthesis ( number

    for ($i=0; $i<strlen($list); $i++) {
      $cc = $list[$i];
      // Start by checking all the possible ()-like structures
      if ($cc == "{") $obn++;
      if ($cc == "[") $okn++;
      if ($cc == "(") $opn++;
      if ($cc == "}") if (--$obn < 0) $obn = 0;
      if ($cc == "]") if (--$okn < 0) $okn = 0;
      if ($cc == ")") if (--$opn < 0) $opn = 0;

      if (($cc == ",") && (($obn+$okn+$opn) == 0)) {
	$_args[$nbargs++] = $myArg;  // Adds the new arguments
	$myArg = "";
      } else {
	$myArg .= $cc;
      }
    }
    $_args[$nbargs] = $myArg;
    return true;
  }

} // End of ifdefined...

?>
