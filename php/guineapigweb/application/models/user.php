<?php
/*
if directory log doesn't exist create it
createClientConf()
if !testClientConf() return false else return true
*/
class User extends CI_Model
{

	function User()
	{
		parent::__construct();
		$this->load->helper('file');
		$this->xwversion= '/usr/bin/xwversion';
	}

	public function authenticate($xwlogin, $xwpassword)
	{

		//error_reporting(E_ALL);
		//ini_set('display_errors', '1');

		//if further login fails this will break current simulation
		$login_path= $this->config->item('users_path').'/'.$xwlogin;
		if ( ! file_exists($login_path)) { mkdir($login_path); chmod($login_path, 0777); }
		//createClientConf
		$template_conf= read_file($this->config->item('users_path')."/xtremweb.client.conf.lri.template");
		$client_conf= str_replace("@DEFAULTUSER@", $xwlogin, $template_conf );
		$client_conf= str_replace("@DEFAULTPASSWORD@", $xwpassword, $client_conf );
		write_file($login_path."/xtremweb.client.conf", $client_conf);
		//testClientConf
		$cmdsh= $this->xwversion.' --xwverbose --xwconfig '.$login_path.'/xtremweb.client.conf > /dev/null; echo $?';
		$output = shell_exec($cmdsh);
		if (trim($output)=="0") { write_file($login_path.'/ok', 'ok'.$output.'-*-'.$cmdsh); return true; }
		else { write_file($login_path.'/nok', 'nok'.$output.'-*-'.$cmdsh); return false; }
		return false; // in case of
	}

}
