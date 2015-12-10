package cn.chinahadoop.kafka.producer;



import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class HadoopConsumer {
	private ConsumerConnector connector;
	private String topic;
	private ExecutorService executor;
	private FileSystem fs;
    private Configuration conf ;
	public HadoopConsumer(String zookeeper, String groupid, String topic) {
		super();
		connector = kafka.consumer.Consumer
				.createJavaConsumerConnector(createConsumerConfig(zookeeper,
						groupid));
		this.topic = topic;

		try {
			conf = new Configuration();
			conf.set("fs.defaultFS", "hdfs://SY-0217:8020");
			conf.setBoolean("dfs.support.append", true);
			this.fs = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String zookeeper = "sy-0217";
		String topic = "hadoop_log_topic3";
		String groupid = "testGroup";
		int thread = 1;
		HadoopConsumer hadoopConsumer = new HadoopConsumer(zookeeper, groupid,
				topic);
		hadoopConsumer.run(thread);
	}

	public void run(int numThreads) {
		try {
			if (!fs.exists(new Path("/kafka"))) {
				fs.mkdirs(new Path("/kafka"));
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(numThreads));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = connector
				.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
		executor = Executors.newFixedThreadPool(numThreads);
		 int threadNumber=0;
		for (final KafkaStream<byte[], byte[]> kafkaStream : streams) {
			executor.submit(new subHadoopConsumer(kafkaStream,threadNumber));
			threadNumber++;
			}
	}
public class subHadoopConsumer implements Runnable{
    private KafkaStream m_stream;
    private int m_threadNumber;
    public subHadoopConsumer(KafkaStream a_stream,int a_threadNumber) {
		m_threadNumber = a_threadNumber;
		m_stream = a_stream;
	}
				public void run() {
					String[] keyStrings = null;
					String str = null;
					String[] datetime = null;
					String year = null;
					String month = null;
					String day = null;
					FSDataOutputStream dos =null;
					ConsumerIterator<byte[], byte[]> iterator = m_stream
							.iterator();
					while (iterator.hasNext()) {
//						MessageAndMetadata<byte[], byte[]> messageAndMetadata = iterator
//								.next();
//						byte[] key = (byte[]) messageAndMetadata.key();
//						
//						keyStrings = new String(key).split("/");
//						str=keyStrings[keyStrings.length-1];
//						datetime = str.split("\\.");
//						year = datetime[2].substring(0, 4);
//						month =datetime[2].substring(4,6);
//						day = datetime[2].substring(6,8);
						//HERE
						MessageAndMetadata<byte[], byte[]> mam = iterator.next();
			    		byte[] message = mam.message();
			    		byte[] decodedKeyBytes = mam.key();
			    		String dateString = "20151130";
			    		if(decodedKeyBytes != null){
			    			String sourceFileName = new String(decodedKeyBytes);
			    			int lastDotIndex = sourceFileName.lastIndexOf(".");
			    			dateString = sourceFileName.substring(lastDotIndex + 1);
			    		}
			    			year = dateString.substring(0, 4);
			    	    	 month = dateString.substring(4, 6);
			    	    	 day = dateString.substring(6);

						try {
							if (!fs.exists(new Path("/kafka/outputdir/"
									+year+"/"+month+"/"+day+"/"+"kafkadata"))) {
								dos = fs.create(
										new Path("/kafka/outputdir/"
												+year+"/"+month+"/"+day+"/"+"kafkadata"), true);
								byte[] by = mam.message();
							    dos.write(by);
								dos.write('\n');
								fs.close();
								fs = FileSystem.get(conf);
							} else {
								dos = fs
										.append(new Path("/kafka/outputdir/"
												+year+"/"+month+"/"+day+"/"+"kafkadata"));
								byte[] by = mam.message();
								 dos.write(by);
								 dos.write('\n');
							}
					
//							System.out.println(new String(key));
//							System.out.println("Thread:  "+new String(by)+"-id:"+Thread.currentThread().getId());
					    }catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (dos != null)
								try {
									dos.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
					}
					}
				}
				
}

	private ConsumerConfig createConsumerConfig(String zookeeper, String groupid) {
		Properties props = new Properties();
		props.put("zookeeper.connect", zookeeper);
		props.put("group.id", groupid);
		props.put("zookeeper.session.timeout.ms", "60000");
		props.put("zookeeper.sync.time.ms", "3000");
		props.put("auto.commit.interval.ms", "1000");
		props.put("auto.offset.reset", "smallest");
		props.put("rebalance.max.retries", "5");
		props.put("rebalance.backoff.ms", "15000");
		return new ConsumerConfig(props);
	}
}
