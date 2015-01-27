<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Xwgp extends MY_Controller { //before CI_Controller

	function __construct()
	{
		parent::__construct();
		$this->users_path= $this->config->item('users_path');
		$this->load->database();
		//$this->load->helper(array('url','form'));
		$this->load->helper('file');
		$this->load->model('xwgp_model');
	}

	public function index()
	{
		$this->data["xwlogin"] = $this->session->userdata('xwlogin');
		$this->load->view('xwgp_view', $this->data);
        $this->load->view('footer');
	}

	function ajax_get_simu()
	{
		$simu_id= $this->input->post('simu_id');
		$the_simu= $this->xwgp_model->get_simu($simu_id);
		if ($the_simu) {
			$result= array('status'=>'ok', 'name'=>$the_simu->name, 'number'=>$the_simu->number, 'id'=>$the_simu->id, 'configuration'=>$the_simu->configuration);
			echo json_encode($result);
			exit();
		}
		else {
			$result= array('status'=>'ok', 'name'=>'');
			echo json_encode($result);
			exit();
		}
	}

	function ajax_get_file_content()
	{
		$content= read_file($this->input->post('file_location'));
		if ($content) {
			$result= array('status'=>'ok', 'content'=>$content);
			echo json_encode($result);
			exit();
		}
		else {
			$result= array('status'=>'ok', 'content'=>'');
			echo json_encode($result);
			exit();
		}
	}

	function ajax_delete_simu()
	{
	  $the_simu= $this->xwgp_model->get_simu($this->input->post('simu_id'));
	  $this->xwgp_model->delete_simu($this->input->post('simu_id'));
	  $simu_path= $this->users_path."/".$this->session->userdata('xwlogin')."/".$the_simu->name;
	  write_file($simu_path.'/remove_my_dir', ' ');
	  $this->ajax_get_list_simu();
	}

	function ajax_run_simu()
	{
	  $the_simu= $this->xwgp_model->get_simu($this->input->post('simu_id'));
	  //$this updates status
	  $this->xwgp_model->run_simu($this->input->post('simu_id'));
	  //create simu sub-dir
	  $simu_path= $this->users_path."/".$this->session->userdata('xwlogin')."/".$the_simu->name;
	  if ( ! file_exists($simu_path)) { mkdir($simu_path);  chmod($simu_path, 0777); } 
	  //generate ready.macro and the input file acc.dat
	  $macro= "";
	  $acc= $the_simu->configuration;
	  $parameters= substr($the_simu->configuration, strpos($the_simu->configuration, '$PARAMETERS'));
	  for ($i=1; $i<=$the_simu->number; $i++) {
		$tmp_param= str_replace("rndm_seed=1;", "rndm_seed=".rand().";", $parameters );
		$tmp_param= str_replace("LC-GENERAL", "LC-GENERAL".$i, $tmp_param );
		$acc.= $tmp_param."";
		//$macro.= "--xwsendwork guineapig ILC LC-GENERAL".$i." output.dat --xwenv ".$simu_path."/acc.dat\r\n";
		$macro.= "--xwsendwork bae32538-f8e7-49bc-a2ee-50d573105efa ILC LC-GENERAL".$i." output.dat --xwenv ".$simu_path."/acc.dat\r\n";
	  }
	  write_file($simu_path.'/acc.dat', $acc);
	  write_file($simu_path.'/ready.macro', $macro);
	  $this->ajax_get_list_simu();
	}
	
	function ajax_save_simu()
	{
	  $simu_id= $this->input->post('simu_id');
	  $name= $this->input->post('name');
	  $number= $this->input->post('number');
	  $configuration= $this->input->post('configuration');
	  $this->xwgp_model->save_simu($name, $number, $configuration, $simu_id);
	}
	
	function ajax_create_simu()
	{
	  //check if a directory with this name already exists (because remove is not instantaneous)
	  $name= $this->input->post('name');
	  $number= $this->input->post('number');
	  $configuration= $this->input->post('configuration');
	  $this->xwgp_model->create_simu($name, $number, $configuration, $this->session->userdata('xwlogin'));
	}

	function ajax_get_list_simu()
	{
		$list_simu= $this->xwgp_model->get_list_simu($this->session->userdata('xwlogin'));
		if ($list_simu->num_rows>0) {
			$list_simu_html= '';

			// if the file last_exec.txt exists we display it
			$last_exec_txt= read_file($this->config->item('users_path').'/last_exec.txt');
			if ($last_exec_txt) $list_simu_html.= 'Last status check : '.$last_exec_txt.'<br /><hr style="color: #000000" /><br /><br />';

			foreach ($list_simu->result() as $simu) {
				$list_simu_html.= '<b>'.$simu->name.'</b>';

				if ($simu->status=="ready") {
				  $list_simu_html.= ' [<a href="javascript:void(0)" title="Edit simulation" id="edit_simu'.$simu->id.'" onclick="click_edit_simu('.$simu->id.')"><img src="'.base_url().'images/b_edit.png">Edit</a>]';

				  $list_simu_html.= ' [<a href="javascript:void(0)" title="Run simulation" id="run_simu'.$simu->id.'" onclick="click_run_simu('.$simu->id.')"><img src="'.base_url().'images/run.png">Run</a>]';
				} else { 
				  $list_simu_html.= ' <span style="color="grey"">Edit</span>'; 
				  $list_simu_html.= ' <span style="color="grey"">Run</span>'; 
				}

				$list_simu_html.= ' [<a href="javascript:void(0)" title="Delete simulation" id="delete_simu'.$simu->id.'" onclick="click_delete_simu('.$simu->id.')"><img src="'.base_url().'images/b_drop.png">Delete</a>]';

				//if the file results.retrieved exists it means this simulation is completed
				if (read_file($this->config->item('users_path').'/'.$this->session->userdata('xwlogin').'/'.$simu->name.'/results.retrieved')) {
				  $list_simu_html.= ' [<a href="javascript:void(0)" title="View results" id="view_results'.$simu->id.'" onclick="click_view_results('.$simu->id.')"><img src="'.base_url().'images/b_browse.png">Results</a>]';
				} else { 
				  $list_simu_html.= ' <span style="color="grey"">Results</span>';
				}

				$jobs_status_txt= read_file($this->config->item('users_path').'/'.$this->session->userdata('xwlogin').'/'.$simu->name.'/jobs_status.txt');
				if ($jobs_status_txt)
				  $list_simu_html.= ' ('.$jobs_status_txt.')';

				$list_simu_html.= '<hr />';
			}
			$list_simu_html.= '';

			$result= array('status'=>'ok', 'content'=>$list_simu_html);
			echo json_encode($result);
			exit();
		}
		else {
			$result= array('status'=>'ok', 'content'=>'');
			echo json_encode($result);
			exit();
		}
	}

	function ajax_get_results()
	{
	  $the_simu= $this->xwgp_model->get_simu($this->input->post('simu_id'));
	  $simu_path= $this->users_path."/".$this->session->userdata('xwlogin')."/".$the_simu->name;
	  $url_path= $this->config->item('users_dir').'/'.$this->session->userdata('xwlogin')."/".$the_simu->name;
	  $list_res_html= '';
	  $numres= 1;
	  $one_result= get_filenames($simu_path.'/res'.$numres);
	  while ($one_result) {
		$list_res_html.= 'result n°'.$numres.' : <br />';
		foreach ($one_result as $file) {
		  $list_res_html.= '<a href="'.base_url().$url_path.'/res'.$numres.'/'.$file.'">'.$file.'</a>';
		  // if file is beam.js propose to plot it
		  if ($file=='beam1.js')
			$list_res_html.= '&nbsp;<a href="javascript:void(0)" onclick="click_view_plot(\''.$url_path.'/res'.$numres.'/'.$file.'\', \''.$the_simu->id.'-'.$numres.'\')" style="color:orange">Plot it</a>';
		  if ($file=='beam1.png' OR $file=='beam2.png')
			$list_res_html.= '&nbsp;<a href="javascript:void(0)" onclick="click_view_img(\''.base_url().$url_path.'/res'.$numres.'/'.$file.'\', \'b1i'.$the_simu->id.'-'.$numres.'\')" style="color:orange">View</a>';
		  $list_res_html.= '<br />';
		}
		//$list_res_html.= var_export($one_result,TRUE);
		$list_res_html.= '<br />';
		$numres++;
		$one_result= get_filenames($simu_path.'/res'.$numres);
	  }
	  $result= array('status'=>'ok', 'content'=>$list_res_html);
	  echo json_encode($result);
	  exit();
	}

}
