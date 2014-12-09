/* 
 * Author: Hanying Huang
 * Time: 04/2014
 * This is for processing the input query
 */


/**
 * parseUrl()
 * parse the query passed from the homepage
 * @returns null
 */
function parseUrl(){
    var query = window.location.search;
    if (query.substring(0, 1) === '?') {
        query = decodeURI(query.substring(1));
        document.getElementById("textBar").value=query;
        searchOnBing();
    }                
}

var currentPage = 1;  //current result page
var totalPages = 1;   //total result pages
var isAppend = false; //flag for checking whether it is an appending action


//Ajax
var res;            //search results
var relateWordRes;  //relative words results
var synonymRes;     //synonyms results
var similarRes;     //homonyms results


var req;
var relateWordReq;
var synonymReq;
var similarReq;
//Initiate request object
if (window.XMLHttpRequest) {//code for IE7+, Firefox, Chrome, Opera, Safari
    req = new XMLHttpRequest();
    relateWordReq = new XMLHttpRequest();
    synonymReq = new XMLHttpRequest();
    similarReq = new XMLHttpRequest();
}
else if (window.ActiveXObject) { //code for IE6, IE5
    req = new ActiveXObject("Microsoft.XMLHTTP");
    relateWordReq = new ActiveXObject("Microsoft.XMLHTTP");
    synonymReq = new ActiveXObject("Microsoft.XMLHTTP");
    similarReq = new ActiveXObject("Microsoft.XMLHTTP");
}
else {
    alert("Your browser does not support XMLHTTP!");
}

/**
 * Functions to get the search results for the query
 * @returns null
 */
function searchOnBing() {
    var search_item = document.getElementById('textBar').value;
    if(isAppend === false){
        document.getElementById("relateList").innerHTML = "";
        isAppend =false;
    }
    document.getElementById("synonymList").innerHTML = "";
    //get related words if the query length is less than 2
    if(search_item.split(" ").length < 2){
       searchRelateWords(search_item);    
    }
    //search the query
    if(search_item !== ""){
        var phpRequest = "../process/search.php?query="+search_item+"&service_op=Web";                                 

        req.onreadystatechange = function() {
            if (req.readyState === 4 && req.status === 200) {
                res = eval(req.responseText);    

                showSearchResult(1,false, false);
                totalPages = Math.ceil(res.length/10);
                showPageSelection(totalPages);
            }
        };
        req.open("GET", phpRequest, true);
        req.send();

    }else{
        alert("Please enter a query!");
    }
    //search similar words, possible synonyms
    if(search_item !== ""){
       searchSimilarWords(search_item);
       getSynonyms(search_item);
    }
}

/**
 * To display the search results on the screen.
 * @param {int} page
 * @param {boolean} previous
 * @param {boolean} next
 * @returns null
 */
function showSearchResult(page,previous, next){
    if(previous === true){
        currentPage--;
    }else if(next === true){
        currentPage++;
    }else{
        currentPage = page;
    }
    
    if(currentPage <= 0){
        currentPage = 1 ;
        alert("No previous page!");
        return ;
    }else if(currentPage > totalPages){
        currentPage = totalPages;
        alert("No more page!");
        return;
    }

    var count = 0;
    //each page shows 10 results
    var resultDiv = "";
    for(var i = (currentPage - 1) * 10; i < res.length && count < 10; i++){           
           resultDiv += "<tr><td> <a href=\""+res[i].Url+"\" class=\"linkBlue\" id=\"title\">"+res[i].Title+"</a></td></tr>";
           resultDiv += "<tr><td class=\"fontGray\" id=\"description\"> "+res[i].Description+"</td></tr>";
           resultDiv += "<tr><td class=\"docSeparate\" id=\"displayURL\"> <a  class=\"linkGold\" href=\""+res[i].Url+"\">"+res[i].DisplayUrl+"</a></td></tr>";

           count++;
    }
    document.getElementById("result").innerHTML = resultDiv;
    showPageSelection(totalPages);

}

/**
 * To show the page selections below the results 
 * @param {int} totalPages
 * @returns null
 */
function showPageSelection(totalPages){
    var list = "<a href=\"#\" class=\"linkBlue_page\" onclick=\"showSearchResult(0, true,false)\" >Previous</a>";
    for(var i = 0; i < totalPages; i++){
        if(i === (currentPage - 1)){
            list += "<a href=\"#\" class=\"currentPage\" >"+(i+1)+"</a>";
        }else{
            list += "<a href=\"#\" class=\"linkBlue_page\"   onclick=\"showSearchResult("+(i+1)+",false,false)\" >"+(i+1)+"</a>";
        }
    }
    list += "<a href=\"#\" class=\"linkBlue_page\" onclick=\"showSearchResult(0, false,true)\" >Next</a>";

    document.getElementById("page_select").innerHTML = list;
}

/**
 * To search similar words (homonyms)
 * @param {String} base
 * @returns 
 */
function searchSimilarWords(base){
   var phpRequest = "../process/similarWords.php?base="+base;    

    similarReq.onreadystatechange = function() {
        console.log(similarReq.status);
        if (similarReq.readyState === 4 && similarReq.status === 200) {
            similarRes = similarReq.responseText;  
            document.getElementById("similarList").innerHTML = similarRes;
        }
    };
    similarReq.open("GET", phpRequest, true);
    similarReq.send();
}

/**
 * To get relative words
 * @param {String} base
 * @returns null
 */
function searchRelateWords(base){              
    var phpRequest = "../process/relateWords.php?base="+base;                                 
    
    relateWordReq.onreadystatechange = function() {
        console.log(relateWordReq.status);
        if (relateWordReq.readyState === 4 && relateWordReq.status === 200) {
            relateWordRes = relateWordReq.responseText;    
            document.getElementById("relateList").innerHTML = relateWordRes;
        }
    };
    relateWordReq.open("GET", phpRequest, true);
    relateWordReq.send();

}
/**
 * To get synonyms
 * @param {String} base
 * @returns null
 */
function getSynonyms(base){
    var phpRequest = "../process/synonym.php?origin="+base;                                 

    synonymReq.onreadystatechange = function() {
        console.log(synonymReq.status);
        if (synonymReq.readyState === 4 && synonymReq.status === 200) {
            synonymRes = eval(synonymReq.responseText);  
            if(synonymRes.length > 0 && synonymRes[0].Synonym !== ""){
                synonymDiv = "<tr class=\"fontNote\"><td>Try these</td></tr>";
                var count = 0;
                for(var i = 0; i < synonymRes.length && count < 5; i++, count++){
                    synonymDiv += "<tr><td id=\"synonym_"+count+"\"><a href=\"#\" class=\"reform_button\" onclick=\"changeQuery('synonym_"+count+"')\">"+synonymRes[i].Synonym+"</a></tr></td>";
                }
                document.getElementById("synonymList").innerHTML = synonymDiv;
            }

        }
    };
    synonymReq.open("GET", phpRequest, true);
    synonymReq.send();
}
/**
 * To change the whole query
 * @param {String} id
 * @returns null
 */
function changeQuery(id){
    document.getElementById("textBar").value = document.getElementById(id).textContent;
    searchOnBing();
}
/**
 * Append to the tail of the current query
 * @param {String} id
 * @returns null
 */
function appendQuery(id){
    document.getElementById("textBar").value += " "+document.getElementById(id).textContent;
    isAppend = true;
    searchOnBing();
}
/**
 * To change the specific term in current query
 * @param {String} word
 * @param {String} old
 * @returns 
 */
function changeWord(word, old){
    var query = document.getElementById("textBar").value;
    query = query.replace(old,word);
    
    document.getElementById("textBar").value = query;
    searchOnBing();
}

