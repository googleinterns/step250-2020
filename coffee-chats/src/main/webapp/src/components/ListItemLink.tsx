import React from "react";
import {Link, useRouteMatch} from "react-router-dom";
import {ListItem, ListItemText} from "@material-ui/core";

interface ListItemLinkProps {
  to: string
  primary: string
}

export function ListItemLink({to, primary}: ListItemLinkProps) {
  const match = useRouteMatch(to)?.isExact;

  const renderLink = React.useMemo(
      () => React.forwardRef(itemProps => <Link to={to} {...itemProps} />),
      [to],
  );

  return (
      <ListItem button component={renderLink} selected={match}>
        <ListItemText primary={primary} />
      </ListItem>
  );
}
