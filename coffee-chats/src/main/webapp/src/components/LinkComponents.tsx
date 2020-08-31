import React from "react";
import {Link, useRouteMatch} from "react-router-dom";
import {ListItem, ListItemText} from "@material-ui/core";

export function useRenderLink(to: string) {
  return React.useMemo(
      () => React.forwardRef(itemProps => <Link to={to} {...itemProps} />),
      [to],
  );
}

interface ListItemLinkProps {
  to: string
  primary: string
}

export function ListItemLink({to, primary}: ListItemLinkProps) {
  const match = useRouteMatch(to)?.isExact;
  const renderLink = useRenderLink(to);

  return (
      <ListItem button component={renderLink} selected={match}>
        <ListItemText primary={primary} />
      </ListItem>
  );
}
