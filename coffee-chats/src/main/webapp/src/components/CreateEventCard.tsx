import React, {useState} from "react";
import {
  Box, Button, Card, CardActions, CardContent, FormControl, InputLabel,
  MenuItem, Select, Typography
} from "@material-ui/core";
import {DateTimePicker} from "@material-ui/pickers";
import {MaterialUiPickersDate} from "@material-ui/pickers/typings/date";

export function CreateEventCard() {
  const [duration, setDuration] = useState(30); // in minutes
  const [start, setStart] = useState<MaterialUiPickersDate>();

  return (
      <Box mt={2}>
        <Card>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Schedule an Event
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

            <Button>
              Pick optimal time
            </Button>
          </CardContent>
          <CardActions>
            <Button
                color="primary"
            >
              Schedule
            </Button>
          </CardActions>
        </Card>
      </Box>
  );
}
