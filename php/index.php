<?php

/*
 * Set maintenance variable to 1 if you want to close your server
 */

$maintenance=0;

/*
 * Set myOwnAddr variable to your IP address if you want to be able to
 * access your server while maintening it.
 */

$myOwnAddr = "";

if ( ($maintenance == 1) && ($REMOTE_ADDR != $myOwnAddr)) {
?>

<html>
<head>
<title>
XWHEP : Server maintenance
</title>
</head>
<body>
<h1>
<center>
<u>
March 3rd, 2005
</u>
</center>
<br>

This server is under maintenance.<br>
Services would be available soon.<br>
<br>
Sorry for inconveniences.
<br>
<br>
</h1>

<address><a href="mailto:lodygens at lal.in2p3.fr">Oleg Lodygensky</a></address>.<br>
</body>
</html>

<?php
return;
}



include ("./phptmpl/PHPTMPL.php");


/*
 * Determine XtremWeb Root Directory
 */

$XWURL = (parse_url($_SERVER['REQUEST_URI']));
$XWURLS = split ("/", $XWURL[path]);
$XWROOTPATH="";
for ($i = 0; $i < count ($XWURLS) - 1; $i++)
  $XWROOTPATH = $XWROOTPATH.$XWURLS[$i]."/";

/*
 * Retreive variables, if any
 */

if (!isset($V_Session)) {
  if (isset($_COOKIE["sessionID"]))
    $V_Session = $_COOKIE["sessionID"];
}
if (!isset($V_Login)) {
  if (isset($_COOKIE["sessionLogin"]))
    $V_Login = $_COOKIE["sessionLogin"];
}

if (!isset($V_FName)) {
  if (isset($_COOKIE["sessionFName"]))
    $V_FName = $_COOKIE["sessionFName"];
}
if (!isset($V_LName)) {
  if (isset($_COOKIE["sessionLName"]))
    $V_LName = $_COOKIE["sessionLName"];
}

$phptmplSample = new Site("XtremWeb", "XtremWeb@LAL", "index.php", false);


$section1 = new Section("section1", "Main Pages");
$subsection1_1 = new Subsection("intro", "Introduction", "./textes/intro.php");
$section1->add($subsection1_1);
$subsection1_2 = new Subsection("new", "News", "./textes/news.html");
$section1->add($subsection1_2);
$subsection1_3 = new Subsection("contact", "Contact", "./textes/contact.php");
$section1->add($subsection1_3);
$subsection1_4 = new Subsection("doc", "Documentations", "./textes/docs.php");
$section1->add($subsection1_4);
$phptmplSample->add($section1);

$section2 = new Section("section2", "Working");

/*
$subsection2_0 = new Subsection("demo", "Demo", "./demo/demoPovRay.php");
$section2->add ($subsection2_0);
*/

if (!isset ($V_Session)) {
  $subsection2_1 = new Subsection("login", "Log in", "./textes/login.php");
  $section2->add($subsection2_1);

  $subsection2_5 = new Subsection("register", "Register", "./textes/register.php");
  $section2->add($subsection2_5);
}
else {
  $subsection2_2 = new Subsection("logout", "Log out<br />", "./textes/logout.php");
  $section2->add($subsection2_2);
  $subsection2_3 = new Subsection("submitTask", "Submit job", "./textes/submitTask.php");
  $section2->add($subsection2_3);
  $subsection2_4 = new Subsection("downloadworker", "Download worker", "./textes/download_worker.php");
  $section2->add($subsection2_4);
  $subsection2_6 = new Subsection("downloadclient", "Download client", "./textes/download_client.php");
  $section2->add($subsection2_6);
  $subsection2_5 = new Subsection("bugReport", "Bug report", "./textes/bugreport.php");
  $section2->add($subsection2_5);
}

if ((!isset ($V_Session)) &&
    (substr($REMOTE_ADDR, 0, 7) == "134.158")) {

  $subsection2_4 = new Subsection("downloadworker", "Download worker", "./textes/download_worker.php");
  $section2->add($subsection2_4);
}


$phptmplSample->add($section2);

$section3 = new Section("section3", "Stats");

/*
$subsection3_0 = new Subsection("ganglia", "Monitoring", "./textes/ganglia.php");
$section3->add($subsection3_0);
*/

$subsection3_1 = new Subsection("serverStats", "Server stats", "./textes/serverStats.php");
$section3->add($subsection3_1);

$subsection3_2 = new Subsection("workerStats", "Workers stats", "./textes/workerStats.php");
$section3->add($subsection3_2);

if (isset ($V_Session)) {
  if ($V_LName != "")
    $Titre = "$V_FName $V_LName stats";
  else if (isset ($V_Login))
    $Titre = "$V_Login stats";
  else
    die ("Something wrong with this session !!");

  $subsection3_2 = new Subsection("userStats", $Titre, "./textes/userStats.php");
  $section3->add($subsection3_2);
}

$phptmplSample->add($section3);

// Let's show !
$phptmplSample->toString();


?>

