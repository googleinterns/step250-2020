package com.google.step.coffee.servlets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.OAuthService;
import com.google.step.coffee.TestHelper;
import com.google.step.coffee.servlets.AuthCalendarServlet.CalAuthResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class AuthCalendarServletTest extends TestHelper {

  AuthCalendarServlet servlet = new AuthCalendarServlet();
  HttpServletRequest httpReq = mock(HttpServletRequest.class);
  JsonServletRequest request = new JsonServletRequest(httpReq);

  @Test
  public void unauthorisedLoggedInUser() throws IOException, HttpError {
    GoogleAuthorizationCodeFlow flow = mock(GoogleAuthorizationCodeFlow.class);
    DataStore<StoredCredential> credStore = (DataStore<StoredCredential>) mock(DataStore.class);

    String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();

    // Try block needed to keep scope of static method mocking to this test method only.
    try(MockedStatic<OAuthService> service = Mockito.mockStatic(OAuthService.class)) {
      // Checking behaviour down to datastore level
      service.when(() -> OAuthService.userHasAuthorised(userId)).thenCallRealMethod();
      service.when(OAuthService::getAuthFlow).thenReturn(flow);
      when(flow.getCredentialDataStore()).thenReturn(credStore);
      when(credStore.containsKey(userId)).thenReturn(false);

      service.when(() -> OAuthService.getAuthURL(request))
          .thenReturn("https://accounts.google.com/o/oauth2/auth?access_type=offline&other_params=X");

      AuthCalendarServlet.CalAuthResponse resp = (CalAuthResponse) servlet.get(request);

      assertThat(resp.isAuthorised(), is(false));
      assertThat(resp.getAuthLink(),
          startsWith("https://accounts.google.com/o/oauth2/auth?access_type=offline"));
    }
  }

  @Test
  public void authorisedLoggedInUser() throws IOException, HttpError {
    GoogleAuthorizationCodeFlow flow = mock(GoogleAuthorizationCodeFlow.class);
    DataStore<StoredCredential> credStore = (DataStore<StoredCredential>) mock(DataStore.class);

    String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();

    // Try block needed to keep scope of static method mocking to this test method only.
    try(MockedStatic<OAuthService> service = Mockito.mockStatic(OAuthService.class)) {
      // Checking behaviour down to datastore level
      service.when(() -> OAuthService.userHasAuthorised(userId)).thenCallRealMethod();
      service.when(OAuthService::getAuthFlow).thenReturn(flow);
      when(flow.getCredentialDataStore()).thenReturn(credStore);
      when(credStore.containsKey(userId)).thenReturn(true);

      AuthCalendarServlet.CalAuthResponse resp = (CalAuthResponse) servlet.get(request);

      assertThat(resp.isAuthorised(), is(true));
    }
  }
}
