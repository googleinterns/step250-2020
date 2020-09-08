import React from "react";
import {Autocomplete, createFilterOptions} from "@material-ui/lab";
import {Chip, TextField} from "@material-ui/core";
import {capitaliseEachWord} from "../util/stringUtils";
import {fetchTags} from "../util/tagsRequest";

interface TagsInputProps {
  label: string;
  tags: string[];
  setTags: (value: string[]) => void;
}

export function TagsInput({label, tags, setTags}: TagsInputProps) {
  const filter = createFilterOptions<string>();
  const [tagOptions, setTagOptions] = React.useState<string[]>([]);

  React.useEffect(() => {
    const initFetchTags = (async () => {
      setTagOptions(await fetchTags());
    });
    initFetchTags();
  }, []);

  return (
      <Autocomplete
          multiple
          options={tagOptions}
          freeSolo
          autoHighlight
          onChange={(_event: any, newValue: string[]) => {
            setTags(newValue);
          }}
          renderTags={(value: string[], getTagProps) =>
              value.map((option: string, index: number) => (
                  <Chip variant="outlined" label={option} {...getTagProps({ index })} />
              ))
          }
          renderInput={(params) => (
              <TextField
                  {...params}
                  variant="outlined"
                  label={label}
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
  );
}
