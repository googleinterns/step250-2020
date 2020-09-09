package com.google.step.coffee;

import static com.google.step.coffee.APIUtils.HTTP_TRANSPORT;
import static com.google.step.coffee.APIUtils.JSON_FACTORY;
import static com.google.step.coffee.data.CalendarUtils.SCOPES;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.http.HttpServletRequest;

/**
 * Provides tools for authorising permissions to Google APIs.
 */
public class OAuthService {

  private static final String CREDENTIALS_FILEPATH = "/client_secret.json";

  private static GoogleClientSecrets clientSecrets = null;

  /**
   * Loads credentials from store of pre-authorised user, null if user has not authorised.
   *
   * @param userId String of userId generated by AppEngine UserService.
   * @return Credential object needed to interact with authorised Google APIs or null on fail.
   */
  public static Credential getCredentials(String userId) {
    try {
      GoogleAuthorizationCodeFlow flow = getAuthFlow();

      return flow.loadCredential(userId);
    } catch (IOException e) {
      System.out.println("Getting credentials failed: " + e.getMessage());

      return null;
    }
  }

  /**
   * Checks whether the given user has authorised the set scopes.
   *
   * @param userId String of userId generated by AppEngine UserService
   * @return boolean of whether there is an authorisation credential for given user.
   */
  public static boolean userHasAuthorised(String userId) throws IOException {
    return getAuthFlow().getCredentialDataStore().containsKey(userId);
  }

  /**
   * Using authorisation code from client-side authorisation page, fetches access and refresh token
   * to store.
   *
   * @param userId String of userId generated by AppEngine UserService.
   * @param authCode String of 'code' query parameter set by OAuth 2.0 authorisation webpage.
   */
  public static void fetchTokenAndStore(String userId, String authCode)
      throws IOException {
    GoogleAuthorizationCodeFlow flow = getAuthFlow();
    GoogleTokenResponse response = flow.newTokenRequest(authCode).execute();

    flow.createAndStoreCredential(response, userId);
  }

  /**
   * Gets authorisation webpage URL for set scopes.
   *
   * @param request Incoming request requiring authorisation URL for response.
   * @return String of authorisation URL which will redirect to OAuth callback handling servlet.
   */
  public static String getAuthURL(HttpServletRequest request) throws IOException {
    return getAuthFlow().newAuthorizationUrl().setRedirectUri(getRedirectUri(request)).build();
  }

  /**
   * Generates URI for redirect to servlet handling OAuth callback.
   */
  public static String getRedirectUri(HttpServletRequest request) {
    GenericUrl url = new GenericUrl(request.getRequestURL().toString());

    url.setRawPath("/api/oauth2callback");
    return url.build();
  }

  /**
   * Generates AuthorisationCodeFlow object for managing Google OAuth 2.0 flow.
   */
  public static GoogleAuthorizationCodeFlow getAuthFlow() throws IOException {
    return new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, getClientSecrets(), SCOPES)
        .setDataStoreFactory(AppEngineDataStoreFactory.getDefaultInstance())
        .setAccessType("offline")
        .setApprovalPrompt("force")
        .build();
  }

  /**
   * Loads credential file containing client secrets created by GCP credential manager.
   */
  private static GoogleClientSecrets getClientSecrets() throws IOException {
    if (clientSecrets == null) {
      InputStream in = OAuthService.class.getResourceAsStream(CREDENTIALS_FILEPATH);

      if (in == null) {
        throw new FileNotFoundException("Credentials resource not found at " + CREDENTIALS_FILEPATH);
      }

      clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    }

    return clientSecrets;
  }
}
