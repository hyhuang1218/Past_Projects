package cardDSSA;

import java.util.ArrayList;

import data.card;
import data.form;
import data.result;

public class cardCompute{
	private int aprPurchaseProIndex=0;
	private int aprTransferProIndex=0;
	
	public ArrayList<result> startCompute(ArrayList<card> cards, form fm){
		Integer[] decision=new Integer[cards.size()]; //cards id to decision node
		ArrayList<String> aprPurchaseOutcome=new ArrayList<String>();
		ArrayList<String> aprTransferOutcome=new ArrayList<String>();
		ArrayList<Double> aprPurchasePro=new ArrayList<Double>();
		ArrayList<Double> aprTransferPro=new ArrayList<Double>();
		ArrayList<Double> profit=new ArrayList<Double>();
		
		ArrayList<result> computingResult=new ArrayList<result>();
		
		double[] payOnTimePro = new double[2];
		double[] approvePro = new double[2*cards.size()];
		String[] chancePro=new String[4]; //probability for 4 chance nodes for each card,
										//[0]:payOnTime;[1]:approve;[2]aprPurchase;[3]aprTransfer
		
		constructModel e=new constructModel();
		
		
		for(int i=0;i<cards.size();i++){ //get all outcomes for chance nodes-apr_purchase, apr_transfer
			String[] aprPurchaseString=cards.get(i).aprPurchase.split(",");
			String[] aprTransferString=cards.get(i).aprBalanceTransfer.split(",");
			for(int j=0;j<aprPurchaseString.length;j++)
				aprPurchaseOutcome.add("c"+cards.get(i).id+"_"+(aprPurchaseString[j].split("\\.").length>1?aprPurchaseString[j].split("\\.")[1]:aprPurchaseString[j].split("\\.")[0])); //only leave float part, because GENIE doesn't accept '.'				
			for(int j=0;j<aprTransferString.length;j++)
				aprTransferOutcome.add("c"+cards.get(i).id+"_"+(aprTransferString[j].split("\\.").length>1?aprTransferString[j].split("\\.")[1]:aprTransferString[j].split("\\.")[0]));
		}
		
		int approveProIndex=0;
		
		
		for(int i=0;i<cards.size();i++){
			decision[i]=cards.get(i).id;
			String[] aprPurchaseString=cards.get(i).aprPurchase.split(",");
			String[] aprTransferString=cards.get(i).aprBalanceTransfer.split(",");
			
			compute cp=new compute(fm,cards.get(i));
			
			chancePro=cp.computeProbability();
			//System.out.println(aprTransferOutcome.size());

			//get the probability arraylist for apr_purchase node						
			aprPurchasePro=getAllPro(true, aprPurchasePro, chancePro[2].split(","), aprPurchaseString, aprPurchaseProIndex, aprPurchaseOutcome.size());
			//get the probability arraylist for apr_balance_transfer node
			aprTransferPro=getAllPro(false, aprTransferPro, chancePro[3].split(","), aprTransferString, aprTransferProIndex, aprTransferOutcome.size());			
			
			for(int j=0;j<2;j++){ //concatenate the probability for approve node
				approvePro[approveProIndex++]=Double.parseDouble((chancePro[1].split(","))[j]);
			}
			
			//compute profit
			boolean payOnTime;//a
			double aprPurchase;//b
			double aprTransfer;//c
			boolean isApprove;//d
			String prefix="c"+cards.get(i).id+"_";
			
			for(int a=0;a<2;a++){
				if(a==0)
					payOnTime=true;
				else
					payOnTime=false;
				for(int b=0, countPurchase=0; b<aprPurchaseOutcome.size();b++){
					
					if(aprPurchaseOutcome.get(b).startsWith(prefix)){
						aprPurchase = Double.parseDouble(aprPurchaseString[countPurchase++]);
					}
					else{
						aprPurchase = 0;
					}
					for (int c=0, countTransfer=0; c<aprTransferOutcome.size(); c++){
						if(aprTransferOutcome.get(c).startsWith(prefix)){
							aprTransfer = Double.parseDouble(aprTransferString[countTransfer++]);
						}
						else{
							aprTransfer = 0;
						}
						for (int d=0;d<2;d++){
							if(d==0)
								isApprove=true;
							else
								isApprove=false;
							
							
							profit.add(cp.computeProfit(isApprove, payOnTime, aprPurchase,aprTransfer));
						}
					}
				}
			}
		
			//cp.computeProfit(true, false, 0.2, 0.1);
		}
		
		//probability for pay_ontime node
		payOnTimePro[0]=Double.parseDouble((chancePro[0].split(","))[0]);
		payOnTimePro[1]=Double.parseDouble((chancePro[0].split(","))[1]);
		
		computingResult=e.createDiagram(decision,payOnTimePro,approvePro,aprPurchaseOutcome,aprPurchasePro,aprTransferOutcome, aprTransferPro, profit);
	
		return computingResult;
	}
	
	public ArrayList<Double> getAllPro(boolean isPurchase, ArrayList<Double> aprPro, String[] proString, String[] aprString, Integer index, int size){
		//System.out.println("aprPro:"+aprPro.size());		
		for(int z=0;z<index;z++){
			aprPro.add(0.0);
			
		}
		for(int z=0;z<aprString.length;z++){
			aprPro.add(Double.parseDouble(proString[z]));
			
			index++;
			//System.out.println(index);
		}
		for(int z=index;z<size;z++){
			aprPro.add(0.0);
		}
		
		if(isPurchase){
			aprPurchaseProIndex = index;
		}
		else{
			aprTransferProIndex = index;
		}
		//System.out.println(aprPurchaseProIndex+","+aprTransferProIndex);
		return aprPro;
	}
}