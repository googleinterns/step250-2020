import React from "react";
import {useParams} from "react-router-dom";
import {Box, Button, Card, CardActions, CardContent, Container, Typography} from "@material-ui/core";
import {useFetchOnce} from "../util/fetch";
import {Group} from "../entity/Group";

export function GroupInfoPage() {
  const {groupId} = useParams();
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
              <Typography variant="body2">{group.description}</Typography>
            </CardContent>
            <CardActions>
              <Button size="small">Edit</Button>
            </CardActions>
          </Card>
        </Container>
      </Box>
  )
}
