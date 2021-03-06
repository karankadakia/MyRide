<?php
	if(!(isset($_POST['name']) &&
		isset($_POST['email']) &&
		isset($_POST['pass']) &&
		isset($_POST['license']) &&
		isset($_POST['image']) &&
		isset($_POST['image_ext']) &&
		isset($_POST['carpic']) &&
		isset($_POST['licensepic']) )) {
		$arr['msg']='error';
		echo json_encode($arr);
	}
	else{
		include 'db.php';
		extract($_POST);
		$table="driver";
		
		$hash=md5(rand(0,1000));
		$pass1=md5($pass);
		$qry="INSERT INTO `$table`(`name`,`email`,`password`,`license`,`conhash`) VALUES ('$name','$email','$pass1','$license','$hash')";
		$qry=$cxn->query($qry);	
		if($qry){
			$qry="SELECT `id` from `$table` where `email` like '$email'";
			$qry=$cxn->query($qry);
			$qry=$qry->fetch_assoc();
			$id=$qry['id'];

			//profile picture
			$url="img/driver/driverPic$id.$image_ext";
			$file=fopen($url, "wb");
			$written=fwrite($file,base64_decode($image));	
			fclose($file);			
			$qry1="UPDATE `$table` SET `image`='$url' WHERE `id`='$id'";
			$cxn->query($qry1);
			
			//car picture 
			$url="img/driver/carPic$id.$image_ext";
			$file=fopen($url, "wb");
			$written=fwrite($file,base64_decode($carpic));	
			fclose($file);			
			$qry1="UPDATE `$table` SET `car`='$url' WHERE `id`='$id'";
			$cxn->query($qry1);			

			//license picture 
			$url="img/driver/licensePic$id.$image_ext";
			$file=fopen($url, "wb");
			$written=fwrite($file,base64_decode($licensepic));	
			fclose($file);			
			$qry1="UPDATE `$table` SET `numberplate`='$url' WHERE `id`='$id'";
			$cxn->query($qry1);			



			$arr['msg']='success';
			$arr['id']=$id;
			echo json_encode($arr);

			//send mail
			require_once('PHPMailer_5.2.4/class.phpmailer.php');
			
			$msg ='
			 
			Thanks for signing up!<br><br>
			Your account has been created, you can login with the following credentials after you have activated your account by pressing the url below.<br><br>
			 
			------------------------<br>
			Username: '.$email.'<br>			
			------------------------
			 <br><br>
			Please click this link to activate your account:<br><br>
			http://klift.16mb.com/driver_verify.php?email='.$email.'&hash='.$hash.'
			 
			';

			$mail = new PHPMailer(); // create a new object
			//From email address and name
			$mail->From = "reg@klift.16mb.com";
			$mail->FromName = "Lift App";

			//To address and name			
			$mail->addAddress("$email"); //Recipient name is optional

			//Send HTML or Plain Text email
			$mail->isHTML(true);

			$mail->Subject = "Signup | Verification";
			$mail->Body = $msg;
			$mail->AltBody = $msg;

			$mail->Send();			
		}
		else{
			$arr['msg']='email already exists';
			echo json_encode($arr);
		}
	}
?>