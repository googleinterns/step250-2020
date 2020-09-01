import React from "react";
import {useParams} from "react-router-dom";
import {Group} from "../entity/Group";
import {useStatefulFetch} from "../util/fetch";
import {Box, Button, Container, Icon, TextField} from "@material-ui/core";

export function GroupEditPage() {
  const {groupId} = useParams();
  const [group, setGroup] = useStatefulFetch<Group>(`/api/groupInfo?id=${groupId}`);

  if (group == null) {
    return null;
  }

  return (
      <Box mt={4}>
        <Container maxWidth="md">
          <TextField
            fullWidth
            margin="normal"
            label="Group name"
            variant="outlined"
            required
            value={group.name}
            onChange={(e) => setGroup({...group, name: e.target.value})}
          />

          <TextField
            fullWidth
            multiline
            margin="normal"
            label="Description"
            variant="outlined"
            value={group.description}
            onChange={(e) => setGroup({...group, description: e.target.value})}
          />

          <Button
              variant="contained"
              color="primary"
              startIcon={<Icon>save</Icon>}>Save</Button>
        </Container>
      </Box>
  )
}
