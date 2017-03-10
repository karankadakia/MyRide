<?php
	if(!(isset($_POST['email']) &&
		isset($_POST['pass']))) {
		$arr['msg']='error';
		echo json_encode($arr);		
	}
	else{
		include 'db.php';
		extract($_POST);
		$table="driver";
		
		$pass=md5($pass);
		$qry="SELECT `id`,`confirmed` from `$table` where `email` like '$email' and `password` like '$pass'";
		$qry=$cxn->query($qry);
		$q=$qry->num_rows;
		if($q==1){
			$qry=$qry->fetch_assoc();
			if($qry['confirmed']==1){
				$arr['msg']='success';
				$arr['id']=$qry['id'];
			}
			else{
				$arr['msg']='confirmation';
			}
			echo json_encode($arr);
		}
		else{
			$arr['msg']='error';
			echo json_encode($arr);
		}
	}
?>