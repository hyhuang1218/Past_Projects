<?php

/* 
 * Author: Hanying Huang
 * Time: 04/2014
 * This is for getting the homonyms/ similiar words
 */
if($_GET['base']){
    $base=urldecode($_GET['base']);
    $con=mysqli_connect('localhost','root','root','voice_search') or die('Could not connect: '.mysql_error());
    //echo $base;
    $base= explode(' ',$base);
    //echo $base[0];
    $returnText='';
    $firstResult = true;
    foreach ($base as $term) {      
        //echo $term;
        $query = "select word from terms t 
                where t.id IN 
                (select similar_id from similar_words sw 
                        where sw.base_id IN (select temp.id from terms temp where temp.word='$term')
                ) 
        order by t.search_freq desc limit 5;";
        //get all homonyms
        $result = mysqli_query($con,$query) or die('Something error'.mysql_error());

        $num=mysqli_num_rows($result);
        
        $base = 'field';
        if($num>0&&$firstResult){
            $firstResult =false;
            $returnText='<tr class="fontNote"><td colspan="2">Sounds like</td></tr>';
        }
        if($num>0){
            
            $returnText=$returnText.'<tr class="fontBlack"><td>'.$term.'</td><td>';
        }

        while ($row = mysqli_fetch_array($result)){
            $returnText=$returnText.'<a href="#" onclick="changeWord(\''.$row[word].'\',\''.$term.'\')">'.$row[word].'</a>&nbsp;&nbsp;';
        }
        if($num>0){
            $returnText=$returnText.'</td></tr>';
        }

        
    }
    mysqli_close($con);
    echo $returnText;
}