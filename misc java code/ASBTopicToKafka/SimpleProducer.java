import java.util.Properties;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;

public class SimpleProducer {
	public static void main(String[] args) throws Exception{
      
        String topicName = "TruckmateOrdersTopic";
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
      
        props.put("retries", 0);
      
        props.put("batch.size", 16384);
      
        props.put("linger.ms", 1);
      
        props.put("buffer.memory", 33554432);
      
        //props.put("key.serializer", "org.apache.kafka.common.serializa-tion.StringSerializer");
         
        //props.put("value.serializer", "org.apache.kafka.common.serializa-tion.StringSerializer");
		
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
		 
		Thread.currentThread().setContextClassLoader(null);
      
        Producer<String, String> producer = new KafkaProducer
         <String, String>(props);
            
        producer.send(new ProducerRecord<String, String>(topicName, 
               "Message Key1", "This is a message from SimpleProducer Class"));
            System.out.println("Message sent successfully");
            producer.close();
   }
}