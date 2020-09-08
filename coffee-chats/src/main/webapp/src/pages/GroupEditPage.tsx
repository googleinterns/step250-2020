import React from "react";
import {useParams} from "react-router-dom";
import {Group} from "../entity/Group";
import {postData, useStatefulFetch} from "../util/fetch";
import {Box, Button, Container, Icon, Snackbar, TextField, Typography} from "@material-ui/core";
import {TagsInput} from "../components/TagsInput";

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
    data.set("tags", JSON.stringify(group.tags));
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

          <Typography variant="body2" color="textSecondary" gutterBottom>
            Markdown is supported
          </Typography>

          <TagsInput label="Tags" tags={group.tags} setTags={(tags) => setGroup({...group, tags})}/>

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
