import React, { useState, ChangeEvent } from "react"
import "./FindChatCard.css"
import { Typography, CardActions, Card, CardContent, Button, CardHeader, createStyles, makeStyles, Theme, Collapse, Grid, MenuItem, InputLabel, Select, FormControl, FormControlLabel, Checkbox, CircularProgress } from "@material-ui/core"
import ExpandMoreIcon from "@material-ui/icons/ExpandMore"
import clsx from "clsx";
import { addWeeks, startOfWeek, addDays } from "date-fns";
import { MultiDatePicker } from "./MultiDatePicker";
import { green } from "@material-ui/core/colors";
import { submitChatRequest } from "../utils/chatRequest";

const useStyles = makeStyles((theme: Theme) => 
  createStyles({
    expand: {
      transform: 'rotate(0deg)',
      transition: theme.transitions.create('transform', {
        duration: theme.transitions.duration.shortest,
      }),
    },
    expandOpen: {
      transform: 'rotate(180deg)',
    },
    actions: {
      display: 'flex',
      justifyContent: 'space-between'
    },
    successBtnWrapper: {
      margin: theme.spacing(1),
      position: 'relative',
    },
    btnSuccess: {
      backgroundColor: green[500],
      '&:hover': {
        backgroundColor: green[700]
      }
    },
    btnProgress: {
      color: green[500],
      position: 'absolute',
      top: '50%',
      left: '50%',
      marginTop: -12,
      marginLeft: -12,
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

  const [loading, setLoading] = React.useState(false);
  const [success, setSuccess] = React.useState(false);

  const handleExpandClick = () => {
    setExpanded(!expanded);
  }

  const chatRequestClick = async () => {
    if (!loading) {
      setSuccess(false);
      setLoading(true);

      setSuccess(await submitChatRequest(interests, dates, numPeople, duration, randomMatchChecked, pastMatched));
      setLoading(false);
    }
  }

  return (
    <Card>
      <CardHeader title="Find a chat" />
      <CardContent>
        <Typography>
          Find someone to chat to about {(interests.length > 0) ? interests.join(", ") : 'anything'}.
        </Typography>
      </CardContent>

      <CardActions className={classes.actions}>
        <Button
          onClick={handleExpandClick}
          aria-expanded={expanded}
          aria-label="more options"
        >
          More Options
          <ExpandMoreIcon className={clsx(classes.expand, {[classes.expandOpen]: expanded})}/>
        </Button>

        <div className={classes.successBtnWrapper}>
          <Button 
            variant="contained"
            color="primary"
            className={clsx({[classes.btnSuccess]: success})}
            disabled={loading}
            onClick={chatRequestClick}
          >
            Find a chat!
          </Button>
          {loading && <CircularProgress size={24} className={classes.btnProgress} />}
        </div>
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
