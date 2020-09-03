import React from "react";
import {Member} from "../entity/Member";
import {Avatar, Box, Card, CardContent, List, ListItem,
  ListItemAvatar, ListItemText, Typography} from "@material-ui/core";

interface GroupMembersListProps {
  members: Member[];
}

export function GroupMembersList({members}: GroupMembersListProps) {
  return (
      <Box mt={2}>
        <Card>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Members
            </Typography>

            <List>
              {members.map(member => (
                <ListItem>
                  <ListItemAvatar>
                    <Avatar src={`https://api.adorable.io/avatars/64/${member.user.id}.png`} />
                  </ListItemAvatar>
                  <ListItemText primary={`<User id="${member.user.id}">`} secondary={member.status} />
                </ListItem>
              ))}
            </List>
          </CardContent>
        </Card>
      </Box>
  )
}
