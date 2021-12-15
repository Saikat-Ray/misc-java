import java.net.URL;
import java.net.HttpURLConnection;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SynLogin {
	public static void main(String[] args){
	        System.out.println("To login to Synergize...");
	        String URL = "https://daydevsyn.tmwcloud.com/SynergizeServices/IdentityService/api/logins";
	        try {
		    	URL url = new URL(URL);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setDoInput(true);
		        connection.setDoOutput(true);
		        connection.setRequestMethod("GET");
		        connection.setRequestProperty("Accept", "application/json;");
		        connection.setRequestProperty("Content-Type", "application/json;");
		        connection.setRequestProperty("Connection", "keep-alive;");
		        String login = "{username: \"primaryuser\", password: \"hM3#ga6xUNTq558xskaXpRk3g\"}";
		        connection.setDoOutput(true);
		        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		        wr.writeBytes(login);
		        wr.flush();
		  		wr.close();
		        int code = connection.getResponseCode();
		        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		        String output;
		        StringBuffer response = new StringBuffer();
		        while ((output = in.readLine()) != null) {
		   			response.append(output);
		  		}
		        in.close();
		        System.out.println("Code.... "+code);
		        System.out.println("Token.... "+response.toString());
		}
		catch (Exception e){
		     	throw new RuntimeException(e.getMessage());
    		}
    }
}