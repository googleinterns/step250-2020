const chatRequestURL = '/chat-request'

const STATUS_OK = 200;

export const submitChatRequest = async (interests: string[], dates: Date[], numPeople: boolean[], duration: number, randomMatch: boolean, pastMatched: boolean) => {
  const data = new URLSearchParams();
  
  data.append('tags', interests.toString());
  data.append('dates', dates.map(d => +d).toString());
  for (let i = 1; i <= numPeople.length; i++) {
    data.append('participants' + i.toString(), numPeople[i - 1].toString())
  }
  data.append('duration', duration.toString());
  data.append('randomMatch', randomMatch.toString());
  data.append('pastMatched', pastMatched.toString());
  
  const options = {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: data.toString()
  }

  try {
    const response = await fetch(chatRequestURL, options);
    return (response.status === STATUS_OK);
  }
  catch (reason) {
    console.log('Chat request failed: ' + reason.toString());
    return false;
  }
}