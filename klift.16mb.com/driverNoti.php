<?php
if(isset($_POST['driver']) &&
    isset($_POST['user'])){    

    require_once __DIR__ . '/firebase.php';

    $firebase = new Firebase();    

    include 'db.php';
    extract($_POST);    

    $qry="select * from `requests` where `rid`='$req'";
    $qry=$cxn->query($qry);
    $qry=$qry->fetch_assoc();

    $payload = array();
    $payload['toLat'] = $qry['toLat'];
    $payload['fromLat'] = $qry['fromLat'];
    $payload['toLng'] = $qry['toLng'];
    $payload['fromLng'] = $qry['fromLng'];
    $payload['seats'] = $qry['seats'];
    $payload['driver'] = $driver;
    $payload['user'] = $user;
    $payload['req'] = $req;
    $payload['ride'] = $ride;

    $qry="select * from `user` where `id`='$user'";
    $qry=$cxn->query($qry);
    $qry=$qry->fetch_assoc();

    $payload['name']=$qry['name'];
    $payload['email']=$qry['email'];

    // notification title
    $title = "Accept Request.";

    // notification message
    $message = "Accept User.";        

    $res = array();
    $res['data']['title'] = $title;    
    $res['data']['message'] = $message;
    $res['data']['payload'] = $payload;        
    
    $response = '';

    $qry="select * from `driver` where `id`='$driver'";
    $qry=$cxn->query($qry);
    $qry=$qry->fetch_assoc();

    $regId = $qry['firebase'];
    $response = $firebase->sendDriver($regId, $res);    

    //echo json_encode($res);
}
?>