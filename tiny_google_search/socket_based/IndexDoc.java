



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
 
public class IndexDoc {
	
	//word count and generate a temp file 
	public  String countFile(List<String> docPart, int index, int blockCount,int fileID, String tempFileName){
		String tempFile=null;
        try {        	       	       	                                 
            //get count for all words
            Map<String,Integer> map = new HashMap<String, Integer>();
            for(int i=0;i<blockCount;i++){
            	StringTokenizer st = new StringTokenizer(docPart.get(index+i)," \t\b\r1234567890\"\'()*;:~{}_+&^@/[]#$=-?,.!\n");
                while (st.hasMoreTokens()) {
                    String word = st.nextToken().toLowerCase();
                    if(word.charAt(0)<'a'||word.charAt(0)>'z'){
                    	continue;
                    }
                    int count;
                    if (map.get(word) == null) {
                        count = 1;
                    } else {
                        count = map.get(word).intValue() + 1;
                    }
                    map.put(word,count);
                }  
            }                                           
            //Create II
            Set<IIEntity> set = new TreeSet<IIEntity>();
            for (String key : map.keySet()) {
            	IIEntity II=new IIEntity(key);
            	DocCount doc=new DocCount(fileID);           
            	doc.setCount(map.get(key));           	            	
            	//System.out.println(doc.getCount()+":");
            	II.addDocCount(doc);
            	//System.out.println(II.getDocCount().get(0).getCount());
                set.add(II);
            }
            //result
            

            tempFile=tempFileName+".txt";
            System.out.println("create temp index file "+tempFile+" for doc:"+fileID);
            //write into MII and FileNameRecord
            BufferedWriter output = new BufferedWriter
            		(new FileWriter(ServerInfo.TempIIPath+tempFile));
            
            
            for (Iterator<IIEntity> it = set.iterator(); it.hasNext(); ) {
            	IIEntity w = it.next();
                output.write(w.toString());
                output.newLine();                
            }
            output.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found!");
        } catch (IOException e) {
            System.out.println("File Read Error");
        }
        
        return tempFile;
    }
	
	public int hasIndexed(String filepathname){
		int fileID=0;
		
		try {
			BufferedReader br;
			
			br = new BufferedReader(new FileReader(ServerInfo.FileRecord));
			
			String record;
	        StringBuffer sb = new StringBuffer();
	        while((record=br.readLine())!=null){
	        	//if(record=="")
	        		//continue;
	        	sb.append(record+"\n");
	        	//System.out.println("record:"+record);
	        	if(record!="\n"&&record.split(",")[1].equals(filepathname)){
	        		System.out.println(filepathname+" has indexed before - skipping");
	        		br.close();
	        		return 0;
	        	} 
	        	else if(record!="\n"){
	        		fileID=Integer.parseInt(record.split(",")[0])+1;
	        	}
	        }
		        br.close();   
	        
		} catch (FileNotFoundException e) {
            System.out.println("FileName File Not Found!");
        } catch (IOException e) {
            System.out.println("File Read Error");
        }
		
		return fileID;
	}
	
	public void recordFile(int fileId,String filePathName){
		BufferedWriter output;
		
		try {
			
				output = new BufferedWriter
						(new FileWriter(ServerInfo.FileRecord,true));
				output.write(fileId+","+filePathName);
		        output.newLine();  
		        output.close();
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("File Read Error");
		}
	}
}
