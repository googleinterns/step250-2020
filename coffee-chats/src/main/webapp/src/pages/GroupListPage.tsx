import React from "react";
import {Box, Container, Grid, TextField} from "@material-ui/core";
import {GroupCard} from "../components/GroupCard";

export function GroupListPage() {
  return (
      <Box mt={4}>
        <Container maxWidth="md">
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                  fullWidth
                  variant="outlined"
                  label="Search your groups"/>
            </Grid>
            {["Board Games Dublin", "Book Club", "Mountain Climbers @ Home"].map(name =>
                <Grid item xs={12}>
                  <GroupCard name={name}/>
                </Grid>
            )}
          </Grid>
        </Container>
      </Box>
  );
}
