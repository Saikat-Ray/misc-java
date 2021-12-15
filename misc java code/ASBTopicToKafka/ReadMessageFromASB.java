import com.azure.messaging.servicebus.*;
import com.azure.core.exception.AzureException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;


public class ReadMessageFromASB {
	static String sbNameSpace = "Endpoint=sb://testqueue-in.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=QmR+OBEOuR0JecL3DWW58A+Y8+Fw6F0cL2ehA0MVoQg=";
	static String qName = "demoq_t";
	
	public static void main(String[] args) throws InterruptedException{
		String message;
		ReadMessageFromASB readMessageFromASB = new ReadMessageFromASB();
		//message = retMessage();
		System.out.println(readMessageFromASB.retMessage());
	}
	public static String retMessage() throws InterruptedException{
		StringBuffer topicMsg = retMessagePvt();
		return topicMsg.toString();
	}
	private static StringBuffer retMessagePvt() throws InterruptedException{
		StringBuffer topicMsg = new StringBuffer("Hello");
		sendMessages();
		return topicMsg;
	}
	// handles received messages
	static void sendMessages() throws InterruptedException
	{
		ServiceBusSenderClient sender = new ServiceBusClientBuilder().connectionString(sbNameSpace)
																	.sender()
																	.queueName(qName)
																	.buildClient();
		List<ServiceBusMessage> messages = Arrays.asList(new ServiceBusMessage("Hello world").setMessageId("1"),
														new ServiceBusMessage("Bonjour").setMessageId("2"));

		sender.sendMessages(messages);

		sender.close();
	}
}