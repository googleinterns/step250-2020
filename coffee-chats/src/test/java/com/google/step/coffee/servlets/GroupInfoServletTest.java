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

public class GroupInfoServletTest extends TestHelper {
  private Group getGroupByKey(Key key) throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(KeyFactory.keyToString(key));
    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    return (Group) new GroupInfoServlet().get(jsonServletRequest);
  }

  @Test(expected = HttpError.class)
  public void testGetInvalidId() throws Exception {
    getGroupByKey(KeyFactory.createKey("group", 12345));
  }

  @Test
  public void testGetValidId() throws Exception {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Entity groupEntity = new Entity("group");
    groupEntity.setProperty("name", "foo");
    groupEntity.setProperty("description", new Text("bar"));
    groupEntity.setProperty("ownerId", "test_user");
    datastore.put(groupEntity);

    assertThat(getGroupByKey(groupEntity.getKey()), equalTo(
        Group.builder()
            .setId(KeyFactory.keyToString(groupEntity.getKey()))
            .setName("foo")
            .setDescription("bar")
            .setOwnerId("test_user")
            .build()
    ));
  }

  @Test
  public void testUpdate() throws Exception {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Entity groupEntity = new Entity("group");
    groupEntity.setProperty("name", "foo");
    groupEntity.setProperty("description", new Text("bar"));
    groupEntity.setProperty("ownerId", "test_user");
    datastore.put(groupEntity);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("id")).thenReturn(KeyFactory.keyToString(groupEntity.getKey()));
    when(request.getParameter("name")).thenReturn("updated foo");
    when(request.getParameter("description")).thenReturn("updated bar");
    when(request.getParameter("ownerId")).thenReturn("updated test_user");
    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    new GroupInfoServlet().post(jsonServletRequest);

    groupEntity = datastore.get(groupEntity.getKey());

    assertThat(Group.fromEntity(groupEntity), equalTo(
        Group.builder()
            .setId(KeyFactory.keyToString(groupEntity.getKey()))
            .setName("updated foo")
            .setDescription("updated bar")
            .setOwnerId("test_user")
            .build()
    ));
  }
}
