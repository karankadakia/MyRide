<?php
define('FIREBASE_API_KEY', "AAAALv9TVaE:APA91bFrPsLwQB_W8-Ao2lPwo43mvhr_J9LTcix5aT8_OedEsTSOhrEVGw7IpVKl_Rmr6PcRuSDYhaskVJA7G9KrTMqi0Dp51fQbV7uTzpUZXbImeXCNXhMPNv-07YCHX7VoQ9DT8_xstYpNWuIwRDfs1aBbbbmrjw");

class Firebase {
        // sending push message to single user by firebase reg id
        public function sendDriver($to, $message) {
            $fields = array(
                'to' => $to,
                'data' => $message,
            );
            return $this->sendPushNotification($fields);
        }
        public function sendUser($to, $message) {
            $fields = array(
                'to' => $to,
                'data' => $message,
            );
            return $this->sendPushNotification($fields);
        }        
        // function makes curl request to firebase servers
        private function sendPushNotification($fields) {
            // Set POST variables
            $url = 'https://fcm.googleapis.com/fcm/send';
            $headers = array(
                'Authorization: key=' . FIREBASE_API_KEY,
                'Content-Type: application/json'
            );
            // Open connection
            $ch = curl_init();
            // Set the url, number of POST vars, POST data
            curl_setopt($ch, CURLOPT_URL, $url);
            curl_setopt($ch, CURLOPT_POST, true);
            curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            // Disabling SSL Certificate support temporarly
            curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
            
            // Execute post
            $result = curl_exec($ch);
            if ($result === FALSE) {
                die('Curl failed: ' . curl_error($ch));
            }
            // Close connection
            curl_close($ch);
            return $result;
        }
    }

?>