import React from "react";
import {useParams} from "react-router-dom";
import {Box, Button, Card, CardActions, CardContent, Container, Typography} from "@material-ui/core";
import {useFetch} from "../util/fetch";
import {Group} from "../entity/Group";

export function GroupInfoPage() {
  const {groupId} = useParams();
  const group: Group = useFetch(`/api/groupInfo?id=${groupId}`)[0];

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
