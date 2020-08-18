import React, { useState, ChangeEvent } from "react"
import "./FindChatCard.css"
import { Typography, CardActions, Card, CardContent, Button, CardHeader, 
  createStyles, makeStyles, Theme, Collapse, Grid, MenuItem, InputLabel, 
  Select, FormControl, FormControlLabel, Checkbox, CircularProgress, Slider
 } from "@material-ui/core"
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
        duration: theme.transitions.duration.standard,
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
  const participantSliderMarks = [1,2,3,4].map((num) => ({value: num, label: num.toString()}))

  const [expanded, setExpanded] = useState(false);
  const [dates, setDates] = useState(Array.from(Array(5).keys()).map((i: number) => addDays(startOfNextWeek, i)))
  const [numPeopleRange, setNumPeopleRange] = useState([1, 1]);
  const [duration, setDuration] = useState(30);
  const [recentMatches, setRecentMatches] = useState(false);
  const [pastMatched, setPastMatched] = useState(true);

  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleExpandClick = () => {
    setExpanded(!expanded);
  }

  const chatRequestClick = async () => {
    if (!loading) {
      setSuccess(false);
      setLoading(true);

      setExpanded(false);
      setSuccess(await submitChatRequest(interests, dates, numPeopleRange, duration, recentMatches, pastMatched));
      setLoading(false);

      setTimeout(() => {
        setSuccess(false);
      }, 2500);
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
                  <Typography id="participants-slider">
                    Number of other participants:
                  </Typography>
                  <Slider 
                    value={numPeopleRange}
                    onChange={(_event: any, newNum: number | number[]) => setNumPeopleRange(newNum as number[])}
                    valueLabelDisplay="off"
                    aria-labelledby="participants-slider"
                    min={1}
                    max={4}
                    marks={participantSliderMarks}
                    step={null}
                  />

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
                          checked={recentMatches}
                          onChange={(event: ChangeEvent<HTMLInputElement>) => setRecentMatches(event.target.checked)}
                          color="primary"
                        />
                      }
                      label="Proceed to schedule chat even if no matches are found for selected topics."
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
                      label="Allow matches to people I have matched with recently."
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
