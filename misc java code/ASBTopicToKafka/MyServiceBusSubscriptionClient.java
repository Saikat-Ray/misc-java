
import com.microsoft.azure.servicebus.*;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import com.google.gson.Gson;
import static java.nio.charset.StandardCharsets.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;


public class MyServiceBusSubscriptionClient {
    static final Gson GSON = new Gson();
	static final String topicName = "Test-Saikat-AnotherTopic";
	static Properties props;
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");  
		
    public static void main(String[] args) throws Exception, ServiceBusException {
		String connectionString = "Endpoint=sb://staging-sb-tcx-0-3-01.servicebus.windows.net/;SharedAccessKeyName=ManageSharedAccessKey;SharedAccessKey=rTxogBWPB0LYH7uYckrXrcdZ4NoruiTUzYAtIp3ttGA=";
        
        SubscriptionClient subscription1Client = new SubscriptionClient(new ConnectionStringBuilder(connectionString, "b5e3676704374d581759bd20/subscriptions/60a7ddde84e1734ad7c8480f"), ReceiveMode.PEEKLOCK);
        
		//initialize the kafka producer process
		try{
			initKafkaTopic();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		//LocalDateTime begin = LocalDateTime.now();  
		//System.out.println(dtf.format(begin)); 
		
        ExecutorService executorService = Executors.newCachedThreadPool();
		System.out.println("Calling registerMessageHandlerOnClient...");
        registerMessageHandlerOnClient(subscription1Client, executorService);
		
		//LocalDateTime end = LocalDateTime.now();  
		//System.out.println(dtf.format(end)); 
		
    }
	
	static void initKafkaTopic() throws Exception{
		props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
	}
	
	static void callProdKafkaTopic(String msgId, String messageText){
		Thread.currentThread().setContextClassLoader(null);
		Producer<String, String> producer = new KafkaProducer<String, String>(props);
            
        producer.send(new ProducerRecord<String, String>(topicName, msgId, messageText));
        System.out.println("Message sent successfully");
        producer.close();
	}
    
    static void registerMessageHandlerOnClient(SubscriptionClient receiveClient, ExecutorService executorService) throws Exception {
        System.out.println("Inside registerMessageHandlerOnClient...");
		receiveClient.registerMessageHandler(
                new IMessageHandler() {
                    public CompletableFuture<Void> onMessageAsync(IMessage message) {
                            byte[] body = message.getBody();
							String msgId = message.getMessageId();
							String messageText = new String(body, UTF_8);
							LocalDateTime end = LocalDateTime.now();  
							System.out.println(dtf.format(end)); 
                            callProdKafkaTopic(msgId, messageText);
                        //}
                        return receiveClient.completeAsync(message.getLockToken());
                    }

                    // callback invoked when the message handler has an exception to report
                    public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
                        System.out.printf(exceptionPhase + "-" + throwable.getMessage());
                    }
                },
                // 1 concurrent call, messages are auto-completed, auto-renew duration
                new MessageHandlerOptions(1, false, Duration.ofMinutes(1)),executorService);
    }
}