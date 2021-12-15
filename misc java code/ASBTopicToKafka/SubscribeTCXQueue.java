package com.dayandross.tcxqueue.subscription;

public class SubscribeTCXQueue {
	public static void main(String[] args){
		System.out.println(args[1]);
		System.out.println(args[2]);
		String KAFKA_TOPIC = "TruckmateOrdersTopic";// "Test-Saikat-AnotherTopic"; //
		String CONNECTIONSTRING = "Endpoint=sb://staging-sb-tcx-0-3-01.servicebus.windows.net/;SharedAccessKeyName=ManageSharedAccessKey;SharedAccessKey=rTxogBWPB0LYH7uYckrXrcdZ4NoruiTUzYAtIp3ttGA=";
		String SERVICE_BUS_TOPIC = "b5e3676704374d581759bd20"; 
		String SERVICE_BUS_SUBSCRIPTION =  "60a7ddde84e1734ad7c8480f"; //"605ba5a5d928bd79ead5cbb7"; //"Last1Day";//
		String KAFKA_HOST = "localhost";
		int KAFKA_PORT = 9092;
		int bufferMemory = 33554432;
		int batchSize = 16384;
		int RETRIES = 0;
		int LINGER_MS = 100;
		String ACK_ALL = "all";
		System.out.println("=========================================================");
		System.out.println("WELCOME...2121");
		System.out.println("Starting TCX Queue listener...");
		System.out.println("=========================================================");
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
				System.out.println("Kafka server is not running... Messages will not be subscribed");
			
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