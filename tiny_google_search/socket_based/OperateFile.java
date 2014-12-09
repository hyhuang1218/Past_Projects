
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/*
 * @author: Hanying Huang
 * @date: 12/07/13
 * Implement the operations on file: merge index, merge RaR, split docs
 */
public class OperateFile {
	private static final String GEN_LCK_FILE = "MainIndex.lck";
    private static FileLock genLock = null;
    private static boolean needSync = true;

	public String mergeIndex(String base, List<String> tempList){
		FileOutputStream fo = null;
		try {
            // Acquire an exclusive lock, assure manager is stand alone			
			if(base.equals(ServerInfo.MIIPath)){
				fo = new FileOutputStream(GEN_LCK_FILE);
				
				System.out.println("Request for the lock...");
	            while(null == (genLock = fo.getChannel().tryLock())){
	            	;//wait until get lock
	            }
            }
			else{
				needSync = false;
			}

            if (null != genLock || needSync == false) {
            	for(int i = 0;i < tempList.size(); i++){
        			File fd = new File(base);
        			if(fd.exists()){
        			
        					
    					BufferedReader br = new BufferedReader(new FileReader(base));  		
    					
    					BufferedReader br2 = new BufferedReader(new FileReader(tempList.get(i)));
    					StringBuffer sb = new StringBuffer();
    					String MII = br.readLine(), TempII=br2.readLine();
    					while(MII != null || TempII != null){
    						//System.out.println("test");
    						if(MII == null) {
    							//System.out.println("test");
    							sb.append(TempII + "\n");
    							TempII = br2.readLine();
    						}
    						else if(TempII == null){
    							sb.append(MII + "\n");
    							MII = br.readLine();
    						}
    						else{
    							String miiterm = MII.split("/")[0];
    							String tempiiterm = TempII.split("/")[0];
    							
    							if(tempiiterm.compareTo(miiterm) < 0){
    								sb.append(TempII+"\n");
    								TempII = br2.readLine();
    							}
    							else if(tempiiterm.compareTo(miiterm) > 0) {
    								sb.append(MII+"\n");
    								MII = br.readLine();
    							}
    							else {
    								String[] docCount = TempII.split("/");
    								sb.append(MII);
    								for(int j = 1;j < docCount.length; j++){
    									sb.append("/"+TempII.split("/")[j]);
    								}
    								sb.append("\n");
    								MII = br.readLine();
    								TempII = br2.readLine();
    							}
    						}
    						
    					}
    					br.close();
    					br2.close();
    					//System.out.println(sb.toString());
    					BufferedWriter output = new BufferedWriter
    		            		(new FileWriter(base));
    					output.write(sb.toString());
    					output.close();
    					//delete temporary file
    					File temp = new File(tempList.get(i));
    					if(temp.exists())
    						temp.delete();
    					
    					System.out.println("Merge done, delete temp file!");
        				
        			}
        			else{ //base index file not exist
        												   			
    					BufferedReader br2 = new BufferedReader(new FileReader(tempList.get(i)));
    					StringBuffer sb = new StringBuffer();
    					String line;
    					while((line = br2.readLine()) != null){
    						sb.append(line+"\n");
    					}
    					br2.close();
    					//System.out.println(sb.toString());
    					BufferedWriter output = new BufferedWriter
    		            		(new FileWriter(base));
    					output.write(sb.toString());
    					output.close();
    					//delete temporary file
    					File temp = new File(tempList.get(i));
    					temp.delete();
    					System.out.println("Merge done, delete temp file!");
        				
        			}
        		}
            } 
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
		
		return base;
	}

	/**
    *Merge the posting list for each term and get the total ranking sore which will be saved in the base file
    *@param String base
    *@param List<String> tempList
    *@return String base
    */
	public String mergeRar(String base, List<String> tempList){
		Map<Integer, Double> totalRank = new HashMap<Integer,Double>();
		Map<Integer, Double> eachRank = new HashMap<Integer,Double>();
		String baseFile = base;
		OperateFile of = new OperateFile();
		for(int i = 0;i < tempList.size(); i++){
			if(i == 0){//initiate totalRank
				if(tempList.get(i) != null){
					totalRank = of.parseRaRFile(tempList.get(i));
					
				}
				else{
					baseFile = null;
					break;
				}
			}
			else{ 
				if(tempList.get(i) != null){
					eachRank = of.parseRaRFile(tempList.get(i));
					Iterator<Entry<Integer, Double>> iter = totalRank.entrySet().iterator();				
					List<Integer> needRemove = new ArrayList<Integer>();
				    while (iter.hasNext()) {
					    Map.Entry<Integer, Double> entry = (Map.Entry<Integer, Double>) iter.next();
					    int id = entry.getKey();
					    
					    if(eachRank.get(id) == null){
					    	//System.out.println("what");
					    	needRemove.add(id);				    	
					    	
					    }else{
					    	double newrank = eachRank.get(id).doubleValue();
					    	double prerank = totalRank.get(id).doubleValue();
					    	double totalrank = prerank + newrank;
					    	//update new rank
					    	totalRank.put(id,totalrank );
					    	//System.out.println(newrank);
					    }
					    //System.out.println("iterator"+id+"  "+rank);
					    
				    }				   
				   
				    //delete wrong docs in total rank
				    if(!needRemove.isEmpty()){
				    	//System.out.println("here");
				    	for(int n = 0;n < needRemove.size(); n++){
				    		totalRank.remove(needRemove.get(n));
				    	}
				    	if(totalRank.isEmpty()){//no docs
				    		
				    		baseFile = null;
				    		break;	
				    	}
				    }
				}
				else{//no docs
					
					baseFile = null;
					break;
				}
			}
		}
		
		if(baseFile != null){ //write into base file
			//System.out.println("Write into new File:"+baseFile);
			try{
				BufferedWriter output = new BufferedWriter
		        		(new FileWriter(baseFile));
				Iterator<Entry<Integer, Double>> iter = totalRank.entrySet().iterator();				
			    while (iter.hasNext()) {
				    Map.Entry<Integer, Double> entry = (Map.Entry<Integer, Double>) iter.next();
				    int id = entry.getKey();
				    output.write(id+","+totalRank.get(id).doubleValue()+"/");					    		    
			    }
			    output.close();
			}catch (FileNotFoundException e) {
		        System.out.println("11File Not Found!");
		    } catch (IOException e) {
		        System.out.println("File Read Error");
		    }
		
		}
		
		for(int i = 0;i < tempList.size(); i++){//delete all temp file
			File temp = new File(tempList.get(i));
			temp.delete();
		}				
		return baseFile;
	}
	/**
    *Parse the file for ranking scores and returns a map which key is the doc id and the value is corresponding score
    *@param String path
    *@return Map<Integer, Double> docRank
    */
	public Map<Integer, Double> parseRaRFile(String path){
		Map<Integer,Double> docRank = new HashMap<Integer,Double>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(path)); 
			String line = "";
			
			
			while((line = br.readLine()) != null && line != "\n"){
				String[] eachDoc = line.split("/");
				for(int i = 0; i < eachDoc.length; i++){
					String[] s = eachDoc[i].split(",");
					docRank.put(Integer.parseInt(s[0]), Double.parseDouble(s[1]));
				}
			}
		
			br.close();
		}catch (FileNotFoundException e) {
	        System.out.println("11File Not Found!");
	    } catch (IOException e) {
	        System.out.println("File Read Error");
	    }
		return docRank;
	}
	/**
    *Split files into small blocks
    *@param String path
    *@return ArrayList<String> docParts
    */
	public ArrayList<String> splitDocByLine(String path){
		ArrayList<String> docParts = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			StringBuffer sb = new StringBuffer();
			String line = "";
			int lineCount = 0;
			
			while((line = br.readLine()) != null){
				sb.append(line + "\n");				
				lineCount++;
				if(lineCount == ServerInfo.segmentLine){
					lineCount = 0;
					docParts.add(sb.toString());					
					sb = new StringBuffer();
				}
			}
			//System.out.print(docParts.get(0));
			//System.out.print(docParts.get(1));
			br.close();
		} catch (FileNotFoundException e) {
            System.out.println("File Not Found!");
        } catch (IOException e) {
            System.out.println("File Read Error");
        }   
		return docParts;
	}
	
	
}
