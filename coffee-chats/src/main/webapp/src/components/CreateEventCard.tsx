import React, {useState} from "react";
import {
  Box, Button, Card, CardActions, CardContent, FormControl, InputLabel,
  MenuItem, Select, TextField, Typography
} from "@material-ui/core";
import {DateTimePicker} from "@material-ui/pickers";
import {MaterialUiPickersDate} from "@material-ui/pickers/typings/date";
import {FindOptimalTimeDialog} from "./FindOptimalTimeDialog";
import {Group} from "../entity/Group";
import {postData} from "../util/fetch";
import {getUnixTime} from "date-fns";

interface CreateEvebtCardProps {
  group: Group;
}

export function CreateEventCard({group}: CreateEvebtCardProps) {
  const [duration, setDuration] = useState(30); // in minutes
  const [start, setStart] = useState<MaterialUiPickersDate>();
  const [description, setDescription] = useState("");
  const [timeDialogOpen, setTimeDialogOpen] = useState(false);

  const submit = async () => {
    const data = new Map();
    data.set("id", group.id);
    data.set("duration", duration);
    data.set("start", getUnixTime(start!));
    data.set("description", description);
    await postData("/api/eventCreate", data);
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
        <Card>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Schedule an event
            </Typography>

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
          </CardContent>
          <CardActions>
            <Button color="primary" onClick={submit}>
              Schedule
            </Button>
          </CardActions>
        </Card>
      </Box>
  );
}
