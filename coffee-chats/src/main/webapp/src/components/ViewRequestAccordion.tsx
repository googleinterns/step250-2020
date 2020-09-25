import React from 'react'
import {
  Accordion, AccordionSummary, Grid, makeStyles, Theme, createStyles, Typography, Chip, darken, 
  lighten, AccordionDetails
} from '@material-ui/core'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import HelpIcon from '@material-ui/icons/Help'
import CancelIcon from '@material-ui/icons/Cancel'
import CheckCircleIcon from '@material-ui/icons/CheckCircle'
import {
  ChatRequest, CompletedRequest, isMatched, isPending, MatchedRequest, ExpiredRequest
} from '../entity/Requests'
import {MatchedRequestDetails} from './MatchedRequestDetails'
import {ChatRequestDetails} from './ChatRequestDetails'

const useStyles = makeStyles((theme: Theme) => {
  const getBackgroundColor = theme.palette.type === 'light' ? lighten : darken;

  return (
    createStyles({
      tagChip: {
        marginTop: 4,
        marginRight: 4
      },
      pending: {
        backgroundColor: getBackgroundColor(theme.palette.warning.main, 0.9),
        '& .statusSymbol' : {
          color: theme.palette.warning.main
        }
      },
      expired: {
        backgroundColor: getBackgroundColor(theme.palette.error.main, 0.9),
        '& .statusSymbol' : {
          color: theme.palette.error.main
        }
      },
      matched: {
        backgroundColor: getBackgroundColor(theme.palette.success.main, 0.9),
        '& .statusSymbol' : {
          color: theme.palette.success.main
        }
      }
    })
  )
});

interface ViewRequestAccordionProps {
  request: ChatRequest | CompletedRequest,
}

export const ViewRequestAccordion: React.FC<ViewRequestAccordionProps> = ({request}) => {
  const classes = useStyles();

  const status = isPending(request) ? "pending" : (isMatched(request) ? "matched" : "expired");
  const colorClass = status === "matched" ? classes.matched : (status === "expired" ? classes.expired : classes.pending)

  return (
    <Accordion>
      <AccordionSummary 
        expandIcon={<ExpandMoreIcon />}
        className={colorClass}
      >
        <Grid container alignItems="center" justify="flex-start">
          <Grid item xs={2} sm={1}>
            {status === "pending" &&
              <HelpIcon className="statusSymbol" />
            }
            {status === "expired" &&
              <CancelIcon className="statusSymbol" />
            }
            {status === "matched" &&
              <CheckCircleIcon className="statusSymbol" />
            }
          </Grid>
          <Grid item xs={10} sm={3}>
            <Typography>
              {status === "pending" && 'Pending'}
              {status === "expired" && 'Expired'}
              {status === "matched" && 'Matched!'}
            </Typography>
          </Grid>

          <Grid item xs={12} sm={8}>
            {isMatched(request) ?
              (<>
                {request.commonTags.map((commonTag) => (
                  <Chip
                    variant="default"
                    color="primary"
                    label={commonTag}
                    className={classes.tagChip}
                    key={commonTag}
                  />
                ))}
                {request.tags.filter((tag) => !request.commonTags.includes(tag))
                    .map((tag) => (
                      <Chip
                        variant="outlined"
                        color="primary"
                        label={tag}
                        className={classes.tagChip}
                        key={tag}
                      />
                    ))
                }
              </>):
              request.tags.map((tag) => (
                <Chip
                  variant="outlined"
                  color="primary"
                  label={tag}
                  className={classes.tagChip}
                  key={tag}
                />
              ))
            }
          </Grid>
        </Grid>
      </AccordionSummary>
      <AccordionDetails>
        {status === "pending" &&
          <ChatRequestDetails request={request as ChatRequest} />
        }
        {status === "matched" &&
          <MatchedRequestDetails request={request as MatchedRequest} />
        }
        {status === "expired" &&
          <ChatRequestDetails request={request as ExpiredRequest} />
        }
      </AccordionDetails>
    </Accordion>
  )
}
