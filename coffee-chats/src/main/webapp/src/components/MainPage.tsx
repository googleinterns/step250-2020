import React, { useState } from "react";
import {Box, Button, Container, Grid, Icon, IconButton, InputAdornment,
  TextField, Tooltip, Typography} from "@material-ui/core";
import {ConnectBackCard} from "./ConnectBackCard";
import { FindChatCard } from "./FindChatCard";

export function MainPage() {
  const [searchTerms, setSearchTerms] = useState<string[]>([]);
  return (
      <Box mt={4}>
        <Container maxWidth="md">
          <Typography variant="h3" align="center" gutterBottom>
            Coffee Chats
          </Typography>

          <TextField
              variant="outlined"
              placeholder="What do you want to chat about?"
              fullWidth={true}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                setSearchTerms(event.target.value.split(" "))
              }}
              InputProps={{endAdornment:
                    <InputAdornment position="end">
                      <Tooltip title="Any topic">
                        <IconButton
                            aria-label="chat about any topic">
                          <Icon>casino</Icon>
                        </IconButton>
                      </Tooltip>
                    </InputAdornment>}}
          />

          <Box mt={2}>
            <Grid container justify="center">
              <Button
                  variant="text"
                  startIcon={<Icon>explore</Icon>}
                  color="primary"
                  size="large">
                Explore Groups
              </Button>
            </Grid>
          </Box>

          <Box mt={2}>
            <Grid container spacing={4}>
              <Grid item xs={12}>
                <FindChatCard interests={searchTerms}/>
              </Grid>
              <Grid item md={4}>
                <ConnectBackCard names={["Natalie Lynn", "Ian Hall"]} tags={["movies"]} />
              </Grid>
            </Grid>
          </Box>
        </Container>
      </Box>
  );
}
