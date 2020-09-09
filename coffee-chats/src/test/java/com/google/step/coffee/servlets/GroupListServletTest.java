package com.google.step.coffee.servlets;

import com.google.appengine.api.datastore.*;
import com.google.step.coffee.JsonServletRequest;
import com.google.step.coffee.TestHelper;
import com.google.step.coffee.data.GroupStore;
import com.google.step.coffee.entity.Group;
import com.google.step.coffee.entity.GroupMembership;
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
    groupEntity.setProperty("description", new Text("bar"));
    groupEntity.setProperty("ownerId", "test_user");
    datastore.put(groupEntity);

    Entity membershipEntity = new Entity("groupMembership");
    membershipEntity.setProperty("group", groupEntity.getKey());
    membershipEntity.setProperty("user", "test_user");
    membershipEntity.setProperty("status", GroupMembership.Status.OWNER.toString());
    datastore.put(membershipEntity);

    assertThat(getGroupList(), equalTo(Collections.singletonList(
        Group.builder()
            .setId(KeyFactory.keyToString(groupEntity.getKey()))
            .setName("foo")
            .setDescription("bar")
            .setOwnerId("test_user")
            .build()
    )));
  }

  @Test
  public void testOtherPeoplesGroupsNotVisible() throws Exception {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Entity groupEntity = new Entity("group");
    groupEntity.setProperty("name", "foo");
    groupEntity.setProperty("description", new Text("bar"));
    groupEntity.setProperty("ownerId", "not_a_test_user");
    datastore.put(groupEntity);

    assertThat(getGroupList(), equalTo(new ArrayList<>()));
  }
}
