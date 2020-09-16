import React, {useState} from "react";
import {Box, Card, CardContent, FormControl, Grid, InputLabel, MenuItem, Select, Typography} from "@material-ui/core";

export function CreateEventCard() {
  const [duration, setDuration] = useState(30); // in minutes

  return (
      <Box mt={2}>
        <Card>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Schedule an Event
            </Typography>

            <FormControl>
              <InputLabel id="duration-label">Duration</InputLabel>
              <Select
                  labelId="duration-label"
                  value={duration}
                  onChange={(e) => setDuration(e.target.value as number)}
              >
                {[15, 30, 45, 60, 90, 120, 150, 180].map((n) => (
                    <MenuItem key={n} value={n}>{n} minutes</MenuItem>
                ))}
              </Select>
            </FormControl>
          </CardContent>
        </Card>
      </Box>
  );
}
