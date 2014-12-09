<?php

/* 
 * Author: Hanying Huang
 * Date: 04/2013
 * Get synonyms for the original word
 */

    $acctKey = 'UeDHBNH/QrR136bzbcLp5dpXeqTQ231jKhRq6R7wr+c';

    

    if ($_GET['origin'])

    {

        $rootUri = 'https://api.datamarket.azure.com/Bing/Synonyms/v1/GetSynonyms?$format=json&Query=';
        // Encode the query and the single quotes that must surround it.

        $query = urlencode("'{$_GET['origin']}'");

        // Construct the full URI for the query.

        $requestUri = "$rootUri$query";
        //echo $requestUri;
        // Encode the credentials and create the stream context.

        $auth = base64_encode("$acctKey:$acctKey");

        $data = array('http' => array('request_fulluri' => true, 'ignore_errors' => true, 'header' => "Authorization: Basic $auth"));

        $context = stream_context_create($data);

        // Get the response from Bing.

        $response = file_get_contents($requestUri, 0, $context);
        $jsonObj = json_decode($response); 
        
        $innerHtml = ''; // Parse each result according to its metadata type. 
        foreach($jsonObj->d->results as $value) { 
            $innerHtml = $innerHtml.$value->Synonym.'\\';
        } 
        
        $contents = json_encode($jsonObj->d->results);
    }   
    
    echo $contents;
?>