import React, {useState, useEffect} from 'react'
import {MatchedRequest} from '../entity/Requests'
import {
  Grid, Box, Divider, Chip, makeStyles, Theme, createStyles, ListItemAvatar, List, ListItem, 
  Avatar, ListItemText
} from '@material-ui/core'
import {format} from 'date-fns'
import {User} from '../entity/User';
import {fetchUsersInfo} from '../util/userRequest';

interface MatchedRequestDetailsProps {
  request: MatchedRequest;
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

export const MatchedRequestDetails: React.FC<MatchedRequestDetailsProps> = ({request}) => {
  const classes = useStyles();

  const [usersInfo, setUsersInfo] = useState<User[]>([]);

  useEffect(() => {
    fetchUsersInfo(request.participants, setUsersInfo);
  }, [request.participants])

  return (
    <Grid container justify="space-around">
      <Grid item xs={12} md={5}>
          <Box fontWeight="fontWeightMedium" my={1}>
            Start:
          </Box> 
          <Box mb={1}>
            {format(request.dateRange.start, 'eeee do MMMM HH:mm (O)')}
          </Box>
          
          <Divider />

          <Box fontWeight="fontWeightMedium" my={1}>
            Duration:
          </Box> 
          <Box mb={1}>
            {request.durationMins} minutes
          </Box>

          <Divider />

          <Box fontWeight="fontWeightMedium" my={1}>
            Shared Tags:
          </Box>
          <Box mb={1}>
            {request.commonTags.map((tag) => (
              <Chip
                variant="default"
                color="primary"
                label={tag}
                className={classes.tagChip}
                key={tag}
              />
            ))}
          </Box>
          
          <Divider />

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
      </Grid>

      <Divider orientation="vertical" flexItem />

      <Grid item xs={12} md={5}>
        <Box fontWeight="fontWeightMedium" mt={1}>
          Participants:
        </Box>
        <List>
          {usersInfo.map((user) => (
            <ListItem divider key={user.id}>
              <ListItemAvatar>
                <Avatar src={user.avatarUrl} />
              </ListItemAvatar>
              <ListItemText primary={user.name} secondary={user.email} />
            </ListItem>
          ))}
        </List>
      </Grid>

    </Grid>
  )
}
