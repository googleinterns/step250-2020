import React from "react";
import {Card, CardActionArea, CardContent, Typography} from "@material-ui/core";
import {Group} from "../entity/Group";
import {useRenderLink} from "./LinkComponents";
import ReactMarkdown from "react-markdown";

interface GroupCardProps {
  group: Group;
}

export function GroupCard({group}: GroupCardProps) {
  return (
      <Card>
        <CardActionArea component={useRenderLink(`/group/${group.id}`)}>
          <CardContent>
            <Typography variant="h5">{group.name}</Typography>
            <ReactMarkdown source={group.description} />
          </CardContent>
        </CardActionArea>
      </Card>
  );
}
