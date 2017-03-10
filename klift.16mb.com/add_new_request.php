<?php	
	if(!(isset($_POST['id']) &&
		isset($_POST['fromLat']) && 
		isset($_POST['fromLng']) && 
		isset($_POST['toLat']) && 
		isset($_POST['toLng']) && 
		isset($_POST['seats']) )) {
		$arr['msg']='error';
		echo json_encode($arr);		
	}
	else{
		include 'db.php';
		extract($_POST);
		$table="requests";

		$added=date("YmdHis");

		$qry="INSERT INTO `$table`(`id`,`fromLat`,`toLat`,`seats`,`added`,`fromLng`,`toLng`) VALUES ('$id','$fromLat','$toLat','$seats','$added','$fromLng','$toLng')";
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