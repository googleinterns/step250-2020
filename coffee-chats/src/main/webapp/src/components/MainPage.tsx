import React from "react";
import {
  Box, Button,
  Container,
  Grid,
  Icon,
  IconButton,
  InputAdornment,
  TextField,
  Tooltip,
  Typography
} from "@material-ui/core";
import {ConnectBackCard} from "./ConnectBackCard";

export function MainPage() {
  return (
      <Box mt={4}>
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
            <Grid container justify="center">
              <Button
                  variant="text"
                  startIcon={<Icon>explore</Icon>}
                  color="primary"
                  size="large">
                Explore Groups
              </Button>
            </Grid>
          </Box>

          <Box mt={2}>
            <Grid container>
              <Grid item md={4}>
                <ConnectBackCard names={["Natalie Lynn", "Ian Hall"]} tags={["movies"]} />
              </Grid>
            </Grid>
          </Box>
        </Container>
      </Box>
  );
}