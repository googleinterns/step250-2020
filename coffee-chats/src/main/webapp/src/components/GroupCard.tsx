import React from "react";
import {Card, CardActionArea, CardContent, Typography} from "@material-ui/core";
import {Group} from "../entity/Group";
import {useRenderLink} from "./LinkComponents";
import ReactMarkdown from "react-markdown";

interface GroupCardProps {
  children: React.ReactNode;
  group: Group;
  clickable: boolean;
}

export function GroupCard({group, children, clickable}: GroupCardProps) {
  const inner = (<React.Fragment>
    <CardContent>
      <Typography variant="h5">{group.name}</Typography>
      <ReactMarkdown source={group.description} />
    </CardContent>
    {children}
  </React.Fragment>);

  const link = useRenderLink(`/group/${group.id}`);

  return (
      <Card>
        {clickable ?
        <CardActionArea component={link}>
          {inner}
        </CardActionArea> : inner}
      </Card>
  );
}

GroupCard.defaultProps = {
  children: null,
  clickable: true
};
