<?php

/*
 * File : mailer.php
 * Date : Dec 1st, 2000
 * Author : Oleg Lodygensky
 * Email : lodygens@lal.in2p3.fr
 *
 */


 /* ----------------------------------------- */
 function attachFile ($pj, $text)
 /* ----------------------------------------- */
 {
  $pj = AddSlashes($pj);

  $piece = split(";",$pj);

  isset($piece) ? "" : $piece=$pj;

  while ( list(,$v) = each($piece))
  {
   if (!file_exists($v)) /*test l'existance du fichier*/
   {
    $m= "can't find attached file!";
   }
   else
   {
    $ext = split("\.", $v,2);
    $ext[1] = strtolower($ext[1]);

    switch ($ext[1])
    {
    case "jpeg" :
    case "jpg" :
     // definition du type MIME
     $type = "image/jpeg";
     $dispo = "Content-Disposition: attachment;\n\tfilename=$v";
     // $p contient tout le fichier en cours de traitement
     $p = fread(fopen($v,"r"), filesize($v));
     // encodage du fichier
     $enc = base64_encode($p);
     // definition du mode de transfert et données à transferer
     $transfert = "base64\n\n$enc\n\n";
     break;

     case "tif" :
     case "tiff" :
      $type = "image/tiff";
      $dispo = "Content-Disposition: attachment;\n\tfilename=$v";
      $p = fread(fopen($v,"r"), filesize($v));
      $enc = base64_encode($p);
      $transfert = "base64\n\n$enc\n\n";
      break;

     case "gif" :
     case "png" :
     case "bmp" :
      echo jourbon;

      $type = "image/$ext[1]";
      $dispo = "Content-Disposition: attachment;\n\tfilename=$v";
      $p = fread(fopen($v,"r"), filesize($v));
      $enc = base64_encode($p);
      $transfert = "base64\n\n$enc\n\n";
      break;

     case "html" :
     case "htm" :
      $type = "text/html";
      $dispo = "";
      $fp = fopen($v,"r");

      /* on prend par paquet de 4096 tout le contenu du fichier dans $p*/
      $p = fgets($fp,4096);
      while (!feof($fp))
      {
       $p .= fgets($fp,4096);
      }
      fclose($fp);
      $transfert = "7bits\n\n$p\n\n";
      break;

     case "tar" :
     case "gz" :
      $type = "application/x-gzip";
      $dispo = "Content-Disposition: attachment;\n\tfilename=$v";
      $p = fread(fopen($v,"r"), filesize($v));
      $enc = base64_encode($p);
      $transfert = "base64\n\n$enc\n\n";
      break;

     case "zip" :
      $type = "application/zip";
      $dispo = "Content-Disposition: attachment;\n\tfilename=$v";
      $p = fread(fopen($v,"r"), filesize($v));
      $enc = base64_encode($p);
      $transfert = "base64\n\n$enc\n\n";
      break;

     default :
      // si le fichier n'est pas un fichier ASCII
      if (ereg("ascii", exec("file ".$v)) == 0)
      {
       $type = "application/octet-stream";
       $dispo = "Content-Disposition: attachment;\n\tfilename=$v";
       $p = fread(fopen($v,"r"), filesize($v));
       $enc = base64_encode($p);
       $transfert = "base64\n\n$enc\n\n";
      }
      else
      {
       $type = "text/plain";
       $dispo = "Content-Disposition: attachment;\n\tfilename=$v";
       $fp = fopen($v,"r");
       $p = fgets($fp,4096);
       while (!feof($fp))
       {
        $p .= fgets($fp,4096);
       }
       fclose($fp);
       $transfert = "7bits\n\n$p\n\n";
      }
      break;
    }

    // liberation de l'espace memoire important utilisé pour le codage
    unset ($p);

    // définition de l'entête à mettre dans le corps du message pour attacher le fichier
    $m2 .= "--SUOF0GtieIMvvwua\nContent-Type: $type;\n\tname=$v\n$dispo\nContent-Transfer-Encoding: $transfert";
   }
  }

  // definition de l'entête du corps du message
  $m = "--SUOF0GtieIMvvwua\nContent-Type: text/plain; charset=iso-8859-1\nContent-Transfer-Encoding: 7bit\n\n$text\n\n";
  // boundary de fin.
  $m .= $m2 . "--SUOF0GtieIMvvwua--";

  return $m;
 }


 /* ----------------------------------------- */
 Function envoiMail ($nom, $emailSrc,  $emailDest, $titre, $text, $attachedFileName)
 /* Returns : 0 on error                      */
 /*           1 if mail has been sent with an */
 /*             attached file                 */
 /*           2 if mail has been sent without */
 /*             any attached file             */
 /* ----------------------------------------- */
 {
  if ($emailSrc == "")
   return 0;

  if ($emailDest == "")
   return 0;

  $to = $emailDest;
  $msg = Stripslashes(Stripslashes($text));

  if ($attachedFileName != "") 
  {
   $body = attachFile ($attachedFile, $msg);

   $type2 = "Content-Type: multipart/mixed; boundary=SUOF0GtieIMvvwua";

   //envoie du mail avec les headers necessaire en cas de piece jointe 

   if (mail($to, $titre, $body, "From: $emailSrc\nreplyTo: $emailSrc\n$type2") == false)
     return 0;

   return 1;
  }
  else
  {
   if (mail($to, $titre, $msg, "From: $emailSrc\nreplyTo: $emailSrc") == false)
     return 0;

   return 2;
  }

  return 0;

 }

?>
