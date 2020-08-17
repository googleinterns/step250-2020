import React from "react";
import {Box, Container, Fab, Grid, Icon, TextField} from "@material-ui/core";
import {GroupCard} from "./GroupCard";
import {makeStyles} from "@material-ui/core/styles";
import {GroupCreateDialog} from "./GroupCreateDialog";

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
              {["Board Games Dublin", "Book Club", "Mountain Climbers @ Home"].map(name =>
                  <Grid item xs={12} key={name}>
                    <GroupCard name={name}/>
                  </Grid>
              )}
            </Grid>
          </Container>
        </Box>
      </React.Fragment>
  );
}
