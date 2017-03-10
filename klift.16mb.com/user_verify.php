<?php
             
if(isset($_GET['email']) && !empty($_GET['email']) AND isset($_GET['hash']) && !empty($_GET['hash'])){
    include 'db.php';
    extract($_GET);
    $table="user";
                 
    $qry = "SELECT `email`,`conhash`,`confirmed` FROM $table WHERE `email` like '$email' AND `conhash` like '$hash' AND `confirmed`='0'";
    $qry=$cxn->query($qry);
    $qry=$qry->num_rows;

    if($qry > 0){
        // We have a match, activate the account
        $qry= "UPDATE $table SET `confirmed`='1' WHERE `email`='$email' AND `conhash`='$hash' AND `confirmed`='0'";
        $qry=$cxn->query($qry);
        if($qry){
            echo '<h1>Your account has been activated, you can now login</h1>';
        }
        else{
            echo '<h1>Failed to activate. Try again Later.</h1>';    
        }
    }else{
        // No match -> invalid url or account has already been activated.
        echo '<h1>The url is either invalid or you already have activated your account.</h1>';
    }
                 
}else{
    // Invalid approach
    echo '<h1>Invalid approach, please use the link that has been send to your email.</h1>';
}
?>