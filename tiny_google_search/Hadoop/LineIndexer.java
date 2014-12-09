import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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

public class LineIndexer {

  public static class LineIndexMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {

    private final static Text word = new Text();
    private final static Text location = new Text();

    public void map(LongWritable key, Text val,
        OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {

        FileSplit fileSplit = (FileSplit)reporter.getInputSplit();
        String fileName = fileSplit.getPath().getName(); //get the file name
        location.set(fileName);

        String line = val.toString();
        StringTokenizer itr = new StringTokenizer(line.toLowerCase()," \t\b\r%1234567890\"\'()*;:~{}_+&^@/[]#$=-?,.!\n");
        while (itr.hasMoreTokens()) {
            word.set(itr.nextToken());
            output.collect(word, location); //for each occurrence of a word, key: word  value: file name
        }
    }
  }



  public static class LineIndexReducer extends MapReduceBase
      implements Reducer<Text, Text, Text, Text> {

    public void reduce(Text key, Iterator<Text> values,
        OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {

        boolean first = true;
        StringBuilder toReturn = new StringBuilder();
        int count = 0;
        String current = "";
        Map<String, Integer> docCount = new HashMap<String,Integer>();
        while (values.hasNext()) {
    	  current = values.next().toString();
          if(!(docCount.containsKey(current))) { //the first occurrence of the key(word) in an document
    		  docCount.put(current, 1);
          } else if(docCount.containsKey(current)) { //not the first occurrence, increasing count
    		  docCount.put(current, docCount.get(current).intValue()+1);
    	  }
        }
            
        Iterator<Entry<String, Integer>> iter = docCount.entrySet().iterator();
        while (iter.hasNext()) { //generate output value from the Map: docName=>count,docName=>count
		    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iter.next();
		    Object mapkey = entry.getKey();
		    Object val = entry.getValue();
		    toReturn.append(mapkey.toString() + "=>" + val.toString() + ",");
	    }
        output.collect(key, new Text(toReturn.toString())); //for each word, key: word value: document list with payload
    }
  }


  /**
   * The actual main() method for our program; this is the
   * "driver" for the MapReduce job.
   */
  public static void main(String[] args) {
      JobClient client = new JobClient();
      JobConf conf = new JobConf(LineIndexer.class);

      conf.setJobName("LineIndexer");
      
      conf.setOutputKeyClass(Text.class);
      conf.setOutputValueClass(Text.class);

      FileInputFormat.addInputPath(conf, new Path(args[0]));
      FileOutputFormat.setOutputPath(conf, new Path(args[1]));

      conf.setMapperClass(LineIndexMapper.class);
      conf.setReducerClass(LineIndexReducer.class);

      client.setConf(conf);

      try {
        JobClient.runJob(conf);
      } catch (Exception e) {
        e.printStackTrace();
      }
  }
}
