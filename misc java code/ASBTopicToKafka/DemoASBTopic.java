import com.azure.messaging.servicebus.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;

public class DemoASBTopic {
	static String connectionString = "Endpoint=sb://staging-sb-tcx-0-3-01.servicebus.windows.net/;SharedAccessKeyName=ManageSharedAccessKey;SharedAccessKey=rTxogBWPB0LYH7uYckrXrcdZ4NoruiTUzYAtIp3ttGA=;EntityPath=b5e3676704374d581759bd20";
	static String topicName = "b5e3676704374d581759bd20";    
	static String subName = "60a7ddde84e1734ad7c8480f";
	
	static void sendMessage()
	{
		// create a Service Bus Sender client for the queue 
		ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
            .connectionString(connectionString)
            .sender()
            .topicName(topicName)
            .buildClient();

		// send one message to the topic
		senderClient.sendMessage(new ServiceBusMessage("Hello, World!"));
		System.out.println("Sent a single message to the topic: " + topicName);        
	}
	
	static List<ServiceBusMessage> createMessages()
	{
		// create a list of messages and return it to the caller
		ServiceBusMessage[] messages = {
    		new ServiceBusMessage("First message"),
    		new ServiceBusMessage("Second message"),
    		new ServiceBusMessage("Third message")
		};
		return Arrays.asList(messages);
	}
	public static void main(String[] args) throws Exception{
		System.out.println(connectionString);
		sendMessage();
	}
}