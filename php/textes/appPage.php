<?php


  function make_seed() {
    list($usec, $sec) = explode(' ', microtime());
    return (float) $sec + ((float) $usec * 100000);
  }

  srand(make_seed());
  $randomDir = rand (0,25000) % 1000;


  if (isset($_COOKIE["sessionLogin"]))
    $V_Login = $_COOKIE["sessionLogin"];

	if (isset ($_POST["indice"])) {
		$appUID = $_POST["indice"];
	}
	if (isset ($_POST["V_AppUID"])) {
		$appUID = $_POST["V_AppUID"];
	}

  $connection = dbConnect();
  if ($connection == 0)
		 return 0;

  $request = "SELECT * FROM apps WHERE uid='".$appUID."'";
		 //	echo $request."<br>";
	$resultat = dbQuery($connection, $request); 
	$rows = mysql_fetch_array($resultat);
	$appName = $rows["name"];

  $request = "SELECT * FROM users WHERE login='".$V_Login."'";
		 //	echo $request."<br>";

	$resultat = dbQuery($connection, $request); 
	$rows = mysql_fetch_array($resultat);
	$userUID = $rows["uid"];


  /*
   * if any param given...
   */
  if (isset ($_POST["V_cmdLine"]))
    $cmdLine = $_POST["V_cmdLine"];
  if (isset ($_POST["V_AppName"]))
    $appName = $_POST["V_AppName"];
  if (isset ($_POST["V_stdinName"]))
    $stdinName = $_POST["V_stdinName"];
  if (isset ($_POST["V_dirinName"]))
    $dirinName = $_POST["V_dirinName"];


  echo "<blockquote>\n";
  echo "<p class=\"TITRE\">Add job for application &quot;".$appName."&quot;</p>\n";
  echo "<br>\n";


  if (isset ($V_nbOccurences)) {
    $connection = dbConnect();
    if ($connection == 0)
      return 0;


    $upload_err = 0;


    /*
     * Upload file dirin
     */
		//    if (is_uploaded_file($dirinName)) {
		if (isset($_FILES['V_dirinName']['name'])) {
      $dirinNameFileName = $_FILES['V_dirinName']['name'];
      $dirinNameFileNameDB = "'".$dirinNameFileName."'";
      $dirinNameFile = $_FILES['V_dirinName']['tmp_name'];
    }

		if($dirinNameFileNameDB == "''") {
      $dirinNameFileNameDB = "NULL";
    }

    /*
     * Upload file stdin
     */
		//    if (is_uploaded_file($stdinName)) {
		if (isset($_FILES['V_stdinName']['name'])) {
      $stdinNameFileName = $_FILES['V_stdinName']['name'];
      $stdinNameFileNameDB = "'".$stdinNameFileName."'";
      $stdinNameFile = $_FILES['V_stdinName']['tmp_name'];
    }

		if($stdinNameFileNameDB == "''") {
      $stdinNameFileNameDB = "NULL";
    }

    if (!isset ($V_nbOccurences))
      $V_nbOccurences = 1;

    echo "<center><h3>";

    for ($i = 0; $i < $V_nbOccurences; $i++) {


      /*
       * Find a random unique ID
       */

      $jobUID = rand (0,32000).":".rand (0,3000000).":".rand (0,128);

      $found = true;
      while ($found) {

				$request = "SELECT uid FROM works WHERE uid='".$jobUID."'";
        $resultat = dbQuery($connection, $request); 

				$found = false;
        while ($rows = mysql_fetch_array($resultat)) { 
					$found = true;
        }
				if ($found) {
					//					echo "found  ".$rows['uid']."<br>";
					$jobUID = rand (0,32000).":".rand (0,3000000).":".rand (0,128);
				}
				//				else
				//					echo "not found $jobUID<br>";
      }

			/*
			echo "jobUID       = $jobUID<br>";
			echo "appUID       = $appUID<br>";
			echo "cmdLine      = $cmdLine<br>";
			echo "dirinName    = $dirinName<br>";
			echo "stdInName    = $stdiInName<br>";
			echo "V_nbOccurences = $V_nbOccurences<br>";
			*/

      /*
       * Update database
			 * Let first note this work as 'INVALID' so that XW server will try to launch it
			 * until insertion is finished (i.e. until dir/files correctly inserted)
       */

      $request = "INSERT  INTO works (uid, app, user, status,resultStatus,cmdLine,dirinName,stdInName) VALUES ('".$jobUID."','".$appUID."','".$userUID."', 'INVALID','UNAVAILABLE','".$cmdLine."',".$dirinNameFileNameDB.",".$stdinNameFileNameDB.")";


			//      echo $request;
			//			echo "<br>".$randomDir;
			//			return;

      $resultat = dbQuery($connection, $request); 

			/*
      $request = "SELECT wid FROM works WHERE uid='".$jobUID."'";

      $wid = "";
      while ($wid == "") {
        $resultat = dbQuery($connection, $request); 
        while ($rows = mysql_fetch_array($resultat)) { 
          $wid = $rows["wid"];
          break;
        }
      }
			*/


      /*
       * create directory structure
       */
      $subdirName = configGetRootDirectory().$randomDir."/";
      $dirName = "".getcwd()."/$subdirName";
      echo "subdir    = $subdirName<br>";

      if (!file_exists ($dirName)) {
//        mkdir ($dirName, 0770) || die ("can't create ".$dirName);
        mkdir ($dirName, 0770) || $upload_err = 1;
        chmod ($dirName, 0770);
      }

			/*
      $dirName = $dirName.$V_Login."/";

      if (!file_exists ($dirName)) {
        mkdir ($dirName, 0770) || die ("can't create ".$dirName);
        chmod ($dirName, 0770);
      }

      $dirName = $dirName.$wid."/";

      if (!file_exists ($dirName)) {
        mkdir ($dirName, 0770) || die ("can't create ".$dirName);
        chmod ($dirName, 0770);
      }
			*/


      /*
       * save uploaded file dirin
       */
      if ((isset ($dirinNameFileName)) && ($upload_err == 0)) {

        echo $dirName.$dirinNameFileName."<br>";

        if (!move_uploaded_file($dirinNameFile, $dirName.$dirinNameFileName)) {
          $upload_err = 1;
					echo "$dirinNameFile, $dirName.$dirinNameFileName error <br>";
        }
				else {
					chmod ($dirName.$dirinNameFileName, 0770);
				}
			}


      /*
       * save uploaded file stdin
       */
      if (($stdinNameFileName != "") && ($upload_err == 0)) {
        if (!move_uploaded_file($stdinNameFile, $dirName.$stdinNameFileName)) {
          $upload_err = 1;
					echo "$stdinNameFile, $dirName.$stdinNameFileName error <br>";
        }
				else {
					chmod ($dirName.$stdinNameFileName, 0770);
				}
			}

      /*
       * Update database
			 * Here,  dir/files are correctly inserted
			 * We can now note this work as 'WAITING' so that XW server would launch it correctly
       */
      echo "Job ".$jobUID;
      if ($upload_err == 1) {
				$request = "DELETE FROM works WHERE uid='".$jobUID."'";
				$resultat = dbQuery($connection, $request); 

        echo " upload error...<br>";
			}
      else {
				
				echo "dirinNameFileNameDB = $dirinNameFileNameDB<br>";
				if($dirinNameFileNameDB != "NULL") {
					$dirinNameFileNameDB = $randomDir."/".$dirinNameFileName;
				}
				echo "dirinNameFileNameDB = $dirinNameFileNameDB<br>";

				echo "stdinNameFileNameDB = $stdinNameFileNameDB<br>";
				if($stdinNameFileNameDB != "NULL") {
					$stdinNameFileNameDB = $randomDir."/".$stdinNameFileName;
				}

				echo "stdinNameFileNameDB = $stdinNameFileNameDB<br>";

				$request = "UPDATE works set status='WAITING', local='true',active='true',dirinName='".$dirinNameFileNameDB."',stdInName='".$stdinNameFileNameDB."' WHERE uid='".$jobUID."'";

				echo $request."<br>";
				$resultat = dbQuery($connection, $request); 
        echo " successfully inserted ...<br>";
			}

    }

    echo "</h3></center><br>\n";


		echo "<form method=\"post\" action=\"".$GLOBALS["XWROOTPATH"]."index.php?section=section3&subsection=userStats\">\n";
	  echo "<input TYPE=\"SUBMIT\" VALUE=\"  Back to user stats main page  \"></form>\n";

    return;
  }


  echo "<form method=\"post\" name=\"sqlRows\" action=\"".$GLOBALS["XWROOTPATH"]."index.php\" enctype=\"multipart/form-data\">\n";

?>

  <input type="hidden" name="section" value="section2">
  <input type="hidden" name="subsection" value="submitTask">


<?php

  echo "<input type=\"hidden\" name=\"V_AppUID\" value=\"".$appUID."\">\n";
  echo "<input type=\"hidden\" name=\"V_AppName\" value=\"".$appName."\">\n";
	echo "<input type=\"hidden\" name=\"name\" value=\"toto\">\n";
?>

  <font size="+1" color="yellow">
Please, fill the following form (examples follow)
  </font>
  <br><br>


  <blockquote>
  <table border="0">

    <tr>
     <td colspan="2">
       <li>the ZIP file helps to recreate needed directories structure;
       <li>the input file will be passed as standard input;
       <li>the arguments are passed to the application (&nbsp;<i>as if the application would be called from any prompt shell</i>&nbsp;).

       <br><br>
     </td>
    </tr>

    <tr>
     <td width="30%">
       Zip file&nbsp;
     </td>
     <td>
		   <input type="file" name="V_dirinName" size="50"><i>
<?php
       echo "(Max size = ".ini_get('upload_max_filesize').")";
?>
       </i>
		 </td>
    </tr>

    <tr>
     <td width="30%">
       Input file&nbsp;
     </td>
     <td>
       <input type="file" name="V_stdinName" size="50"><i>
<?php
       echo "(Max size = ".ini_get('upload_max_filesize').")";
?>
		   </i>
     </td>
    </tr>

    <tr>
     <td width="30%">
       Arguments&nbsp;
     </td>
     <td>
       <input type="text" name="V_cmdLine" size="50">
     </td>
    </tr>

    <tr>
     <td width="30%">
       Occurences&nbsp;
     </td>
     <td>
       <input type="text" name="V_nbOccurences" size="3" value="1">
       <i><font color="lightgrey">(This specifies how many occurences of this work you want)
			 </font></i>
     </td>
    </tr>

    <tr>
     <td colspan="2" align="center">
       <input type="submit" value="Submit">
     </td>
    </tr>
  </table>
  </blockquote>
  </form>


<br><br>

<u>Example</u>:
<blockquote>
<ol>
<li> First of all, note the <i>Occurences</i> parameter&nbsp;; this tells how many occurences
     of the same work you want to submit.<br>
     Default is &quot;1&quot;&nbsp;; it inserts one work only.<br>
<br><br>

<li> suppose you would like to execute an application which name is <b>

<?php
     echo $appName;
?>

</b>, with the argument <b><i>-l</i></b><br>
In an shell, you would type :
<pre>

<?php
     echo "$&gt; ".$appName." -l";
?>

</pre>
       We can see that your application needs no input from standard input, neither it needs any dedicated directories structure...<br>

The previous form should then been filled like this&nbsp;:
 <ul>
  <li> leave <u>Zip file</u> <b>empty</b>;
  <li> leave <u>input file</u> <b>empty</b>;
  <li> fill <u>argument</u> with <b>-l</b>.
 </ul>

<br><br>

<li> now, if you want to execute what should be in an shell :
<pre>

<?php
     echo "$&gt; ".$appName." -b &lt; myFile.txt";
?>

</pre>
Here, your application needs a standard input (<i>myFile.txt</i>), an argument (<i>-b</i>), but still no dedicated directories structure...<br>

The previous form should then been filled like this&nbsp;:
 <ul>
  <li> leave <u>Zip file</u> <b>empty</b>;
  <li> fill <u>input file</u> with <b>myFile.txt</b> (use the "browse" button);
  <li> fill <u>argument</u> with <b>-b</b>.
 </ul>
<br><br>

<li> finally, to execute what should be in an shell :
<pre>

<?php
     echo "$&gt; ".$appName." myDirectory/myFile.txt";
?>

</pre>
Then, your application needs no standard input, no argument, but a dedicated directories structure containing your directory named <i>myDirectory</i>...<br>

The previous form should then been filled like this&nbsp;:
 <ul>
  <li> zip your directory named <i>myDirectory</i> in a zip file;
  <li> fill <u>zip file</u> with <b>your zip file</b> (use the "browse" button);
  <li> leave <u>input file</u> <b>empty</b>;
  <li> leave <u>argument</u> <b>empty</b>.
 </ul>

<br><br>
<li> Of course, all parameters combinaisons shown above are allowed...


</ol>

</blockquote>
