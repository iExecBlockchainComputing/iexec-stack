<?php

  /*
   * File   : html_graph/library.php
   * Date   : Sep 26th, 2003
   * Author : Oleg Lodygensky
   * Email  : lodygens@lal.in2p3.fr
   */




  /* ------------------------------------------ */
function graphTask ($user = "", $delay="week") {
  /*
   * @user is the user name
   * @param delay of tasks
   *        "week" for the last wekk tasks
   *        "month" for the last month tasks
   *        "year" for the last year tasks
   * @return: 0 on success
   *          1 if no task found
   */
  /* ------------------------------------------ */

  if ($user == "") { 
    if ($delay == "hour")
      $request = "SELECT COUNT(status) AS nbTasks, TO_DAYS(StartDate) AS period,DATE_FORMAT(StartDate,'%b %d') AS same_day, status FROM tasks WHERE status='COMPLETED' or status='ERROR' and ((UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(StartDate) <= 3600)) GROUP BY period, status ORDER BY period";
    if ($delay == "day")
      $request = "SELECT COUNT(status) AS nbTasks, TO_DAYS(StartDate) AS period,DATE_FORMAT(StartDate,'%b %d') AS same_day, status FROM tasks WHERE status='COMPLETED' or status='ERROR' and ((TO_DAYS(NOW()) - TO_DAYS(StartDate) = 0)) GROUP BY period, status ORDER BY period";
    if ($delay == "week")
      $request = "SELECT COUNT(status) AS nbTasks, TO_DAYS(StartDate) AS period,DATE_FORMAT(StartDate,'%b %d') AS same_day, status FROM tasks WHERE (status='COMPLETED' or status='ERROR') and ((TO_DAYS(NOW()) - TO_DAYS(StartDate) < 7)) GROUP BY period, status ORDER BY period";
    if ($delay == "month")
      $request = "SELECT COUNT(status) AS nbTasks, TO_DAYS(StartDate) AS day,DATE_FORMAT(StartDate,'%b %d') AS period, status FROM tasks WHERE (status='COMPLETED' or status='ERROR') and  ((TO_DAYS(NOW()) - TO_DAYS(StartDate) < 31)) GROUP BY period,status ORDER BY period";
    if ($delay == "year")
      $request = "SELECT COUNT(status) AS nbTasks, TO_DAYS(StartDate) AS day,DATE_FORMAT(StartDate,'%b/%y') AS period, status FROM tasks WHERE (status='COMPLETED' or status='ERROR') and ((TO_DAYS(NOW()) - TO_DAYS(StartDate) < 450)) GROUP BY period, status ORDER BY laststartdate";

    $labelTail = "";
  }
  else {

    if ($delay == "week")
      $request = "SELECT COUNT(tasks.status) AS nbTasks, TO_DAYS(tasks.StartDate) AS period,DATE_FORMAT(tasks.StartDate,'%a') AS same_day, tasks.status, works.userName FROM tasks,works WHERE ((TO_DAYS(NOW()) - TO_DAYS(StartDate) < 7))";
    else if ($delay == "month")
      $request = "SELECT COUNT(tasks.status) AS nbTasks, TO_DAYS(tasks.StartDate) AS day,DATE_FORMAT(tasks.StartDate,'%b %d') AS period, tasks.status, works.userName FROM tasks,works WHERE ((TO_DAYS(NOW()) - TO_DAYS(StartDate) < 31))";
    else if ($delay == "year")
      $request = "SELECT COUNT(tasks.status) AS nbTasks, TO_DAYS(tasks.StartDate) AS day,DATE_FORMAT(tasks.StartDate,'%b') AS period, tasks.status, works.userName FROM tasks,works WHERE ((TO_DAYS(NOW()) - TO_DAYS(StartDate) < 365))";

    $request = $request." AND (tasks.wid=works.wid) AND (works.userName ='".$user."')";

    $request = $request." GROUP BY period, status ORDER BY period";

    $labelTail = "for ".$user;
  }

  $connection = dbConnect ();
  $resultat = dbQuery($connection, $request);

  if ($resultat)
    $nb_enr   = mysql_num_rows( $resultat );


  if ($nb_enr == 0)
    return 1;

  $i = $index = $largest = 0;

  $i=-1;
  $olddate= "";

  while ($activity = mysql_fetch_array ($resultat)) {
		
    $status  = $activity[3];
    $date    = $activity[2];
    $nbTasks = $activity[0];
    
    if($olddate != $date) {
      $i++;
      $olddate=$date;
    }

    if (!$lostTasks[$i])
      $lostTasks[$i]=0;
    if (!$completedTasks[$i])
      $completedTasks[$i]=0;

    //      echo " i: $i  status: $status  date: $jours_semaine[$date] ::: $nbTasks<br>\n";
    if ($status == "COMPLETED") {
      $completedTasks[$i] = $nbTasks;
    }
    else if ($status == "ERROR") {
      $lostTasks[$i]  = $nbTasks;
    }

    $names[$i]    = "<b>" . $date ."</b>";
    $completedTasksBars[$i] = "pics/rainbow_blue.png";
    $lostTasksBars[$i]     = "pics/rainbow_red.gif";
		
    if( $largest < $nbTasks ) {
      $largest = $nbTasks;
    }
    //finish the loop
		
  }


  if ($i < 0)
    return 1;

  $graph_vals = array(
		      "vlabel"       => "T<BR>a<BR>s<BR>k<BR>s",
		      "hlabel"       => "This ".$delay." ".$labelTail,
		      "type"         => 3,
		      "cellspacing"  => "1",
		      "vfcolor"      => "#FDFC65",
		      "hfcolor"      => "#FDFC65",
		      "vbgcolor"     => "050064",
		      "hbgcolor"     => "050064",
		      "width"        => 350,
		      "vfstyle"      => "Helvetica, Arial",
		      "hfstyle"      => "Helvetica, Arial",
		      "valuebgcolor" => "000000",
		      "scale"        => 150/$largest,
		      "namefcolor"   => "#1C2D67",
		      "namebgcolor"  => "#FDFC65",
		      "namefstyle"   => "Helvetica, Arial");
		
  echo "<!--- here is the graph -->\n";
	
  html_graph( $names, $lostTasks, $lostTasksBars, $graph_vals, $completedTasks, $completedTasksBars );

  return 0;
  }


/* ------------------------------------------ */
function graphTaskLastHour ($user = "") {
  /*
   * @see graphTask()
   */
  /* ------------------------------------------ */

  return graphTask ($user, "hour");
}


/* ------------------------------------------ */
function graphTaskLastDay ($user = "") {
  /*
   * @see graphTask()
   */
  /* ------------------------------------------ */

  return graphTask ($user, "day");
}


/* ------------------------------------------ */
function graphTaskLastWeek ($user = "") {
  /*
   * @see graphTask()
   */
  /* ------------------------------------------ */

  return graphTask ($user, "week");
}


/* ------------------------------------------ */
function graphTaskLastMonth ($user = "") {
  /*
   * @see graphTask()
   */
  /* ------------------------------------------ */

  return graphTask ($user, "month");
}


/* ------------------------------------------ */
function graphTaskLastYear ($user = "") {
  /*
   * @see graphTask()
   */
  /* ------------------------------------------ */

  return graphTask ($user, "year");
}



/* ------------------------------------------ */
function graphHost ($user = "", $delay="week") {
  /*
   * @user is the user name
   * @param delay of tasks
   *        "week" for the last wekk tasks
   *        "month" for the last month tasks
   *        "year" for the last year tasks
   * @return: 0 on success
   *          1 if no task found
   */
  /* ------------------------------------------ */

  if ($user == "") { 
    if($delay == "hour")
      $request = "select hour(lastalive),count(*) from hosts where isdeleted='false' and not(isnull(lastalive)) and hour(lastalive)=hour(now()) and date(lastalive)=date(now()) group by hour(lastalive)";
    if ($delay == "day")
      $request="select date_format(lastalive,'%b %d'),count(*) from hosts where isdeleted='false' and not(isnull(lastalive)) and date(lastalive)=date(now())  group by ipaddr,hour(lastalive)";
      /*
      $request = "SELECT COUNT(*) AS nbHosts FROM hosts,works,tasks WHERE not isnull(hosts.lastalive) and works.uid=tasks.uid and tasks.hostuid=hosts.uid and works.status='COMPLETED' and   (TO_DAYS(NOW()) - TO_DAYS(StartDate) = 1)";
      */
    if ($delay == "week")
      $request = "select date_format(lastalive,'%b %d'),count(*) from hosts where isdeleted='false' and not(isnull(lastalive)) and (date(now())-date(lastalive) < 7 ) group by date(lastalive)";
    if ($delay == "month")
      $request = "select date_format(lastalive,'%b %d'),count(*) from hosts where isdeleted='false' and not(isnull(lastalive)) and (date(now())-date(lastalive) < 32 ) group by day(lastalive)";
    if ($delay == "year")
      $request = "select date_format(lastalive,'%b/%y'),count(*) from hosts where isdeleted='false' and not(isnull(lastalive)) and (date(now())-date(lastalive) < 450 ) group by month(lastalive)";

    $labelTail = "";
  }
  else {
    if($delay == "hour")
      $request = "select hour(lastalive),count(*) from hosts where isdeleted='false' and not(isnull(lastalive)) and hour(lastalive)=hour(now()) and date(lastalive)=date(now()) group by hour(lastalive)";
    if ($delay == "day")
      $request="select hour(lastalive),count(*) from hosts where isdeleted='false' and not(isnull(lastalive)) and date(lastalive)=date(now())  group by hour(lastalive)";
      /*
      $request = "SELECT COUNT(*) AS nbHosts FROM hosts,works,tasks WHERE not isnull(hosts.lastalive) and works.uid=tasks.uid and tasks.hostuid=hosts.uid and works.status='COMPLETED' and   (TO_DAYS(NOW()) - TO_DAYS(StartDate) = 1)";
      */
    if ($delay == "week")
      $request = "select date(lastalive),count(*) from hosts where isdeleted='false' and not(isnull(lastalive)) and (date(now())-date(lastalive) < 7 ) group by date(lastalive)";
    if ($delay == "month")
      $request = "select date(lastalive),count(*) from hosts where isdeleted='false' and not(isnull(lastalive)) and (date(now())-date(lastalive) < 32 ) group by date(lastalive)";
    if ($delay == "year")
      $request = "select date(lastalive),count(*) from hosts where isdeleted='false' and not(isnull(lastalive)) and (date(now())-date(lastalive) < 365 ) group by date(lastalive)";

    $labelTail = "";
  }

  $connection = dbConnect ();
  $resultat = dbQuery($connection, $request);

  if ($resultat)
    $nb_enr   = mysql_num_rows( $resultat );


  if ($nb_enr == 0)
    return 1;

  $i = $index = $largest = 0;

  $i=-1;

  while ($activity = mysql_fetch_array ($resultat)) {

    $date    = $activity[0];
    $nbHosts = $activity[1];

    $i++;

    if (!$connectedHosts[$i])
      $connectedHosts[$i]=0;
    $connectedHosts[$i]=$nbHosts;
    $connectedHostsBars[$i] = "pics/rainbow_blue.png";

    $names[$i]    = "<b>" . $date ."</b>";
		
    if( $largest < $nbHosts ) {
      $largest = $nbHosts;
    }
    //finish the loop
		
  }


  if ($i < 0)
    return 1;

  $graph_vals = array(
		      "vlabel"       => "H<BR>o<BR>s<BR>t<BR>s",
		      "hlabel"       => "This ".$delay." connected resources ".$labelTail,
		      "type"         => 3,
		      "cellspacing"  => "1",
		      "vfcolor"      => "#FDFC65",
		      "hfcolor"      => "#FDFC65",
		      "vbgcolor"     => "050064",
		      "hbgcolor"     => "050064",
		      "width"        => 350,
		      "vfstyle"      => "Helvetica, Arial",
		      "hfstyle"      => "Helvetica, Arial",
		      "valuebgcolor" => "FFFFFF",
		      "scale"        => 150/$largest,
		      "namefcolor"   => "#1C2D67",
		      "namebgcolor"  => "#FDFC65",
		      "namefstyle"   => "Helvetica, Arial");
		
  echo "<!--- here is the graph -->\n";
	
  html_graph( $names, $connectedHosts, $connectedHostsBars, $graph_vals);

  return 0;
}


/* ------------------------------------------ */
function graphHostLastHour ($user = "") {
  /*
   * @see graphHost()
   */
  /* ------------------------------------------ */

  return graphHost ($user, "hour");
}


/* ------------------------------------------ */
function graphHostLastDay ($user = "") {
  /*
   * @see graphHost()
   */
  /* ------------------------------------------ */

  return graphHost ($user, "day");
}


/* ------------------------------------------ */
function graphHostLastWeek ($user = "") {
  /*
   * @see graphHost()
   */
  /* ------------------------------------------ */

  return graphHost ($user, "week");
}


/* ------------------------------------------ */
function graphHostLastMonth ($user = "") {
  /*
   * @see graphHost()
   */
  /* ------------------------------------------ */

  return graphHost ($user, "month");
}


/* ------------------------------------------ */
function graphHostLastYear ($user = "") {
  /*
   * @see graphHost()
   */
  /* ------------------------------------------ */

  return graphHost ($user, "year");
}




?>

