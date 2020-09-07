import React, {useState} from "react";
import {useParams} from "react-router-dom";
import {Box, Button, CardActions, Container} from "@material-ui/core";
import {useFetchOnce} from "../util/fetch";
import {Group} from "../entity/Group";
import {useRenderLink} from "../components/LinkComponents";
import {GroupCard} from "../components/GroupCard";
import {GroupDeleteDialog} from "../components/GroupDeleteDialog";
import {GroupMembersList} from "../components/GroupMembersList";
import {Member} from "../entity/Member";
import {AuthState, AuthStateContext} from "../entity/AuthState";

export function GroupInfoPage() {
  const authState: AuthState = React.useContext(AuthStateContext);
  const {groupId} = useParams();
  const editLink = useRenderLink(`/group/${groupId}/edit`);
  const group: Group = useFetchOnce(`/api/groupInfo?id=${groupId}`);
  const members: Member[] = useFetchOnce(`/api/groupMembers?id=${groupId}`);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  if (group == null || members == null) {
    return null;
  }

  const status = members.find(member => member.user.id == authState.user.id)?.status || "NOT_A_MEMBER";

  return (
      <Box mt={4}>
        <GroupDeleteDialog group={group} open={deleteDialogOpen} setOpen={setDeleteDialogOpen} />
        <Container maxWidth="md">
          <GroupCard group={group} clickable={false}>
            <CardActions>
              {(status == "ADMINISTRATOR" || status == "OWNER") ?
                  <React.Fragment>
                    <Button component={editLink}>Edit</Button>
                    <Button onClick={() => setDeleteDialogOpen(true)} color="secondary">Delete</Button>
                  </React.Fragment> : null
              }

              {(status == "MEMBER" || status == "ADMINISTRATOR") ?
                  <Button color="secondary">Leave</Button> : null
              }
            </CardActions>
          </GroupCard>
          <GroupMembersList members={members} />
        </Container>
      </Box>
  )
}
