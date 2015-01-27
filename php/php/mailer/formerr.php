<?php_track_vars?>

<html>

 <head>

  <script language="javascript">

  function displayMsg()
  {
   document.write (msg + "<BR>");
  }

  </script>

 </head>


 <body>
  <center>
   <?php

/*
    phpinfo();
    echo $PHP_SELF;

    echo "<hr>";
*/
    echo $HTTP_GET_VARS["msgerr"];
   ?>
  </center>
 </body>

</html>
