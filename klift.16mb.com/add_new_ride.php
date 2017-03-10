<?php
	if(!(isset($_POST['id']) &&
		isset($_POST['route']) &&
		isset($_POST['seats']) &&
		isset($_POST['line']) 	)) {
		$arr['msg']='error';
		echo json_encode($arr);		
	}
	else{
		include 'db.php';
		extract($_POST);
		$table="rides";

		$added=date("YmdHis");
		$route=$cxn->real_escape_string($route);
		$line=$cxn->real_escape_string($line);

		$qry="INSERT INTO `$table`(`id`,`seats`,`added`,`route`,`line`) VALUES ('$id','$seats','$added','$route','$line')";
		$qry=$cxn->query($qry);		
		if($qry){
			$qry="select * from `$table` where `id`='$id' AND `added`='$added'";
			$qry=$cxn->query($qry);
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