import React from 'react';
import { ListItem, ListItemSecondaryAction, IconButton, Grid } from '@material-ui/core';
import { DateTimePicker } from '@material-ui/pickers';
import { MaterialUiPickersDate } from '@material-ui/pickers/typings/date';
import DeleteIcon from '@material-ui/icons/Delete';
import AddIcon from '@material-ui/icons/Add';

interface DatetimeRangeListItemProps {
  selectedDates: {dateStart: MaterialUiPickersDate, dateEnd: MaterialUiPickersDate},
  setSelectedDateStart: (date: MaterialUiPickersDate) => void,
  setSelectedDateEnd: (date: MaterialUiPickersDate) => void,
  btnAction: () => void,
  isLast: boolean
}

export const DatetimeRangeListItem: React.FC<DatetimeRangeListItemProps> = ({selectedDates, setSelectedDateStart, setSelectedDateEnd, btnAction, isLast}) => {
  const {dateStart, dateEnd} = selectedDates;

  return (
    <ListItem alignItems="center">
      <Grid container spacing={1}>
        <Grid item xs={12} sm={6}>
          <DateTimePicker
            autoOk
            clearable
            variant="dialog"
            inputVariant="outlined"
            label="Start"
            value={dateStart}
            onChange={setSelectedDateStart}
            disablePast
            maxDate={dateEnd}
            maxDateMessage="Start date and time should be before end."
          />
        </Grid>

        <Grid item xs={12} sm={6}>
          <DateTimePicker
            autoOk
            clearable
            variant="dialog"
            inputVariant="outlined"
            label="End"
            value={dateEnd}
            onChange={setSelectedDateEnd}
            disablePast
            minDate={dateStart}
            minDateMessage="End date and time should be after start"
          />
        </Grid>
      </Grid>

      <ListItemSecondaryAction>
        <IconButton edge="end" aria-label="delete" onClick={btnAction}>
          {isLast ?
            <AddIcon /> :
            <DeleteIcon />
          }
        </IconButton>
      </ListItemSecondaryAction>
    </ListItem>
  )
};
