

public class DocCount implements Comparable<DocCount>{
	private int docID;
	private int count;
	public DocCount(int id){
		this.docID=id;
	}
	public int getDocID(){
		return docID;
	}
	public void setDocID(int id){
		this.docID=id;
	}
	public int getCount(){
		return count;
	}
	public void setCount(int count){
		this.count=count;
	}
	public int compareTo(DocCount o) {
		// TODO Auto-generated method stub
		int cmp=count-o.getCount();
		return -cmp;
	}
	
}
