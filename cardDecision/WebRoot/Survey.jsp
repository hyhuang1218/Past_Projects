
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>Survey</title>
<style type="text/css">
<!--
body {
	background-image: url(./image/juanzhou_fuben.png);
	background-repeat: no-repeat;
	background-position:inherit;
	background-color: #E8E8E8;
}
.STYLE2 {
	font-size: 85px;
	font-family: "Courier New", Courier, monospace;
	line-height: 0px;
}
.STYLE4 {
	font-family: "Courier New", Courier, monospace;
	font-weight: bold;
	font-size: 18px;
}
.icon1 {
	position:absolute; left:0px; top:1700px;
}
.icon2 {
	position:absolute; left:0px; top:1750px;
}
.icon3 {
	position:absolute; left:0px; top:1800px;
}
.icon4 {
	position:absolute; left:0px; top:1850px;
}
.icon5 {
	position:absolute; left:0px; top:270px;
}
.icon6 {
	position:absolute; left:0px; top:320px;
}
.icon7 {
	position:absolute; left:0px; top:370px;
}
.icon8 {
	position:absolute; left:0px; top:420px;
}

-->
</style>
<script language="javascript"> 
	function validate(){
		var transferAmount = document.getElementById("transferAmount").value;
		var loanInterest = document.getElementById("loanInterest").value;
		var remainPurchaseBalance = document.getElementById("remainPurchaseBalance").value;
		var remainTransferBalance = document.getElementById("remainTransferBalance").value;
		var yearSpend = document.getElementById("yearSpend").value;
		var superSpend = document.getElementById("superSpend").value;
		var gasSpend = document.getElementById("gasSpend").value;
		var flightSpend = document.getElementById("flightSpend").value;
		var cardNum = document.getElementById("cardNum").value;
		var currentLimit = document.getElementById("currentLimit").value;
		var overAmount = document.getElementById("overAmount").value;
		//not empty and are numbers
		if(isNaN(transferAmount)||isNaN(loanInterest)||isNaN(remainPurchaseBalance)
				||isNaN(remainTransferBalance)||isNaN(yearSpend)||isNaN(superSpend)||isNaN(gasSpend)||isNaN(flightSpend)||isNaN(cardNum)||isNaN(currentLimit)
				||isNaN(overAmount)||transferAmount == ""||loanInterest == ""||remainPurchaseBalance == ""||remainTransferBalance == ""
				||yearSpend == ""||superSpend == ""||gasSpend == ""||flightSpend == ""||cardNum == ""||currentLimit == ""||overAmount == "")
		{
			alert("You must enter a number in each text field!"+transferAmount);
			}
		else if(yearSpend<=0){ //must be positive number
			
			alert("You must enter a positive number in question 7! (not include 0)")
		}else if(parseInt(gasSpend)<0 || parseInt(superSpend)<0 || parseInt(flightSpend)<0){ //must be positive number or 0
			
			alert("You must enter a positive number in question 8, 9, 10! (include 0)")
		}
		else if(parseInt(yearSpend)<(parseInt(gasSpend)+parseInt(superSpend)+parseInt(flightSpend))){
			alert("The year spend should be equal to or greater than the sum of different kinds of spend in question 7");
			}
		else{ //submit form
			alert("Submit Successfully!");
			document.getElementById("form1").submit();
		}
	}
</script>
</head>

<body>
<p>&nbsp;</p>
<form id="form1" name="form1" method="post" action="servlet">
   &nbsp;
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <table width="940" border="0">
        
    <tr>
      <td width="442" rowspan="21">
	    <a href="index.jsp"><img src="./image/jiaobiao_HOME.png" alt="home page" width="284" height="33" border="0" class="icon5"/></a>
        <a href="Survey.jsp"><img src="./image/jiaobiao_SURVEY.png" alt="question form" width="284" height="33" border="0" class="icon6"/></a>
        <a href="CardList.jsp"><img src="./image/jiaobiao_CARD LIST.png" alt="credit card list" width="284" height="33" border="0" class="icon7"/></a>
        <a href="Group.html"><img src="./image/jiaobiao_WE ARE.png" alt="group information" width="284" height="33" border="0" class="icon8"/></a>	  </td>
      <td width="488"><div align="center">
        <p class="STYLE2">SURVEY </p>
        </div></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 1. What is your credit score ?</td>
    </tr>
    <tr>
      <td height="41" class="STYLE4"><p>
           &nbsp;
           &nbsp; 
          <input name="creditLevel" type="radio" value="1" checked="checked" />
      A.300-550
        &nbsp; 
        <input type="radio" name="creditLevel" value="2" />
      B.550-620</p>
        <p>
           &nbsp;
           &nbsp; 
          <input type="radio" name="creditLevel" value="3" />
          C.620-680
          &nbsp;
           <input type="radio" name="creditLevel" value="4" />
      D.>680</p></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 2. Do you have any bad record?</td>
    </tr>
    <tr>
      <td height="18" class="STYLE4">&nbsp;&nbsp;&nbsp; <input type="radio" name="badRecord" value="1" />
      Yes&nbsp; <input name="badRecord" type="radio" value="0" checked="checked" />
      No</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 3. Are you going to close the card within one year?</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp;
        <input name="cancelWithinYear" type="radio" value="1" checked="checked" />
Yes&nbsp;
<input type="radio" name="cancelWithinYear" value="0" />
No</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 4. What is the average amount you transfer each year? </td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp; <input name="transferAmount" type="text" id="transferAmount" value="0" /></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 5. Do you need money now?</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp;
        <input onclick= "hid1.style.display= 'inline'; hid2.style.display= 'inline'" type="radio" name="needMoney" value="1" />
      A.Yes&nbsp; <input onclick= "hid1.style.display= 'none'; hid2.style.display= 'none'" name="needMoney" type="radio" value="0" checked="checked" />
      B.No</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4"> <div id= "hid1" style="display:none">&nbsp; 5.1. What is your average current interest rate on loans?</div></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp; <div id="hid2" style="display:none"><input name="loanInterest" type="text" id="loanInterest" value="0" /></div></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 6. Do you pay your balance in full and on time?</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp; <input onclick= "hid3.style.display= 'none'; hid4.style.display= 'none'; hid5.style.display= 'none'; hid6.style.display= 'none'" name="payBalanceOnTime" type="radio" value="always" checked="checked" />
      A.Always&nbsp; <input onclick= "hid3.style.display= 'inline'; hid4.style.display= 'inline'; hid5.style.display= 'inline'; hid6.style.display= 'inline'" type="radio" name="payBalanceOnTime" value="sometimes" />
      B.Sometimes 
      <input onclick= "hid3.style.display= 'inline'; hid4.style.display= 'inline'; hid5.style.display= 'inline'; hid6.style.display= 'inline'" type="radio" name="payBalanceOnTime" value="rarely" />
      C.Rarely</td>
    </tr>
    <tr>
      <td rowspan="53">&nbsp;</td>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4"><div id="hid3" style="display:none">&nbsp; 6.1. How much purchase bill will not be paid in one month? </div></td>
    </tr>
    <tr>
      <td class="STYLE4"><div id="hid4" style="display:none">&nbsp;&nbsp;&nbsp; <input name="remainPurchaseBalance" type="text" id="remainPurchaseBalance" value="0" /></div></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4"><div id="hid5" style="display:none">&nbsp; 6.2. How much transfer bill will not be paid in one month?</div></td>
    </tr>
    <tr>
      <td class="STYLE4"><div id="hid6" style="display:none">&nbsp;&nbsp;&nbsp; <input name="remainTransferBalance" type="text" id="remainTransferBalance" value="0" /></div></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 7. How much money you spend per year?</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp; <input name="yearSpend" type="text" id="yearSpend" value="0" /></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 8. How much money you spend on supermarket per year on average?</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp; <input name="superSpend" type="text" id="superSpend" value="0" /></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 9. How much money you spend on gas per year on average?</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp; <input name="gasSpend" type="text" id="gasSpend" value="0" /></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 10. How much money you spend on flight per year on average?</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp; <input name="flightSpend" type="text" id="flightSpend" value="0" /></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 11. Do you accept a card with an annual fee?</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp; <input name="acceptAnnualFee" type="radio" value="1" checked="checked" />
      Yes&nbsp; <input type="radio" name="acceptAnnualFee" value="0" />
      No</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 12. What kinds of perks do you prefer?</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp; 
        <input name="cardPreference" type="radio" value="cash" checked="checked" />
        A.Cash back&nbsp; 
        <input type="radio" name="cardPreference" value="flight" />
      B.Miles
      <p>
         &nbsp;
         &nbsp; 
         <input type="radio" name="cardPreference" value="point" />
      C.Points&nbsp; 
      &nbsp;&nbsp; 
      <input type="radio" name="cardPreference" value="none" />
      D.Either</p></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 13. How many credit cards do you own now?</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp; <input name="cardNum" type="text" id="cardNum" value="0" /></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 14. What is your highest credit limit?</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp; <input name="currentLimit" type="text" id="currentLimit" value="0" /></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp; 15. Do you use your credit card over limited frequently?</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp; <input onclick= "hid7.style.display= 'inline'; hid8.style.display= 'inline'" type="radio" name="overLimit" value="1" />
      A.Yes&nbsp; <input onclick= "hid7.style.display= 'none'; hid8.style.display= 'none'" name="overLimit" type="radio" value="0" checked="checked" />
      B.No</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4"><div id="hid7" style="display:none">&nbsp; 15.1 How much money do you spend over your credit limit per month on average?</div></td>
    </tr>
    <tr>
      <td class="STYLE4"><div id="hid8" style="display:none">&nbsp;&nbsp;&nbsp; <input name="overAmount" type="text" id="overAmount" value="0" /></div></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;</td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;
        
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <input name="Submit" type="button" class="STYLE4" value="Submit" onclick="validate()"/>
     &nbsp; <input name="Reset" type="reset" class="STYLE4" id="Reset" value="Reset" />
     <div align="left"></div></td>
    </tr>
    <tr>
      <td class="STYLE4">&nbsp;&nbsp;&nbsp;</td>
    </tr>
  </table>
</form>
	<a href="index.jsp"><img src="./image/jiaobiao_HOME.png" alt="home page" width="284" height="33" border="0" class = "icon1"/></a>
	<a href="Survey.jsp"><img src="./image/jiaobiao_SURVEY.png" alt="question form" width="284" height="33" border="0" class = "icon2"/></a>
	<a href="CardList.jsp"><img src="./image/jiaobiao_CARD LIST.png" alt="credit card list" width="284" height="33" border="0" class = "icon3"/></a>
	<a href="Group.html"><img src="./image/jiaobiao_WE ARE.png" alt="group information" width="284" height="33" border="0" class = "icon4"/></a>
</body>
</html>
