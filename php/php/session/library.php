<?php

/*
 * File   : session/library.php
 * Date   : May 26th, 2003
 * Author : Oleg Lodygensky
 * Email  : lodygens@lal.in2p3.fr
 */




/* ------------------------------------------ */
function isPrivileged ($user,$session) {
/* ------------------------------------------ */
  return  ((dbGetUserRights ($user) == 100) && ($session == sessionMake($user)));
}


/* ------------------------------------------ */
function codePassword($password) {
/* ------------------------------------------ */
  return substr ( md5 ($password.confiGetPassPhrase ()), 0, 50);
}


/* ------------------------------------------ */
function sessionMake($login) {
/* ------------------------------------------ */
  return md5($login.date("F j H, Y").confiGetPassPhrase ());
}

/* ------------------------------------------ */
function sessionOpen ($login, $password, &$fname, &$lname) {
/*
 */
/* ------------------------------------------ */


  $connection = dbConnect ();

  if ($connection == FALSE) {
    return;
  }

  $request = "SELECT * FROM users WHERE login='".$login."'";
  $request = $request." AND password='".codePassword($password)."';";
  $query_resultat = dbQuery($connection, $request); 

  $nb_enr = mysql_num_rows($query_resultat);
  $users= mysql_fetch_array($query_resultat);

  
  if ($nb_enr != 1) {
    return;
  }

  $fname = $users["fname"];
  $lname = $users["lname"];

  return sessionMake ($login);
}

?>
