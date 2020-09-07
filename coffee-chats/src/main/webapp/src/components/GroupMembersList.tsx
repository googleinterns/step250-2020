import React from "react";
import {Member, MembershipStatus} from "../entity/Member";
import {Box, Card, CardContent, List, Typography} from "@material-ui/core";
import {GroupMemberListItem} from "./GroupMemberListItem";

interface GroupMembersListProps {
  members: Member[];
  status: MembershipStatus;
}

export function GroupMembersList({members, status}: GroupMembersListProps) {
  return (
      <Box mt={2}>
        <Card>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Members
            </Typography>

            <List>
              {members.map(member => (
                  <GroupMemberListItem member={member} status={status} />
              ))}
            </List>
          </CardContent>
        </Card>
      </Box>
  )
}
