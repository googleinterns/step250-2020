package com.google.step.coffee;

import com.google.step.coffee.data.GroupStore;
import com.google.step.coffee.entity.Group;
import com.google.step.coffee.entity.GroupMembership;
import com.google.step.coffee.entity.GroupMembership.Status;
import com.google.step.coffee.entity.User;
import org.junit.Test;

import static com.google.step.coffee.entity.GroupMembership.Status.*;

public class PermissionCheckerTest extends TestHelper {
  @Test
  public void testEnsureLoggedInWhenLoggedIn() throws Exception {
    PermissionChecker.ensureLoggedIn();
  }

  @Test(expected = HttpError.class)
  public void testEnsureLoggedInWhenLoggedOut() throws Exception {
    helper.setEnvIsLoggedIn(false);
    PermissionChecker.ensureLoggedIn();
  }

  private boolean doGroupTest(Status prev, Status cur) {
    GroupStore groupStore = new GroupStore();

    Group group = groupStore.put(Group.builder()
        .setName("group")
        .setDescription("")
        .setOwnerId("some_other_user")
        .build());

    User user = UserManager.getCurrentUser();

    groupStore.updateMembershipStatus(group, user, prev);

    try {
      PermissionChecker.ensureCanUpdateMembershipStatus(group, user, cur);
      return true;
    } catch (HttpError error) {
      return false;
    }
  }

  private boolean doGroupTestOther(Status my, Status prev, Status cur) {
    GroupStore groupStore = new GroupStore();

    Group group = groupStore.put(Group.builder()
        .setName("group")
        .setDescription("")
        .setOwnerId("some_other_user")
        .build());

    User me = UserManager.getCurrentUser();
    User user = User.builder().setId("yet_another_user").build();

    groupStore.updateMembershipStatus(group, me, my);
    groupStore.updateMembershipStatus(group, user, prev);

    try {
      PermissionChecker.ensureCanUpdateMembershipStatus(group, user, cur);
      return true;
    } catch (HttpError error) {
      return false;
    }
  }

  @Test
  public void testCanJoinGroup() {
    assert doGroupTest(NOT_A_MEMBER, REGULAR_MEMBER);
  }

  @Test
  public void testCanLeaveGroup() {
    assert doGroupTest(REGULAR_MEMBER, NOT_A_MEMBER);
  }

  @Test
  public void testCannotJoinGroupAsAdmin() {
    assert !doGroupTest(NOT_A_MEMBER, ADMINISTRATOR);
    assert !doGroupTest(NOT_A_MEMBER, OWNER);
  }

  @Test
  public void testCannotPromoteThemselves() {
    assert !doGroupTest(REGULAR_MEMBER, ADMINISTRATOR);
    assert !doGroupTest(REGULAR_MEMBER, OWNER);
  }

  @Test
  public void testCannotDemoteThemselves() {
    assert !doGroupTest(ADMINISTRATOR, REGULAR_MEMBER);
    assert !doGroupTest(OWNER, ADMINISTRATOR);
    assert !doGroupTest(OWNER, REGULAR_MEMBER);
  }

  @Test
  public void testCannotEnrollOtherPeople() {
    assert !doGroupTestOther(ADMINISTRATOR, NOT_A_MEMBER, REGULAR_MEMBER);
    assert !doGroupTestOther(OWNER, NOT_A_MEMBER, REGULAR_MEMBER);
    assert !doGroupTestOther(ADMINISTRATOR, NOT_A_MEMBER, ADMINISTRATOR);
    assert !doGroupTestOther(OWNER, NOT_A_MEMBER, ADMINISTRATOR);
    assert !doGroupTestOther(ADMINISTRATOR, NOT_A_MEMBER, OWNER);
    assert !doGroupTestOther(OWNER, NOT_A_MEMBER, OWNER);
  }

  @Test
  public void testRegularUsersCannotKickOtherPeople() {
    assert !doGroupTestOther(REGULAR_MEMBER, REGULAR_MEMBER, NOT_A_MEMBER);
  }

  @Test
  public void testRegularUsersCannotPromote() {
    assert !doGroupTestOther(REGULAR_MEMBER, REGULAR_MEMBER, ADMINISTRATOR);
  }

  @Test
  public void testAdminsCanKickOtherPeople() {
    assert doGroupTestOther(ADMINISTRATOR, REGULAR_MEMBER, NOT_A_MEMBER);
  }

  @Test
  public void testAdminsCannotKickAdmins() {
    assert !doGroupTestOther(ADMINISTRATOR, ADMINISTRATOR, NOT_A_MEMBER);
  }
}
