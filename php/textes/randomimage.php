<?php

	if(!isset($V1)) {
	  if(isset($_GET["V1"])) {
	    $V1 = $_GET["V1"];
	  }
	  else {
	    $V1=rand(0,1000);
	  }
	}

	  $string = $V1;

	$font_size = 5;
	$width=imagefontwidth($font_size)*strlen($string);
	$height=imagefontheight($font_size)*2;
	$rndimg = imagecreate($width,$height);
	$bg = imagecolorallocate($rndimg,225,225,225);
	$black = imagecolorallocate($rndimg,0,0,0);
	$len=strlen($string);

	for($i=0;$i<$len;$i++)
	{
		$xpos=$i*imagefontwidth($font_size);
		$ypos=rand(0,imagefontheight($font_size));
		imagechar($rndimg,$font_size,$xpos,$ypos,$string,$black);
		$string = substr($string,1);   
	}

	header("Content-Type: image/gif");
	imagegif($rndimg);
	imagedestroy($rndimg);

?>
