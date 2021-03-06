import {MaterialUiPickersDate} from "@material-ui/pickers/typings/date";
import {ChatRequestResponse, respToChatRequest, ChatRequest, CompletedRequest, CompletedRequestResponse, respToCompletedRequests} from "../entity/Requests";

const chatRequestURL = '/api/chat-request';
const getRequestsURL = (type: string) => (`/api/requests?type=${type}`);

const STATUS_OK = 200;

/**
 * A function to handle formatting and sending the data for the chat request.
 * It returns whether the request was successfully accepted or not (does not 
 * indicate whether the chat request has been fulfilled yet).
 * 
 * @param interests - Array of user inputted tags they prefer to speak about.
 * @param startDates - Array of starting datetimes user wants to speak on.
 * @param endDates - Array of ending datetimes user wants to speak on.
 * @param numPeopleRange - Array of two numbers with the range of number of participants.
 * @param durationMins - Duration of the requested chat in minutes.
 * @param matchRandom - Boolean to still match if no interest matches are found.
 * @param matchRecents - Boolean to still match with people spoken to recently.
 */
export const submitChatRequest = async (interests: string[],
    startDates: MaterialUiPickersDate[], endDates: MaterialUiPickersDate[],
    numPeopleRange: number[], durationMins: number, matchRandom: boolean,
    matchRecents: boolean) => {
  const data = new URLSearchParams();
  
  data.append('tags', interests.toString());
  data.append('startDates', startDates.map(d => +d!).toString());
  data.append('endDates', endDates.map(d => +d!).toString());
  data.append('minPeople', numPeopleRange[0].toString());
  data.append('maxPeople', numPeopleRange[1].toString());
  data.append('durationMins', durationMins.toString());
  data.append('matchRandom', matchRandom.toString());
  data.append('matchRecents', matchRecents.toString());
  
  const options = {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: data.toString()
  };

  try {
    const response = await fetch(chatRequestURL, options);
    return (response.status === STATUS_OK);
  }
  catch (reason) {
    console.log('Chat request failed: ' + reason.toString());
    return false;
  }
};

/**
 * Fetches pending requests from backend for the current user, updates state with the response via 
 * given function.
 * 
 * @param setPendingRequests - Function to set the current pending requests user has.
 */
export const fetchPendingRequests = (setPendingRequests: (requests: ChatRequest[]) => void) => {
  fetch(getRequestsURL('pending'))
  .then(response => {
    if (!response.ok) {
      return null;
    }

    return response.json()
  })
  .then(data => data as ChatRequestResponse[])
  .then(respData => respData.map(respToChatRequest))
  .then(chatRequests => setPendingRequests(chatRequests))
};

/**
 * Fetches completed requests from backend for the current user, updates state with the response 
 * via given function.
 * 
 * @param setCompletedRequests - Function to set the current completed requests the user has.
 */
export const fetchCompletedRequests = 
    (setCompletedRequests: (requests: CompletedRequest[]) => void) => {
  fetch(getRequestsURL('completed'))
  .then(response => {
    if (!response.ok) {
      return null;
    }

    return response.json()
  })
  .then(data => data as CompletedRequestResponse[])
  .then(respData => respData.map(respToCompletedRequests))
  .then(requests => setCompletedRequests(requests))
};