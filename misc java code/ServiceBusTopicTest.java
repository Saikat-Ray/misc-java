import com.azure.messaging.servicebus.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;

public class ServiceBusTopicTest //throws Exception
{
  static String connectionString = "Endpoint=sb://staging-sb-tcx-0-3-01.servicebus.windows.net/;SharedAccessKeyName=ManageSharedAccessKey;SharedAccessKey=rTxogBWPB0LYH7uYckrXrcdZ4NoruiTUzYAtIp3ttGA=;EntityPath=b5e3676704374d581759bd20";
  static String topicName = "b5e3676704374d581759bd20";
  static String subName = "605ba5a5d928bd79ead5cbb7";

  private static void processMessage(ServiceBusReceivedMessageContext context) {
      ServiceBusReceivedMessage message = context.getMessage();
      System.out.println("Processing message. Session: %s, Sequence #: %s. Contents: %s%n"); /*, message.getMessageId(),
          message.getSequenceNumber(), message.getBody());*/
  }
  private static void processError(ServiceBusErrorContext context, CountDownLatch countdownLatch) {
      System.out.printf("Error when receiving messages from namespace: '%s'. Entity: '%s'%n",
          context.getFullyQualifiedNamespace(), context.getEntityPath());

      /*if (!(context.getException() instanceof ServiceBusException)) {
          System.out.printf("Non-ServiceBusException occurred: %s%n", context.getException());
          return;
      }*/

      ServiceBusException exception = (ServiceBusException) context.getException();
      ServiceBusFailureReason reason = exception.getReason();

      if (reason == ServiceBusFailureReason.MESSAGING_ENTITY_DISABLED
          || reason == ServiceBusFailureReason.MESSAGING_ENTITY_NOT_FOUND
          || reason == ServiceBusFailureReason.UNAUTHORIZED) {
          System.out.println("An unrecoverable error occurred. Stopping processing with reason %s: %s%n");/*,
              reason, exception.getMessage());*/

          countdownLatch.countDown();
      } else if (reason == ServiceBusFailureReason.MESSAGE_LOCK_LOST) {
          System.out.println("Message lock lost for message: %s%n"); /*, context.getException());*/
      } else if (reason == ServiceBusFailureReason.SERVICE_BUSY) {
          try {
              // Choosing an arbitrary amount of time to wait until trying again.
              TimeUnit.SECONDS.sleep(1);
          } catch (InterruptedException e) {
              System.err.println("Unable to sleep for period of time");
          }
      } else {
          System.out.printf("Error source %s, reason %s, message: %s%n"); /*, context.getErrorSource(),
              reason, context.getException());*/
      }
  }
  // handles received messages
  static void receiveMessages() throws InterruptedException
  {
      CountDownLatch countdownLatch = new CountDownLatch(1);

      // Create an instance of the processor through the ServiceBusClientBuilder
      ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
          .connectionString(connectionString)
          .processor()
          .topicName(topicName)
          .subscriptionName(subName)
          .processMessage(ServiceBusTopicTest::processMessage)
          .processError(context -> processError(context, countdownLatch))
          .buildProcessorClient();

      System.out.println("Starting the processor");
      processorClient.start();

      TimeUnit.SECONDS.sleep(10);
      System.out.println("Stopping and closing the processor");
      processorClient.close();
  }
}