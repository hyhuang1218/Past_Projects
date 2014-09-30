package cardDSSA;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import data.card;
import data.form;


public class preSelect{
	public form fm;
	public preSelect(form fm){
		this.fm=fm;
	}

	public ArrayList<card> cardSelect(){
		

		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/decisionFinal";
		String user = "root";
		String pwd = "123456";

		ArrayList<card> cardList = new ArrayList<card>();

		try{
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, pwd);
			if(!conn.isClosed()){
				//System.out.println("Successed in connecting to database!");
				Statement st = conn.createStatement();
				String sql;
				//System.out.println(fm.badRecord);
				if(fm.badRecord){
					sql = "select * from credit_card C, card_description D where C.category='secured' and C.id = D.id";
					//System.out.println(1111);
				}
			 	else{
			      sql= "select * from credit_card C, card_description D where C.id = D.id";
				if(fm.needMoney){
					sql+=" and C.over_limit_fee=0 and C.category!='secured' and C.apr_on_purchase<="+"'"+String.valueOf(fm.loanInterest)+"'";
					//System.out.println(2222+sql);

				}

				if(!fm.acceptAnnualFee){
					sql+= " and C.annual_fee=0";
					//System.out.println(3333);

				}

				String type=fm.cardPreference;
				   if(!type.equals("none")){
			        sql+=" and C.category="+"'"+type+"'";
			        //System.out.println(4444);
				   }

				}
				//System.out.println(sql);
				ResultSet rs = st.executeQuery(sql);
				//System.out.println("Result shows below:");
				while(rs.next()){
					card c = new card();
					c.id = rs.getInt("id");
					c.name = rs.getString("name");
					c.category = rs.getString("category");
					c.bank = rs.getString("bank");
					c.freeAnnual = rs.getInt("free_annual_fee_period");
					c.annualFee = rs.getInt("annual_fee");
					c.bonus = rs.getInt("bonus");
					c.gasMultiple = rs.getDouble("gas_purchase_multiplyer");
					c.superMultiple = rs.getDouble("supermarket_multiplyer");
					c.flightMultiple = rs.getDouble("flight_purchase_multiplyer");
					c.cashBackRate = rs.getDouble("base_cash_back_rate");
					c.mileBackRate = rs.getDouble("base_mile_rate");
					c.pointBackRate = rs.getDouble("base_point_rate");
					c.freeAprMonths = rs.getInt("free_apr_on_purchase_period");
					c.aprPurchase = rs.getString("apr_on_purchase");
					c.aprBalanceTransfer = rs.getString("apr_on_balance_transfer");
					c.transferFee = rs.getString("fee_on_balance_transfer");
					c.overLimitFee = rs.getDouble("over_limit_fee");
					c.approvedLevel = rs.getInt("approved_level");
					c.description = rs.getString("description");
					c.link = rs.getString("link");
					cardList.add(c);
				}
				rs.close();
			}
		}catch(ClassNotFoundException e){
			System.out.println("Sorry, can't find the Driver!");
			e.printStackTrace();
		}catch(SQLException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println(cardList.get(0).name);
		return cardList;
	}
}
