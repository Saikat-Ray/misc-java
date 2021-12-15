@ECHO OFF

set KAFKA_HOST=localhost
set KAFKA_PORT=9092
set KAFKA_TOPIC=TruckmateOrdersTopic
set CONNECTION_STRING=Endpoint=sb://staging-sb-tcx-0-3-01.servicebus.windows.net/;SharedAccessKeyName=ManageSharedAccessKey;SharedAccessKey=rTxogBWPB0LYH7uYckrXrcdZ4NoruiTUzYAtIp3ttGA=
set SERVICE_BUS_TOPIC=b5e3676704374d581759bd20
set SERVICE_BUS_SUBSCRIPTION=605ba5a5d928bd79ead5cbb7


java -Xms128m -Xmx384m -Xnoclassgc com.dayandross.tcxqueue.subscription.SubscribeTCX %KAFKA_HOST% %KAFKA_PORT% %KAFKA_TOPIC% %CONNECTION_STRING% %SERVICE_BUS_TOPIC% %SERVICE_BUS_SUBSCRIPTION%