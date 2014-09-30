package cardDSSA;

import java.util.ArrayList;
import data.result;
import smile.Network;
import smile.SMILEException;

public class constructModel{
	ArrayList<result> computingResult=new ArrayList<result>(); 
	
	public ArrayList<result> createDiagram(Integer[] decision, double[] payOnTimePro, double[] approvePro, ArrayList<String> aprPurchaseOutcome, ArrayList<Double> aprPurchasePro, ArrayList<String> aprTransferOutcome, ArrayList<Double> aprTransferPro,ArrayList<Double> profit) {
		
		try {
		   
		   Network net = new Network();
		   //net.readFile("D:/tutorial_b.xdsl");
		   net.addNode(Network.NodeType.Cpt, "Apr_purchase");		   
		   net.addNode(Network.NodeType.Cpt, "Pay_ontime");
		   net.addNode(Network.NodeType.Cpt, "Apr_transfer");	   
		   net.addNode(Network.NodeType.Cpt, "Approve");
		   net.addNode(Network.NodeType.List, "Card");
		   net.addNode(Network.NodeType.Table, "profit");
		   //construct connection between the nodes;
		   net.addArc("Card", "Apr_purchase");
		   net.addArc("Card", "Apr_transfer");
		   net.addArc("Card", "Approve");
		   net.addArc("Card", "profit");
		   net.addArc("Pay_ontime", "profit");
		   net.addArc("Apr_purchase", "profit");
		   net.addArc("Apr_transfer", "profit");
		   net.addArc("Approve", "profit");	     
		   net.setNodePosition("Apr_purchase", 120, 300, 100, 30);
		   net.setNodePosition("Card", 250, 30, 80, 30);
		   net.setNodePosition("Approve", 400, 100, 80, 30);
		   net.setNodePosition("Pay_ontime", 90, 100, 130, 30);
		   net.setNodePosition("Apr_transfer", 380, 300, 80, 30);
		   net.setNodePosition("profit", 250, 150, 80, 30);	   
		   net.writeFile("D:/2.xdsl");
		   constructOutcome(decision, payOnTimePro,approvePro,aprPurchaseOutcome,aprPurchasePro,aprTransferOutcome,aprTransferPro,profit);
		   
		   
		 }
		 catch (SMILEException e) {
		   System.out.println(e.getMessage());
		 }
		 return computingResult;
		}
	public void constructOutcome(Integer[] decision, double[] payOnTimePro,double[] approvePro,
											    ArrayList<String> aprPurchaseOutcome, ArrayList<Double> aprPurchasePro, ArrayList<String> aprTransferOutcome,
												ArrayList<Double> aprTransferPro,ArrayList<Double> profit){
		
		try{
			int decisionSize=decision.length;
			int aprPurchaseSize=aprPurchaseOutcome.size(); //outcomes
			int aprTransferSize=aprTransferOutcome.size();
			int profitSize=decisionSize*aprPurchaseSize*aprTransferSize*2*2;
			
			System.out.println("decisionsize:"+decisionSize);
			System.out.println("aprPurchaseSize:"+aprPurchaseSize);
			System.out.println("aprTransferSize:"+aprTransferSize);
			System.out.println("profitsize:"+profitSize);
			
			int [] outcomeCount={aprPurchaseSize,aprTransferSize,2,2};
			String []chanceNode={"Apr_purchase","Apr_transfer","Approve","Pay_ontime"};
			String [][]nodeOutcome={(String[])aprPurchaseOutcome.toArray(new String[aprPurchaseSize]),(String[])aprTransferOutcome.toArray(new String[aprTransferSize]),{"Approve","Reject"},{"On_time","Delay"}};	
			
			double[] proForAprPurchase=new double[aprPurchasePro.size()];
			double[] proForAprTransfer=new double[aprTransferPro.size()];
			double[] profitValue=new double[profit.size()];
			for (int i = 0; i < aprPurchasePro.size(); ++i) {  
				proForAprPurchase[i] = aprPurchasePro.get(i);  //copy arraylist to double[]
			}
			for (int i = 0; i < aprTransferPro.size(); ++i) {  
				proForAprTransfer[i] = aprTransferPro.get(i);  //copy arraylist to double[]
			
			}
			for (int i = 0; i < profit.size(); ++i) {  
				profitValue[i] = profit.get(i);  //copy arraylist to double[]
			
			}
			
			Network net = new Network();
			net.readFile("D:/2.xdsl");
			net.getNode("Apr_purchase");
			net.getNode("Apr_transfer");
			net.getNode("Approve");
			net.getNode("Pay_ontime");
			net.getNode("Card");
			net.getNode("profit");
			//String []decisionDef= {"sun","honey"};
			//int decisionCount=2;
			for(int n=0;n<decisionSize;n++){
				net.addOutcome("Card","c"+decision[n].toString());		
			}
			for(int n=0;n<2;n++){
				net.deleteOutcome("Card","Choice"+n);		
			}
			
			for(int i=0;i<4;i++){
				for(int j=0;j<outcomeCount[i];j++){
					net.addOutcome(chanceNode[i],nodeOutcome[i][j] );					
				}
			}
			
			for(int m=0;m<4;m++){
				net.deleteOutcome(chanceNode[m],0);
				net.deleteOutcome(chanceNode[m],0);
			}
		

			double [][]nodeProbability={proForAprPurchase,proForAprTransfer,approvePro,payOnTimePro};
			
			for(int i=0;i<4;i++){				
				net.setNodeDefinition(chanceNode[i], nodeProbability[i]);					
			}
			//System.out.println("Profit Size:"+profitSize);
			
			double[] profitDef= new double[profitSize];
			for(int i=0;i<profitSize;i++){
				 profitDef[i]=profitValue[i];
			}
			
			net.setNodeDefinition("profit", profitDef);
			
			net.writeFile("D:/2.xdsl");
			InferenceWithInfluenceDiagram();
			
		}
		catch (SMILEException e) {
			   System.out.println(e.getMessage());
			 }
		
	}
	public void InferenceWithInfluenceDiagram() { //get computingResult of decision node
		
		 try {
		   // Loading and updating the influence diagram: 
		   Network net = new Network();
		   net.readFile("D:/2.xdsl");
		   net.updateBeliefs();
		   
		   // Getting the utility node's handle:		   		   
		   int nodeDecision = net.getNode("Card");
		   String decisionName = net.getNodeName(nodeDecision);
		   
		   // Displaying the possible expected values:
		   System.out.println("These are the expected utilities:");
		   for (int i = 0; i < net.getOutcomeCount(nodeDecision); i++) {
			   result rs=new result();
		     String parentOutcomeId = net.getOutcomeId(nodeDecision, i);
		     
		     double expectedUtility = net.getNodeValue("Card")[i];
		     System.out.print("  - \"" + decisionName + "\" = " + parentOutcomeId + ": ");
		     System.out.println("Expected Utility = " + expectedUtility);
		     rs.setCardId(parentOutcomeId);
		     rs.setEMV(expectedUtility);
		     //add to computing result
		     computingResult.add(rs);
		   }
		   
		   
		 }
		 catch (SMILEException e) {
		   System.out.println(e.getMessage());
		 }
		 
		}
}

