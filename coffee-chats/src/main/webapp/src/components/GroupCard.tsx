import React from "react";
import {
  Box,
  Card,
  CardActionArea,
  CardContent,
  Chip,
  createStyles,
  makeStyles,
  Theme,
  Typography
} from "@material-ui/core";
import {Group} from "../entity/Group";
import {useRenderLink} from "./LinkComponents";
import ReactMarkdown from "react-markdown";

interface GroupCardProps {
  children: React.ReactNode;
  group: Group;
  clickable: boolean;
}

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    tagChip: {
      marginTop: 4,
      marginRight: 4
    }
  })
);

export function GroupCard({group, children, clickable}: GroupCardProps) {
  const classes = useStyles();

  const inner = (<React.Fragment>
    <CardContent>
      <Typography variant="h5">{group.name}</Typography>
      <ReactMarkdown source={group.description} />
      <Box mt={1}>
        {group.tags.map(tag =>
            <Chip variant="outlined" label={tag} className={classes.tagChip} />)}
      </Box>
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
