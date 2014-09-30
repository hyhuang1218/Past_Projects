<?php

/* 
 * Author: Hanying Huang
 * Time: 04/2014
 * This is for getting the relative words
 */
$rootUri = 'http://www.veryrelated.com/related-api-v1.php?key=sampleapikey&results=6&base=';
if ($_GET['base']){
    $base = $_GET['base'];
    $requestUri = "$rootUri$base";
    

    $data = array('http' => array('request_fulluri' => true, 'ignore_errors' => true));

    $context = stream_context_create($data);

    $response = file_get_contents($requestUri, 0, $context);
    if (!($xml = strstr($response, '<?xml'))) {  
        $xml = null;  
    }
    $simple_xml = simplexml_load_string($xml);  
    
    $n=0;
    foreach($simple_xml->Result as $result)  {      
        if($n==0){
            $innerHtml ='<tr><td colspan="2" class="fontNote">Guess you like</td></tr>';
        }
        $innerHtml = $innerHtml.'<tr><td class="fontBlack rightSeparate" id="relative_'.$n.'">'.$result->Text.'</td>'
                . '<td>'
                . '<a href="#" class="reform_button" onclick="changeQuery(\'relative_'.$n.'\')">Change</a>'.'&nbsp;&nbsp;'
                . '<a href="#" class="reform_button" onclick="appendQuery(\'relative_'.$n.'\')">Append</a>'.'&nbsp;&nbsp;</td></tr>'
                ;
        $n=$n+1;
    }
    
    echo $innerHtml;
}