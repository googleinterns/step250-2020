import React, {useState} from "react";
import {useParams} from "react-router-dom";
import {Box, Button, CardActions, Container} from "@material-ui/core";
import {postData, useFetch, useFetchOnce} from "../util/fetch";
import {Group} from "../entity/Group";
import {useRenderLink} from "../components/LinkComponents";
import {GroupCard} from "../components/GroupCard";
import {GroupDeleteDialog} from "../components/GroupDeleteDialog";
import {GroupMembersList} from "../components/GroupMembersList";
import {Member, MembershipStatus} from "../entity/Member";
import {AuthState, AuthStateContext} from "../entity/AuthState";

export function GroupInfoPage() {
  const authState: AuthState = React.useContext(AuthStateContext);
  const {groupId} = useParams();
  const editLink = useRenderLink(`/group/${groupId}/edit`);
  const group: Group = useFetchOnce(`/api/groupInfo?id=${groupId}`);
  const [members, updateMembers]: [Member[], () => void] = useFetch(`/api/groupMembers?id=${groupId}`);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  if (group == null || members == null) {
    return null;
  }

  const status = members.find(member => member.user.id === authState.user.id)?.status || "NOT_A_MEMBER";

  const setMembershipStatus = async (status: MembershipStatus, user = authState.user) => {
    const data = new Map();
    data.set("id", groupId);
    data.set("user", user.id);
    data.set("status", status);
    await postData(`/api/groupMembers`, data);
    updateMembers();
  };

  return (
      <Box mt={4}>
        <GroupDeleteDialog group={group} open={deleteDialogOpen} setOpen={setDeleteDialogOpen} />
        <Container maxWidth="md">
          <GroupCard group={group} clickable={false}>
            <CardActions>
              {(status === "ADMINISTRATOR" || status === "OWNER") &&
                  <React.Fragment>
                    <Button component={editLink}>Edit</Button>
                    <Button onClick={() => setDeleteDialogOpen(true)} color="secondary">Delete</Button>
                  </React.Fragment>
              }

              {(status === "REGULAR_MEMBER" || status === "ADMINISTRATOR") &&
                  <Button onClick={() => setMembershipStatus("NOT_A_MEMBER")} color="secondary">Leave</Button>
              }

              {(status === "NOT_A_MEMBER") &&
                  <Button onClick={() => setMembershipStatus("REGULAR_MEMBER")}>Join</Button>
              }
            </CardActions>
          </GroupCard>
          <GroupMembersList
              members={members}
              status={status}
              setMembershipStatus={setMembershipStatus}
          />
        </Container>
      </Box>
  )
}
