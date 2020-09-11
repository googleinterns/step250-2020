import React from 'react'
import {Dialog, DialogContent, DialogTitle, DialogContentText, DialogActions, Button} from '@material-ui/core'
import {AuthState, AuthStateContext} from "../entity/AuthState";

interface OAuthDialogProps {
  open: boolean,
  setOpen: (open: boolean) => void
}

export const OAuthDialog: React.FC<OAuthDialogProps> = ({open, setOpen}) => {
  const authState: AuthState = React.useContext(AuthStateContext);

  const dialogClose = () => {
    setOpen(false);
  };

  const submitAuthRequest = () => {
    window.location.href = authState.oauthLink!;
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
          This app requires access to your Google calendar
          to check for availability and to create the event once you are matched.
        </DialogContentText>
        <DialogActions>
          <Button onClick={dialogClose} color="primary">
            Later
          </Button>
          <Button onClick={submitAuthRequest} color="primary" variant="contained">
            Authorise
          </Button>
        </DialogActions>
      </DialogContent>
    </Dialog>
  )
};
