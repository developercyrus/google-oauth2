package example2;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;

public class GetAccessTokenByGoogleOauthClient {
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();	
    private static final Collection<String> scopes = Arrays.asList("https://spreadsheets.google.com/feeds", "https://www.googleapis.com/auth/blogger", "https://www.googleapis.com/auth/tasks");

    private static Credential authorize() throws Exception {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(GetAccessTokenByGoogleOauthClient.class.getResourceAsStream("/client_secrets.json")));		
        
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                                                httpTransport
                                                , JSON_FACTORY
                                                , clientSecrets
                                                , scopes)
                                                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String[] args) {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = authorize();
            
            System.out.println("Access token: " + credential.getAccessToken());
            System.out.println("Refresh token: " + credential.getRefreshToken());
            System.out.println("Expires in seconds: " + credential.getExpiresInSeconds()); 

        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
    }	
}