<blockquote>
<p class="TITRE">Information form</p>
<BR>

<?php

if (isset($_POST["email"]) && ($_POST["email"] != "" ) && ($V1 == $V2)) {

  $receiver="lodygens@lal.in2p3.fr";

  warning (0);

  if(mail($receiver, "XW@LAL : account request",
	  "EMAIL:".$firstname." ".$name." <".$email.">\nInstitut : ".$institute."\nPays :".$country."\nCommentaire:\n---\n".$comments."\n---\n",
	  "From: ".$firstname." ".$name."<".$email.">")) {

?>

    <center>
    <font color="yellow" size="+1">Your request has been notified. Thank you!</font>
    </center>

<?php
    return;
  }
  else {

echo <<< EOF
    <CENTER><H1><FONT COLOR="red">
    Your request could not be notified,<BR>please advise
    <A HREF="mailto:webmaster@localhost?SUBJECT=XtremWeb download request notification
    problem">webmaster</A> about it.
    </FONT></H1></CENTER>
EOF;
    return;
  }

}

?>

  <form method="post" action="index.php?section=section2&subsection=register">
  <input type="hidden" name="section" value="section2">
  <input type="hidden" name="subsection" value="register">
  <font size="+1" color="yellow">You have to fill the following form to get a Desktop Grid account.
</font>
  <br><br>
  
  
  <blockquote>
  <table border="0">
  <tr><td>First name :</td><td><input type="text" size="40" name="firstname"></td></tr>
  <tr><td>Name :</td><td><input type="text" size="40" name="name"></td></tr>
  <tr>
     <td>
      Email : <font color="red"><i>(required)</i></font>
     </td><td><input type="text" size="40" name="email"></td>
  </tr>
  <tr><td>Institute/company :</td><td><input type="text" size="40" name="institute"></td></tr>
  <tr><td>Country :</td><td><input type="text" size="40" name="country"></td></tr>

  <tr>
    <td>Anti spam code :</td>
    <td>
      <?php
        $rndstring = rand(0,1000);
	$graphargs="V1=".$rndstring;
	echo "<img src='./textes/randomimage.php?$graphargs' />";
	echo "<input type=\"hidden\" value=\"".$rndstring."\" name=\"V1\" />\n";
      ?>
    </td>
  </tr>

  <tr>
    <td>Verify anti spam code :</td><td><input type="text" size="40" name="V2"></td>
  </tr>

  </td></tr>

  <tr><td colspan="2" align="center"><BR>Any comment</td></tr>
  <tr><td colspan="2" align="center"><textarea name="comments" cols="50" rows="15"></textarea></td>
  <tr><td><input type="submit" name="submit" value="Submit"></td>
  <td><input type="reset" name="reset" value="Reset"></td></tr>
  </table>
  </blockquote>
  </form>

<?php


?>

