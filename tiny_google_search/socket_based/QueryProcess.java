

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.List;



public class QueryProcess {
	
	private static final String GEN_LCK_FILE = "FileRecord.lck";
    private static FileLock genLock = null;
	public static String startIndex(String pathName, String threadID){ //return word count list

		String tempFile=null;
		OperateFile operate=new OperateFile();
		FileOutputStream fo = null;
		int fileID=1;
		IndexDoc count=new IndexDoc();		
		//read previous file record-check whether it has been indexed before or not	
		int rs=1;
		try {
            // Acquire an exclusive lock, assure manager is stand alone
			fo=new FileOutputStream(GEN_LCK_FILE);
			
			System.out.println("Request for the lock...");
            while(null==(genLock=fo.getChannel().tryLock())){
            	;//wait until get lock
            }

            if (null != genLock) {
            	File temp=new File(ServerInfo.FileRecord);
            	if(temp.exists()){
            		//System.out.println(temp.exists());
            		
            		if((rs=count.hasIndexed(pathName))==0)
            			;//has indexed
            		else{//not indexed
            			fileID=rs;
            			count.recordFile(fileID, pathName);
            		}
            	}
            	else{
            		count.recordFile(fileID, pathName);
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
    	
    	if(rs!=0){//need to index				
			//word count for one document/one segment   generate one tempfile
			List<String> parts=operate.splitDocByLine(pathName);			
			tempFile=count.countFile(parts,0,parts.size(),fileID,"tempII"+fileID);
			//record file
						
    	}
		//}
		
		return tempFile;
	}
		
	public static String startSearch(String searchquery, String threadID){
		SearchDoc search=new SearchDoc();
		String[] searchKey=searchquery.split("@");
		String tempFile;
		//StringBuffer sb = new StringBuffer();
		
		IIEntity II=new IIEntity(searchKey[0]);
		
			//System.out.println("For key "+searchKeys[i]);	
		File temp=new File(ServerInfo.FileRecord);
		if(!temp.exists())
			tempFile=null;
		II=search.searchKey(ServerInfo.MIIPath, 
				ServerInfo.FileRecord,searchKey[0]);
		if(II==null){
			tempFile=null;
		}
		else{
			tempFile=search.newRaR(II, Integer.parseInt(searchKey[1]), "tempRar"+searchKey[1]);
		}
		//return temp file path
		return tempFile;
	}
	
	
	
}
