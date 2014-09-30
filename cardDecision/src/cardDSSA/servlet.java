package cardDSSA;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;

import data.card;
import data.form;
import data.result;

public class servlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public servlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
		
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("Result.jsp");		
		form fm=new form();	
		card cd=new card();
		ArrayList<result> computingResult=new ArrayList<result>();
		
		fm.creditLevel=Integer.parseInt(request.getParameter("creditLevel"));
		fm.cardPreference=request.getParameter("cardPreference");
		fm.cardNum=Integer.parseInt(request.getParameter("cardNum"));
		fm.currentLimit=Integer.parseInt(request.getParameter("currentLimit"));
		fm.loanInterest=Double.parseDouble(request.getParameter("loanInterest"));
		fm.transferAmount=Double.parseDouble(request.getParameter("transferAmount"));
		fm.remainPurchaseBalance=Double.parseDouble(request.getParameter("remainPurchaseBalance"));
		fm.remainTransferBalance=Double.parseDouble(request.getParameter("remainTransferBalance"));
		fm.yearSpend=Double.parseDouble(request.getParameter("yearSpend"));
		fm.gasSpend=Double.parseDouble(request.getParameter("gasSpend"));
		fm.superSpend=Double.parseDouble(request.getParameter("superSpend"));
		fm.flightSpend=Double.parseDouble(request.getParameter("flightSpend"));
		fm.otherSpend=fm.yearSpend-fm.gasSpend-fm.superSpend-fm.flightSpend;
		fm.overAmount=Double.parseDouble(request.getParameter("overAmount"));
		fm.cancelWithinYear=(Integer.parseInt(request.getParameter("cancelWithinYear"))==1)?true:false;
		fm.payBalanceOnTime=request.getParameter("payBalanceOnTime");
		fm.badRecord=(Integer.parseInt(request.getParameter("badRecord"))==1)?true:false;
		fm.needMoney=(Integer.parseInt(request.getParameter("needMoney"))==1)?true:false;
		fm.acceptAnnualFee=(Integer.parseInt(request.getParameter("acceptAnnualFee"))==1)?true:false;

		
		cd.freeAnnual=0;
		cd.annualFee=65;
		cd.bonus=150;
		cd.freeAprMonths=0;
		cd.transferFee="0.03,5";
		cd.overLimitFee=35;
		cd.gasMultiple=1;
		cd.superMultiple=1;
		cd.flightMultiple=1;
		cd.cashBackRate=0;
		cd.mileBackRate=0.02;
		cd.pointBackRate=0;
		cd.aprPurchase="0.15,0.17,0.25";
		cd.aprBalanceTransfer="0.15,0.17,0.25";
		cd.approvedLevel=2;
		
		preSelect preSel=new preSelect(fm);
		ArrayList<card> cards=preSel.cardSelect();
		ArrayList<card> recommendedCards=new ArrayList<card>();
		
		
		
		if(cards.size()<=0){
			System.out.println("There isn't any card for you in our database./nPlease use other information.");
			request.setAttribute("recommends", recommendedCards);
			rd.forward(request,response);
		}
		else if(cards.size()==1){
			System.out.println("Complet Selection:"+cards.get(0).name);	
			recommendedCards.add(cards.get(0));
			request.setAttribute("recommends", recommendedCards);
			rd.forward(request,response);
		}
		else{
			cardCompute computeCards=new cardCompute();
			computingResult=computeCards.startCompute(cards,fm);
		}
		
		//sort result and select cards
		double largeEMV=computingResult.get(0).getEMV();
		recommendedCards.add(cards.get(0));//same order as the computingResult
		for(int i=1; i<computingResult.size();i++){
			double EMV;
			if((EMV=computingResult.get(i).getEMV())>largeEMV){
				recommendedCards.clear();
				recommendedCards.add(cards.get(i));
				largeEMV=EMV;
			}
			else if((EMV==largeEMV)){
				recommendedCards.add(cards.get(i));
			}
			//System.out.println(largeEMV+"///"+recommendedCards);
		}
		request.setAttribute("recommends", recommendedCards);
		rd.forward(request,response);
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
