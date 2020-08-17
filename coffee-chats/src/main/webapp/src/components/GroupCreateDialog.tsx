import React from "react";
import {Button, Dialog, DialogActions, DialogContent,
  DialogContentText, DialogTitle, TextField} from "@material-ui/core";

interface GroupCreateDialogProps {
  open: boolean;
  setOpen: (value: boolean) => void;
}

export function GroupCreateDialog({open, setOpen}: GroupCreateDialogProps) {
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
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)} color="primary">Create</Button>
        </DialogActions>
      </Dialog>
  );
}
