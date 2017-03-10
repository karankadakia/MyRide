<?php
if(isset($_POST['req']) &&
    isset($_POST['ride'])){    	

	error_reporting(E_ALL);
	include 'db.php';

	extract($_POST);

	$qry="select * from `requests` where `rid`='$req'";
	$qry=$cxn->query($qry);
	$qry=$qry->fetch_assoc();
	$seats=$qry['seats'];	
	$user=$qry['id'];

	$qry="select * from `rides` where `rid`='$ride'";
	$qry=$cxn->query($qry);
	$qry=$qry->fetch_assoc();
	$occ=$qry['occupied']+$seats;
	$seats=$qry['seats']-$occ;
	$reqs=$qry['req'].$req.';';

	if($seats>=0){
		$qry="update `requests` set `booked`='1',`ride`='$ride' where `rid`='$req'";
		$qry=$cxn->query($qry);

    	$qry1="update `rides` set `occupied`='$occ',`req`='$reqs' where `rid`='$ride'";
    	$qry1=$cxn->query($qry1);
		if($qry1 && $qry){
			//notification to user
			require_once __DIR__ . '/firebase.php';
			$firebase = new Firebase();

			$qry="select * from `user` where `id`='$user'";
			$qry=$cxn->query($qry);
			$qry=$qry->fetch_assoc();

			$res = array();
			$res['title'] = '"Ride Accepted"';    
			$res['body'] = '"Ride Arriving soon"';

			$regId = $qry['firebase'];
			$response = $firebase->sendDriver($regId, $res);

			echo "success";
		}
	}
}
?>