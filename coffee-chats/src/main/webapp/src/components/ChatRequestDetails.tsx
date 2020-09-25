import React from 'react'
import {ChatRequest, ExpiredRequest} from '../entity/Requests'
import {
  Grid, Box, Divider, Typography, Chip, Checkbox, makeStyles, Theme, createStyles
} from '@material-ui/core'
import {format} from 'date-fns'

interface ChatRequestDetailsProps {
  request: ChatRequest | ExpiredRequest;
}

const useStyles = makeStyles((theme: Theme) => {
  return (
    createStyles({
      tagChip: {
        marginTop: 4,
        marginRight: 4
      }
    })
  );
});

export const ChatRequestDetails: React.FC<ChatRequestDetailsProps> = ({request}) => {
  const classes = useStyles();

  return (
    <Grid container justify="space-around">
      <Grid item xs={12} md={5}>
        <Box fontWeight="fontWeightMedium" my={1}>
          Date Ranges:
        </Box>
        <Box fontStyle="italic" fontSize="caption.fontSize" fontWeight="fontWeightLight" mb={1}>
          (Times are in {format(request.dateRanges[0].start, 'O')})
        </Box>
        {request.dateRanges.map((range) => (
          <div key={range.start.toString()}>
            <Box my={1}>
              <Grid container justify="space-between">
                <Grid item xs={5}>
                  <Typography align="left">
                    {format(range.start, 'eee do MMM HH:mm')}
                  </Typography>
                </Grid>
                <Grid item xs={2}>
                  <Typography align="center">
                    &mdash;
                  </Typography>
                </Grid>
                <Grid item xs={5}>
                  <Typography align="right">
                    {format(range.end, 'eee do MMM HH:mm')}
                  </Typography>
                </Grid>
              </Grid>
            </Box>

            <Divider />
          </div>
        ))}

        <Box fontWeight="fontWeightMedium" my={1}>
          Group Size:
        </Box>
        <Box mb={1}>
          {request.minPeople + 1}
          &nbsp;&nbsp;&mdash;&nbsp;&nbsp;
          {request.maxPeople + 1} people
        </Box>

        <Divider />
      </Grid>

      <Divider orientation="vertical" flexItem />

      <Grid item xs={12} md={5}>
        <Box fontWeight="fontWeightMedium" my={1}>
          Requested Tags:
        </Box> 
        <Box mb={1}>
          {request.tags.map((tag) => (
            <Chip
              variant="outlined"
              color="primary"
              label={tag}
              className={classes.tagChip}
              key={tag}
            />
          ))}
        </Box>

        <Divider />

        <Box fontWeight="fontWeightMedium" my={1}>
          Match randomly if no tag matches found:
          <Checkbox checked={request.matchRandom} color="primary" />
        </Box>

        <Box fontWeight="fontWeightMedium" my={1}>
          Match with recently matched users:
          <Checkbox checked={request.matchRecents} color="primary" />
        </Box>

        <Divider />
      </Grid>
    </Grid>
  )
}
