import {
  Avatar, Icon, IconButton, ListItem, ListItemAvatar, ListItemSecondaryAction,
  ListItemText, Menu, MenuItem
} from "@material-ui/core";
import React from "react";
import {Member, MembershipStatus} from "../entity/Member";

interface GroupMemberListItemProps {
  member: Member;
  status: MembershipStatus;
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

export function GroupMemberListItem({member, status}: GroupMemberListItemProps) {
  const [menuOpen, setMenuOpen] = React.useState(false);
  const menuButtonRef = React.useRef(null);

  return (
      <ListItem>
        <ListItemAvatar>
          <Avatar src={`https://api.adorable.io/avatars/64/${member.user.id}.png`} />
        </ListItemAvatar>
        <ListItemText primary={`<User id="${member.user.id}">`} secondary={convertMembershipStatus(member.status)} />
        { ((status === "ADMINISTRATOR" || status === "OWNER") && member.status !== "OWNER") ?
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
                        <MenuItem>Promote to admin</MenuItem> :
                        <MenuItem>Demote to regular user</MenuItem> : null}

                <MenuItem>Remove from group</MenuItem>
              </Menu>
            </ListItemSecondaryAction> : null}
      </ListItem>
  );
}
