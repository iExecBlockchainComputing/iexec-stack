<?php
class Xwgp_model extends CI_Model {

	function Xwgp_model()
	{
		parent::__construct();
	}

	function save_simu($name, $number, $configuration, $simu_id)
	{
		$query_str= "UPDATE gpsimu SET name=?, number=?, configuration=?";
		$query_str.= " WHERE id=?";
		$this->db->query($query_str, array($name, $number, $configuration, $simu_id));
	}

	function delete_simu($user_id)
	{
		$query_str= "DELETE FROM gpsimu WHERE id=?";
		$this->db->query($query_str, array($user_id));
	}

	function create_simu($name, $number, $configuration, $xwlogin)
	{
		$query_str= "INSERT INTO gpsimu SET name=?, number=?, configuration=?";
		$query_str.= ", xwlogin = ?";
		$this->db->query($query_str, array($name, $number, $configuration, $xwlogin));
	}

	function get_simu($simu_id)
	{
		$query_str= "SELECT id, name, number, configuration, status";
		$query_str.= " FROM gpsimu WHERE id = ?";
		$result= $this->db->query($query_str, $simu_id);
		$result2= $result->result();
		return($result2[0]);
	}

	function get_list_simu($xwlogin)
	{
		$query_str= "SELECT id, name, status, number, configuration";
		$query_str.= " FROM gpsimu WHERE xwlogin = ?";
		$result= $this->db->query($query_str, $xwlogin);
		return($result);
	}

	function run_simu($simu_id)
	{
		$query_str= "UPDATE gpsimu SET status='running'";
		$query_str.= " WHERE id=?";
		$this->db->query($query_str, array($simu_id));
	}

}
