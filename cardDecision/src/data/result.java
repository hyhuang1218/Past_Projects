package data;

public class result{
	private int cardId;
	private double EMV;
	
	public int getCardId(){
		return this.cardId;
	}
	public void setCardId(String decisionAlternative){
		this.cardId = Integer.parseInt(decisionAlternative.split("c")[1]);
	}
	
	public double getEMV(){
		return this.EMV;
	}
	public void setEMV(double EMV){
		this.EMV=EMV;
	}
}