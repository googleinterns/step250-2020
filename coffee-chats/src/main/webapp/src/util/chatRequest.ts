const chatRequestURL = '/api/chat-request';

const STATUS_OK = 200;

/**
 * A function to handle formatting and sending the data for the chat request.
 * It returns whether the request was successfully accepted or not (does not 
 * indicate whether the chat request has been fulfilled yet).
 * 
 * @param interests - Array of user inputted tags they prefer to speak about.
 * @param dates - Array of dates user wants to speak on.
 * @param numPeopleRange - Array of two numbers with the range of number of participants.
 * @param durationMins - Duration of the requested chat in minutes.
 * @param matchRandom - Boolean to still match if no interest matches are found.
 * @param matchRecents - Boolean to still match with people spoken to recently.
 */
export const submitChatRequest = async (interests: string[], dates: Date[],
    numPeopleRange: number[], durationMins: number, matchRandom: boolean, 
    matchRecents: boolean) => {
  const data = new URLSearchParams();
  
  data.append('tags', interests.toString());
  data.append('dates', dates.map(d => +d).toString());
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
