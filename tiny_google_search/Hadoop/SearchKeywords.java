

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.io.DoubleWritable;

public class SearchKeywords {

  public static class SearchKeyMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {

	  static String[] keyword;
	  private final static Text docName = new Text();
	    private final static Text rank = new Text();
	    
	  public void configure(JobConf jobConf) {
		    keyword = jobConf.get("keywords").split(",");
		    //System.out.println("keyword>> " + keyword);
		    super.configure(jobConf);
		  }
	  
	  public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
	      throws IOException {
		    for(int i=0;i<keyword.length;i++){
			    if (value.toString().split("\t")[0].equals(keyword[i])) {  //find the term
			    	String[] docCount=value.toString().split("\t")[1].split(",");
			    	for(int j=0;j<docCount.length;j++){
                        //calculate the weighted score for each documents
			    		double score=Double.parseDouble(docCount[j].split("=>")[1])*2*((double)1/(i+1));
			    		docName.set(docCount[j].split("=>")[0]);
                        
                         //apend the number of keywords for further processing
				    	rank.set(String.valueOf(score)+","+keyword.length);
                        
                         //key: document name  value: weighted score for one keyword and size of keywords
				        output.collect(docName, rank);
			    	}

			      break;
			    }
		    }
	   // }
	  }
  }



  public static class SearchKeyReducer extends MapReduceBase
      implements Reducer<Text, Text, Text, DoubleWritable> {
    public void reduce(Text key, Iterator<Text> values,
            OutputCollector<Text, DoubleWritable> output, Reporter reporter)
        throws IOException {  
    	double score=0.0;
    	int iterSize=0;  //the number of keywords which the document(key) contains
    	int keywordCount=0;
    	while(values.hasNext()){  //aggregate the scores for each document(key)
    		iterSize++;
    		String[] eachValue=values.next().toString().split(",");
    		keywordCount=Integer.parseInt(eachValue[1]);    		
    		score=Double.parseDouble(eachValue[0])+score;
    	}
    	if(iterSize==keywordCount){  //contains all keywords
    		 //key: document name  value:total weighted score
    		output.collect(key,new DoubleWritable(score));
    	}
      
    }
  }


  /**
   * The actual main() method for our program; this is the
   * "driver" for the MapReduce job.
   */
  public static void main(String[] args) {
    JobClient client = new JobClient();
    JobConf conf = new JobConf(SearchKeywords.class);

    conf.setJobName("SearchKeywords");

    conf.setOutputKeyClass(Text.class);
    conf.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(conf, new Path(args[0]));
    FileOutputFormat.setOutputPath(conf, new Path(args[1]));
    conf.set("keywords", args[2]);
    conf.setMapperClass(SearchKeyMapper.class);
    conf.setReducerClass(SearchKeyReducer.class);


    client.setConf(conf);

    try {
      JobClient.runJob(conf);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
