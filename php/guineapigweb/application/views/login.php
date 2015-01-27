<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>GuineaPig++ simulations on XtremWeb-HEP</title>
<link type="text/css" href="<?php echo base_url();?>css/xwgp.css" rel="stylesheet" />
</head>
<body>

<h2>Login</h2>
<?php echo form_open('sessions/authenticate'); ?>
    <dl>
        <dt><?php echo form_label('Login', 'user_xwlogin'); ?></dt>
        <dd><?php echo form_input(array(
            'name' => 'user[xwlogin]', 
            'id' => 'user_xwlogin'
        )); ?></dd>

        <dt><?php echo form_label('Password', 'user_xwpassword'); ?></dt>
        <dd><?php echo form_password(array(
            'name' => 'user[xwpassword]', 
            'id' => 'user_xwpassword'
        )); ?></dd>
    </dl>
    <ul>
        <li><?php echo form_submit('submit', 'Login'); ?></li>
        <!--<li><a href="<?php echo site_url('/'); ?>">Cancel</a></li>-->
    </ul>
<?php echo form_close(); ?>

