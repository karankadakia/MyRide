<?php
	if(isset($_POST['driver'])){
		include 'db.php';
		extract($_POST);

		$qry="select `id` from `rides` where `rid`='$driver'";
		$qry=$cxn->query($qry);
		$qry=$qry->fetch_assoc();
		$driver=$qry['id'];

		$qry="select * from `driver` where `id`='$driver'";		
		$qry=$cxn->query($qry);
		$qry=$qry->fetch_assoc();

		$res['name']=$qry['name'];
		$res['email']=$qry['email'];

		$file=fopen($qry['image'], "r");
		$res['image']=base64_encode(fread($file,filesize($qry['image'])));
		fclose($file);

		$file=fopen($qry['car'], "r");
		$res['car']=base64_encode(fread($file,filesize($qry['car'])));
		fclose($file);

		$file=fopen($qry['numberplate'], "r");
		$res['numberplate']=base64_encode(fread($file,filesize($qry['numberplate'])));
		fclose($file);

		echo json_encode($res);
	}
?>