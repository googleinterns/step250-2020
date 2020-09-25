import React, {useState} from "react";
import {Box, Container, Grid, Icon, IconButton, Tooltip, Typography} from "@material-ui/core";
import {FindChatCard} from "../components/FindChatCard";
import {TagsInput} from "../components/TagsInput";
import {Group} from "../entity/Group";
import {getFetchErrorPage, hasFetchFailed, useFetch} from "../util/fetch";
import {GroupCard} from "../components/GroupCard";
import {fromUnixTime} from "date-fns";

interface GroupSuggestion {
  group: Group;
  nearestEvent: number;
}

export function MainPage() {
  const [tags, setTags] = useState<string[]>([]);
  const suggestions = useFetch<GroupSuggestion[]>(`/api/groupSuggest?tags=${JSON.stringify(tags)}`);
  const groups = useFetch<Group[]>(`/api/groupList?all=true`);

  if (hasFetchFailed(suggestions, groups)) {
    return getFetchErrorPage(suggestions, groups);
  }

  return (
      <Box mt={4}>
        <Container maxWidth="md">
          <Typography variant="h3" align="center" gutterBottom>
            Coffee Chats
          </Typography>

          <Grid container alignItems="center" justify="space-around" spacing={1}>
            <Grid item xs={10} sm={11}>
              <TagsInput
                  tags={tags}
                  setTags={setTags}
                  label="What do you want to chat about?"
                  suggestGroups={groups.value}
              />
            </Grid>

            <Grid item xs={2} sm={1}>
              <Tooltip title="Any topic">
                <IconButton
                    aria-label="chat about any topic"
                    onClick={() => {
                      if (!tags.includes('Random')) {
                        setTags([...tags, 'Random']);
                      }
                    }}
                >
                  <Icon>casino</Icon>
                </IconButton>
              </Tooltip>
            </Grid>
          </Grid>

          <Box mt={2}>
            <Grid container spacing={4}>
              <Grid item xs={12}>
                <FindChatCard interests={tags} />
              </Grid>
              {suggestions.value.map(({group, nearestEvent}) =>
                  <Grid item md={4} key={group.id}>
                    <GroupCard
                        group={group}
                        withDescription={false}
                        nearestEvent={(nearestEvent === 0) ? null : fromUnixTime(nearestEvent)}
                    />
                  </Grid>
              )}
            </Grid>
          </Box>
        </Container>
      </Box>
  );
}
