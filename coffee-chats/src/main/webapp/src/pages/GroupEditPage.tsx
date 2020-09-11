import React from "react";
import {useParams} from "react-router-dom";
import {Group} from "../entity/Group";
import {getFetchErrorPage, hasFetchFailed, postData, useFetch} from "../util/fetch";
import {Box, Button, Container, Icon, Snackbar, TextField, Typography} from "@material-ui/core";

export function GroupEditPage() {
  const {groupId} = useParams();
  const infoUrl = `/api/groupInfo?id=${groupId}`;
  const group = useFetch<Group>(infoUrl);
  const [snackbarOpen, setSnackbarOpen] = React.useState(false);

  if (hasFetchFailed(group)) {
    return getFetchErrorPage(group);
  }

  const submit = async () => {
    const data = new Map();
    data.set("name", group.value.name);
    data.set("description", group.value.description);
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
              value={group.value.name}
              onChange={(e) => group.set({...group.value, name: e.target.value})}
          />

          <TextField
              fullWidth
              multiline
              margin="normal"
              label="Description"
              variant="outlined"
              value={group.value.description}
              onChange={(e) => group.set({...group.value, description: e.target.value})}
          />

          <Typography variant="body2" color="textSecondary">
            Markdown is supported
          </Typography>

          <Box mt={1}>
            <Button
                variant="contained"
                color="primary"
                startIcon={<Icon>save</Icon>}
                onClick={submit}>Save</Button>
          </Box>
        </Container>
      </Box>
  )
}
