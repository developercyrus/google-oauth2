package example0;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class GetAccessTokenByRawHttp {
    public static void main(String[] args) throws IOException {
        /* 
         	The clientId and clientSecret are copied from Google console        	
         	1. go to https://console.developers.google.com
         	2. go to Permission > Credentials > "Client ID for native application" 
         	3. find "Client ID" and "Client secret"	
         */
        String clientId = "";
        String clientSecret = "";
        String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";        
        /*
          	this can be obtained by running the first time
         */
        String refresh_token = "";
        String scopes = "https://spreadsheets.google.com/feeds https://www.googleapis.com/auth/blogger https://www.googleapis.com/auth/tasks";


        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();	
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(GetAccessTokenByRawHttp.class.getResourceAsStream("/client_secrets.json")));		
		clientId = clientSecrets.getDetails().getClientId();
		clientSecret = clientSecrets.getDetails().getClientSecret();
        /*
			https://accounts.google.com/o/oauth2/auth
	        	?client_id=
	        	&redirect_uri=
	        	&response_type=code
	        	&scope=         
         */        
        String authorizationUrl = "https://accounts.google.com/o/oauth2/auth"
        							+ "?client_id=" + clientId
        							+ "&redirect_uri=" + redirectUrl
        							+ "&response_type=code" 
        							+ "&scope=" + scopes;

        System.out.println("Go to the following link in your browser:");
        System.out.println(authorizationUrl);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("What is the authorization code?");
        String authorizationCode = in.readLine();

        
        
        
        
        /*
	      	https://accounts.google.com/o/oauth2/token
	      		?code=
				&client_id=
				&client_secret=
				&redirect_uri=
				&grant_type=authorization_code
         */
        URL urlObtainToken;
        HttpURLConnection connectionObtainToken; 
        OutputStreamWriter writer;
        
        urlObtainToken = new URL("https://accounts.google.com/o/oauth2/token");
        connectionObtainToken =  (HttpURLConnection) urlObtainToken.openConnection();
        connectionObtainToken.setRequestMethod("POST");
        connectionObtainToken.setDoOutput(true);

        writer  = new OutputStreamWriter(connectionObtainToken.getOutputStream());
        writer.write("code="+authorizationCode+"&");  
        writer.write("client_id="+clientId+"&");   
        writer.write("client_secret="+clientSecret+"&");  
        writer.write("redirect_uri="+redirectUrl+"&");   
        writer.write("grant_type=authorization_code");  
        writer.close();

        if (connectionObtainToken.getResponseCode() == HttpURLConnection.HTTP_OK){
        	StringBuilder sbLines = new StringBuilder("");
        	BufferedReader reader = new BufferedReader(new InputStreamReader(connectionObtainToken.getInputStream(), "utf-8"));
        	String strLine = "";
        	while((strLine=reader.readLine())!=null){
        		sbLines.append(strLine);
        	}

        	try {        		
        		JSONObject json = new JSONObject(sbLines.toString());
        		System.out.println(json);
        		System.out.println("Access token: " + json.getString("access_token")); 
        		System.out.println("Refresh token: " + json.getString("refresh_token"));
        		System.out.println("Expires in seconds: " + json.getLong("expires_in"));
        		System.out.println("Token type: " + json.getString("token_type"));
        	} catch (JSONException e) {
        		e.printStackTrace();
        	}
        }    
        
        
        
        
        /*
	      	https://accounts.google.com/o/oauth2/token
	      		?client_id=
				&client_secret=
				&redirect_uri=
				&refresh_token=
				&grant_type=refresh_token
         */
        urlObtainToken = new URL("https://accounts.google.com/o/oauth2/token");
        connectionObtainToken =  (HttpURLConnection) urlObtainToken.openConnection();
        connectionObtainToken.setRequestMethod("POST");
        connectionObtainToken.setDoOutput(true);
        
        writer  = new OutputStreamWriter(connectionObtainToken.getOutputStream());
        writer.write("client_id="+clientId+"&");   
        writer.write("client_secret="+clientSecret+"&");  
        writer.write("redirect_uri="+redirectUrl+"&");   
        writer.write("refresh_token="+refresh_token+"&"); 
        writer.write("grant_type=refresh_token");  
        writer.close();

        if (connectionObtainToken.getResponseCode() == HttpURLConnection.HTTP_OK){
        	StringBuilder sbLines = new StringBuilder("");
        	BufferedReader reader = new BufferedReader(new InputStreamReader(connectionObtainToken.getInputStream(), "utf-8"));
        	String strLine = "";
        	while((strLine=reader.readLine())!=null){
        		sbLines.append(strLine);
        	}

        	try {        		
        		JSONObject json = new JSONObject(sbLines.toString());
        		System.out.println(json); 
        		System.out.println("Access token: " + json.getString("access_token")); 
        		System.out.println("Expires in seconds: " + json.getLong("expires_in"));
        		System.out.println("Token type: " + json.getString("token_type"));
        	} catch (JSONException e) {
        		e.printStackTrace();
        	}
        }    
    }
}
