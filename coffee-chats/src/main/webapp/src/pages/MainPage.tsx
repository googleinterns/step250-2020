import React, {useState, ChangeEvent} from "react";
import {Box, Container, Grid, Icon, IconButton, Tooltip, Typography, Tabs, Tab} from "@material-ui/core";
import {ConnectBackCard} from "../components/ConnectBackCard";
import {FindChatCard} from "../components/FindChatCard";
import {TagsInput} from "../components/TagsInput";

export function MainPage() {
  const CHATS_VIEW = 0;

  const [currView, setCurrView] = useState(CHATS_VIEW);
  const [tags, setTags] = useState<string[]>([]);

  return (
      <Box mt={4}>
        <Container maxWidth="md">
          <Typography variant="h3" align="center" gutterBottom>
            Coffee Chats
          </Typography>

          <Tabs
            value={currView}
            onChange={(_event: ChangeEvent<{}>, newView: number) => setCurrView(newView)}
            indicatorColor="primary"
            textColor="primary"
            variant="fullWidth"
            centered
          >
            <Tab label="Chats" />
            <Tab label="Groups" />
          </Tabs>

          <br />

          <Grid container alignItems="center" justify="space-around" spacing={1}>
            <Grid item xs={10} sm={11}>
              <TagsInput
                  tags={tags}
                  setTags={setTags}
                  label={currView === CHATS_VIEW ?
                  'What do you want to chat about?' :
                  'What are you interested in?'
              } />
            </Grid>

            <Grid item xs={2} sm={1}>
              {currView === CHATS_VIEW ?
                <Tooltip title="Any topic">
                  <IconButton
                      aria-label="chat about any topic">
                    <Icon>casino</Icon>
                  </IconButton>
                </Tooltip> :
                <Tooltip title="Search Filters">
                  <IconButton 
                      aria-label="filter search results">
                    <Icon>tune</Icon>
                  </IconButton>
                </Tooltip>
              }
            </Grid>
          </Grid>

          <Box mt={2}>
            <Grid container spacing={4}>
              <Grid item xs={12}>
                <FindChatCard interests={tags} />
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
