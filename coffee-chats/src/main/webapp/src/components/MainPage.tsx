import React, { useState, ChangeEvent, useEffect } from "react";
import {Box, Container, Grid, Icon, IconButton, TextField, Tooltip, Typography,
  Tabs, Tab, Chip} from "@material-ui/core";
import { Autocomplete, createFilterOptions } from '@material-ui/lab'
import {ConnectBackCard} from "./ConnectBackCard";
import { capitaliseEachWord } from "../util/stringUtils";
import { fetchTags } from "../requests/tags";


export function MainPage() {
  const CHATS_VIEW = 0;
  
  const filter = createFilterOptions<string>();
  const [tags, setTags] = useState<string[]>([]);

  const [currView, setCurrView] = useState(CHATS_VIEW);

  useEffect(() => {
    const initFetchTags = (async () => {
      const fetchedTags = await fetchTags();
      setTags(fetchedTags as string[]);
    })
    initFetchTags();
  }, []);

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
              <Autocomplete 
                multiple
                options={tags}
                freeSolo
                autoHighlight
                renderTags={(value: string[], getTagProps) =>
                  value.map((option: string, index: number) => (
                    <Chip variant="outlined" label={option} {...getTagProps({ index })} />
                  ))
                }
                renderInput={(params) => (
                  <TextField 
                    {...params}
                    variant="outlined"
                    label={currView === CHATS_VIEW ?
                      'What do you want to chat about?' :
                      'What are you interested in?'
                    }
                  />
                )}
                filterOptions={(options, params) => {
                  const filtered = filter(options, params);
                  const currInput = capitaliseEachWord(params.inputValue);
          
                  // Suggest the creation of a new tag
                  if (params.inputValue !== '' && !options.includes(currInput)) {
                    filtered.push(currInput);
                  }

                  return filtered;
                }}
              />
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
            <Grid container>
              <Grid item md={4}>
                <ConnectBackCard names={["Natalie Lynn", "Ian Hall"]} tags={["movies"]} />
              </Grid>
            </Grid>
          </Box>
        </Container>
      </Box>
  );
}
