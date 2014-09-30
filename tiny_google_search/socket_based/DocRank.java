

public class DocRank implements Comparable<DocRank>{
	private int docID;
	private double rank;
	public DocRank(int id,double rank){
		this.docID=id;
		this.rank=rank;
	}
	public int getDocID(){
		return docID;
	}
	public void setDocID(int id){
		this.docID=id;
	}
	public double getRank(){
		return rank;
	}
	public void setRank(double rank){
		this.rank=rank;
	}
	public int compareTo(DocRank o) {
		// TODO Auto-generated method stub
		double cmp=rank-o.getRank();
		return cmp>0?-1:(cmp==0.0?0:1);
	}
	public String toString(){
		String s;
		s="\nid:"+this.docID+"  rank:"+rank;
		
		return s;
		
	}
	
}
