<!doctype HTML public "-//w3c//dTD HTML 4.0 TRansitional//en">
<HTML>
<HEAD>
<?php
echo "<META HTTP-EQUIV=\"Refresh\" CONTENT=\"300; URL=".$GLOBALS["XWROOTPATH"]."index.php?section=section3&subsection=userStats\">\n";
?>
</HEAD>

<BODY>

<script language="JavaScript" type="text/javascript" src="./scripts/xtremweb.js">
</script>


<br><br>
<blockquote>
<p class="TITRE">

<?php

  include "php/config/library.php";
  include "php/db/library.php";
  include "php/session/library.php";

  /*
	 * Remove back-slash inserted by HTTP protocols ( i.e. \' becomes ' )
	 */
  if (!isset ($V_Conditions))
    if (isset ($_POST["V_Conditions"]))
      $V_Conditions = $_POST["V_Conditions"];
		else
      $V_Conditions = "";
  $V_Conditions = stripslashes ($V_Conditions);


  /*
   * rows/page
   */
  $PAGELENGTH = 100;

  /*
   * Test variables
   */

  if (isset($_COOKIE["sessionLogin"]))
    $V_Login = $_COOKIE["sessionLogin"];
  if (isset($_COOKIE["sessionFName"]))
    $V_FName = $_COOKIE["sessionFName"];
  if (isset($_COOKIE["sessionLName"]))
    $V_LName = $_COOKIE["sessionLName"];
  if (isset($_COOKIE["sessionID"]))
    $V_Session = $_COOKIE["sessionID"];

  if (!isset ($V_FName))
    $V_FName = "";
  if (!isset ($V_LName))
    $V_LName = "";

  if ($V_LName != "")
    echo "$V_FName $V_LName";
  else if (isset ($V_Login))
    echo "$V_Login";

  $done = false;


  /*
   * Is user valid ?
   */

  if (isset ($V_Login) && ($V_Login != "")) {

    /*
     * Is there any action to complete ?
     */

    if ((isset ($V_Action)) && (isset ($indices)) && (isset ($V_TableName))) {
      if ($V_Action == "Delete") {
				$nbDel = dbDeleteFromTable ($V_Login, $V_TableName, $indices);
				$done = true;
      }
      else if ($V_Action == "Results") {
				dbGetResultsFromTable ($V_Login, $V_TableName, $indices);
				$done = true;
      }
    }
  }

?>

statistics</P>

<center>
<HR WIDTH="100%">



<table width="92%" border="0">
 <tr>
  <td>

   <p align="left">
   <table>
    <tr>
     <td colspan="2" align="center">
<?php
  echo date ("F d, Y - H:i");
?>
     </td>
    </tr>

<?php
   /*
    * Is there a specific table to display ?
    */
   $V_TableName = "works";

	 /*
    * prepare next/previous browsing buttons
    */

   $minPage = 0;
   $nbRows = dbCountUserRows ($V_Login,
															$V_TableName,
															$V_Conditions);

   $maxPage = round ($nbRows / $PAGELENGTH);

   if (isset ($_GET["V_Page"]))
     $V_Page = $_GET["V_Page"];
   else
     $V_Page = $minPage;

   $V_PagePrev = $V_Page - 1;
   if ($V_PagePrev < $minPage)
     $V_PagePrev = $minPage;

   $V_PageNext = $V_Page + 1;
   if ($V_PageNext > $maxPage)
     $V_PageNext = $maxPage;

   if ($maxPage > 0) 
     $V_PageLast = $maxPage - 1;

   $V_PageFirst = $minPage;

   $V_NbPages = round (($V_PageLast) / $PAGELENGTH);
   if ($V_NbPages < $V_PageLast)
     $V_NbPages = $V_PageLast;
?>

   <tr>
     <td colspan="3" align="center">

<?php
       echo "<form method=\"post\" action=\"".$GLOBALS["XWROOTPATH"]."index.php?section=section3&subsection=userStats\">\n";
?>

	  <input TYPE="SUBMIT" VALUE="  Back to user stats main page  ">
       </form>
       <br>
     </td>
   </tr>

   <tr>

     <!-- select/unselect rows  -->

     <td>
        <form name="select1" action="javascript:void(0)" onsubmit="return false">
        <select name="filter" onchange="makeSelection();">
            <option value="" selected="selected">Select:</option>
            <option value="!0">All</option>
            <option value="0">None</option>
        </form>
     </td>


     <!-- delete selection  -->

     <td width="200" align="right">
       <a href="" onclick="Submit('Delete'); return false;"
          onmouseover="status='Delete selected rows'; return true;"
          onmouseout="status='';">Delete selection</a>
       &nbsp;&nbsp;
     </td>


    <!-- get result for selection  -->

     <td width="200" align="left">
       &nbsp;&nbsp;
       <a href="" onclick="Submit('Results'); return false;"
          onmouseover="status='Get results for selected rows'; return true;"
          onmouseout="status='';">Get&nbsp;results&nbsp;for&nbsp;selection</a>
     </td>

<?php
    if ($V_Page > $minPage) {
?>


     <!-- goto first page button  -->

     <td>
<?php
       echo "<form name=\"firstPage\" action=\"".$GLOBALS["XWROOTPATH"]."index.php\">\n";
?>
       <input type="hidden" name="section" value="section3">
       <input type="hidden" name="subsection" value="userStats">
<?php
echo "       <input type=\"hidden\" name=\"V_TableName\" value=\"".$V_TableName."\">";
echo "       <input type=\"hidden\" name=\"V_Page\" value=\"".$V_PageFirst."\">\n";
?>
       <input type="submit" value="!&lt;&lt;">
       </form>
     </td>


     <!-- goto previous page button  -->

     <td>
<?php
       echo "<form name=\"prevPage\" action=\"".$GLOBALS["XWROOTPATH"]."index.php\">\n";
?>
       <input type="hidden" name="section" value="section3">
       <input type="hidden" name="subsection" value="userStats">
<?php
echo "       <input type=\"hidden\" name=\"V_TableName\" value=\"".$V_TableName."\">";
echo "       <input type=\"hidden\" name=\"V_Page\" value=\"".$V_PagePrev."\">\n";
?>
       <input type="submit" value="&lt;&lt;">
       </form>
     </td>

<?php
    }

    /*
     * display curent page number
     */
    echo "<td><h3>Page ".$V_Page."&nbsp;/&nbsp;".$V_NbPages."</h3></td>";

    if ($V_PageNext < $maxPage) {
?>


     <!-- goto next page button  -->

     <td>
<?php
       echo "<form name=\"nextPage\" action=\"".$GLOBALS["XWROOTPATH"]."index.php\">\n";
?>
       <input type="hidden" name="section" value="section3">
       <input type="hidden" name="subsection" value="userStats">
<?php
echo "       <input type=\"hidden\" name=\"V_TableName\" value=\"".$V_TableName."\">";
echo "       <input type=\"hidden\" name=\"V_Page\" value=\"".$V_PageNext."\">\n";
?>
       <input type="submit" value="&gt;&gt;">
       </form>
     </td>


     <!-- goto last page button  -->

     <td>
<?php
       echo "<form name=\"lastPage\" action=\"".$GLOBALS["XWROOTPATH"]."index.php\">\n";
?>
       <input type="hidden" name="section" value="section3">
       <input type="hidden" name="subsection" value="userStats">
<?php
echo "       <input type=\"hidden\" name=\"V_TableName\" value=\"".$V_TableName."\">";
echo "       <input type=\"hidden\" name=\"V_Page\" value=\"".$V_PageLast."\">\n";
?>
       <input type="submit" value="&gt;&gt;!">
       </form>
     </td>

<?php
    }
?>

   </tr>

   </table>
   </p>

  </td>
 </tr>

 <tr>
  <td>
   <br>

  </td>
 </tr>
</table>

<?php

  /*
   * If everything done, exit
   */
   if ($done == true) {
     if (isset ($nbDel))
       echo "<h3>".$nbDel." rows deleted</h3>\n";
     return;
   }

?>


<table WIDTH="92%" border ="0">

<?php

		/*
		 * This is a very special case : user has clicked on 'View associations'
		 * This is only possible if user were browsing works table
		 */
    if (isset ($V_Action) && ($V_Action == "Relations")) {

			$V_Conditions = "";
			for ($i = 0; $i < count($indices); $i++) {
				if ($V_Conditions != "")
					$V_Conditions = $V_Conditions." OR ";
				else
					$V_Conditions = $V_Conditions."(";
				$V_Conditions = $V_Conditions."(works.wid=".$indices [$i].")";
			}

			$V_Conditions = $V_Conditions.")";
			$V_TableName = "tasks";

		}

    /*
		 * Finally, view selected table
		 */

    echo "<h3><u>Table ".$V_TableName."</u></h3><br><br>\n";

		$firstRow = $V_Page * $PAGELENGTH;

    echo "<form name=\"sqlRows\" action=\"".$GLOBALS["XWROOTPATH"]."index.php\">\n";

    echo <<< EOF
    <input type="hidden" name="section" value="section3">
    <input type="hidden" name="subsection" value="userStats">
    <input type="hidden" name="V_TableName" value="$V_TableName">
    <input type="hidden" name="V_Action" value="">
EOF;

    dbDisplayTable ($V_TableName,
										$V_Conditions,
										$V_Login,
										$firstRow,
										$PAGELENGTH);
    echo "</form>";
?>

</table>

</center>
</blockquote>
</body>
</html>
