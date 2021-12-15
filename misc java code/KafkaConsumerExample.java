import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.*;

//import java.util.Collections;
//import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


public class KafkaConsumerExample {
    private final static String TOPIC = "DR_Test";
    private final static String BOOTSTRAP_SERVERS = "pkc-lzvrd.us-west4.gcp.confluent.cloud:9092";
	private final static String processURL = "https://apigw-pod1.dm-us.informaticacloud.com/t/dev-iics.dayross.com/SampleProcess";
    public static void main(String args[]) throws Exception
    {
		final String config_fileName = "java.config";
		final Properties props = loadConfig(config_fileName);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonDeserializer");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-Saikat");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    	runConsumer(props);
		//runKafkaConsumer(props);
		//getPartitionList(props);
    }
	static void getPartitionList(Properties configs)throws InterruptedException{
		String topicName = "DR_Test";
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);
		Date date = new Date();
		long timestamp = date.getTime();
		System.out.println(timestamp);
		try{
			List<PartitionInfo> partitionInfos = consumer.partitionsFor(topicName);
			List<TopicPartition> topicPartitionList = partitionInfos.stream().map(info -> new TopicPartition(topicName, info.partition())).collect(Collectors.toList());
			consumer.assign(topicPartitionList);
			//System.out.println("topicPartitionList size.."+topicPartitionList.size());
			//System.out.println("partitionInfos size.."+partitionInfos.size());
			partitionInfos.forEach(System.out::println);
			topicPartitionList.forEach(System.out::println);
			for (int i=0; i< topicPartitionList.size(); i++){
				
			}
			
			Map<TopicPartition, Long> partitionTimestampMap = topicPartitionList.stream().collect(Collectors.toMap(tp -> tp, tp -> timestamp));
			partitionTimestampMap.forEach((key, value) -> System.out.println(key + ":" + value));
			
			Map<TopicPartition, OffsetAndTimestamp> partitionOffsetMap = consumer.offsetsForTimes(partitionTimestampMap);
			//partitionOffsetMap.forEach((tp, offsetAndTimestamp) -> consumer.seek(tp, offsetAndTimestamp.offset()));
			//partitionOffsetMap.forEach(System.out::println);
			
			partitionOffsetMap.forEach((key, value) -> System.out.println(key + ":" + value));
		}
		finally{
			consumer.close();
			System.out.println("--Complete--");
		}
	}
	static void runKafkaConsumer(Properties configs)throws InterruptedException{
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);
		try{
			Map<TopicPartition, Long> timestamps = new HashMap<>();
			timestamps.put(new TopicPartition("DR_Test", 0), System.currentTimeMillis()-1*1000);
			Map<TopicPartition, OffsetAndTimestamp> offsets = consumer.offsetsForTimes(timestamps);
			System.err.println(offsets);
		}
		finally{
			consumer.close();
			System.out.println("--Done--");
		}
	}
    static void runConsumer(Properties props)throws InterruptedException
    {
		final Consumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("DR_Test"));
		try{
			while (true) {
			    ConsumerRecords<String, String> consumerRecords  = consumer.poll(1000);
				System.out.println("Here");
				for (ConsumerRecord<String, String> record : consumerRecords) {
					System.out.println("Here1");
					String key = record.key();
					//String value = (String)record.value();
					System.out.println("Key..."+key);
					/*try{
						System.out.println("Status: "+callCAIProcess(processURL));
					}
					catch(Exception e){
						System.out.println("Exceptoin caught..");
					}*/
				}
					//consumerRecords.forEach(record -> {
					//System.out.printf("Consumer Record:(%d, %s, %d, %d)\n",record.key(), record.value(),record.partition(), record.offset());

        	 	//});
			 	//consumer.commitAsync();
			}
		}
		finally{
			consumer.close();
			System.out.println("--DONE--");
		}

	}

	public static Properties loadConfig(String configFile) throws IOException {
  //public static void loadConfig(String configFile) throws IOException {
		if (!Files.exists(Paths.get(configFile))) {
			throw new IOException(configFile + " not found.");
		}
		else{
			System.out.println("File Exists...");
		}
		final Properties cfg = new Properties();
		try (InputStream inputStream = new FileInputStream(configFile)) {
			cfg.load(inputStream);
		}
		return cfg;
	}
	public static String callCAIProcess(String processURL) throws Exception{
		System.out.println("Calling CAI process");
		String ret = "Success";
		URL prcURL = new URL(processURL);
		HttpURLConnection l_conn = (HttpURLConnection) prcURL.openConnection();
		l_conn.setDoInput(true);
		l_conn.setDoOutput(true);
		l_conn.setRequestMethod("POST");
		l_conn.setRequestProperty("Content-Type", "application/json;");
		String payload = "{\"Payload\":\"Test\"}";
		l_conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(l_conn.getOutputStream());
		wr.writeBytes(payload);
		wr.flush();
		wr.close();
		l_conn.connect();
		int code = l_conn.getResponseCode();
		System.out.println("Code: "+code);

		return ret;
	}
}