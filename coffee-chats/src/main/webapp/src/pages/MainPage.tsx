import React, {useState} from "react";
import {Box, Container, Grid, Icon, IconButton, Tooltip, Typography} from "@material-ui/core";
import {FindChatCard} from "../components/FindChatCard";
import {TagsInput} from "../components/TagsInput";
import {Group} from "../entity/Group";
import {getFetchErrorPage, hasFetchFailed, useFetch} from "../util/fetch";
import {GroupCard} from "../components/GroupCard";

export function MainPage() {
  const [tags, setTags] = useState<string[]>([]);
  const groups = useFetch<Group[]>("/api/groupList?all=true");

  if (hasFetchFailed(groups)) {
    return getFetchErrorPage(groups);
  }

  const tagsSet = new Set(tags);

  const suggestGroups = groups.value.filter(group =>
      group.tags.filter(tag => tagsSet.has(tag)).length > 0
  );

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
                    aria-label="chat about any topic">
                  <Icon>casino</Icon>
                </IconButton>
              </Tooltip>
            </Grid>
          </Grid>

          <Box mt={2}>
            <Grid container spacing={4}>
              <Grid item xs={12}>
                <FindChatCard interests={tags}/>
              </Grid>
              {suggestGroups.map(group =>
                  <Grid item md={4} key={group.id}>
                    <GroupCard group={group} withDescription={false} />
                  </Grid>
              )}
            </Grid>
          </Box>
        </Container>
      </Box>
  );
}
