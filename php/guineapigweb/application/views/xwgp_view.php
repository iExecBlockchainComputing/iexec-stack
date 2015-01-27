<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>GuineaPig++ simulations on XtremWeb-HEP</title>

<style type="text/css"></style>
<link type="text/css" href="<?php echo base_url(); ?>extjs/jquery-ui/css/smoothness/jquery-ui-1.8.16.custom.css" rel="stylesheet" />
<link type="text/css" href="<?php echo base_url(); ?>css/xwgp.css" rel="stylesheet" />

<script type="text/javascript" charset="utf-8" src="<?php echo base_url();?>extjs/jquery-1.6.2.min.js"></script>
<script type="text/javascript" charset="utf-8" src="<?php echo base_url();?>extjs/jquery-ui/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" charset="utf-8" src="<?php echo base_url();?>extjs/flot/jquery.flot.js"></script>
<script type="text/javascript" charset="utf-8" src="<?php echo base_url();?>extjs/flot/jquery.flot.resize.js"></script>
<script type="text/javascript" charset="utf-8" src="<?php echo base_url();?>js/xwgp.js"></script>

<script type="text/javascript">
var base_url= "<?php echo base_url(); ?>";//needed in xwgp.js  //baseurl() returns http://domain/dir/
var xwlogin= "<?php echo $xwlogin; ?>";
</script>

</head>
<body>

<h1> GuineaPig++ simulations on XtremWeb-HEP </h1>

Hello <b><?php echo $xwlogin; ?></b>
&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;
<a href="javascript:void(0)" title="Create simulation" id="ahref_create_simu" onclick="click_create_simu()"><img src="<?php echo base_url();?>images/b_insrow.png">Create simulation</a>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<?php echo anchor('sessions/logout/','<img src="'.base_url().'images/s_loggoff.png">Disconnect'); ?>
<hr />

<div id="list_simu"></div>

<!--<p><br />Page rendered in {elapsed_time} seconds</p>-->

<div id="viewResultsDialog" title="View Results" style="display: none"></div>

<div id="viewPlotDialog" title="View Plot" style="display: none">
  <div id="divPlot" title="Plot" style="display: none"></div>
</div>

<!-- edit configuration form in dialog box  ?for ie ? visibility: hidden; / visible-->
<div id="editConfDialog" style="display: none">
    <!--<div>-->
        <form action="" method="post">
               <button name="save_simu" id="save_simu" type="button" style="display: none">Save</button> 
               <button name="create_simu" id="create_simu" type="button" style="display: none">Create</button> 
			<br />
               <label for="name">Name</label>
               <input type="text" id="name" name="name" />
               <br />
               <label for="name">Amount of job</label>
               <input type="text" id="number" name="number" size="4" />
			<br />
               <textarea id="configuration" name="configuration" cols="25" rows="20"></textarea>
               <input type="hidden" id="simu_id" name="simu_id" />
        </form>
    <!--</div>-->
</div>

<script type="text/javascript">
//.everyTime(5000,function(i){ });
var refreshId = setInterval(function() { get_list_simu();}, 60000);//240000
//$('#list_simu').fadeOut("slow").load('response.php').fadeIn("slow");
</script>




