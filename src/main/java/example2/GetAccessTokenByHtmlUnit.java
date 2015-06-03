package example2;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import example3.GetAccessTokenByGoogleApi;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;

public class GetAccessTokenByHtmlUnit {
    public static void main(String[] args) throws IOException, InterruptedException {
        /* 
         	reference: 
         	http://stackoverflow.com/questions/21440101/what-is-an-example-of-using-oauth-2-0-and-google-spreadsheet-api-with-java
         	
        	The clientId and clientSecret are copied from Google console        	
         	1. go to https://console.developers.google.com
         	2. go to Permission > Credentials > "Client ID for native application" 
         	3. find "Client ID" and "Client secret"	
         */
    	String username = "";
    	String password = "";
        String clientId = "";
        String clientSecret = "";
        String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
        Collection<String> scopes = Arrays.asList("https://www.googleapis.com/auth/tasks");

        
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();	
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(GetAccessTokenByGoogleApi.class.getResourceAsStream("/client_secrets.json")));		
		clientId = clientSecrets.getDetails().getClientId();
		clientSecret = clientSecrets.getDetails().getClientSecret();
        
        
        String authorizationUrl = new GoogleAuthorizationCodeRequestUrl(clientId, redirectUrl, scopes).build();
        System.out.println(authorizationUrl);
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setJavaScriptEnabled(true);       
        webClient.getOptions().setThrowExceptionOnScriptError(true);
        webClient.getOptions().setCssEnabled(true);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getCookieManager().setCookiesEnabled(true);

        HtmlPage page = webClient.getPage(authorizationUrl);
        HtmlSubmitInput signInButton = (HtmlSubmitInput)page.getElementByName("signIn");
        HtmlTextInput userNameField = (HtmlTextInput)page.getElementByName("Email");
        HtmlPasswordInput passwordField = (HtmlPasswordInput)page.getElementByName("Passwd");
        userNameField.setValueAttribute(username);
        passwordField.setValueAttribute(password);
        HtmlPage allowAccessPage = signInButton.click();        
        System.out.println(allowAccessPage.getUrl());
        /*          	
          	suggest to use following, but doesn't work. so use this workaround 
          	webClient.setAjaxController(new NicelyResynchronizingAjaxController());
          	
          	reference: http://stackoverflow.com/questions/19551043/process-ajax-request-in-htmlunit
         */
        webClient.waitForBackgroundJavaScript(2000);
        HtmlButton allowAccessButton = (HtmlButton)allowAccessPage.getElementById("submit_approve_access");
        HtmlPage tokenPage = allowAccessButton.click();

        HtmlTextInput tokenElement = (HtmlTextInput)tokenPage.getElementById("code");
        String authorizationCode = tokenElement.getText();
        webClient.close();        
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                                        httpTransport, 
                                        jsonFactory,
                                        clientId, 
                                        clientSecret,
                                        authorizationCode,
                                        redirectUrl).execute();
        
        String accessToken = response.getAccessToken();
        String refreshToken = response.getRefreshToken();
        Long expiresInSeconds = response.getExpiresInSeconds();
        System.out.println("Access token: " + accessToken);
        System.out.println("Refresh token: " + refreshToken);
        System.out.println("Expires in seconds: " + expiresInSeconds);           
    }
}
