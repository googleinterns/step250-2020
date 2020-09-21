import React from "react";
import {Box, Button, Card, CardActions, CardContent, Typography} from "@material-ui/core";
import {Event} from "../entity/Event";
import {format, fromUnixTime} from "date-fns";
import {MembershipStatus} from "../entity/Member";

interface EventCardProps {
  event: Event;
  status: MembershipStatus;
  onDelete: () => void;
}

export function EventCard({event, status, onDelete}: EventCardProps) {
  return (
      <Box mt={2}>
        <Card>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Upcoming event at {format(fromUnixTime(event.start.seconds), "PP p")}
            </Typography>

            {event.description}
          </CardContent>
          {(status === "ADMINISTRATOR" || status === "OWNER") &&
          <CardActions>
            <Button onClick={onDelete} color="secondary">Delete</Button>
          </CardActions>}
        </Card>
      </Box>
  )
}
