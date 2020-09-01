import React from "react";
import ReactMarkdown from "react-markdown";
import {useParams} from "react-router-dom";
import {Box, Button, Card, CardActions, CardContent, Container, Typography} from "@material-ui/core";
import {useFetchOnce} from "../util/fetch";
import {Group} from "../entity/Group";
import {useRenderLink} from "../components/LinkComponents";

export function GroupInfoPage() {
  const {groupId} = useParams();
  const editLink = useRenderLink(`/group/${groupId}/edit`);
  const group: Group = useFetchOnce(`/api/groupInfo?id=${groupId}`);

  if (group == null) {
    return null;
  }

  return (
      <Box mt={4}>
        <Container maxWidth="md">
          <Card>
            <CardContent>
              <Typography variant="h4" gutterBottom>{group.name}</Typography>
              <ReactMarkdown source={group.description} />
            </CardContent>
            <CardActions>
              <Button component={editLink} >Edit</Button>
            </CardActions>
          </Card>
        </Container>
      </Box>
  )
}
