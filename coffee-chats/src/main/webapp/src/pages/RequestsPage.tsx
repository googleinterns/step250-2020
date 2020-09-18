import React, { useState, ChangeEvent } from 'react'
import { Box, Container, Typography, Tabs, Tab } from '@material-ui/core'
import { ViewRequestAccordion } from '../components/ViewRequestAccordion';

export const RequestsPage = () => {
  const [currTab, setCurrTab] = useState(0);
  const PENDING_TAB = 0;
  const COMPLETED_TAB = 1;

  return (
    <Box mt={2}>
      <Container maxWidth="md">
        <Typography variant="h4" align="left" gutterBottom>
          Requests
        </Typography>

        <Tabs
          value={currTab}
          onChange={(_event: ChangeEvent<{}>, newValue: number) => setCurrTab(newValue)}
          indicatorColor="primary"
          variant="fullWidth"
          aria-label="switch between pending and completed requests"
        >
          <Tab label="Pending" />
          <Tab label="Completed" />
        </Tabs>

        {currTab === PENDING_TAB &&
          <ViewRequestAccordion tags={['Photography', 'Random']} status="pending" />
        }

        {currTab === COMPLETED_TAB &&
          <>
            <ViewRequestAccordion tags={['Football', 'Hip Hop', 'Gardening']} status="matched" />
            <ViewRequestAccordion tags={['Basketball', 'NBA']} status="expired" />
            <ViewRequestAccordion tags={['Hiking', 'Marathons']} status="matched" />
          </>
        }
      </Container>
    </Box>
  )
}
