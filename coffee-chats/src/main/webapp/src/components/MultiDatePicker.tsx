import React, {ReactElement, MouseEvent} from "react";
import "./MultiDatePicker.css"
import {DatePicker} from "@material-ui/pickers"
import {isSameDay} from "date-fns";
import {MaterialUiPickersDate} from "@material-ui/pickers/typings/date";

interface MultiDatePickerProps {
  dates: Date[],
  setDates: (dates: Date[]) => void,
}


export const MultiDatePicker: React.FC<MultiDatePickerProps> = ({dates, setDates}) => {

  // Custom rendering function for day Element on calendar
  const renderDay = (day: MaterialUiPickersDate, selectedDate: MaterialUiPickersDate, dayInCurrentMonth: boolean, dayComponent: ReactElement) => {
    return React.cloneElement(dayComponent, {
      onClick: (e: MouseEvent) => {
        e.stopPropagation();
        const foundIndex = dates.findIndex(d => isSameDay(d, day as Date));

        // Toggle selection of day appearing in selected dates array
        if (foundIndex >= 0) {
          const newDates = [...dates];
          newDates.splice(foundIndex, 1);
          setDates(newDates)
        } else {
          setDates([...dates, day as Date])
        }
      },

      selected: dates.find(d => isSameDay(d, day as Date)) != null
    });
  };

  return (
    <div className="datePicker">
      <DatePicker
        disablePast
        disableToolbar
        value={dates[0]}
        variant="static"
        renderDay={renderDay}
        onChange={() => {}}
      />
    </div>
  )
};