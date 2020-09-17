import React from "react";
import {Box, Container, Fab, Grid, Icon, TextField, Tooltip} from "@material-ui/core";
import {GroupCard} from "../components/GroupCard";
import {makeStyles} from "@material-ui/core/styles";
import {GroupCreateDialog} from "../components/GroupCreateDialog";
import {getFetchErrorPage, hasFetchFailed, useFetch} from "../util/fetch";
import {Group} from "../entity/Group";
import {AuthState, AuthStateContext} from "../entity/AuthState";

const useStyles = makeStyles((theme) => ({
  extendedIcon: {
    marginRight: theme.spacing(.5),
  },
  shrink: {
    flexGrow: 0
  }
}));

export function GroupListPage() {
  const classes = useStyles();
  const authState: AuthState = React.useContext(AuthStateContext);
  const groups = useFetch<Group[]>("/api/groupList");
  const [createDialogOpen, setCreateDialogOpen] = React.useState(false);

  if (hasFetchFailed(groups)) {
    return getFetchErrorPage(groups);
  }

  return (
      <React.Fragment>
        <GroupCreateDialog
            open={createDialogOpen}
            setOpen={setCreateDialogOpen}
            onSubmit={groups.reload}/>
        <Box mt={4}>
          <Container maxWidth="md">
            <Grid container spacing={2}>
              <Grid item xs>
                <TextField
                    fullWidth
                    variant="outlined"
                    label="Search your groups"/>
              </Grid>
              <Grid item xs className={classes.shrink}>
                <Tooltip
                    title={authState.oauthAuthorized ? "" :
                        "You need to authorize the app with your Google Calendar before you can create groups"}>
                  <span>
                    <Fab
                        variant="extended"
                        color="primary"
                        onClick={() => setCreateDialogOpen(true)}
                        disabled={!authState.oauthAuthorized}
                    >
                      <Icon className={classes.extendedIcon}>add</Icon>
                      create
                    </Fab>
                  </span>
                </Tooltip>
              </Grid>
              {groups.value.map(group =>
                  <Grid item xs={12} key={group.id}>
                    <GroupCard group={group} withDescription={false}/>
                  </Grid>
              )}
            </Grid>
          </Container>
        </Box>
      </React.Fragment>
  );
}
