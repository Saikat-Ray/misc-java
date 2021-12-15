
import com.microsoft.azure.servicebus.*;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
//import com.google.gson.Gson;
import static java.nio.charset.StandardCharsets.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    
import java.rmi.UnknownHostException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;


public class ConsumeASBProduceKafka {
    static final String KAFKA_TOPIC = "Test-Saikat-AnotherTopic"; //"TruckmateOrdersTopic";// 
	static final String connectionString = "Endpoint=sb://staging-sb-tcx-0-3-01.servicebus.windows.net/;SharedAccessKeyName=ManageSharedAccessKey;SharedAccessKey=rTxogBWPB0LYH7uYckrXrcdZ4NoruiTUzYAtIp3ttGA=";;
	static final String SERVICE_BUS_TOPIC = "b5e3676704374d581759bd20"; 
	static final String SUBSCRIPTION_TEXT = "/subscriptions/";
	static final String SERVICE_BUS_SUBSCRIPTION =  "605ba5a5d928bd79ead5cbb7"; //"60a7ddde84e1734ad7c8480f"; //"Last1Day";//
	static final String KAFKA_HOST = "localhost";
	static final int KAFKA_PORT = 9092;
	static final int BUFFER_MEMORY = 33554432;
	static final int BATCH_SIZE = 16384;
	static final int RETRIES = 0;
	static final int LINGER_MS = 100;
	static final String ACK_ALL = "all";
	static final String VALUE_SERIALIZER_CLASS_CONFIG = "org.apache.kafka.common.serialization.StringSerializer";
	static final String KEY_SERIALIZER_CLASS_CONFIG = "org.apache.kafka.common.serialization.StringSerializer";
	static Properties props;
	static SubscriptionClient subscriptionClient;
    static ExecutorService executorService;
	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		
	
    public static void main(String[] args) throws Exception, ServiceBusException {
		//check host availaibility of Kafka Server
		CheckServer chk1 = new CheckServer();
		
		if (chk1.hostAvailabilityCheck()){
			try{
				initKafkaTopic();
			
				//subscriptionClient = new SubscriptionClient(new ConnectionStringBuilder(connectionString, SERVICE_BUS_TOPIC+SUBSCRIPTION_TEXT+SERVICE_BUS_SUBSCRIPTION), ReceiveMode.PEEKLOCK);
				subscriptionClient = new SubscriptionClient(new ConnectionStringBuilder(connectionString, SERVICE_BUS_TOPIC+SUBSCRIPTION_TEXT+SERVICE_BUS_SUBSCRIPTION), ReceiveMode.RECEIVEANDDELETE);
        	
				executorService = Executors.newCachedThreadPool();
				registerMessageHandlerOnClient(subscriptionClient, executorService);
			}
			catch (Exception e){
				e.printStackTrace();
			}	
		}
		else {
			System.out.println("Kafka Server not available.");
		}
    }
	
	static void initKafkaTopic() throws Exception{
		props = new Properties();
		props.put("bootstrap.servers", KAFKA_HOST+":"+String.valueOf(KAFKA_PORT));
		props.put("acks", ACK_ALL);
        props.put("retries", RETRIES);
        props.put("batch.size", BATCH_SIZE);
        props.put("linger.ms", LINGER_MS);
        props.put("buffer.memory", BUFFER_MEMORY);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, VALUE_SERIALIZER_CLASS_CONFIG);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KEY_SERIALIZER_CLASS_CONFIG);
	}
	
	static void callProdKafkaTopic(String msgId, String messageText){
		System.out.println("Message1");
		Thread.currentThread().setContextClassLoader(null);
		System.out.println("Message2");
		Producer<String, String> producer = new KafkaProducer<String, String>(props);
        System.out.println("Message3");    
        producer.send(new ProducerRecord<String, String>(KAFKA_TOPIC, msgId, messageText));
        System.out.println("Message sent successfully");
        producer.close();
	}
    
    static void registerMessageHandlerOnClient(SubscriptionClient receiveClient, ExecutorService executorService) throws Exception {
        CheckServer chk = new CheckServer();
		System.out.println(String.valueOf(chk.hostAvailabilityCheck()));
		if (chk.hostAvailabilityCheck()){	
			receiveClient.registerMessageHandler(
                new IMessageHandler() {
					public CompletableFuture<Void> onMessageAsync(IMessage message) {
                            System.out.println("Here"); 
							byte[] body = message.getBody();
							String msgId = message.getMessageId();
							String messageText = new String(body, UTF_8);
							LocalDateTime end = LocalDateTime.now();  
							System.out.println(messageText); 
							System.out.println("callProdKafkaTopic"); 
                            callProdKafkaTopic(msgId, messageText);
                        //return receiveClient.completeAsync(message.getLockToken());
						return CompletableFuture.completedFuture(null);
						
                    }

                    // callback invoked when the message handler has an exception to report
                    public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
                        System.out.printf(exceptionPhase + "-" + throwable.getMessage());
                    }
                },
                // 1 concurrent call, messages are auto-completed, auto-renew duration
                //new MessageHandlerOptions(100, false, Duration.ofMinutes(1)),executorService);
				new MessageHandlerOptions(100, false, Duration.ofMillis(1000)),executorService);
		}
		else {
			System.out.println("Cannnot pull from Service bus, Kafka cluster not running.");
		}
    }
}