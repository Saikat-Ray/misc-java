package com.dayandross.tcxqueue.subscription;

public class SubscribeTCX {
	public static void main(String[] args){
		int len = args.length;
		if (len < 6)
			System.out.println ("FATAL ERROR!!! Not enough parameters to run the program.");
		String KAFKA_HOST = args[0]; //"localhost";
		int KAFKA_PORT = Integer.valueOf(args[1]); //9092;
		String KAFKA_TOPIC = args[2]; //"TruckmateOrdersTopic";// "Test-Saikat-AnotherTopic"; //
		String CONNECTIONSTRING = args[3]; // "Endpoint=sb://staging-sb-tcx-0-3-01.servicebus.windows.net/;SharedAccessKeyName=ManageSharedAccessKey;SharedAccessKey=rTxogBWPB0LYH7uYckrXrcdZ4NoruiTUzYAtIp3ttGA=";
		String SERVICE_BUS_TOPIC = args[4]; //"b5e3676704374d581759bd20"; 
		String SERVICE_BUS_SUBSCRIPTION = args[5]; // "60a7ddde84e1734ad7c8480f"; //"605ba5a5d928bd79ead5cbb7"; //"Last1Day";//
		int bufferMemory = 33554432;
		int batchSize = 16384;
		int RETRIES = 0;
		int LINGER_MS = 100;
		String ACK_ALL = "all";
		System.out.println("=========================================================");
		System.out.println("|                         WELCOME                        |");
		System.out.println("|                Starting TCX Queue listener...          |");
		System.out.println("=========================================================");
		System.out.println("*");
		System.out.println("*");
		System.out.println("....Checking all applications running....          ");
		try{
			ASBToKafka atok = new ASBToKafka();
			//set all variables
			atok.setKafka_topic(KAFKA_TOPIC);
			atok.setConnectionString(CONNECTIONSTRING);
			atok.setServiceBusTopic(SERVICE_BUS_TOPIC);
			atok.setServiceBusSubscription(SERVICE_BUS_SUBSCRIPTION);
			atok.setKafkaHost(KAFKA_HOST);
			atok.setKafkaPort(KAFKA_PORT);
			atok.setBufferMemory(bufferMemory);
			atok.setBatchSize(batchSize);
			atok.setRetries(RETRIES);
			atok.setLingerMs(LINGER_MS);
			atok.setAckAll(ACK_ALL);
			
			//check if Kafka running
			if (atok.hostAvailabilityCheck())
				System.out.println("Hurray!!! Kafka server is up and running. Ready to stream....");
			else
			{
				System.out.println("CRITICAL ERROR!!! Kafka server is not running... Messages ARE NOT be subscribed now.");
				System.out.println("No need to terminate this listener. It will run and wait for Kafka to start. Once Kafka starts messages will be subscribed and posted...");
			}
				
			
			//initiate Kafka 
			atok.initKafkaTopic();
			
			//run subscription
			atok.initSubscriptionClient();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}