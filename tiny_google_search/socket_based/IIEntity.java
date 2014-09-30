
 import java.util.*;
 
public class IIEntity implements Comparable<IIEntity> {
	private String term;
    private List<DocCount> docCount;
    public IIEntity (String term) {
        this.term = term;
        docCount = new ArrayList<DocCount>();
    }
 
   public String getTerm() {
        return term;
    }
 
   public List<DocCount> getDocCount() {
        return docCount;
    }
   public void addDocCount(DocCount doc){
	   this.docCount.add(doc);
   }
@Override
	public int compareTo(IIEntity II2) {
		// TODO Auto-generated method stub
		int cmp = term.compareTo(II2.getTerm());
		return cmp;
	}
	public String toString(){
		String s=term;
		for(int i=0;i<docCount.size();i++){
			s+="/"+docCount.get(i).getDocID()+","+docCount.get(i).getCount();
		}
		return s;
		
	}
	public boolean equals(Object obj) {
                System.out.println("here");
        if(obj instanceof IIEntity){
        	IIEntity ii = (IIEntity)obj;
        
        	return this.term.equals(ii.term)?true:false;
    	}else if(obj instanceof String){
    		return this.term.equals((String)obj)?true:false;
    	}else{
    		return false;
    	}
    }
	public static IIEntity parseIIEntity(String line){
		
		String[] termII=line.split("/");
		IIEntity II=new IIEntity(termII[0]);
		for(int i=1;i<termII.length;i++){
			String[] idCount=termII[i].split(",");
			DocCount doc=new DocCount(Integer.parseInt(idCount[0]));
			doc.setCount(Integer.parseInt(idCount[1]));
			II.addDocCount(doc);
    		
    	}
		return II;
	}
}


