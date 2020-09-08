import React from "react";
import {Autocomplete, createFilterOptions} from "@material-ui/lab";
import {Chip, Grid, Icon, TextField} from "@material-ui/core";
import {capitaliseEachWord} from "../util/stringUtils";
import {fetchTags} from "../util/tagsRequest";
import {Group} from "../entity/Group";

interface TagsInputProps {
  label: string;
  tags: string[];
  setTags: (value: string[]) => void;
  suggestGroups: Group[];
}

interface Suggestion {
  name: string;
  group: Group | null;
}

export function TagsInput({label, tags, setTags, suggestGroups}: TagsInputProps) {
  const filter = createFilterOptions<Suggestion>();
  const [tagOptions, setTagOptions] = React.useState<string[]>([]);
  const [suggestions, setSuggestions] = React.useState<Suggestion[]>([]);

  React.useEffect(() => {
    setSuggestions(suggestGroups.map(group => ({
      name: group.name,
      group
    } as Suggestion)).concat(tagOptions.map(tag => ({
      name: tag,
      group: null
    }) as Suggestion)));
  }, [tagOptions, suggestGroups]);

  React.useEffect(() => {
    const initFetchTags = (async () => {
      setTagOptions(await fetchTags());
    });
    initFetchTags();
  }, []);

  return (
      <Autocomplete
          multiple
          value={tags.map(name => ({name} as Suggestion))}
          options={suggestions}
          getOptionLabel={option => option.name}
          freeSolo
          autoHighlight
          onChange={(_event: any, newValue: any[]) => {
            const value = newValue as Suggestion[];
            setTags(value.map(suggestion => {
              if (suggestion.group) {
                window.location.hash = `#/group/${suggestion.group.id}`;
              }
              return suggestion.name;
            }));
          }}
          renderTags={(value: Suggestion[], getTagProps) =>
              value.map((option: Suggestion, index: number) => (
                  <Chip variant="outlined" label={option.name} {...getTagProps({ index })} />
              ))
          }
          renderOption={(option, state) => {
            return (
                <Grid container spacing={2}>
                  <Grid item>
                    <Icon>{option.group ? "people" : "local_offer"}</Icon>
                  </Grid>
                  <Grid item xs>
                    {option.name}
                  </Grid>
                </Grid>
            );
          }}
          renderInput={(params) => (
              <TextField
                  {...params}
                  variant="outlined"
                  label={label}
              />
          )}
          filterOptions={(options, params) => {
            let filtered = filter(options, params);
            const currInput = capitaliseEachWord(params.inputValue);

            if (currInput === "") {
              filtered = filtered.filter(suggestion => suggestion.group === null);
            }

            // Suggest the creation of a new tag
            if (params.inputValue !== '' && !options.map(x => x.name).includes(currInput)) {
              filtered.push({
                name: currInput
              } as Suggestion);
            }

            return filtered;
          }}
      />
  );
}

TagsInput.defaultProps = {
  suggestGroups: []
};
