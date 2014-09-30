package cardDSSA;

import data.form;
import data.card;

public class compute {
	public form fm;
	public card cd;
	public compute(form fm, card cd){
		this.fm=fm;
		this.cd=cd;
	}


	
	public String[] computeProbability(){
		String[] chancePro=new String[4];
		Double[] onTimePro=new Double[2];
		Double[] aprPurchasePro=new Double[3];
		Double[] aprTransferPro=new Double[3];
		Double[] approvePro=new Double[2];
		
		String onTime=fm.payBalanceOnTime;
		
		if(onTime.equals("always")){
		 onTimePro[0]=(Double) 0.9;       
		}
		else if(onTime.equals("sometimes")){
		  onTimePro[0]=(Double) 0.5;
		}
		else{
		  onTimePro[0]=(Double) 0.1;
		}
		onTimePro[1]=1-onTimePro[0];
		
		int baseScore=fm.creditLevel;
		int cardScore=cd.approvedLevel;
		int cardNum=fm.cardNum;
		int limit=fm.currentLimit;
		String[] aprPurchase=cd.aprPurchase.split(",");
		
		
		int different=baseScore-cardScore; //user's credit level compared with required level, 
											//positive:higher than required, negative:lower than required
		
		if(different==0){
			approvePro[0]=(Double) 0.6;
			
		}
		else if(different==1){
		   approvePro[0]=(Double) 0.7;
		}	
		else if(different==2){
				   approvePro[0]=(Double) 0.8;
		}	
		else if(different==3){
				   approvePro[0]=(Double) 0.9;
		}	
		else if(different==-1){
				   approvePro[0]=(Double) 0.5;
		}
		else if(different==-2){
			   approvePro[0]=(Double) 0.35;
		}
		else{
			   approvePro[0]=(Double) 0.2;
		}

		if(cardNum>=8){
		approvePro[0]*=0.75;
		}
		else if(cardNum<8&&cardNum>5){
		approvePro[0]*=0.87;
		}
		else if(cardNum<=5&&cardNum>3){
		approvePro[0]*=0.95;
		}
		else{
		approvePro[0]*=1;	
		}
		
		if(limit>10000){
		approvePro[0]*=1.1;
		}
		else if(limit<=10000&&limit>5000){
		approvePro[0]*=1.07;
		}
		else if(limit<=5000&&limit>2000){
		approvePro[0]*=1.01;
		}
		else if(limit<=2000){
		approvePro[0]*=0.97;	
		}
		approvePro[1]=1-approvePro[0];
		
		
		
		
		chancePro[0]=onTimePro[0]+","+onTimePro[1];
		chancePro[1]=approvePro[0]+","+approvePro[1];
		
		
		
		if(aprPurchase.length==1){
			
		aprPurchasePro[0]=(double) 1;
		chancePro[2]=""+aprPurchasePro[0];		
		}
		else if(aprPurchase.length==3){
			//System.out.println(approvePro[0]);
			if(approvePro[0]>=0.9){
				aprPurchasePro[0]=(double)0.88; //low apr purchase
				aprPurchasePro[1]=(double)0.1;
				aprPurchasePro[2]=(double)0.02; //hight apr purchase
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
			else if(approvePro[0]>=0.8&&approvePro[0]<0.9){
				aprPurchasePro[0]=(double)0.8;
				aprPurchasePro[1]=(double)0.15;
				aprPurchasePro[2]=(double)0.05;
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
		    else if(approvePro[0]>=0.7&&approvePro[0]<0.8){
				//System.out.println("fffffffffffffffff");
				aprPurchasePro[0]=(double)0.7;
				aprPurchasePro[1]=(double)0.2;
				aprPurchasePro[2]=(double)0.1;
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
		    else if(approvePro[0]>=0.65&&approvePro[0]<0.7){
				aprPurchasePro[0]=(double)0.4;
				aprPurchasePro[1]=(double)0.35;
				aprPurchasePro[2]=(double)0.25;
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
		    else if(approvePro[0]>=0.6&&approvePro[0]<0.65){
				aprPurchasePro[0]=(double)0.44;
				aprPurchasePro[1]=(double)0.31;
				aprPurchasePro[2]=(double)0.25;
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
		    else if(approvePro[0]>=0.55&&approvePro[0]<0.6){
				aprPurchasePro[0]=(double)0.35;
				aprPurchasePro[1]=(double)0.4;
				aprPurchasePro[2]=(double)0.25;
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
			else if(approvePro[0]>=0.5&&approvePro[0]<0.55){
				aprPurchasePro[0]=(double)0.3;
				aprPurchasePro[1]=(double)0.45;
				aprPurchasePro[2]=(double)0.25;
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
			else if(approvePro[0]>=0.45&&approvePro[0]<0.5){
				aprPurchasePro[0]=(double)0.25;
				aprPurchasePro[1]=(double)0.3;
				aprPurchasePro[2]=(double)0.45;
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
			else if(approvePro[0]>=0.4&&approvePro[0]<0.45){
				aprPurchasePro[0]=(double)0.2;
				aprPurchasePro[1]=(double)0.3;
				aprPurchasePro[2]=(double)0.5;
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
			else if(approvePro[0]>=0.35&&approvePro[0]<0.4){
				aprPurchasePro[0]=(double)0.15;
				aprPurchasePro[1]=(double)0.25;
				aprPurchasePro[2]=(double)0.6;
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
			else if(approvePro[0]>=0.3&&approvePro[0]<0.35){
				aprPurchasePro[0]=(double)0.1;
				aprPurchasePro[1]=(double)0.2;
				aprPurchasePro[2]=(double)0.7;
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
			else if(approvePro[0]>=0.25&&approvePro[0]<0.3){
				aprPurchasePro[0]=(double)0.05;
				aprPurchasePro[1]=(double)0.15;
				aprPurchasePro[2]=(double)0.8;
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
			else if(approvePro[0]>=0.15&&approvePro[0]<0.25){
				aprPurchasePro[0]=(double)0.03;
				aprPurchasePro[1]=(double)0.07;
				aprPurchasePro[2]=(double)0.9;
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
			else{
				aprPurchasePro[0]=(double)0.03;
				aprPurchasePro[1]=(double)0.03;
				aprPurchasePro[2]=(double)0.94;
				chancePro[2]=aprPurchasePro[0]+","+aprPurchasePro[1]+","+aprPurchasePro[2];
			}
			
		}
		
		
		
		String[] aprTransfer=cd.aprBalanceTransfer.split(",");
		
		
		if(aprTransfer.length==1){
			aprTransferPro[0]=(double) 1;
			chancePro[3]=""+aprTransferPro[0];
			
		}
		else if(aprTransfer.length==3){
			if(approvePro[0]>=0.9){
				aprTransferPro[0]=(double)0.88;
				aprTransferPro[1]=(double)0.1;
				aprTransferPro[2]=(double)0.02;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
			else if(approvePro[0]>=0.8&&approvePro[0]<0.9){
				aprTransferPro[0]=(double)0.8;
				aprTransferPro[1]=(double)0.15;
				aprTransferPro[2]=(double)0.05;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
			else if(approvePro[0]>=0.7&&approvePro[0]<0.8){
				aprTransferPro[0]=(double)0.4;
				aprTransferPro[1]=(double)0.2;
				aprTransferPro[2]=(double)0.1;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
			else if(approvePro[0]>=0.65&&approvePro[0]<0.7){
				aprTransferPro[0]=(double)0.4;
				aprTransferPro[1]=(double)0.35;
				aprTransferPro[2]=(double)0.25;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
			else if(approvePro[0]>=0.6&&approvePro[0]<0.65){
				aprTransferPro[0]=(double)0.44;
				aprTransferPro[1]=(double)0.31;
				aprTransferPro[2]=(double)0.25;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
			else if(approvePro[0]>=0.55&&approvePro[0]<0.6){
				aprTransferPro[0]=(double)0.35;
				aprTransferPro[1]=(double)0.4;
				aprTransferPro[2]=(double)0.25;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
			else if(approvePro[0]>=0.5&&approvePro[0]<0.55){
				aprTransferPro[0]=(double)0.3;
				aprTransferPro[1]=(double)0.45;
				aprTransferPro[2]=(double)0.25;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
			else if(approvePro[0]>=0.45&&approvePro[0]<0.5){
				aprTransferPro[0]=(double)0.25;
				aprTransferPro[1]=(double)0.3;
				aprTransferPro[2]=(double)0.45;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
			else if(approvePro[0]>=0.4&&approvePro[0]<0.45){
				aprTransferPro[0]=(double)0.2;
				aprTransferPro[1]=(double)0.3;
				aprTransferPro[2]=(double)0.5;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
			else if(approvePro[0]>=0.35&&approvePro[0]<0.4){
				aprTransferPro[0]=(double)0.15;
				aprTransferPro[1]=(double)0.25;
				aprTransferPro[2]=(double)0.6;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
			else if(approvePro[0]>=0.3&&approvePro[0]<0.35){
				aprTransferPro[0]=(double)0.1;
				aprTransferPro[1]=(double)0.2;
				aprTransferPro[2]=(double)0.7;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
			else if(approvePro[0]>=0.25&&approvePro[0]<0.3){
				aprTransferPro[0]=(double)0.05;
				aprTransferPro[1]=(double)0.15;
				aprTransferPro[2]=(double)0.8;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
			else if(approvePro[0]>=0.15&&approvePro[0]<0.25){
				aprTransferPro[0]=(double)0.03;
				aprTransferPro[1]=(double)0.07;
				aprTransferPro[2]=(double)0.9;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
			else{
				aprTransferPro[0]=(double)0.03;
				aprTransferPro[1]=(double)0.03;
				aprTransferPro[2]=(double)0.94;
				chancePro[3]=aprTransferPro[0]+","+aprTransferPro[1]+","+aprTransferPro[2];
			}
				
		}
																																						
		//System.out.println(chancePro[1]+"1111111111111 "+chancePro[3]);
		
		return chancePro;
		
		
		
	}
	
	public double computeProfit( boolean isApprove, boolean payBalanceOnTime, double aprPurchase, double aprTransfer){
		double profit=0.0;
		double cost=0.0, perk=0.0;
		String[] transferFeeString;
		double transferFeeRate;
		
		if(isApprove){
			if (!fm.cancelWithinYear||cd.freeAnnual==0){	
				cost = cd.annualFee;
				//System.out.println("Add annual fee");
			}
			//System.out.println(cost+"\n");
			//compute cost
			transferFeeString=cd.transferFee.split(",");
			transferFeeRate=Double.parseDouble(transferFeeString[0]);
			
			if(transferFeeRate!=0){
				if(transferFeeString.length<=1){
					cost += fm.transferAmount*transferFeeRate;
				}
				else {
				cost += ((fm.transferAmount*transferFeeRate) > 5?(fm.transferAmount*transferFeeRate):5);
				}
			}
			
			if((fm.cancelWithinYear&&cd.freeAprMonths==0&&!payBalanceOnTime)||(!fm.cancelWithinYear)){
				cost += fm.remainTransferBalance*aprTransfer + fm.remainPurchaseBalance * aprPurchase;
			}
			//System.out.println(cost+"\n");
			cost += cd.overLimitFee*fm.overAmount/(fm.yearSpend/12);
			//System.out.println(cost+"\n");
			//compute perk
			perk = cd.bonus;
			perk +=  fm.gasSpend*cd.gasMultiple*cd.cashBackRate + fm.gasSpend*cd.gasMultiple*cd.mileBackRate + fm.gasSpend*cd.gasMultiple*cd.pointBackRate;
			perk += fm.superSpend*cd.superMultiple*cd.cashBackRate + fm.superSpend*cd.superMultiple*cd.mileBackRate + fm.superSpend*cd.superMultiple*cd.pointBackRate;
			perk += fm.flightSpend*cd.flightMultiple*cd.cashBackRate + fm.flightSpend*cd.flightMultiple*cd.mileBackRate + fm.flightSpend*cd.flightMultiple*cd.pointBackRate;
			perk += fm.otherSpend*cd.cashBackRate + fm.otherSpend*cd.mileBackRate + fm.otherSpend*cd.pointBackRate;
			
			//System.out.println(perk+"\n");
			//compute profit
			profit = perk - cost;
			//System.out.println("Profit:"+profit);
			return profit;
		}
		else{
			return 0.0;
		}

	}
}