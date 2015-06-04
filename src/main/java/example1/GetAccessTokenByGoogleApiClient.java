package example1;
import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import example2.GetAccessTokenByGoogleOauthClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;

public class GetAccessTokenByGoogleApiClient {
    public static void main(String[] args) throws IOException {
        /* 
         	reference: 
         	http://stackoverflow.com/questions/21440101/what-is-an-example-of-using-oauth-2-0-and-google-spreadsheet-api-with-java
         	
         	The clientId and clientSecret are copied from Google console        	
         	1. go to https://console.developers.google.com
         	2. go to Permission > Credentials > "Client ID for native application" 
         	3. find "Client ID" and "Client secret"	
         */
        String clientId = "";
        String clientSecret = "";
        String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
        Collection<String> scopes = Arrays.asList("https://spreadsheets.google.com/feeds", "https://www.googleapis.com/auth/blogger", "https://www.googleapis.com/auth/tasks");

        
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();	
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(GetAccessTokenByGoogleApiClient.class.getResourceAsStream("/client_secrets.json")));		
		clientId = clientSecrets.getDetails().getClientId();
		clientSecret = clientSecrets.getDetails().getClientSecret();

        String authorizationUrl = new GoogleAuthorizationCodeRequestUrl(clientId, redirectUrl, scopes).build();
        /*
			https://accounts.google.com/o/oauth2/auth
	        	?client_id=
	        	&redirect_uri=
	        	&response_type=
	        	&scope=         
         */        
        System.out.println("Go to the following link in your browser:");
        System.out.println(authorizationUrl);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("What is the authorization code?");
        String authorizationCode = in.readLine();

        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                                        httpTransport, 
                                        jsonFactory,
                                        clientId, 
                                        clientSecret,
                                        authorizationCode,
                                        redirectUrl).execute();
        /*
          	https://accounts.google.com/o/oauth2/token
          		?code=
				&client_id=
				&client_secret=
				&redirect_uri=
				&grant_type=authorization_code
         */
        
        String accessToken = response.getAccessToken();
        String refreshToken = response.getRefreshToken();
        Long expiresInSeconds = response.getExpiresInSeconds();
        System.out.println("Access token: " + accessToken);
        System.out.println("Refresh token: " + refreshToken);
        System.out.println("Expires in seconds: " + expiresInSeconds);          
    }
}
