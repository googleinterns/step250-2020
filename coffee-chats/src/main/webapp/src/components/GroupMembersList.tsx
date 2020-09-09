import React from "react";
import {Member, MembershipStatus} from "../entity/Member";
import {Box, Card, CardContent, List, Typography} from "@material-ui/core";
import {GroupMemberListItem} from "./GroupMemberListItem";
import {User} from "../entity/User";

interface GroupMembersListProps {
  members: Member[];
  status: MembershipStatus;
  setMembershipStatus: (status: MembershipStatus, user: User) => Promise<void>;
}

export function GroupMembersList({members, status, setMembershipStatus}: GroupMembersListProps) {
  return (
      <Box mt={2}>
        <Card>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Members
            </Typography>

            <List>
              {members.map(member => (
                  <GroupMemberListItem
                      key={member.user.id}
                      member={member}
                      status={status}
                      setMembershipStatus={setMembershipStatus}
                  />
              ))}
            </List>
          </CardContent>
        </Card>
      </Box>
  )
}
