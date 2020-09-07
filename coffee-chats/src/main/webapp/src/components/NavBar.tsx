import React from "react";

import {AppBar, Grid, Icon, IconButton, List, SwipeableDrawer, Toolbar, Tooltip} from "@material-ui/core";
import {ListItemLink} from "./LinkComponents";
import {AuthState, AuthStateContext} from "../entity/AuthState";

interface NavBarButtonsProps {
  onDrawerOpen: () => void;
}

function NavBarButtons({onDrawerOpen}: NavBarButtonsProps) {
  const authState: AuthState = React.useContext(AuthStateContext);

  return (
      <Grid justify="space-between" container>
        <Grid item>
          {/* Left side of the navbar */}
          <IconButton aria-label="Toggle navbar" onClick={onDrawerOpen}>
            <Icon>menu</Icon>
          </IconButton>
        </Grid>

        <Grid item>
          {/* Right side of the navbar */}
          <Grid container spacing={2}>
            <Grid item>
              <Tooltip title="Opt out of new chats">
                <IconButton edge="end" aria-label="Opt out of new chats">
                  <Icon>notifications_none</Icon>
                </IconButton>
              </Tooltip>
            </Grid>

            <Grid item>
              <Tooltip title="Log out">
                <IconButton edge="end" aria-label="Log out" href={authState.logoutUrl}>
                  <Icon>exit_to_app</Icon>
                </IconButton>
              </Tooltip>
            </Grid>
          </Grid>
        </Grid>
      </Grid>
  );
}

function DrawerButtons() {
  return (
      <List>
        <ListItemLink to="/" primary="Main page" />
        <ListItemLink to="/groups" primary="My groups" />
        <ListItemLink to="/upcoming" primary="Upcoming chats" />
        <ListItemLink to="/history" primary="History" />
      </List>
  );
}

export function NavBar() {
  const [drawerOpen, setDrawerOpen] = React.useState(false);

  return (
      <React.Fragment>
        <AppBar position="static" style={{ background: "transparent", boxShadow: "none"}}>
          <Toolbar>
            <NavBarButtons onDrawerOpen={() => setDrawerOpen(true)} />
          </Toolbar>
        </AppBar>
        <SwipeableDrawer
            anchor="left"
            onClose={() => setDrawerOpen(false)}
            onOpen={() => setDrawerOpen(true)}
            open={drawerOpen}>
          <DrawerButtons/>
        </SwipeableDrawer>
      </React.Fragment>
  );
}
