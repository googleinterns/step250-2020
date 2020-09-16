import React, {useState} from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle, Grid,
} from "@material-ui/core";
import {MultiDatePicker} from "./MultiDatePicker";
import {TimePicker} from "@material-ui/pickers";
import {MaterialUiPickersDate} from "@material-ui/pickers/typings/date";
import {setHours, setMinutes} from "date-fns";

interface FindOptimalTimeDialogProps {
  open: boolean;
  setOpen: (value: boolean) => void;
}

export function FindOptimalTimeDialog({open, setOpen}: FindOptimalTimeDialogProps) {
  const [dates, setDates] = useState([new Date()]);

  const [timeStart, setTimeStart] = useState<MaterialUiPickersDate>( // from 9:00
      setHours(setMinutes(new Date(), 0), 9)
  );

  const [timeEnd, setTimeEnd] = useState<MaterialUiPickersDate>( // to 17:00
      setHours(setMinutes(new Date(), 0), 17)
  );

  return (
      <Dialog open={open} onClose={() => setOpen(false)}>
        <DialogTitle>Find an Optimal Time</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Please choose when to consider scheduling the event.
            We will try to find a time so that the maximum number of members can attend.
          </DialogContentText>
          <Grid container spacing={2}>
            <Grid item sm={7}>
              <MultiDatePicker dates={dates} setDates={setDates} />
            </Grid>
            <Grid item sm={5}>
              <TimePicker
                  value={timeStart}
                  onChange={setTimeStart}
                  minutesStep={5}
                  label="From"
                  fullWidth
                  margin="normal"
              />

              <TimePicker
                  value={timeEnd}
                  onChange={setTimeEnd}
                  minutesStep={5}
                  label="To"
                  fullWidth
                  margin="normal"
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button color="primary">Find</Button>
        </DialogActions>
      </Dialog>
  );
}
