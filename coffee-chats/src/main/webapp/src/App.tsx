import React from 'react';
import logo from './logo.svg';
import './App.css';
import {
  Container, TextField, Typography, Box, Tooltip,
  Icon, Button, Grid, InputAdornment, IconButton} from "@material-ui/core";

function App() {
  return (
    <Box mt={8}>
      <Container maxWidth="md">
        <Typography variant="h3" align="center" gutterBottom>
          Coffee Chats
        </Typography>
        <TextField
            variant="outlined"
            placeholder="What do you want to chat about?"
            fullWidth={true}
            InputProps={{endAdornment:
              <InputAdornment position="end">
                <Tooltip title="Any topic">
                  <IconButton
                      aria-label="chat about any topic">
                    <Icon>casino</Icon>
                  </IconButton>
                </Tooltip>
              </InputAdornment>}}
        />
        <Box mt={2}>
          <Grid
              container
              justify="center"
          >
            <Button
                variant="text"
                startIcon={<Icon>explore</Icon>}
                color="primary"
                size="large">
              Explore Groups
            </Button>
          </Grid>
        </Box>
      </Container>
    </Box>
  );
}

export default App;
