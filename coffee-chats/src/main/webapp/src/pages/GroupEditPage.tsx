import React from "react";
import {useParams} from "react-router-dom";
import {Group} from "../entity/Group";
import {postData, useStatefulFetch} from "../util/fetch";
import {Box, Button, Container, Icon, Snackbar, TextField} from "@material-ui/core";

export function GroupEditPage() {
  const {groupId} = useParams();
  const infoUrl = `/api/groupInfo?id=${groupId}`;
  const [group, setGroup] = useStatefulFetch<Group>(infoUrl);
  const [snackbarOpen, setSnackbarOpen] = React.useState(false);

  if (group == null) {
    return null;
  }

  const submit = async () => {
    const data = new Map();
    data.set("name", group.name);
    data.set("description", group.description);
    await postData(infoUrl, data);
    setSnackbarOpen(true);
  };

  return (
      <Box mt={4}>
        <Snackbar
            anchorOrigin={{
              vertical: 'bottom',
              horizontal: 'right',
            }}
            open={snackbarOpen}
            onClose={() => setSnackbarOpen(false)}
            autoHideDuration={2500}
            message="Changes saved"
        />

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
              startIcon={<Icon>save</Icon>}
              onClick={submit}>Save</Button>
        </Container>
      </Box>
  )
}
