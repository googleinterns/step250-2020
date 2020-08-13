import React from "react";

import {AppBar, Grid, Icon, IconButton, Toolbar, Tooltip} from "@material-ui/core";

export function NavBar() {
  return (
      <AppBar position="static" style={{ background: "transparent", boxShadow: "none"}}>
        <Toolbar>
          <Grid justify="space-between" container>
            <Grid item>
              {/* Left side of the navbar */}
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
                    <IconButton edge="end" aria-label="Log out">
                      <Icon>exit_to_app</Icon>
                    </IconButton>
                  </Tooltip>
                </Grid>
              </Grid>
            </Grid>
          </Grid>
        </Toolbar>
      </AppBar>
  )
}