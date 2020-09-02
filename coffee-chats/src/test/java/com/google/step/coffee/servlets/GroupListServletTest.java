package com.google.step.coffee.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.TestHelper;
import com.google.step.coffee.entity.Group;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class GroupListServletTest extends TestHelper {
  @SuppressWarnings("unchecked")
  private List<Group> getGroupList() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    JsonServletRequest jsonServletRequest = new JsonServletRequest(request);
    return (List<Group>) new GroupListServlet().get(jsonServletRequest);
  }

  @Test
  public void testEmpty() throws Exception {
    assertThat(getGroupList(), equalTo(new ArrayList<>()));
  }

  @Test
  public void testNonEmpty() throws Exception {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Entity groupEntity = new Entity("group");
    groupEntity.setProperty("name", "foo");
    groupEntity.setProperty("description", "bar");
    groupEntity.setProperty("ownerId", "test_user");
    datastore.put(groupEntity);

    assertThat(getGroupList(), equalTo(Collections.singletonList(
        Group.builder()
            .setId(KeyFactory.keyToString(groupEntity.getKey()))
            .setName("foo")
            .setDescription("bar")
            .setOwnerId("test_user")
            .build()
    )));
  }
}
