import React from "react";
import {Box, Card, CardContent, Typography} from "@material-ui/core";
import {Event} from "../entity/Event";
import {format, fromUnixTime} from "date-fns";

interface EventCardProps {
  event: Event;
}

export function EventCard({event}: EventCardProps) {
  return (
      <Box mt={2}>
        <Card>
          <CardContent>
            <Typography color="textSecondary" gutterBottom>
              Upcoming event at {format(fromUnixTime(event.start.seconds), "PP p")}
            </Typography>

            {event.description}
          </CardContent>
        </Card>
      </Box>
  )
}
