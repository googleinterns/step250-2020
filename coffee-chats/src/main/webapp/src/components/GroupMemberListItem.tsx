import {
  Avatar, Icon, IconButton, ListItem, ListItemAvatar, ListItemSecondaryAction,
  ListItemText, Menu, MenuItem
} from "@material-ui/core";
import React from "react";
import {Member, MembershipStatus} from "../entity/Member";
import {User} from "../entity/User";

interface GroupMemberListItemProps {
  member: Member;
  status: MembershipStatus;
  setMembershipStatus: (status: MembershipStatus, user: User) => Promise<void>;
}

function convertMembershipStatus(status: MembershipStatus): string {
  switch (status) {
    case "NOT_A_MEMBER":
      return "Not a member";
    case "REGULAR_MEMBER":
      return "Member";
    case "ADMINISTRATOR":
      return "Administrator";
    case "OWNER":
      return "Owner";
  }
}

export function GroupMemberListItem({member, status, setMembershipStatus}: GroupMemberListItemProps) {
  const [menuOpen, setMenuOpen] = React.useState(false);
  const menuButtonRef = React.useRef(null);

  const setMembership = (status: MembershipStatus) => {
    setMenuOpen(false);
    return setMembershipStatus(status, member.user);
  };

  return (
      <ListItem>
        <ListItemAvatar>
          <Avatar src={`https://api.adorable.io/avatars/64/${member.user.id}.png`} />
        </ListItemAvatar>
        <ListItemText primary={`<User id="${member.user.id}">`} secondary={convertMembershipStatus(member.status)} />
        { (((status === "ADMINISTRATOR" && member.status === "REGULAR_MEMBER") || status === "OWNER") && member.status !== "OWNER") ?
            <ListItemSecondaryAction>
              <IconButton
                  edge="end"
                  aria-label="manage"
                  ref={menuButtonRef}
                  onClick={() => setMenuOpen(true)}>
                <Icon>more_vert</Icon>
              </IconButton>
              <Menu
                  anchorEl={menuButtonRef.current}
                  open={menuOpen}
                  onClose={() => setMenuOpen(false)}
              >
                {(status === "OWNER") ?
                    (member.status !== "ADMINISTRATOR") ?
                        <MenuItem onClick={() => setMembership("ADMINISTRATOR")}>Promote to admin</MenuItem> :
                        <MenuItem onClick={() => setMembership("REGULAR_MEMBER")}>Demote to regular member</MenuItem> : null}

                <MenuItem onClick={() => setMembership("NOT_A_MEMBER")}>Remove from group</MenuItem>
              </Menu>
            </ListItemSecondaryAction> : null}
      </ListItem>
  );
}
