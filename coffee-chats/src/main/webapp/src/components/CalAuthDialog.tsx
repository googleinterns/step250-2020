import React from 'react'
import {Dialog, DialogContent, DialogTitle, DialogContentText, DialogActions, Button} from '@material-ui/core'

interface CalAuthDialogProps {
  submitAuthRequest: () => void,
  open: boolean,
  setOpen: (open: boolean) => void
}

export const CalAuthDialog: React.FC<CalAuthDialogProps> = ({submitAuthRequest, open, setOpen}) => {  
  const dialogClose = () => {
    setOpen(false);
  };
  
  return (
    <Dialog
      open={open}
      onClose={dialogClose}
      aria-labelledby="authorise-dialog-title"
      aria-describedby="authorise-dialog-description"
    >
      <DialogTitle id="authorise-dialog-title">
        Authorise access to your Google Calendar?
      </DialogTitle>
      <DialogContent>
        <DialogContentText id="auhtorise-dialog-description">
          To schedule this chat into your calendar, this app requires access to
          your Google calendar to check for availability and to create the 
          event once you are matched.
        </DialogContentText>
        <DialogActions>
          <Button onClick={dialogClose} color="primary">
            Cancel
          </Button>
          <Button onClick={submitAuthRequest} color="primary" variant="contained">
            Authorise
          </Button>
        </DialogActions>
      </DialogContent>
    </Dialog>
  )
};
