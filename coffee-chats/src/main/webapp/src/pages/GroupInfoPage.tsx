import React, {useState} from "react";
import {useParams} from "react-router-dom";
import {Box, Button, CardActions, Container, Tooltip} from "@material-ui/core";
import {getFetchErrorPage, hasFetchFailed, postData, useFetch} from "../util/fetch";
import {Group} from "../entity/Group";
import {Event} from "../entity/Event";
import {useRenderLink} from "../components/LinkComponents";
import {GroupCard} from "../components/GroupCard";
import {GroupDeleteDialog} from "../components/GroupDeleteDialog";
import {GroupMembersList} from "../components/GroupMembersList";
import {Member, MembershipStatus} from "../entity/Member";
import {AuthState, AuthStateContext} from "../entity/AuthState";
import {CreateEventCard} from "../components/CreateEventCard";
import {EventCard} from "../components/EventCard";

export function GroupInfoPage() {
  const authState: AuthState = React.useContext(AuthStateContext);
  const {groupId} = useParams();
  const editLink = useRenderLink(`/group/${groupId}/edit`);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  const group = useFetch<Group>(`/api/groupInfo?id=${groupId}`);
  const members = useFetch<Member[]>(`/api/groupMembers?id=${groupId}`);
  const events = useFetch<Event[]>(`/api/eventList?groups=${JSON.stringify([groupId])}`);

  if (hasFetchFailed(group, members, events)) {
    return getFetchErrorPage(group, members, events);
  }

  const setMembershipStatus = async (status: MembershipStatus, user = authState.user) => {
    const data = new Map();
    data.set("id", groupId);
    data.set("user", user.id);
    data.set("status", status);
    await postData(`/api/groupMembers`, data);
    members.reload();
  };

  const status = members.value.find(member => member.user.id === authState.user.id)?.status || "NOT_A_MEMBER";

  return (
      <Box mt={4}>
        <GroupDeleteDialog group={group.value} open={deleteDialogOpen} setOpen={setDeleteDialogOpen}/>
        <Container maxWidth="md">
          <GroupCard group={group.value} clickable={false}>
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
              <Tooltip
                  title={authState.oauthAuthorized ? "" :
                      "You need to authorize the app with your Google Calendar before you can join groups"}>
                <span>
                  <Button
                      onClick={() => setMembershipStatus("REGULAR_MEMBER")}
                      disabled={!authState.oauthAuthorized}
                  >
                      Join
                  </Button>
                </span>
              </Tooltip>
              }
            </CardActions>
          </GroupCard>
          {(status === "ADMINISTRATOR" || status === "OWNER") && <CreateEventCard group={group.value} />}
          {events.value.map(event => <EventCard event={event} key={event.id} />)}
          <GroupMembersList
              members={members.value}
              status={status}
              setMembershipStatus={setMembershipStatus}
          />
        </Container>
      </Box>
  )
}
