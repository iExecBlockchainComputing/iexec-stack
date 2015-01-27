<?php

 /* ------------------------------------------ */
 Function htmlGetTitle ($fName)
 /* ------------------------------------------ */
 {
  if (($fName == "") || ($fName == "."))
    return "";

  $file = fopen ($fName, "r");
  if ($file == FALSE)
    return "";

  $found = 0;
  $title = "";

  while (($title == "") && (feof($file)!=1))
  {
    $line = fgets ($file, 255);
    $title = strstr ($line, "<title>");
/*
    if ($title != "" )
    {
      echo "title = ".strip_tags($title)."<BR>";
    }
*/
  }

  fclose ($file);

  return strip_tags($title);
 }


 /* ------------------------------------------- */
 Function htmlNewFile ($fName, $fTitle)
 /* Purpose : insert a HTML header if necessary */
 /*           create text file if not present   */
 /*           insert a header if file is empty  */
 /* Returns : 0 if no header inserted           */
 /*           1 otherwise                       */
 /* ------------------------------------------- */
 {
  $fExists = file_exists ($fName);
  $file = fopen ($fName, "a+");
  $fSize = fileSize ($fName);

  if ($fExists == 0 || $fSize == 0)
  {
   fputs ($file, "<HTML>\n");
   fputs ($file, "<HEAD>\n");
   fputs ($file, $fTitle."\n");
   fputs ($file, "</HEAD>\n");
   fputs ($file, "<BODY>\n");
   fputs ($file, "</BODY>\n");
   fputs ($file, "</HTML>\n");
  }

  fclose ($file);
 }


 /* ------------------------------------------ */
 Function htmlInsertLine ($fName, $newLigne)
 /* ------------------------------------------ */
 {
  if (($fName == "") || ($fName == "."))
    return 0;

  $file = fopen ($fName, "r+");
  if ($file == FALSE)
    return 0;

  $found = 0;
  $endBody = "";

  while (feof($file)!=1)
  {
    $line = fgets ($file, 255);
    $endBody = strstr ($line, "</BODY>");
    if (strlen ($endBody) == 0 )
      fputs ($file, $ligne);
    else
    {
      fputs ($file, $newLigne."\n");
      fputs ($file, "</BODY>\n");
    }
  }

  fclose ($file);

  return 1;
 }

?>

