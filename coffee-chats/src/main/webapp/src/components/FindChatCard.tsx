import React, { useState, ChangeEvent } from "react"
import "./FindChatCard.css"
import { Typography, CardActions, Card, CardContent, Button, CardHeader, createStyles, makeStyles, Theme, Collapse, Grid, TextField, MenuItem, InputLabel, Select, FormControl, FormGroup, FormControlLabel, Checkbox } from "@material-ui/core"
import ExpandMoreIcon from "@material-ui/icons/ExpandMore"
import clsx from "clsx";
import { addWeeks, startOfWeek, addDays } from "date-fns";
import { MultiDatePicker } from "./MultiDatePicker";

const useStyles = makeStyles((theme: Theme) => 
  createStyles({
    expand: {
      transform: 'rotate(0deg)',
      marginRight: 'auto',
      transition: theme.transitions.create('transform', {
        duration: theme.transitions.duration.shortest,
      }),
    },
    expandOpen: {
      transform: 'rotate(180deg)',
    }
  })
);

interface FindChatCardProps {
  interests: string[],
}

export const FindChatCard: React.FC<FindChatCardProps> = ({ interests }) => {
  
  const classes = useStyles();

  const MONDAY = 1;
  const startOfNextWeek = startOfWeek(addWeeks(new Date(), 1), {weekStartsOn: MONDAY});

  const [expanded, setExpanded] = useState(false);
  const [dates, setDates] = useState(Array.from(Array(5).keys()).map((i: number) => addDays(startOfNextWeek, i)))
  const [numPeople, setNumPeople] = useState(1);
  const [duration, setDuration] = useState(30);
  const [randomMatchChecked, setRandomMatchChecked] = useState(false);
  const [pastMatched, setPastMatched] = useState(true);


  const handleExpandClick = () => {
    setExpanded(!expanded);
  }

  const findAction = () => {
    // Send request
  }

  return (
    <Card>
      <CardHeader title="Find a chat" />
      <CardContent>
        <Typography>
          Find someone to chat to about {(interests.length > 0) ? interests.join(", ") : 'anything'}.
        </Typography>
      </CardContent>

      <CardActions>
        <Button
          onClick={handleExpandClick}
          aria-expanded={expanded}
          aria-label="more options"
        >
          More Options
          <ExpandMoreIcon className={clsx(classes.expand, {[classes.expandOpen]: expanded})}/>
        </Button>
        <Button onClick={findAction} variant="contained" color="primary">Find a chat!</Button>
      </CardActions>

      <Collapse in={expanded} timeout="auto" unmountOnExit>
        <CardContent className="more-options-content">

          <Grid container justify="space-around" alignItems="center" spacing={4}>
            <Grid item md={6}>
                <Typography>
                  Select available dates:
                </Typography>
                <MultiDatePicker dates={dates} setDates={setDates}/>
            </Grid>

            <Grid item md={6}>
              <Grid container direction="column" spacing={4}>
                
                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <InputLabel id="select-num-people-label">Number of other Particpants: </InputLabel>
                    <Select
                      labelId="select-num-people-label"
                      variant="filled"
                      value={numPeople}
                      onChange={(event: ChangeEvent<{ value: unknown }>) => setNumPeople(parseInt(event.target.value as string))}
                      className="input-field"
                    >
                      {[1,2,3,4].map((num) => (
                        <MenuItem value={num} key={num.toString()}>
                          {num.toString()}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <InputLabel id="select-chat-duration-label">Duration of chat: </InputLabel>
                    <Select
                      labelId="select-chat-duration-label"
                      variant="filled"
                      value={duration}
                      onChange={(event: ChangeEvent<{ value: unknown }>) => setDuration(parseInt(event.target.value as string))}
                      className="input-field"
                    >
                      {[15, 30, 45, 60].map((num) => (
                        <MenuItem value={num} key={num.toString()}>
                          {num.toString()} minutes
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <FormControlLabel 
                      control={
                        <Checkbox 
                          checked={randomMatchChecked}
                          onChange={(event: ChangeEvent<HTMLInputElement>) => setRandomMatchChecked(event.target.checked)}
                          color="primary"
                        />
                      }
                      label="Match randomly if no interest matches found."
                    />
                  </FormControl>
                </Grid>

                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <FormControlLabel 
                      control={
                        <Checkbox 
                          checked={pastMatched}
                          onChange={(event: ChangeEvent<HTMLInputElement>) => setPastMatched(event.target.checked)}
                          color="primary"
                        />
                      }
                      label="Match with previous matches."
                    />
                  </FormControl>
                </Grid>
              </Grid>
            </Grid>
          </Grid>
        </CardContent>
      </Collapse>
    </Card>
  )
}
