import React from "react";
import {Button, Card, CardActions, CardContent, Typography} from "@material-ui/core";

export function GroupCard(props: {name: string}) {
  return (
      <Card>
        <CardContent>
          <Typography variant="h5">{props.name}</Typography>
        </CardContent>
        <CardActions>
          <Button size="small">More</Button>
        </CardActions>
      </Card>
  );
}
