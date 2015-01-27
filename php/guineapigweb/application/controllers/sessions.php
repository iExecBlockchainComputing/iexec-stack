<?php

class Sessions extends CI_Controller
{
    function __construct()
    {
        parent::__construct();

        $this->load->library('session');
		$this->load->helper(array('url','form'));
    }

    function login()
    {
        //$this->load->view('header');
        $this->load->view('login');
        $this->load->view('footer');
    }

    function authenticate()
    {
        $this->load->model('user', '', true);

        $user = $this->input->post('user');

        if ($this->user->authenticate($user['xwlogin'], $user['xwpassword']))
        {
            $this->session->set_userdata('loggedin', true);
            $this->session->set_userdata('xwlogin', $user['xwlogin']);
       }

        redirect('/');
    }

    function logout()
    {
        $this->session->unset_userdata('loggedin');

        redirect('/');
    }
}