<?php

/*
 * File   : db/library.php
 * Date   : 03/08/2003
 * Author : Oleg Lodygensky
 * Email  : lodygens@lal.in2p3.fr
 */





/* ---------------------------------------------- */
Function dbGetTableColumns ($table)
/* ---------------------------------------------- */
/* This returns columns names for the given table */
/* @param : $table is the table name              */
/* @return : an array of String contaning the     */
/*           columns names                        */
/* ---------------------------------------------- */
{
  $connection = dbConnect();
  if ($connection == 0)
    return 0;

  $request = "SHOW COLUMNS FROM ".$table;
  $resultat = dbQuery($connection, $request); 
  $nb_enr = mysql_num_rows($resultat);

  $i = 0;
  $columns = array ();

  while ($rows = mysql_fetch_array($resultat)) { 

    $columns [$i++] = $rows["Field"];
  }

  return $columns;
}


/* ---------------------------------------------- */
Function dbCountRows ($tableName, $conditions = "")
/* ---------------------------------------------- */
/* This returns the rows amount, according to the */
/* conditions, if any                             */
/* @param $tableName is the name of the table     */
/* @param $conditions is extraction conditions    */
/* @return : an integer containing the row amount */
/* ---------------------------------------------- */
{
  $connection = dbConnect ();
  if ($connection == 0)
    return -1;

  $request = "SELECT COUNT(*) FROM ".$tableName;

  if ($conditions != "") {
    $request = $request." WHERE ".$conditions;
  }


  $resultat = dbQuery($connection, $request); 
  $nb_enr = mysql_num_rows($resultat);
  $rows= mysql_fetch_array($resultat);

  return $rows[0];
}


/* ---------------------------------------------- */
Function dbCountUserRows ($V_Login, $tableName, $conditions = "")
/* ---------------------------------------------- */
/* This returns the rows amount for the given user*/
/* into the gien table, according to the          */
/* conditions, if any                             */
/* @param $V_Login is the user login              */
/* @param $tableName is the name of the table     */
/* @param $conditions is extraction conditions    */
/* @return : an integer containing the row amount */
/* ---------------------------------------------- */
{
  if ($tableName == "works")  return dbCountUserWorks ($V_Login, $conditions);
  if ($tableName == "tasks")  return dbCountUserTasks ($V_Login, $conditions);
}


Function dbLogin2UID($login) {

  $connection = dbConnect ();
  $request = "Select uid from users where login='$login'";

  $resultat = dbQuery($connection, $request); 
  $rows = mysql_fetch_array($resultat);
  
  return $rows[0];
}

/* ---------------------------------------------- */
Function dbCountUserWorks ($V_Login, $conditions = "")
/* ---------------------------------------------- */
/* This returns the works amount, for the given   */
/* user                                           */
/* @param $V_Login is the user login              */
/* @param $conditions is extraction conditions    */
/* @return : an integer containing the row amount */
/* ---------------------------------------------- */
{
  $connection = dbConnect ();
  if ($connection == 0)
    return -1;

  $tableName = "works";
  if ($conditions != "")
    $conditions = $conditions." AND ";
  $conditions = $conditions." (user = '".dbLogin2UID($V_Login)."')";

  return dbCountRows ($tableName, $conditions);
}


/* ---------------------------------------------- */
Function dbCountUserTasks ($V_Login, $conditions = "")
/* ---------------------------------------------- */
/* This returns the tasks amount, for the given   */
/* user                                           */
/* @param $V_Login is the user login              */
/* @param $conditions is extraction conditions    */
/* @return : an integer containing the row amount */
/* ---------------------------------------------- */
{
  $connection = dbConnect ();
  if ($connection == 0)
    return -1;

  $tableName = "works, tasks";
  if ($conditions != "")
    $conditions = $conditions." AND ";
  $conditions = $conditions." (works.user = '".dbLogin2UID($V_Login)."') AND (tasks.uid = works.uid)";

  return dbCountRows ($tableName, $conditions);
}



/* ------------------------------------------ */
Function dbConnect ()
/* ------------------------------------------ */
{
  $dbHost = configGetDbHost ();
  $dbUser = configGetDbUser ();
  $dbUserPassword = configGetDbUserPassword ();

  //$errLevel = error_reporting (0);

  $connection = mysql_connect($dbHost, $dbUser, $dbUserPassword);
  if ($connection == FALSE)
    displayMessage ("DB connection error", "unable to connect to database",
                    "http://".configGetDispatcherHost ().$GLOBALS["XWROOTPATH"]);

  //error_reporting ($errLevel);
  return $connection;
}

/* ------------------------------------------ */
Function dbCreate ()
/* ------------------------------------------ */
{
  $dbName = configGetDbName ();
  $connection = dbConnect ();

  $request = "CREATE DATABASE IF NOT EXISTS ".$dbName.";";
  $resultat = mysql_query($request, $connection)
    or die ("DB create non reussie");

  $request = "CREATE TABLE IF NOT EXISTS ".$dbName.".users (".
            "code int(11) DEFAULT '0' NOT NULL auto_increment,".
            "login char(25) NOT NULL,".
            "password char(25) NOT NULL,".
            "email char(50),".
            "fname char(25),".
            "lname char(25),".
            "team char(25),".
            "country char(25),".
            "PRIMARY KEY (code),".
            "INDEX (login),".
            "INDEX (email),".
            "INDEX (country),".
            "INDEX (team)".
            ");";

  $resultat = mysql_query($request, $connection)
    or die ("Table creation non reussie");
  return;
}

/* ---------------------------------------------- */
Function dbGetPhpFromApps ($code)
/* ---------------------------------------------- */
/* This retreive php page for given application   */
/* @param code is the application code in table   */
/* @return a String containing the page name      */
/* ---------------------------------------------- */
{
  $connection = dbConnect();
  if ($connection == 0)
    return 0;

  $request = "SELECT * FROM apps WHERE code =".$code;
  $resultat = dbQuery($connection, $request); 
  $nb_enr = mysql_num_rows($resultat);

  $found = 0;

  while ($rows = mysql_fetch_array($resultat)) { 

    return $rows["wwwpath"];
  }

  return "";
}


/* ------------------------------------------ */
Function dbQuery ($connection, $request)
/* ------------------------------------------ */
{
  //  $errLevel = error_reporting (0);

  $dbName = configGetDbName ();
  mysql_select_db($dbName, $connection);
  $resultat = mysql_query($request, $connection); 

  //  error_reporting ($errLevel);

  return $resultat;
}

/* ---------------------------------------------- */
Function dbDeleteFromTable ($user, $table, $uids)
/* ---------------------------------------------- */
/* This deletes rows from table                   */
/* @param : $table is a String as the table name  */
/* @param : $uids is an array of work UID         */
/* @param : $user is the user name (optionnal)    */
/* @return: nb of rows deleted                    */
/* ---------------------------------------------- */
{
  if($table == "works") return dbDeleteFromTableWorks ($user, $uids);
  if($table == "tasks") return dbDeleteFromTableTasks ($user, $uids);
}


/* ---------------------------------------------- */
Function dbDeleteFromTableWorks ($user, $uids)
/* ---------------------------------------------- */
/* This deletes rows from table Works             */
/* This also deletes associated rows from         */
/*           table Tasks                          */
/* @param : $user is the user name (optionnal)    */
/* @param : $uids is an array of work UID         */
/* @return: nb of rows deleted                    */
/* ---------------------------------------------- */
{
  $connection = dbConnect();
  if ($connection == 0)
    return 0;

  $conditions = "";

  for ($i = 0; $i < count ($uids); $i++) {
		if ($i == 0)
			$conditions = " WHERE ";
    $conditions = $conditions." uid='".$uids[$i]."'";
    if ($i < count ($uids) - 1)
      $conditions = $conditions." OR";
  }

  // retreive output dir/file
  $request = "SELECT dirOutName FROM works";

  $resultat = dbQuery($connection, $request.$conditions); 
  $dirs = array();

	if (is_dir (configGetRootDirectory()) === false) {
      echo "<table><tr><td bgcolor=\"red\"><h3>This server configuration does not allow deletions<br />Please use the XtremWeb client instead</h3></td></tr></table>\n";
      return;
  }

  $i = 0;
  while ($rows = mysql_fetch_array($resultat)) { 
    $dirs [$i++] = $rows["dirOutName"];
  }

  // delete from table Works
  $request = "DELETE FROM works";

  $resultat = dbQuery($connection, $request.$conditions); 

  // delete from table Tasks
  $request = "DELETE FROM tasks";
  $resultat = dbQuery($connection, $request.$conditions); 

  // delete from disks

  for ($i = 0; $i < count ($uids); $i++) {

		if ($dirs [$i] == "")
			continue;

		$fileName = configGetRootDirectory().$dirs [$i];
		if (file_exists ($fileName))
				unlink ($fileName);

/*
		$dirName = substr ($fileName, 0, strrpos ($fileName, "/"));
    if (is_dir ($dirName))
      rmdir ($dirName);
*/
  }

  return count ($uids);
}


/* ---------------------------------------------- */
Function dbDeleteFromTableTasks ($user, $uids)
/* ---------------------------------------------- */
/* This deletes rows from table Tasks             */
/* This also deletes associated rows from         */
/*           table Works                          */
/* @param : $user is the user name (optionnal)    */
/* @param : $uids is an array of task UID         */
/* @return: nb of rows deleted                    */
/* ---------------------------------------------- */
{
  $connection = dbConnect();
  if ($connection == 0)
    return 0;


  // first, search for associated rows in table Works

  $request = "SELECT uid FROM tasks WHERE ";
  $conditions = "";

  for ($i = 0; $i < count ($uids); $i++) {
    $conditions = $conditions." uid=".$uids[$i];
    if ($i < count ($uids) - 1)
      $conditions = $conditions." OR";
  }

  // then, find uid of Works to delete
  $resultat = dbQuery($connection, $request.$conditions); 
  $uids = array();
  $i = 0;
  while ($rows = mysql_fetch_array($resultat)) { 
    $uids[$i++] = $rows["wid"];
  }

  // this deletes rows from table Works **and** from table Tasks too!
  return dbDeleteFromTableWorks ($user, $uids);
}


/* ---------------------------------------------- */
Function dbGetResultsFromTable ($user, $table, $uids)
/* ---------------------------------------------- */
/* This retreive results from table               */
/* @param : $user is the user name                */
/* @param : $table is a String as the table name  */
/* @param : $uids is an array of jobs UID         */
/* ---------------------------------------------- */
{
  if($table == "works") dbGetResultsFromTableWorks ($user, $uids);
  if($table == "tasks") dbGetResultsFromTableTasks ($user, $uids);
}


/* ---------------------------------------------- */
Function dbGetResultsFromTableWorks ($user, $uids)
/* ---------------------------------------------- */
/* This retreive results from table Works         */
/* @param : $user is the user name                */
/* @param : $uids is an array of jobs UID         */
/* ---------------------------------------------- */
{
  $connection = dbConnect();
  if ($connection == 0)
    return 0;

  $conditions = "";

  for ($i = 0; $i < count ($uids); $i++) {
		if($conditions == "") 
			$conditions = " WHERE "; 
    $conditions = $conditions." uid=\"".$uids[$i]."\"";
    if ($i < count ($uids) - 1)
      $conditions = $conditions." OR";
  }

  // retreive task codes to delete from disk 
  $request = "SELECT uid FROM tasks";

  // then, find code of Tasks to delete
  $resultat = dbQuery($connection, $request.$conditions); 
  $tid = array();
  $i = 0;
  while ($rows = mysql_fetch_array($resultat)) { 
    $tid [$i++] = $rows["code"];
  }

  // retreive application names
  $request = "SELECT * FROM works";
  $resultat = dbQuery($connection, $request.$conditions); 
  $columns = dbGetTableColumns ("works");
  $nbColumns = count($columns);

  dbDisplayTableHeader ("Works", $columns);

  $contentIdx = 0;
  $found = 0;
  $i = 0;

  while ($rows = mysql_fetch_array($resultat)) { 

    $found = 1;

    $contentClass = "CONTENT".(($contentIdx++ % 2) + 1);

    echo "<tr>\n";
		$zipFile = $GLOBALS["XWROOTPATH"]."db/".$rows["dirOutName"];
		if (file_exists ($zipFile)) {

      echo "<td class=\"".$contentClass."\"><a href=\"".$zipFile."\">Download</a></td>";

      for ($i = 0; $i < count ($columns); $i++) {
        echo "<td class=\"".$contentClass."\">".$rows[$columns[$i]]."</td>\n";
      }
    }
    else {
      echo "<td align=\"left\" bgcolor=\"red\" colspan=\"".count($rows)."\"><i>This server configuration does not allow downloads<br />Please use the XtremWeb client instead</i></td></tr></table>\n";
      return;
    }
    echo "</tr>\n";
  }


  if ($found == 0)
    echo "<tr><td CLASS=\"CONTENT1\" colspan=\"".$nbColumns."\"><i><b>N/A</b></i></td></tr>\n";

  echo "</table>\n";
}


/* ---------------------------------------------- */
Function dbGetResultsFromTableTasks ($user, $tid)
/* ---------------------------------------------- */
/* This retreive results from table Works         */
/* @param : $table is a String as the table name  */
/* @param : $indices are primary key values of    */
/*          rows to retreive results for          */
/* @param : $user is the user name                */
/* ---------------------------------------------- */
{
  $connection = dbConnect();
  if ($connection == 0)
    return 0;


  // first, search for associated rows in table Works

  $request = "SELECT wid FROM tasks WHERE ";
  $conditions = "";

  for ($i = 0; $i < count ($tid); $i++) {
    $conditions = $conditions." code=".$tid[$i];
    if ($i < count ($tid) - 1)
      $conditions = $conditions." OR";
  }

  // then, find wid of Works to delete
  $resultat = dbQuery($connection, $request.$conditions); 
  $wid = array();
  $i = 0;
  while ($rows = mysql_fetch_array($resultat)) { 
    $wid[$i++] = $rows["wid"];
  }

  // this deletes rows from table Works **and** from table Tasks too!
  dbGetResultsFromTableWorks ($user, $wid);
}


/* ---------------------------------------------- */
Function dbDisplayTableSummary ($V_Login, $tableName, $content, $texts, $conditions)
/* ---------------------------------------------- */
/* This display summary of a given table          */
/* It first displays the total number of row in   */
/* the table, including an 'edit' button          */
/* It also includes table details, accordingly to */
/* the provided conditions                        */
/* @param $V_Login is the user login              */
/* @param $tableName is the table name            */
/* @param $content is the TD class to use         */
/* @param $texts is an array containing details   */
/*        informations                            */
/* @param $conditions is an array containing      */
/*        conditions to detail table              */
/* ---------------------------------------------- */
{
  $nbRows = dbCountUserRows ($V_Login, $tableName);


  echo "<div align=\"center\">\n";
  echo "<form METHOD=\"POST\" ACTION=\"".$GLOBALS["XWROOTPATH"]."index.php\">\n";

echo <<< EOF

  <h3>$tableName&nbsp;<i>($nbRows rows)</i>

  <input type="HIDDEN" name="section" value="section3">
  <input type="HIDDEN" name="subsection" value="userStats">
  <input type="HIDDEN" name="V_TableName" value="$tableName">
  <input type="SUBMIT" value="  Edit  ">
  </form></h3><br>
  </div>


  <table border="1">

EOF;

  for ($i = 0; $i < count($conditions); $i++) {
    
    $text = $texts[$i];
    $condition = $conditions[$i];
    $nbRows = dbCountUserRows ($V_Login, $tableName, $condition);

    echo "<tr><td width=\"70%\">\n";
    echo "<form METHOD=\"POST\" ACTION=\"".$GLOBALS["XWROOTPATH"]."index.php\">\n";

echo <<< EOF

    &nbsp;&nbsp;$nbRows $text

    <input type="HIDDEN" name="section" value="section3">
    <input type="HIDDEN" name="subsection" value="userStats">
    <input type="HIDDEN" name="V_TableName" value="$tableName">
    <input type="HIDDEN" name="V_Conditions" value="$condition">
    </td><td width="30%" align="center">
EOF;
    if ($nbRows > 0)
      echo "<input type=\"SUBMIT\" value=\"  Edit  \">\n";

echo <<< EOF


    </form><br>
    </td></tr>
EOF;

  }

  echo "</table>\n";
}

 
/* ---------------------------------------------- */
Function dbDisplayTableHeader ($tableName, $columns)
/* ---------------------------------------------- */
/* This display header of a given table           */
/* @param : $tableName is the name of the table   */
/* @param : $columns is an array of String        */
/* ---------------------------------------------- */
{
  $nbColumns = count($columns);

  echo "<table border =\"1\" bordercolor=\"#e5dc7e\">\n";
  /*
  echo "<caption align=\"top\">".$tableName."</caption>\n";
  */
  echo "<tr>\n";
  echo "<th>&nbsp;</th>\n";

  for ($i = 0; $i < $nbColumns; $i++) {
    echo "<th>".$columns[$i]."</th>\n";
  }

  echo "</tr>\n";
}


/* ---------------------------------------------- */
Function dbDisplayTableRow ($rows, $columns, $contentClass, $buttonType = "checkbox")
/* ---------------------------------------------- */
/* This display a table rows of the given table   */
/* @param rows is a rows set from MySQL           */
/* @param columns is the table columns set        */
/* @param contentClass is an HTML TD class        */
/* @param buttonType is an HTML button type       */
/* ---------------------------------------------- */
{
	echo "<tr>\n";
  if ($buttonType == "checkbox")
		$name = "indices[]";
  else
    $name="indice";

  echo "<td class=\"".$contentClass."\"><input type=\"".$buttonType."\" name=\"".$name."\" value=\"".$rows ["uid"]."\"></td>\n";
  for ($i = 0; $i < count ($columns); $i++) {
	  echo "<td class=\"".$contentClass."\" name=\"".$columns[$i]."s[]\">".$rows [$columns[$i]]."</td>\n";
  }

	echo "</tr>\n";
}


/* ---------------------------------------------- */
Function dbDisplayTable ($table, $conditions = "", $user = "", $firstRow = 0, $pageLength = 100)
/* ---------------------------------------------- */
/* This display table contents of the given table */
/* for the given user, if provided                */
/* @param : $table is a String as the table name  */
/* @param : $conditions id the request conditions */
/* @param : $user is the user name (optionnal)    */
/* @param : $firstRow is the first row primary    */
/*          key to display (optionnal)            */
/* @param : $pageLength is the number of row to   */
/*          display (optionnal)                   */
/* @return: 0 on success, 1 otherwise             */
/* ---------------------------------------------- */
{
  if      ($table == "users") return dbDisplayTableUsers ($conditions, $firstRow, $pageLength);
  else if ($table == "hosts") return dbDisplayTableHosts ($user, $conditions, $firstRow, $pageLength);
  else if ($table == "apps")  return dbDisplayTableApps  ($user, $conditions, $firstRow, $pageLength);
  else if ($table == "tasks") return dbDisplayTableTasks ($user, $conditions, $firstRow, $pageLength);
  else if ($table == "works") return dbDisplayTableWorks ($user, $conditions, $firstRow, $pageLength);
}


/* ---------------------------------------------- */
Function dbDisplayTableUsers ($conditions="", $firstRow = 0, $pageLength = 100)
/* ---------------------------------------------- */
/* This displays Users table                      */
/* @see dbDisplayTable ()                         */
/* ---------------------------------------------- */
{
  $connection = dbConnect();
  if ($connection == 0)
    return 1;

  $request = "SELECT * FROM users";
  if ($conditions != "")
    $request = $request. " WHERE ".$conditions;

  $resultat = dbQuery($connection, $request); 
  $nb_enr = mysql_num_rows($resultat);

  $columns = dbGetTableColumns ("users");
  $nbColumns = count($columns);

  dbDisplayTableHeader ("Users", $columns);

  $contentIdx = 0;
  $found = 0;

  while ($rows = mysql_fetch_array($resultat)) { 

    $found = 1;

    $contentClass = "CONTENT".(($contentIdx++ % 2) + 1);
    dbDisplayTableRow ($rows, $columns, $contentClass);

  }

  if ($found == 0)
    echo "<tr><td CLASS=\"CONTENT1\" colspan=\"".$nbColumns."\"><i><b>N/A</b></i></td></tr>\n";

  echo "</table>\n";

  return 0;
}


/* ---------------------------------------------- */
Function dbDisplayTableWorks ($user = "", $conditions, $firstRow = 0, $pageLength = 100)
/* ---------------------------------------------- */
/* This display Works table                       */
/* @see dbDisplayTable ()                         */
/* ---------------------------------------------- */
{
  $connection = dbConnect();
  if ($connection == 0)
    return 1;

  $request = "SELECT * FROM works";

  if ($conditions != "")
    $request = $request. " WHERE ".$conditions;

  if ($user != "")
	  if ($conditions != "")
  	  $request = $request." AND (user=\"".dbLogin2UID($user)."\")";
		else
  	  $request = $request." WHERE user=\"".dbLogin2UID($user)."\"";

  $request = $request." ORDER BY arrivalDate DESC LIMIT ".$firstRow.",".$pageLength;

  $resultat = dbQuery($connection, $request); 
  $nb_enr = mysql_num_rows($resultat);

  $columns = dbGetTableColumns ("works");
  $nbColumns = count($columns);

  dbDisplayTableHeader ("Works", $columns);

  $contentIdx = 0;
  $nbFound = 0;

  while (($rows = mysql_fetch_array($resultat)) && ($nbFound < $pageLength)) { 

    $nbFound++;
    
    $contentClass = "CONTENT".(($contentIdx++ % 2) + 1);
		if(($rows["status"] == "COMPLETED") 
				|| ($rows["status"] == "ERROR") 
				|| ($rows["status"] == "WAITING"))
	    $contentClass = $rows["status"];

    dbDisplayTableRow ($rows, $columns, $contentClass);
  }

  if ($nbFound == 0)
    echo "<tr><td CLASS=\"CONTENT1\" colspan=\"".$nbColumns."\"><i><b>N/A</b></i></td></tr>\n";

  echo "</TABLE>\n";

  return 0;
}


/* ---------------------------------------------- */
Function dbDisplayTableTasks ($user = "", $conditions, $firstRow = 0, $pageLength = 100)
/* ---------------------------------------------- */
/* This display Tasks table                       */
/* @see dbDisplayTable ()                         */
/* ---------------------------------------------- */
{
  $connection = dbConnect();
  if ($connection == 0)
    return 1;

  $request = "SELECT * FROM tasks, works WHERE tasks.uid=works.uid";

  $request = $request." AND (tasks.code >= ".$firstRow.") AND (tasks.code < ".($firstRow + $pageLength).")";

  if ($user != "")
    $request = $request." AND works.user=\"".dbLogin2UID($user)."\"";

  if ($conditions != "")
    $request = $request. " AND ".$conditions;

  $resultat = dbQuery($connection, $request); 
  $nb_enr = mysql_num_rows($resultat);

  $columns = dbGetTableColumns ("tasks");
  $nbColumns = count($columns);

  dbDisplayTableHeader ("Tasks", $columns);

  $contentIdx = 0;
  $nbFound = 0;

  while (($rows = mysql_fetch_array($resultat)) && ($nbFound < $pageLength)) { 
 
    $nbFound++;

    $contentClass = "CONTENT".(($contentIdx++ % 2) + 1);
    dbDisplayTableRow ($rows, $columns, $contentClass);
  }

  if ($nbFound == 0)
    echo "<tr><td CLASS=\"CONTENT1\" colspan=\"".$nbColumns."\"><i><b>N/A</b></i></td></tr>\n";

  echo "</TABLE>\n";

  return 0;
}


/* ---------------------------------------------- */
Function dbDisplayTableApps ($user = "", $conditions = "", $firstRow = 0, $pageLength = 100)
/* ---------------------------------------------- */
/* This display Apps table                        */
/* @see dbDisplayTable ()                         */
/* ---------------------------------------------- */
{
  $connection = dbConnect();
  if ($connection == 0)
    return 1;

  $request = "SELECT * FROM apps";

  if ($conditions != "")
    $request = $request. " WHERE ".$conditions;

  $resultat = dbQuery($connection, $request); 
  $nb_enr = mysql_num_rows($resultat);

  $columns = dbGetTableColumns ("apps");
  $nbColumns = count($columns);

  dbDisplayTableHeader ("Apps", $columns);

  $contentIdx = 0;
  $found = 0;

  while ($rows = mysql_fetch_array($resultat)) { 

    $found = 1;

    $contentClass = "CONTENT".(($contentIdx++ % 2) + 1);

    dbDisplayTableRow ($rows, $columns, $contentClass, "radio");
  }

  if ($found == 0)
    echo "<tr><td CLASS=\"CONTENT1\" colspan=\"".$nbColumns."\"><i><b>N/A</b></i></td></tr>\n";

  echo "</TABLE>\n";

  return 0;
}


/* ---------------------------------------------- */
Function dbDisplayTableHosts ($user = "", $conditions, $firstRow = 0, $pageLength = 100)
/* ---------------------------------------------- */
/* This display Hosts table                       */
/* @see dbDisplayTable ()                         */
/* ---------------------------------------------- */
{
  $connection = dbConnect();
  if ($connection == 0)
    return 1;

  $request = "SELECT * FROM hosts";

  if ($conditions != "")
    $request = $request. " WHERE ".$conditions;

  $resultat = dbQuery($connection, $request); 
  $nb_enr = mysql_num_rows($resultat);

  $columns = dbGetTableColumns ("hosts");
  $nbColumns = count($columns);

  dbDisplayTableHeader ("Hosts", $columns);

  $contentIdx = 0;
  $found = 0;

  while ($rows = mysql_fetch_array($resultat)) { 

    $found = 1;

    $contentClass = "CONTENT".(($contentIdx++ % 2) + 1);
    dbDisplayTableRow ($rows, $columns, $contentClass);
  }

  if ($found == 0)
    echo "<tr><td CLASS=\"CONTENT1\" colspan=\"".$nbColumns."\"><i><b>N/A</b></i></td></tr>\n";

  echo "</TABLE>\n";

  return 0;
}


/* ---------------------------------------------- */
Function dbDisplayUser ($row)
/* ---------------------------------------------- */
/* This display a Users table row                 */
/* @param : $row is the row data                 */
/* ---------------------------------------------- */
{
  echo " <TR ALIGN=\"CENTER\" VALIGN=\"CENTER\">\n";
  echo  "  <TD>Code</TD><TD><INPUT TYPE=\"HIDDEN\" NAME=\"V_Code\" VALUE=\"".$row["code"]."\">".$row["code"]."</TD></TR>\n";
  echo  "  <TR><TD>E-mail</TD><TD>".$row["email"]."</TD></TR>\n";
  echo  "  <TR><TD>Password</TD><TD>**********</TD></TR>\n";
  echo  "  <TR><TD>First name</TD><TD>".$row["fname"]."</TD></TR>\n";
  echo  "  <TR><TD>Last name</TD><TD>".$row["lname"]."</TD></TR>\n";
  echo  "  <TR><TD>Team</TD><TD>".$row["team"]."</TD></TR>\n";
  echo  "  <TR><TD>Country</TD><TD>".$row["country"]."</TD></TR>\n";
  echo  "  <TR><TD>Right level</TD><TD>".$row["rights"]."</TD></TR>\n";
}


/* ---------------------------------------------- */
Function dbEditUser ($row)
/* ---------------------------------------------- */
/* This display a Users table row in edit mode    */
/* @param : $row is the row data                 */
/* ---------------------------------------------- */
{
  echo " <TR ALIGN=\"CENTER\" VALIGN=\"CENTER\">\n";
  echo  "  <TD>Code</TD><TD><INPUT TYPE=\"HIDDEN\" NAME=\"V_Code\" VALUE=\"".$row["code"]."\">".$row["code"]."</TD></TR>\n";
  echo  "  <TR><TD>E-mail</TD><TD><INPUT TYPE=\"TEXT\" NAME=\"V_Email\" VALUE=\"".$row["email"]."\"></TD></TR>\n";
  echo  "  <TR><TD>Login</TD><TD><INPUT TYPE=\"TEXT\" NAME=\"V_Login\" VALUE=\"".$row["login"]."\"></TD></TR>\n";
  echo  "  <TR><TD>Password</TD><TD>*****</TD></TR>\n";
  echo  "  <TR><TD>First name</TD><TD><INPUT TYPE=\"TEXT\" NAME=\"V_Prenom\" VALUE=\"".$row["fname"]."\"></TD></TR>\n";
  echo  "  <TR><TD>Last name</TD><TD><INPUT TYPE=\"TEXT\" NAME=\"V_Nom\" VALUE=\"".$row["lname"]."\"></TD></TR>\n";
  echo  "  <TR><TD>Team</TD><TD><INPUT TYPE=\"TEXT\" NAME=\"V_Team\" VALUE=\"".$row["team"]."\"></TD></TR>\n";
  echo  "  <TR><TD>Country</TD><TD><INPUT TYPE=\"TEXT\" NAME=\"V_Country\" VALUE=\"".$row["country"]."\"></TD></TR>\n";
  echo  "  <TR><TD>Right level</TD><TD><INPUT TYPE=\"TEXT\" NAME=\"V_Rights\" VALUE=\"".$row["rights"]."\"></TD></TR>\n";
}


/* ---------------------------------------------- */
Function dbGetUserRights ($user)
/* ---------------------------------------------- */
/* This retreives user rights from table users    */
/* @return an int : the user rights               */
/*                  -1 on error                   */
/* ---------------------------------------------- */
{
  $connection = dbConnect();
  if ($connection == 0)
    return -1;

  $request = "SELECT rights FROM users WHERE login='".$user."'";

  $resultat = dbQuery($connection, $request); 
  $nb_enr = mysql_num_rows($resultat);

  $rows = mysql_fetch_array($resultat);
	if ($rows == FALSE)
		return -1;

	return $rows["rights"];
}

?>
