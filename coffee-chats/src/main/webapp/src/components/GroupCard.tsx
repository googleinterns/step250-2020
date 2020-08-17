import React from "react";
import {Button, Card, CardActions, CardContent, Typography} from "@material-ui/core";
import {Group} from "../entity/Group";

interface GroupCardProps {
  group: Group;
}

export function GroupCard({group}: GroupCardProps) {
  return (
      <Card>
        <CardContent>
          <Typography variant="h5">{group.name}</Typography>
          <Typography variant="body2" color="textSecondary">{group.description}</Typography>
        </CardContent>
        <CardActions>
          <Button size="small">More</Button>
        </CardActions>
      </Card>
  );
}
