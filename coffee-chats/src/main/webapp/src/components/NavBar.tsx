import React from "react";

import {AppBar, Grid, Icon, IconButton, Toolbar, Tooltip} from "@material-ui/core";

export function NavBar() {
  return (
      <AppBar position="static" style={{ background: "transparent", boxShadow: "none"}}>
        <Toolbar>
          <Grid justify="space-between" container>
            <Grid item>
            </Grid>
            <Grid item>
              <Tooltip title="Log out">
                <IconButton edge="end">
                  <Icon>exit_to_app</Icon>
                </IconButton>
              </Tooltip>
            </Grid>
          </Grid>
        </Toolbar>
      </AppBar>
  )
}