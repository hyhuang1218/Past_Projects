<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@ page import="data.card"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

ArrayList<card> cardList = (ArrayList<card>)request.getAttribute("recommends");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>Result</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<style type="text/css">
		<!--
		body {
			background-image: url(./image/7.jpg);
			background-repeat: no-repeat;
		}
		.icon1 {
			position:absolute; left:0px; top:370px;
		}
		.icon2 {
			position:absolute; left:0px; top:420px;
		}
		.icon3 {
			position:absolute; left:0px; top:470px;
		}
		.icon4 {
			position:absolute; left:0px; top:520px;
		}
		.icon5 {
			position:absolute; left:0px; top:3650px;
		}
		.icon6 {
			position:absolute; left:0px; top:3700px;
		}
		.icon7 {
			position:absolute; left:0px; top:3750px;
		}
		.icon8 {
			position:absolute; left:0px; top:3800px;
		}
		#form1 {
			position:absolute; left:350px; top:375px;
		}
		.STYLE1 {
			font-family: Georgia, "Times New Roman", Times, serif;
			color: #333333;
			font-size: 14px;
			text-align: center;
		}
		.STYLE7 {
			font-family: Georgia, "Times New Roman", Times, serif;
			color: #333333;
			font-weight: bold;
			font-size: 12px;
			text-align: left;
		}
		.STYLE8 {font-weight: bold}
		.STYLE9 {font-weight: bold}
		.STYLE10 {
			font-size: 18px;
			font-weight: bold;
		}
		
		-->
	</style>
  </head>
  
  <body>
 
    <form id="form1" name="form1" method="post" action="">
     <%if(cardList.size()==0){%>
     <p>
     	We'are sorry that we couldn't find a card recommended to you.<br/>
     	Maybe you can return to the <a href="Survey.jsp">Survey Page</a> to complete the form with different information.
     </p>
     <%} %>
  <table width="768" border="0">
 	<tr>
      <td height="22" colspan="4" >&nbsp;<h2>Selection Result</h2><hr/></td>

    </tr>
    <tr>
      <td height="28"><div align="center" class="STYLE10">Card Name </div></td>
      <td class="STYLE10"><div align="center">Introduction</div></td>
      <td><div align="center" class="STYLE10">Bank</div></td>
      <td><div align="center" class="STYLE10">Category</div></td>
    </tr>
    <tr>
      <td height="22" colspan="4">&nbsp;<hr/></td>

    </tr>
    <%for(int i=0;i<cardList.size();i++){ 
    card cd=cardList.get(i);%>
    <tr>
      <td width="277" height="190"><p align="center" class="STYLE1"><%=cd.name %></p>
        <p align="center" class="STYLE1"><img src="./image/card<%=cd.id+1 %>.jpg" width="228" height="143" /></p>
      <p align="center">&nbsp;</p></td>
      <td width="325" class="STYLE7"><%=cd.description %>
		<p align="center"><a href="<%=cd.link %>" target="_blank">See Details</a> </p>
      <a href="https://creditcards.citi.com/credit-cards/citi-simplicity/" target="_blank"></a>      	   </td>
      <td width="68" class="STYLE7"><div align="center"><%=cd.bank %> </div></td>
      <td width="80" class="STYLE7"><div align="center"><%=cd.category %></div></td>
    </tr>
    
    <tr>
      <td height="22" colspan="4">&nbsp;<hr/></td>

    </tr>
    <%} %>
    <tr>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
  </table>
</form>
<a href="index.jsp"><img src="./image/jiaobiao_HOME.png" alt="home page" width="284" height="33" border="0" class="icon1"/></a> <a href="Survey.jsp"><img src="./image/jiaobiao_SURVEY.png" alt="question form" width="284" height="33" border="0" class="icon2"/></a> <a href="CardList.jsp"><img src="./image/jiaobiao_CARD LIST.png" alt="credit card list" width="284" height="33" border="0" class="icon3"/></a> <a href="Untitled-1.html"><img src="./image/jiaobiao_WE ARE.png" alt="group information" width="284" height="33" border="0" class="icon4"/></a>
<a href="index.jsp"><img src="./image/jiaobiao_HOME.png" alt="home page" width="284" height="33" border="0" class="icon5"/></a> <a href="Survey.jsp"><img src="./image/jiaobiao_SURVEY.png" alt="question form" width="284" height="33" border="0" class="icon6"/></a> <a href="CardList.jsp"><img src="./image/jiaobiao_CARD LIST.png" alt="credit card list" width="284" height="33" border="0" class="icon7"/></a> <a href="Untitled-1.html"><img src="./image/jiaobiao_WE ARE.png" alt="group information" width="284" height="33" border="0" class="icon8"/></a>
  </body>
</html>
