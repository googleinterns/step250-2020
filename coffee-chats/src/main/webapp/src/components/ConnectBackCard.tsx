import React from 'react';

import {Card, CardContent, CardActions, Button, Typography} from "@material-ui/core";
import {humanReadableJoin} from "../util/format";

export function ConnectBackCard(props: { names: string[], tags: string[] }) {
  return (
      <Card>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            Connect Back
          </Typography>
          <Typography color="textSecondary" gutterBottom>
            You've recently chatted
            about {humanReadableJoin(props.tags)} {}
            with {}
            <Typography color="textPrimary" component={'span'} >
              {humanReadableJoin(props.names)}
            </Typography>.
          </Typography>
        </CardContent>
        <CardActions>
          <Button size="small">More</Button>
        </CardActions>
      </Card>
  );
}
