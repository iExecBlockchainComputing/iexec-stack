<?php

/*
  SectionClass.php

  Author: Geraud P. Krawezik (gk@lri.fr)
*/

if(!defined("_SectionClass_php_")) {
  define ("_SectionClass_php_", true);
  
  Class Section {
    var $name;
    var $title;
    var $subsections;
    var $visible;
    var $index;
    var $lastsubsection_index;    // Only Visible ones!!!
    

    function Section($_name, $_title) {
      $this->name                 = $_name;
      $this->title                = $_title;
      $this->lastsubsection_index = 0;
    }

    function add($_new_subsection) {
      $_new_subsection->visible = true;
      if (isset($this->subsections[$_new_subsection->name])) {
	Warning("Trying to add an already existing subsection(".$_new_subsection.") to section ".$this->name);
	return ;
      }
      $_new_subsection->index   = ++$this->lastsubsection_index;
      $this->subsections[$_new_subsection->name] = $_new_subsection;
    }

    function addInvisible($_new_subsection) {
      $_new_subsection->visible = false;
      if (isset($this->subsections[$_new_subsection->name])) {
	Warning("Trying to add an already existing subsection(".$_new_subsection.") to section ".$this->name);
	return ;
      }
      $_new_subsection->index   = -1;
      $this->subsections[$_new_subsection->name] = $_new_subsection;
    }

    function setVisible($_visibility) {
      $this->visible = $_visibility;
    }

  }

}

?>
