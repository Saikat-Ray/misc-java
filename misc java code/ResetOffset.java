import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.*;

import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ResetOffset {
	private final static String TOPIC = "DR_Test";
    private final static String BOOTSTRAP_SERVERS = "pkc-lzvrd.us-west4.gcp.confluent.cloud:9092";
	private final static String processURL = "https://apigw-pod1.dm-us.informaticacloud.com/t/dev-iics.dayross.com/SampleProcess";

	public static void main(String args[]) throws Exception
    {
		final String config_fileName = "java.config";
		final Properties props = loadConfig(config_fileName);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonDeserializer");
		//props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-Saikat");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "100");
		//props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    	getPartitions(props);
    }
	private static Properties loadConfig(String configFile) throws IOException {
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
	private static void getPartitions(Properties configs){
		String topicName = "ASB_DR";
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);
		consumer.subscribe(Arrays.asList("ASB_DR"));
		int partition = 4;
		TopicPartition tp = new TopicPartition(topicName, partition);
		long position = 0;
		ConsumerRecords<String, String> consumerRecords  = consumer.poll(1000);
		long currentPosition = consumer.position(tp);
		System.out.println("Current position: "+currentPosition);
		consumer.seek(tp, position);
		currentPosition =  consumer.position(tp);
		System.out.println("New current position: "+currentPosition);
		try{
			//while (true){
				consumerRecords  = consumer.poll(10);
				for (ConsumerRecord<String, String> record : consumerRecords) {
					String key = record.key();
					System.out.println("Key..."+key);
				}
			//}
		}
		finally{
			consumer.close();
			System.out.println("--DONE--");
		}

	}

}