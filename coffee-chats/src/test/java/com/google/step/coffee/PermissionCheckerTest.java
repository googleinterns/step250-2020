package com.google.step.coffee;

import com.google.step.coffee.data.GroupStore;
import com.google.step.coffee.entity.Group;
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

  private boolean canChangeOwnMembershipStatus(Status prev, Status cur) {
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

  private boolean canChangeOthersMembershipStatus(Status my, Status prev, Status cur) {
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
    assert canChangeOwnMembershipStatus(NOT_A_MEMBER, REGULAR_MEMBER);
  }

  @Test
  public void testCanLeaveGroup() {
    assert canChangeOwnMembershipStatus(REGULAR_MEMBER, NOT_A_MEMBER);
  }

  @Test
  public void testAdminCanLeaveGroup() {
    assert canChangeOwnMembershipStatus(ADMINISTRATOR, NOT_A_MEMBER);
  }

  @Test
  public void testOwnerCannotLeaveGroup() {
    assert !canChangeOwnMembershipStatus(OWNER, NOT_A_MEMBER);
  }

  @Test
  public void testCannotJoinGroupAsAdmin() {
    assert !canChangeOwnMembershipStatus(NOT_A_MEMBER, ADMINISTRATOR);
    assert !canChangeOwnMembershipStatus(NOT_A_MEMBER, OWNER);
  }

  @Test
  public void testCannotPromoteThemselves() {
    assert !canChangeOwnMembershipStatus(REGULAR_MEMBER, ADMINISTRATOR);
    assert !canChangeOwnMembershipStatus(REGULAR_MEMBER, OWNER);
    assert !canChangeOwnMembershipStatus(ADMINISTRATOR, OWNER);
  }

  @Test
  public void testCannotDemoteThemselves() {
    assert !canChangeOwnMembershipStatus(ADMINISTRATOR, REGULAR_MEMBER);
    assert !canChangeOwnMembershipStatus(OWNER, ADMINISTRATOR);
    assert !canChangeOwnMembershipStatus(OWNER, REGULAR_MEMBER);
  }

  @Test
  public void testCannotEnrollOtherPeople() {
    assert !canChangeOthersMembershipStatus(ADMINISTRATOR, NOT_A_MEMBER, REGULAR_MEMBER);
    assert !canChangeOthersMembershipStatus(OWNER, NOT_A_MEMBER, REGULAR_MEMBER);
    assert !canChangeOthersMembershipStatus(ADMINISTRATOR, NOT_A_MEMBER, ADMINISTRATOR);
    assert !canChangeOthersMembershipStatus(OWNER, NOT_A_MEMBER, ADMINISTRATOR);
    assert !canChangeOthersMembershipStatus(ADMINISTRATOR, NOT_A_MEMBER, OWNER);
    assert !canChangeOthersMembershipStatus(OWNER, NOT_A_MEMBER, OWNER);
  }

  @Test
  public void testRegularUsersCannotKickOtherPeople() {
    assert !canChangeOthersMembershipStatus(REGULAR_MEMBER, REGULAR_MEMBER, NOT_A_MEMBER);
  }

  @Test
  public void testRegularUsersCannotPromote() {
    assert !canChangeOthersMembershipStatus(REGULAR_MEMBER, REGULAR_MEMBER, ADMINISTRATOR);
  }

  @Test
  public void testAdminsCanKickOtherPeople() {
    assert canChangeOthersMembershipStatus(ADMINISTRATOR, REGULAR_MEMBER, NOT_A_MEMBER);
  }

  @Test
  public void testAdminsCannotKickAdmins() {
    assert !canChangeOthersMembershipStatus(ADMINISTRATOR, ADMINISTRATOR, NOT_A_MEMBER);
  }
}
