<!DOCTYPE html>
<html>
<head>
	<script type="text/javascript"
	  src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDOEV2YLBC5hfsZ_trCx9NGN-owdKBfQDA&libraries=geometry">
	</script>
	<script src="http://code.jquery.com/jquery-1.11.3.min.js"></script>
<?php
	//check each ride for the request
	if(isset($_GET['id'])){		
		extract($_GET);
		include 'db.php';		

		echo '<script type="text/javascript">';		

		$table='requests';
		$qry="select * from `requests` where `rid`='$id'";
		$qry=$cxn->query($qry);	
		$qry=$qry->fetch_assoc();
		$seats=$qry['seats'];	
		echo 'var user='.json_encode($qry).";";

		$table='rides';
		$qry="select * from `$table` where `completed`='0' AND `seats`-`occupied` >= '$seats'";
		$qry=$cxn->query($qry);
		$rows = $qry->num_rows;

		$result = array();
		while($row=$qry->fetch_assoc()){
			$result[]=$row;
		}

			echo 'var result='.json_encode($result).";";
			echo 'var src = new google.maps.LatLng(user.fromLat,user.fromLng);';
			echo 'var dest = new google.maps.LatLng(user.toLat,user.toLng);';
			echo 'var i=0;';
			echo "for(;i<". $rows .";i++){";
				echo 'var latLng = google.maps.geometry.encoding.decodePath(result[i].line);';
				echo 'var poly = new google.maps.Polyline({ path: latLng });';

				echo 'var srcIn=google.maps.geometry.poly.isLocationOnEdge(src, poly,  0.005);';	
				echo 'var destIn=google.maps.geometry.poly.isLocationOnEdge(dest, poly,  0.005);';

				echo 'if (srcIn && destIn) {
						document.write(result[i].id+":true<br>");
			   			document.write(i+":true<br>");
			   			$.post("driverNoti.php",{driver: result[i].id,user: user.id,req: user.rid,ride: result[i].rid},function(data){});			   						   			 			   			  
			   			break;
					}else{
						document.write(i+":false<br>");
					}';
			echo '}';
			//echo 'document.write(i)';

		echo '</script>';
	}
	//check each request for the ride
	elseif (isset($_GET['rid'])) {
		extract($_GET);
		include 'db.php';		

		echo '<script type="text/javascript">';		
	
		$qry="select * from `rides` where `rid`='$rid'";
		$qry=$cxn->query($qry);	
		$qry=$qry->fetch_assoc();
		echo 'var driver='.json_encode($qry).";";
		$seats=$qry['seats']-$qry['occupied'];

		$table='requests';
		$qry="select * from `$table` where `booked`='0' AND `seats`<= '$seats'";
		$qry=$cxn->query($qry);
		$rows = $qry->num_rows;

		$result = array();
		while($row=$qry->fetch_assoc()){
			$result[]=$row;
		}

			echo 'var result='.json_encode($result).";";
			echo 'var latLng = google.maps.geometry.encoding.decodePath(driver.line);';
			echo 'var poly = new google.maps.Polyline({ path: latLng });';

			echo 'var i=0;';
			echo "for(;i<". $rows .";i++){";
				echo "var user=result[i];";
				echo 'var src = new google.maps.LatLng(user.fromLat,user.fromLng);';
				echo 'var dest = new google.maps.LatLng(user.toLat,user.toLng);';

				echo 'var srcIn=google.maps.geometry.poly.isLocationOnEdge(src, poly,  0.005);';	
				echo 'var destIn=google.maps.geometry.poly.isLocationOnEdge(dest, poly,  0.005);';

				echo 'if (srcIn && destIn) {
						document.write(user.id+":true<br>");
			   			document.write(i+":true<br>");
			   			$.post("driverNoti.php",{driver: driver.id,user: user.id,req: user.rid,ride: driver.rid},function(data){});			   						   			 			   			  
			   			break;
					}else{
						document.write(i+":false<br>");
					}';
			echo '}';
			//echo 'document.write(i)';

		echo '</script>';
	}
	else{
		die();
	}
?>
</head>
</html>