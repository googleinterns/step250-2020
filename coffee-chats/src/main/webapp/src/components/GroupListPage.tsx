import React from "react";
import {Box, Container, Fab, Grid, Icon, TextField} from "@material-ui/core";
import {GroupCard} from "./GroupCard";
import {makeStyles} from "@material-ui/core/styles";
import {GroupCreateDialog} from "./GroupCreateDialog";
import {useFetch} from "../util/fetch";
import {Group} from "../entity/Group";

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
  const groups: Group[] = useFetch("/api/groupList") || [];
  const [createDialogOpen, setCreateDialogOpen] = React.useState(false);

  return (
      <React.Fragment>
        <GroupCreateDialog open={createDialogOpen} setOpen={setCreateDialogOpen} />
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
                <Fab
                    variant="extended"
                    color="primary"
                    onClick={() => setCreateDialogOpen(true)}>
                  <Icon className={classes.extendedIcon}>add</Icon>
                  create
                </Fab>
              </Grid>
              {groups.map(group =>
                  <Grid item xs={12} key={group.id}>
                    <GroupCard group={group} />
                  </Grid>
              )}
            </Grid>
          </Container>
        </Box>
      </React.Fragment>
  );
}
