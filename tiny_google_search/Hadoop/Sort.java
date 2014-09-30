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
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableComparable;

public class Sort {

	  public static class SortMapper extends MapReduceBase
	      implements Mapper<LongWritable, Text, DoubleWritable, Text> {


	    public void map(LongWritable key, Text val,
	        OutputCollector<DoubleWritable, Text> output, Reporter reporter)
	        throws IOException {

	      FileSplit fileSplit = (FileSplit)reporter.getInputSplit();


	      String[] line = val.toString().split("\t");
           //in order to descendly sort the documents based on the rank score, the score multiplies -1
	      double score=Double.parseDouble(line[1])*(-1);
	      Text fileName=new Text();
	      fileName.set(line[0]);
           //key:negative score  values:document name
	      output.collect(new DoubleWritable(score), fileName);
                
	    }
	  }



	  public static class SortReducer extends MapReduceBase
	      implements Reducer<DoubleWritable, Text, Text,Text> {

	    public void reduce(DoubleWritable key, Iterator<Text> values,
	        OutputCollector<Text,Text> output, Reporter reporter)
	        throws IOException {
	    	Text empty=new Text();
	    	empty.set("");
	    	while(values.hasNext())
	    		output.collect(values.next(),empty);
                //key:document name value:""
	    }
	  }

	 
	  /**
	   * The actual main() method for our program; this is the
	   * "driver" for the MapReduce job.
	   */
	  public static void main(String[] args) {
	    JobClient client = new JobClient();
	    JobConf conf = new JobConf(Sort.class);

	    conf.setJobName("Sort");

	    conf.setOutputKeyClass(DoubleWritable.class);
	    conf.setOutputValueClass(Text.class);

	    FileInputFormat.addInputPath(conf, new Path(args[0]));
	    FileOutputFormat.setOutputPath(conf, new Path(args[1]));
	    
	    conf.setMapperClass(SortMapper.class);
	    conf.setReducerClass(SortReducer.class);

	    client.setConf(conf);

	    try {
	      JobClient.runJob(conf);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }
}
