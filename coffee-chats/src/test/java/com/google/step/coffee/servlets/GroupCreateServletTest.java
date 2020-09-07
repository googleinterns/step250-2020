package com.google.step.coffee.servlets;

import com.google.appengine.api.datastore.*;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.TestHelper;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class GroupCreateServletTest extends TestHelper {
  private void withTestData(List<String> nameList) throws Exception {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    GroupCreateServlet servlet = new GroupCreateServlet();

    List<Entity> expected = new ArrayList<>();

    for (String name : nameList) {
      HttpServletRequest request = mock(HttpServletRequest.class);
      when(request.getParameter("name")).thenReturn(name);

      JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
      servlet.post(jsonServletRequest);

      Entity entity = new Entity("group");
      entity.setProperty("name", name);
      entity.setProperty("description", new Text(""));
      entity.setProperty("ownerId", "test_user");
      expected.add(entity);
    }

    Query query = new Query("group");
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

    assertThat(results.stream().map(PropertyContainer::getProperties).toArray(),
        equalTo(expected.stream().map(PropertyContainer::getProperties).toArray()));
  }

  @Test
  public void testEmpty() throws Exception {
    withTestData(new ArrayList<>());
  }

  @Test
  public void testSingleton() throws Exception {
    withTestData(Collections.singletonList("foo"));
  }
}
