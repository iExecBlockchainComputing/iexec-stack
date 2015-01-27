<?php
//http://davidwinter.me/articles/2011/01/29/authentication-with-codeigniter-2-0/
class MY_Controller extends CI_Controller 
{
	function __construct()
	{
		parent::__construct();
		$this->load->library('session');
		$this->load->helper(array('url','form'));
		if (!$this->session->userdata('loggedin'))
		{
			redirect('sessions/login');
		}
	}
}
