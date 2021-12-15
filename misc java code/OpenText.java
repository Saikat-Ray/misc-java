import java.net.HttpURLConnection;
import java.io.DataOutputStream;
import java.net.URL;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class OpenText{
	private static String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ship=\"http://dayrossgroup.com/web/public/webservices/shipmentServices\">\n"+
   								"<soapenv:Header/>\n"+
							   	"<soapenv:Body>\n"+
      							"<ship:CreateShipmentPCL>\n"+
         						"<ship:LoadType>SB Regular</ship:LoadType>\n"+
         						"<ship:AttachedFileName>10001.pdf</ship:AttachedFileName>\n"+
         						"<ship:AttachedFile>ssss</ship:AttachedFile>\n"+
         						"<ship:TransactionDate>2020-12-14</ship:TransactionDate>\n"+
         						"<ship:ShipmentID>100001541</ship:ShipmentID>\n"+
         						"<ship:GatewayID>JVL</ship:GatewayID>\n"+
         						"<ship:ArrivalDate>2021-03-14</ship:ArrivalDate>\n"+
         						"<ship:CustomerID>RLBOSTON</ship:CustomerID>\n"+
         						"<ship:ShipperID>TRANSPLACE CANADA LTD_17</ship:ShipperID>\n"+
         						"<ship:SalesRepEmail>anadey@deloitte.com</ship:SalesRepEmail>\n"+
         						"<ship:Weight>100</ship:Weight>\n"+
         						"<ship:Pieces>2</ship:Pieces>\n"+
         						"<ship:BillToName>billTo</ship:BillToName>\n"+
         						"<ship:BillToAccount>aaa</ship:BillToAccount>\n"+
         						"<ship:BrokerID>987</ship:BrokerID>\n"+
         						"<ship:BrokerOffice>office</ship:BrokerOffice>\n"+
      							"</ship:CreateShipmentPCL>\n"+
   								"</soapenv:Body>\n"+
								"</soapenv:Envelope>";
	public static void main(String[] args) throws IOException{
		URL l_url = new URL("https://stg-dr-dmz-web.mccain.com/public/opentextqa.asmx");
		HttpURLConnection l_conn = (HttpURLConnection) l_url.openConnection();
		l_conn.setDoInput(true);
		l_conn.setDoOutput(true);
		l_conn.setRequestMethod("POST");
		l_conn.setRequestProperty("Content-Type"," text/xml;charset=UTF-8");
		l_conn.setRequestProperty("Authorization", "No Authorization");
		l_conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(l_conn.getOutputStream());
		wr.writeBytes(xml);
		wr.flush();
		wr.close();
		l_conn.connect();
		int code = l_conn.getResponseCode();
		System.out.println("Code: "+code);
		System.out.println("Message: "+l_conn.getResponseMessage());
		InputStreamReader isr = new InputStreamReader(l_conn.getInputStream());
		BufferedReader in = new BufferedReader(isr);
		String inputLine;
		while ((inputLine = in.readLine())!= null){
			System.out.println(inputLine);
		}
		StringBuffer hello = new StringBuffer("Hello");
		StringBuffer world = new StringBuffer("World");
		hello.append(world);
		System.out.println(hello);
	}
}