<?php
	error_reporting(E_ALL);
	if(!(isset($_POST['type']) &&
		 isset($_POST['id']) && 
		 isset($_POST['firekey']) )){
		$arr['msg']='error';
		echo json_encode($arr);
	}
	else{
		include 'db.php';
		extract($_POST);

		if($type=="driver"){
			$table="driver";

			$firekey=$cxn->real_escape_string($firekey);

			$qry="update `$table` set `firebase`='$firekey' where `id`='$id'";
			$qry=$cxn->query($qry);
			if($qry){		
				$arr['msg']='success';
				echo json_encode($arr);
			}
			else{
				$arr['msg']='error';
				echo json_encode($arr);
			}
		}
		else if($type=="user"){
			$table="user";

			$firekey=$cxn->real_escape_string($firekey);

			$qry="update `$table` set `firebase`='$firekey' where `id`='$id'";
			$qry=$cxn->query($qry);
			if($qry){						
				$arr['msg']='success';
				echo json_encode($arr);
			}
			else{
				$arr['msg']='error';
				echo json_encode($arr);
			}
		}
	}
?>