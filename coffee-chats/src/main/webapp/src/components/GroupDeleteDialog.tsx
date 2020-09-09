import React from "react";
import {Button, Dialog, DialogActions, DialogContent,
  DialogContentText, DialogTitle} from "@material-ui/core";
import {Group} from "../entity/Group";
import {useHistory} from "react-router-dom";
import {postData} from "../util/fetch";

interface GroupDeleteDialogProps {
  group: Group;
  open: boolean;
  setOpen: (value: boolean) => void;
}

export function GroupDeleteDialog({group, open, setOpen}: GroupDeleteDialogProps) {
  const history = useHistory();

  const submit = async () => {
    setOpen(false);

    const data = new Map();
    data.set("id", group.id);
    await postData("/api/groupDelete", data);

    history.push("/groups");
  };

  return (
      <Dialog open={open} onClose={() => setOpen(false)}>
        <DialogTitle>Delete group &ldquo;{group.name}&rdquo;</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete the group?
            This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button onClick={submit} color="secondary">Delete</Button>
        </DialogActions>
      </Dialog>
  );
}
