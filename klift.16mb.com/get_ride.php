<?php
	if(!(isset($_POST['id']))){
		if(!(isset($_POST['rid']))){
			$arr['msg']='error';
			echo json_encode($arr);
		}
		else{
			include 'db.php';
			extract($_POST);
			$table="rides";

			$qry="select * from `$table` where `rid`='$rid'";
			$qry=$cxn->query($qry);
			if($qry->num_rows ==1 ){
				$qry=$qry->fetch_assoc();			
				$qry['msg']='success';
				echo json_encode($qry);
			}
			else{
				$arr['msg']='error';
				echo json_encode($arr);
			}
		}
	}
	else{
		include 'db.php';
		extract($_POST);
		$table="rides";

		$qry="select * from `$table` where `id`='$id' AND `completed`='0'";
		$qry=$cxn->query($qry);
		if($qry->num_rows ==1 ){
			$qry=$qry->fetch_assoc();			
			$qry['msg']='success';
			echo json_encode($qry);
		}
		else{
			$arr['msg']='error';
			echo json_encode($arr);
		}
	}

?>