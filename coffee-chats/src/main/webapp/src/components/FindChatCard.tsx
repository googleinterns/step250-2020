import React, {useState, ChangeEvent} from "react";
import "./FindChatCard.css";
import {
  Typography, CardActions, Card, CardContent, Button, CardHeader,
  createStyles, makeStyles, Theme, Collapse, Grid, MenuItem, InputLabel,
  Select, FormControl, FormControlLabel, Checkbox, CircularProgress, Slider,
  Snackbar, List, Chip, Tooltip
} from "@material-ui/core";
import MuiAlert from '@material-ui/lab/Alert';
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import AddIcon from "@material-ui/icons/Add";
import clsx from "clsx";
import {green} from "@material-ui/core/colors";
import {submitChatRequest} from "../util/chatRequest";
import {AuthState, AuthStateContext} from "../entity/AuthState";
import {DatetimeRangeListItem} from "./DatetimeRangeListItem";
import {MaterialUiPickersDate} from "@material-ui/pickers/typings/date";

const useStyles = makeStyles((theme: Theme) => 
  createStyles({
    tagChip: {
      marginTop: 4,
      marginRight: 4
    },
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

const MIN_PARTICIPANTS = 1;
const MAX_PARTICIPANTS = 4;

interface FindChatCardProps {
  interests: string[]
}

export const FindChatCard: React.FC<FindChatCardProps> = ({interests}) => {
  const classes = useStyles();
  const authState: AuthState = React.useContext(AuthStateContext);

  const participantSliderMarks = [1,2,3,4].map((num) => ({value: num, label: num.toString()}));

  const [expanded, setExpanded] = useState(false);
  const [numPeopleRange, setNumPeopleRange] = useState([1, 4]);
  const [durationMins, setDurationMins] = useState(30);
  const [matchRandom, setMatchRandom] = useState(false);
  const [matchRecents, setMatchRecents] = useState(true);

  const [startDates, setStartDates] = useState<MaterialUiPickersDate[]>([null]);
  const [endDates, setEndDates] = useState<MaterialUiPickersDate[]>([null]);
  const [numRanges, setNumRanges] = useState(1);
  
  const setStartDate = (i: number) => {
    return (start: MaterialUiPickersDate) => {
      let newStarts = [...startDates];

      if (start !== null) {
        start.setSeconds(0,0);
      }

      newStarts[i] = start;
      setStartDates(newStarts);
    }
  };

  const setEndDate = (i: number) => {
    return (end: MaterialUiPickersDate) => {
      let newEnds = [...endDates];

      if (end !== null) {
        end.setSeconds(0, 0);
      }

      newEnds[i] = end;
      setEndDates(newEnds);
    }
  };

  const addDateRange = () => {
    setStartDates([...startDates, null]);
    setEndDates([...endDates, null]);
    setNumRanges(numRanges + 1);
  }

  const removeDateRange = (i: number) => {
    return () => {
      let newStarts = [...startDates];
      newStarts.splice(i, 1);
  
      let newEnds = [...endDates];
      newEnds.splice(i, 1);
  
      setStartDates(newStarts);
      setEndDates(newEnds);
      setNumRanges(numRanges - 1);
    }
  }

  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [invalidDates, setInvalidDates] = useState(false);

  const validParamters = () => {
    for (let i = 0; i < numRanges; i++) {
      if ((startDates[i] === null) || (endDates[i] === null)) {
        setInvalidDates(true);
        return false;
      }
    }

    return numPeopleRange[0] <= numPeopleRange[1] &&
        numPeopleRange[0] >= MIN_PARTICIPANTS &&
        numPeopleRange[1] <= MAX_PARTICIPANTS;
  };

  const handleExpandClick = () => {
    setExpanded(!expanded);
  };

  const chatRequestSend = async () => {
    if (!loading && validParamters()) {
      setSuccess(false);
      setLoading(true);

      setExpanded(false);
      setSuccess(await submitChatRequest(interests, startDates, endDates,
        numPeopleRange, durationMins, matchRandom, matchRecents));
      setLoading(false);

      setTimeout(() => {
        setSuccess(false);
      }, 2500);
    }
  };

  return (
    <Card>
      <CardHeader title="Find a chat" />
      <CardContent>

        <Grid container alignItems="center">
          <Grid item xs={12} md={4}>
            <Typography>
              Find someone to chat to about {(interests.length > 0) ? ':' : 'anything.'}
            </Typography>
          </Grid>
          <Grid item xs={12} md={8}>
            {interests.map((tag) => 
              <Chip variant="outlined" color="primary" label={tag} className={classes.tagChip} key={tag}/>  
            )}
          </Grid>
          
          <Grid item xs={12} md={4}>
            <Typography>
              Select when to chat (note your calendar will be taken to account to ensure availability):
            </Typography>
          </Grid>
          <Grid item xs={12} md={8}>
            <List dense>
              {Array.from(Array(numRanges).keys()).map((num) => (
                <DatetimeRangeListItem
                  selectedDates={{dateStart: startDates[num], dateEnd: endDates[num]}}
                  setSelectedDateStart={setStartDate(num)}
                  setSelectedDateEnd={setEndDate(num)}
                  btnAction={removeDateRange(num)}
                  onlyRange={numRanges === 1}
                  key={num}
                />
              ))}
            </List>
            <Button
              variant="text"
              onClick={addDateRange}
            >
              Add Range
              <AddIcon />
            </Button>
          </Grid>
        </Grid>
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
          <Tooltip title={authState.oauthAuthorized ? "" : "You need to authorize the app with your Google Calendar first"}>
            <span>
              <Button
                variant="contained"
                color="primary"
                className={clsx({[classes.btnSuccess]: success})}
                disabled={loading || !authState.oauthAuthorized}
                onClick={chatRequestSend}
              >
                Find a chat!
              </Button>
            </span>
          </Tooltip>
          {loading && <CircularProgress size={24} className={classes.btnProgress} />}
        </div>
      </CardActions>

      <Collapse in={expanded} timeout="auto" unmountOnExit>
        <CardContent className="more-options-content">

          <Grid container justify="space-around" alignItems="center" spacing={4}>
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
                      value={durationMins}
                      onChange={(event: ChangeEvent<{ value: unknown }>) => setDurationMins(parseInt(event.target.value as string))}
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
                          checked={matchRandom}
                          onChange={(event: ChangeEvent<HTMLInputElement>) => setMatchRandom(event.target.checked)}
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
                          disabled
                          checked={matchRecents}
                          onChange={(event: ChangeEvent<HTMLInputElement>) => setMatchRecents(event.target.checked)}
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

      <Snackbar open={invalidDates} autoHideDuration={3000} onClose={() => setInvalidDates(false)}>
        <MuiAlert elevation={6} variant="filled" onClose={() => setInvalidDates(false)} severity="error">
          Please enter a start and end date/time for each range.
        </MuiAlert>
      </Snackbar>

      <Snackbar open={success} autoHideDuration={3000}>
        <MuiAlert elevation={6} variant="filled" severity="success">
          Request created!
        </MuiAlert>
      </Snackbar>
    </Card>
  )
};
