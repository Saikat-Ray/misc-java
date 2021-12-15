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

public class CheckServer {
	public  boolean hostAvailabilityCheck() throws Exception
	{ 
		String SERVER_ADDRESS = "127.0.0.1";
		int TCP_SERVER_PORT = 9092;
		boolean available = true; 
		Socket s;
		System.out.println("inside hostAvailabilityCheck");
		try	{
			s = new Socket(SERVER_ADDRESS, TCP_SERVER_PORT);
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
				s=null;
			}
		}
		catch(ConnectException cExcep){
			available = false; 
		}
		finally {
			return available;   
		}
	} 
}