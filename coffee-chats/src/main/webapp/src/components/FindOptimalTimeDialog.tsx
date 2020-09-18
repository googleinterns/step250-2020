import React, {useState} from "react";
import {
  Button, CircularProgress, Dialog, DialogActions, DialogContent,
  DialogContentText, DialogTitle, Grid, Typography
} from "@material-ui/core";
import {MultiDatePicker} from "./MultiDatePicker";
import {TimePicker} from "@material-ui/pickers";
import {MaterialUiPickersDate} from "@material-ui/pickers/typings/date";
import {getHours, getMinutes, setHours, setMinutes, startOfDay} from "date-fns";
import {postData} from "../util/fetch";
import {Group} from "../entity/Group";

interface FindOptimalTimeDialogProps {
  open: boolean;
  setOpen: (value: boolean) => void;
  setDate: (value: Date) => void;
  group: Group;
  duration: number; // in minutes
}

export function FindOptimalTimeDialog({open, setOpen, setDate, group, duration}: FindOptimalTimeDialogProps) {
  const [loading, setLoading] = useState(false);
  const [warning, setWarning] = useState(false);
  const [dates, setDates] = useState([new Date()]);

  const [timeStart, setTimeStart] = useState<MaterialUiPickersDate>( // from 9:00
      setHours(setMinutes(new Date(), 0), 9)
  );

  const [timeEnd, setTimeEnd] = useState<MaterialUiPickersDate>( // to 17:00
      setHours(setMinutes(new Date(), 0), 17)
  );

  const submit = async () => {
    setLoading(true);

    const ranges = dates.map(startOfDay).map(date => (
        {
          start: setMinutes(setHours(date, getHours(timeStart!)), getMinutes(timeStart!)),
          end: setMinutes(setHours(date, getHours(timeEnd!)), getMinutes(timeEnd!)),
        }
    ));

    const data = new Map();
    data.set("id", group.id);
    data.set("duration", duration.toString());
    data.set("ranges", JSON.stringify(ranges));

    const response = await postData("/api/scheduleEvent", data);

    if (response.status === 200) {
      const dateText = await response.json();
      if (dateText !== null) {
        setDate(new Date(dateText));
        setOpen(false);
      } else {
        setWarning(true);
      }
    }

    setLoading(false);
  };

  return (
      <Dialog open={open} onClose={() => setOpen(false)}>
        <DialogTitle>Find an Optimal Time</DialogTitle>
        <DialogContent>
          <DialogContentText>
            {warning && <Typography color="secondary">
              Couldn't find a suitable time slot.
            </Typography>}
            Please choose when to consider scheduling the event.
            We will try to find a time so that the maximum number of members can attend.
          </DialogContentText>
          <Grid container spacing={2}>
            <Grid item sm={7}>
              <MultiDatePicker dates={dates} setDates={setDates}/>
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
          {loading ? <CircularProgress /> : <Button color="primary" onClick={submit}>Find</Button>}
        </DialogActions>
      </Dialog>
  );
}
