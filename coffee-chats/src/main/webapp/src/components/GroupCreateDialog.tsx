import React, {useState} from "react";
import {Button, Dialog, DialogActions, DialogContent,
  DialogContentText, DialogTitle, TextField} from "@material-ui/core";
import {postData} from "../util/fetch";

interface GroupCreateDialogProps {
  open: boolean;
  setOpen: (value: boolean) => void;
  onSubmit: () => void;
}

export function GroupCreateDialog({open, setOpen, onSubmit}: GroupCreateDialogProps) {
  const [name, setName] = useState("");

  const submit = async () => {
    setOpen(false);

    const data = new Map();
    data.set("name", name);
    await postData("/api/groupCreate", data);

    onSubmit();
  };

  return (
      <Dialog open={open} onClose={() => setOpen(false)}>
        <DialogTitle>Create a New Group</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Enter the group's name below.
            Please use a descriptive name.
          </DialogContentText>
          <TextField
              autoFocus
              fullWidth
              margin="dense"
              label="Group Name"
              value={name}
              onChange={(e) => setName(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={submit} color="primary">Create</Button>
        </DialogActions>
      </Dialog>
  );
}
