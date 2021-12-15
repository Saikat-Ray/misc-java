
import java.net.Socket;
import java.util.*;
import java.rmi.UnknownHostException;
import java.io.IOException;
import java.net.ConnectException;
import com.microsoft.azure.servicebus.*;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
//import com.google.gson.Gson;
import static java.nio.charset.StandardCharsets.*;
import java.time.Duration;
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

public class ASBToKafka {
	private String KAFKA_TOPIC;
	private String CONNECTIONSTRING;
	private String SERVICE_BUS_TOPIC;
	private final String SUBSCRIPTION_TEXT = "/subscriptions/";
	private String SERVICE_BUS_SUBSCRIPTION;
	private String KAFKA_HOST;
	private int KAFKA_PORT;// = 9092;
	private int BUFFER_MEMORY; // = 33554432;
	private int BATCH_SIZE; // = 16384;
	private int RETRIES; // = 0;
	private int LINGER_MS; // = 100;
	private String ACK_ALL; // = "all";
	private final String VALUE_SERIALIZER_CLASS_CONFIG = "org.apache.kafka.common.serialization.StringSerializer";
	private final String KEY_SERIALIZER_CLASS_CONFIG = "org.apache.kafka.common.serialization.StringSerializer";
	private Properties props;
	private SubscriptionClient subscriptionClient;
    private ExecutorService executorService;

	public void setKafka_topic(String kafka_topic){
		KAFKA_TOPIC = kafka_topic;
	}
	public String getKafka_topic(){
		return KAFKA_TOPIC;
	}
	public void setConnectionString(String connectionString){
		CONNECTIONSTRING = connectionString;
	}
	public String getConnectionString(){
		return CONNECTIONSTRING;
	}
	public void setServiceBusTopic(String serviceBusTopic){
		SERVICE_BUS_TOPIC = serviceBusTopic;
	}
	public String getServiceBusTopic(){
		return SERVICE_BUS_TOPIC;
	}
	public void setServiceBusSubscription(String serviceBusSubscription){
		SERVICE_BUS_SUBSCRIPTION = serviceBusSubscription;
	}
	public String getServiceBusSubscription(){
		return SERVICE_BUS_SUBSCRIPTION;
	}
	public void setKafkaHost(String kafkaHost){
		KAFKA_HOST = kafkaHost;
	}
	public String getKafkaHost(){
		return KAFKA_HOST;
	}
	public void setKafkaPort(int kafkaport){
		KAFKA_PORT = kafkaport;
	}
	public int getKafkaPort(){
		return KAFKA_PORT;
	}
	public void setBufferMemory(int bufferMemory){
		BUFFER_MEMORY = bufferMemory;
	}
	public int getBufferMemory(){
		return BUFFER_MEMORY;
	}
	public void setBatchSize(int batchSize){
		BATCH_SIZE = batchSize;
	}
	public int getBatchSize(){
		return BATCH_SIZE;
	}
	public void setRetries(int retries){
		RETRIES = retries;
	}
	public int getRetries(){
		return RETRIES;
	}
	public void setLingerMs(int ms){
		LINGER_MS = ms;
	}
	public int getLingerMs(){
		return LINGER_MS;
	}
	public void setAckAll(String ackAll){
		ACK_ALL = ackAll;
	}
	public String getAckAll(){
		return ACK_ALL;
	}
	public boolean hostAvailabilityCheck()//throws Exception
	{
		boolean available = true;
		Socket s;
		//System.out.println("inside hostAvailabilityCheck");
		try	{
			s = new Socket(KAFKA_HOST, KAFKA_PORT);
			try {
				if (s.isConnected())
					s.close();
			}
			catch (UnknownHostException e)
			{
				available = false;
				s = null;
			}
			catch (IOException e) {
				available = false;
				s = null;
			}
			catch (NullPointerException e) {
				available = false;
				s = null;
			}
		}
		catch(ConnectException cExcep){
			available = false;
			System.out.println("Kafka server not running...");
		}
		finally {
			return available;
		}
	}
	public void initKafkaTopic() throws Exception{
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
	public void initSubscriptionClient() throws Exception
	{
		subscriptionClient = new SubscriptionClient(new ConnectionStringBuilder(CONNECTIONSTRING, SERVICE_BUS_TOPIC+SUBSCRIPTION_TEXT+SERVICE_BUS_SUBSCRIPTION), ReceiveMode.PEEKLOCK);
		executorService = Executors.newCachedThreadPool();
		registerMessageHandlerOnClient(subscriptionClient, executorService);
	}

	private void callProdKafkaTopic(String msgId, String messageText){
		Thread.currentThread().setContextClassLoader(null);
		Producer<String, String> producer = new KafkaProducer<String, String>(props);
        producer.send(new ProducerRecord<String, String>(KAFKA_TOPIC, msgId, messageText));
        //System.out.println("Message sent successfully");
        producer.close();
	}

	private void registerMessageHandlerOnClient(SubscriptionClient receiveClient, ExecutorService executorService) throws Exception {
        receiveClient.registerMessageHandler(
            new IMessageHandler() {
				public CompletableFuture<Void> onMessageAsync(IMessage message) {
					try{
						if (hostAvailabilityCheck()){
							System.out.println("Here");
							byte[] body = message.getBody();
							String msgId = message.getMessageId();
							String messageText = new String(body, UTF_8);
							LocalDateTime end = LocalDateTime.now();
							System.out.println(messageText);
							//System.out.println("callProdKafkaTopic");
							callProdKafkaTopic(msgId, messageText);
							return receiveClient.completeAsync(message.getLockToken());
						}
						else{
							try{
								receiveClient.abandon(message.getLockToken());
							}
							catch (Exception e){
								//e.printStackTrace();
								System.out.println("Exception while calling receiveClient.abandon");
								System.out.println(e);
							}
							return null;
						}
					}
					catch(Exception e){
						//e.printStackTrace();
						System.out.println("Exception while calling callProdKafkaTopic");
						System.out.println(e);
					}
					return null;
                }

                // callback invoked when the message handler has an exception to report
                public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
                    //System.out.printf(exceptionPhase + "-" + throwable.getMessage());
					System.out.println("Exception occured in phase: "+exceptionPhase);
					//System.out.println("Exception message is: "+throwable.getMessage());
                }
                },
                // 1 concurrent call, messages are auto-completed, auto-renew duration
                //new MessageHandlerOptions(100, false, Duration.ofMinutes(1)),executorService);
				new MessageHandlerOptions(200, false, Duration.ofMillis(1000)),executorService);
    }
}