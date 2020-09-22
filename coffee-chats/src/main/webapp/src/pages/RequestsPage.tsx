import React, {useState, ChangeEvent, useEffect} from 'react';
import {Box, Container, Typography, Tabs, Tab} from '@material-ui/core';
import {ViewRequestAccordion} from '../components/ViewRequestAccordion';
import {ChatRequest, CompletedRequest} from '../entity/Requests';
import {fetchPendingRequests, fetchCompletedRequests} from '../util/chatRequest';

export const RequestsPage = () => {
  const PENDING_TAB = 0;
  const COMPLETED_TAB = 1

  const [currTab, setCurrTab] = useState(PENDING_TAB);
  const [pendingReqs, setPendingReqs] = useState<ChatRequest[]>([]);
  const [completedReqs, setCompletedReqs] = useState<CompletedRequest[]>([]);

  useEffect(() => {
    fetchPendingRequests(setPendingReqs);
    fetchCompletedRequests(setCompletedReqs);
  }, [])

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
          pendingReqs.map((req) => (
            <ViewRequestAccordion tags={req.tags} status="pending" key={req.requestId} />
          ))
        }

        {currTab === COMPLETED_TAB &&
          completedReqs.map((req, i) => (
            <ViewRequestAccordion tags={req.tags} status={req.matched ? "matched" : "expired"} key={i} />
          ))
        }
      </Container>
    </Box>
  )
}
