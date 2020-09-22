import React, {useState} from "react";
import {
  Accordion, AccordionActions, AccordionDetails, AccordionSummary, Box, Button, FormControl, Icon,
  InputLabel, MenuItem, Select, TextField, Typography
} from "@material-ui/core";
import {DateTimePicker} from "@material-ui/pickers";
import {MaterialUiPickersDate} from "@material-ui/pickers/typings/date";
import {FindOptimalTimeDialog} from "./FindOptimalTimeDialog";
import {Group} from "../entity/Group";
import {postData} from "../util/fetch";
import {getUnixTime, roundToNearestMinutes} from "date-fns";
import {makeStyles} from "@material-ui/core/styles";

interface CreateEventCardProps {
  group: Group;
  onSubmit: () => void;
}

const useStyles = makeStyles((theme) => ({
  content: {
    width: "100%"
  }
}));

export function CreateEventCard({group, onSubmit}: CreateEventCardProps) {
  const classes = useStyles();
  const [duration, setDuration] = useState(30); // in minutes
  const [start, setStart] = useState<MaterialUiPickersDate>(roundToNearestMinutes(new Date(), {
    nearestTo: 15
  }));
  const [description, setDescription] = useState("");
  const [timeDialogOpen, setTimeDialogOpen] = useState(false);
  const [expanded, setExpanded] = useState(false);

  const submit = async () => {
    const data = new Map();
    data.set("id", group.id);
    data.set("duration", duration);
    data.set("start", getUnixTime(start!));
    data.set("description", description);
    await postData("/api/eventCreate", data);
    setExpanded(false);
    onSubmit();
  };

  return (
      <Box mt={2}>
        <FindOptimalTimeDialog
            open={timeDialogOpen}
            setOpen={setTimeDialogOpen}
            group={group}
            setDate={setStart}
            duration={duration}
        />
        <Accordion
            expanded={expanded}
            onChange={(e, isExpanded) => setExpanded(isExpanded)}>
          <AccordionSummary expandIcon={<Icon>expand_more</Icon>}>
            <Typography color="textSecondary">
              Schedule an event
            </Typography>
          </AccordionSummary>
          <AccordionDetails>
            <div className={classes.content}>
            <FormControl fullWidth margin="normal">
              <InputLabel id="duration-label">Duration</InputLabel>
              <Select
                  fullWidth
                  labelId="duration-label"
                  value={duration}
                  onChange={(e) => setDuration(e.target.value as number)}
              >
                {[15, 30, 45, 60, 90, 120, 150, 180].map((n) => (
                    <MenuItem key={n} value={n}>{n} minutes</MenuItem>
                ))}
              </Select>
            </FormControl>

            <FormControl fullWidth margin="normal">
              <DateTimePicker
                  fullWidth
                  ampm={false}
                  variant="dialog"
                  value={start}
                  onChange={setStart}
                  label="When"
              />
            </FormControl>

            <Button onClick={() => setTimeDialogOpen(true)}>
              Find an optimal time
            </Button>

            <FormControl fullWidth margin="normal">
              <TextField
                  fullWidth
                  multiline
                  label="Description"
                  placeholder="Enter a short description of the event"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
              />
            </FormControl>
            </div>
          </AccordionDetails>
          <AccordionActions>
            <Button color="primary" onClick={submit}>
              Schedule
            </Button>
          </AccordionActions>
        </Accordion>
      </Box>
  );
}
