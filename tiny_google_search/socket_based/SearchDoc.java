
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.*;
import java.util.Map.Entry;

/*
 * @author: Hanying Huang
 * @date: 12/03/13
 * search docs with given query
 */
public class SearchDoc {
	private static final String GEN_LCK_FILE = "FileRecord.lck";
    private static FileLock genLock = null;	

	public IIEntity searchKey(String MIIPath, String FileRecord, String key){
		IIEntity II = null;
		try{
			BufferedReader br = new BufferedReader(new FileReader(MIIPath));    
			BufferedReader br2 = new BufferedReader(new FileReader(FileRecord));  
	        String s;
	        
	        
	        while ((s = br.readLine()) != null) {
	        	String[] termII=s.split("/");
	        	//term found
	        	if(termII[0].equals(key)){
	        		//reconstruct the IIEntity
	        		II = new IIEntity(key);
	        		II = IIEntity.parseIIEntity(s);
	        		//System.out.println(s);
	        	}
	        		
	        }
	        
	        br.close();
	        br2.close();
		}catch (FileNotFoundException e) {
            System.out.println("File Not Found!");
        } catch (IOException e) {
            System.out.println("File Read Error");
        }
		return II;
	}
	/**
    *calculate the ranking score for all documents which contains the term(one) and store the score into a temp file
    *@param IIEntity II
    *@param int n
    *@param String tempFileName
    *@return String tempFile
    */
	public String newRaR(IIEntity II,  int n, String tempFileName){
		String tempFile = null;
		List<DocCount> docList = II.getDocCount();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < docList.size(); i++){
			int docID = docList.get(i).getDocID();
			int count = docList.get(i).getCount();
			double rar = count * 2 * ((double)1 / n);
			sb.append(docID+","+rar+"/");			
		}
		
		try{								   			
			tempFile = tempFileName+".txt";
			BufferedWriter output = new BufferedWriter(new FileWriter(tempFile));
			System.out.println("create temp file "+tempFile+" for keyword:"+II.getTerm());
			output.write(sb.toString());
			output.close();
		}catch (FileNotFoundException e) {
            System.out.println("File Not Found!");
        } catch (IOException e) {
            System.out.println("File Read Error");
        }
	
		return tempFile;
	}
	
	public String[] getDocNameByRaRFile(String pathName){
		FileOutputStream fo = null;
		OperateFile of = new OperateFile();
		Map<Integer,Double> totalRank = of.parseRaRFile(pathName);
		List<DocRank> docList = new ArrayList<DocRank>();
		Iterator<Entry<Integer, Double>> iter = totalRank.entrySet().iterator();
	    while (iter.hasNext()) {
		    Map.Entry<Integer, Double> entry = (Map.Entry<Integer, Double>) iter.next();
		    int id = entry.getKey();
		    double rank = (double)entry.getValue();
		    //System.out.println("iterator"+id+"  "+rank);
		    DocRank dr = new DocRank(id,rank);
		    docList.add(dr);
	    }
	    //sort list
	    Collections.sort(docList);
		System.out.println("Sorted Rank:"+docList.toString());
		
		String s = null;
		String[] docName = new String[docList.size()];		
		try {
            // Acquire an exclusive lock, assure manager is stand alone
			fo = new FileOutputStream(GEN_LCK_FILE);
			
			System.out.println("Request for the lock...");
            while(null == (genLock = fo.getChannel().tryLock())){
            	;//wait until get lock
            }
		
        	BufferedReader br2 = new BufferedReader(new FileReader(ServerInfo.FileRecord));  
            
    	    
			while((s = br2.readLine()) != null){
				
				String[] record = s.split(",");
				for(int i = 0;i < docList.size();i++){
			    	if(Integer.parseInt(record[0]) == (docList.get(i).getDocID())){
			    		//System.out.println(record[1]);
			    		docName[i] = record[1];
			    	}
				}
			}
			br2.close();
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != genLock) {
                try {
                    genLock.release();
                    fo.close();
                    // delete the lock file
                    //new File(GEN_LCK_FILE).delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		
		return docName;
	}
}
