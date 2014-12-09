<?php

/****

* Simple PHP application for using the Bing Search API

*/
    //You should change acctKey to yours
    $acctKey = 'UeDHBNH/QrR136bzbcLp5dpXeqTQ231jKhRq6R7wr+c';

    

    if ($_GET['query'])

    {

        $rootUri = 'https://api.datamarket.azure.com/Bing/Search';
        // Encode the query and the single quotes that must surround it.

        $query = urlencode("'{$_GET['query']}'");
        
        // Get the selected service operation (Web or Image).

        $serviceOp = $_GET['service_op'];

        // Construct the full URI for the query.

        $requestUri = "$rootUri/$serviceOp?\$format=json&Query=$query";
        
        // Encode the credentials and create the stream context.
        $auth = base64_encode("$acctKey:$acctKey");

        $data = array('http' => array('request_fulluri' => true, 'ignore_errors' => true, 'header' => "Authorization: Basic $auth"));

        $context = stream_context_create($data);

        // Get the response from Bing.

        $response = file_get_contents($requestUri, 0, $context);

        // Decode the response. 
        $jsonObj = json_decode($response); 

        $contents = json_encode($jsonObj->d->results);
        

    }   

    echo $contents;
    

?>