import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DownloadFiles {
	private static String o_new_token = "";
	private static String o_URL = "https://daydevsyn.tmwcloud.com/SynergizeServices/DocumentService/api/Repositories/FREIGHTDOCSDEV/Documents/Syn10/File?annotations=false&redactions=true&extension=pdf&addFormData=false";
	private static String o_filePath ="C:\\Users\\sairay\\java\\";
	private static String o_fileName = "Demo.pdf";
	private static String o_billNumber = "1024";
	
	private static String loginURL = "https://daydevsyn.tmwcloud.com/SynergizeServices/IdentityService/api/logins";
	
	public static void main(String[] args){
		//call login API
		try {
			URL l_url = new URL(loginURL);
			HttpURLConnection l_conn = (HttpURLConnection) l_url.openConnection();
			l_conn.setDoInput(true);
			l_conn.setDoOutput(true);
			l_conn.setRequestMethod("GET");
			l_conn.setRequestProperty("Accept", "application/json;");
			l_conn.setRequestProperty("Content-Type", "application/json;");
			l_conn.setRequestProperty("Connection", "keep-alive;");
			String login = "{username: \"primaryuser\", password: \"hM3#ga6xUNTq558xskaXpRk3g\"}";
			l_conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(l_conn.getOutputStream()); 
			wr.writeBytes(login);
			wr.flush();
			wr.close();
			int code = l_conn.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(l_conn.getInputStream()));
			String output;
			StringBuffer response = new StringBuffer();
			while ((output = in.readLine()) != null) {
				response.append(output);
			}
			in.close();
        
			int endIdx = response.toString().lastIndexOf("\"");
			String token = response.toString().substring(10,endIdx);
			//logInfo("o_token.... "+o_token.toString());
			System.out.println("o_token: "+token);
			o_new_token = "Bearer "+token;
		}
		catch (Exception e){
			throw new RuntimeException(e.getMessage());
		}
		
		
		//call download file API
		try {
			URL url = new URL(o_URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Content-Type", "application/json;");
			connection.setRequestProperty("Authorization",o_new_token );
			String dirPath = o_filePath + o_billNumber;
			File bnDir = new File(dirPath);
			if (!bnDir.exists()){
				bnDir.mkdirs();
			}
			String fileName = dirPath +"\\"+o_fileName;
			System.out.println("FileName: "+fileName);

			int code = connection.getResponseCode();
			System.out.println("Response code to fetch file: "+code);
			try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
				FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
				byte dataBuffer[] = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
					fileOutputStream.write(dataBuffer, 0, bytesRead);
				}
			} 
			catch (IOException e) {
                throw new RuntimeException(e.getMessage());
			}
			connection.disconnect();
        }
		catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }		
	}
}