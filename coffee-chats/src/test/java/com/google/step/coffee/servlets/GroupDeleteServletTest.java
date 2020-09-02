package com.google.step.coffee.servlets;

import com.google.appengine.api.datastore.*;
import com.google.step.coffee.HttpError;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.TestHelper;
import com.google.step.coffee.entity.Group;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class GroupDeleteServletTest extends TestHelper {
  private void deleteGroup(Key key) throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(KeyFactory.keyToString(key));
    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    new GroupDeleteServlet().post(jsonServletRequest);
  }
  
  @Test
  public void testDelete() throws Exception {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Entity groupEntity1 = new Entity("group");
    groupEntity1.setProperty("name", "foo");
    groupEntity1.setProperty("description", "foo's desc");
    datastore.put(groupEntity1);

    Entity groupEntity2 = new Entity("group");
    groupEntity2.setProperty("name", "bar");
    groupEntity2.setProperty("description", "bar's desc");
    datastore.put(groupEntity2);

    Entity groupEntity3 = new Entity("group");
    groupEntity3.setProperty("name", "baz");
    groupEntity3.setProperty("description", "baz's desc");
    datastore.put(groupEntity3);

    deleteGroup(groupEntity2.getKey());

    // test that the entities are still there
    datastore.get(groupEntity1.getKey());
    datastore.get(groupEntity3.getKey());

    // test that the deleted entity is gone
    try {
      datastore.get(groupEntity2.getKey());
      assert false;
    } catch (EntityNotFoundException ignored) {}
  }
}
