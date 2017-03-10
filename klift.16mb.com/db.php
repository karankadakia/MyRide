<?php
	$dbname="u840856201_klift";
	$cxn = new mysqli("mysql.hostinger.in",$dbname,"6!X:I6Sax25",$dbname);

	if($cxn->connect_error)
	{
		echo "Connection Error";
		die();
	}

	define ('SITE_ROOT', realpath(dirname(__FILE__)));	
?>