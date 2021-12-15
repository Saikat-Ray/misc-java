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
import java.util.*;

public class SynergizeGetFiles {
	private static String loginURL = "https://daydevsyn.tmwcloud.com/SynergizeServices/IdentityService/api/logins";

	private static String fListURL1 = "https://daydevsyn.tmwcloud.com/SynergizeServices/DocumentService/api/Repositories/FREIGHTDOCSDEV/Documents?$skip=0&returnHierarchy=true&$count=true&$filter=(FBNumber%20eq%20%27";

	private static String fDLURL1 = "https://daydevsyn.tmwcloud.com/SynergizeServices/DocumentService/api/Repositories/FREIGHTDOCSDEV/Documents/";
	private static String fDLURL2 = "/File?annotations=false&redactions=true&extension=pdf&addFormData=false";

	private static String o_filePath ="C:\\Users\\sairay\\java\\";
	private static String billNumber = "007165";


	public static void main(String[] args){

		String token;
		ArrayList<String> fileList = new ArrayList<String>();
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
			StringBuffer l_response = new StringBuffer();
			while ((output = in.readLine()) != null) {
				l_response.append(output);
			}
			in.close();

			int endIdx = l_response.toString().lastIndexOf("\"");
			token = l_response.toString().substring(10,endIdx);
			token = "Bearer "+token;
		}
		catch (Exception e){
			throw new RuntimeException(e.getMessage());
		}

		//call get File List API
		try{
			URL gf_url = new URL(fListURL1+billNumber+"%27)");
			HttpURLConnection gf_conn = (HttpURLConnection) gf_url.openConnection();
			gf_conn.setDoInput(true);
			gf_conn.setDoOutput(true);
			gf_conn.setRequestMethod("GET");
			gf_conn.setRequestProperty("Accept", "application/json;");
			gf_conn.setRequestProperty("Authorization", token);

			int code = gf_conn.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(gf_conn.getInputStream()));
			String output;
			StringBuffer fl_response = new StringBuffer();
			while ((output = in.readLine()) != null) {
				fl_response.append(output);
			}
			in.close();
			String respJson = fl_response.toString();

			String docId = "In_DocID", fileId;
			int lastIdx = 0, commaIdx = 0;
			while (lastIdx != -1){
				lastIdx = respJson.indexOf(docId,lastIdx);
				if (lastIdx != -1){
					commaIdx = respJson.indexOf(",",lastIdx);
					fileList.add(respJson.substring(lastIdx+11,commaIdx-1));
					lastIdx += docId.length();
				}
			}
		}
		catch (Exception e){
			throw new RuntimeException(e.getMessage());
		}
		for (String fileId: fileList){
			String fDLURL = fDLURL1+fileId+fDLURL2;
			//System.out.println(fDLURL);
			//call Download file API
			try {
				URL dl_url = new URL(fDLURL);
				HttpURLConnection dl_conn = (HttpURLConnection) dl_url.openConnection();
				dl_conn.setDoInput(true);
				dl_conn.setDoOutput(true);
				dl_conn.setRequestMethod("GET");
				dl_conn.setRequestProperty("Accept", "*/*");
				dl_conn.setRequestProperty("Content-Type", "application/json;");
				dl_conn.setRequestProperty("Authorization", token);
				String dirPath = o_filePath + billNumber;
				File bnDir = new File(dirPath);
				if (!bnDir.exists()){
					bnDir.mkdirs();
				}
				String fileName = dirPath +"\\"+fileId+".pdf";
				//System.out.println("FileName: "+fileName);

				int code = dl_conn.getResponseCode();
				System.out.println("Response code to fetch file: "+code);
				try (BufferedInputStream in = new BufferedInputStream(dl_conn.getInputStream());
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
				dl_conn.disconnect();
			}
			catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}
}