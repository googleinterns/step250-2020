export interface ChatRequest {
  tags: string[],
  dateRanges: DateRange[],
  minPeople: number,
  maxPeople: number,
  durationMins: number,
  matchRandom: boolean,
  matchRecents: boolean,
  userId: string,
  requestId: number
};

export type CompletedRequest = ExpiredRequest | MatchedRequest;

export interface MatchedRequest {
  matched: boolean,
  dateRange: DateRange,
  durationMins: number,
  userId: string,
  tags: string[],
  participants: string[],
  commonTags: string[]
};

export interface ExpiredRequest {
  matched: boolean,
  durationMins: number,
  userId: string,
  tags: string[],
  dateRanges: DateRange[],
  minPeople: number,
  maxPeople: number,
  matchRandom: boolean,
  matchRecents: boolean,
};

export interface DateRange {
  start: Date,
  end: Date
};

export interface ChatRequestResponse {
  tags: string[],
  dateRanges: DateRangeResponse[],
  minPeople: number,
  maxPeople: number,
  duration: DurationResponse,
  matchRandom: boolean,
  matchRecents: boolean,
  userId: string,
  requestId: number
  hasRequestId: boolean
};

export type CompletedRequestResponse = MatchedRequestResponse | ExpiredRequestResponse;

interface MatchedRequestResponse {
  matched: boolean,
  firstDateRange: DateRangeResponse,
  duration: DurationResponse,
  userId: string,
  tags: string[],
  participants: string[],
  commonTags: string[]
};

interface ExpiredRequestResponse {
  matched: boolean,
  firstDateRange: DateRangeResponse,
  duration: DurationResponse,
  userId: string,
  tags: string[],
  dateRanges: DateRangeResponse[],
  minPeople: number,
  maxPeople: number,
  matchRandom: boolean,
  matchRecents: boolean
};

interface DateRangeResponse {
  start: string,
  end: string
};

interface DurationResponse {
  seconds: number,
  nanos: number
};

export const isPending = (request: ChatRequest | CompletedRequest): request is ChatRequest => {
  return (request as ChatRequest).requestId !== undefined;
};

export const isMatched = (request: ChatRequest | CompletedRequest): request is MatchedRequest => {
  return (request as MatchedRequest).participants !== undefined;
};

const isMatchedResponse = (response: CompletedRequestResponse): response is MatchedRequestResponse => {
  return (response as MatchedRequestResponse).participants !== undefined;
};

export const respToChatRequest = (response: ChatRequestResponse): ChatRequest => {
  let request: ChatRequest = {
    tags: response.tags,
    dateRanges: response.dateRanges.map(respToDateRange),
    minPeople: response.minPeople,
    maxPeople: response.maxPeople,
    durationMins: respToDurationMins(response.duration),
    matchRandom: response.matchRandom,
    matchRecents: response.matchRecents,
    userId: response.userId,
    requestId: response.requestId
  };

  return request;
};

export const respToCompletedRequests = (response: CompletedRequestResponse): CompletedRequest => {
  if (isMatchedResponse(response)) {
    const request: MatchedRequest = {
      matched: response.matched,
      dateRange: respToDateRange(response.firstDateRange),
      durationMins: respToDurationMins(response.duration),
      userId: response.userId,
      tags: response.tags,
      participants: response.participants,
      commonTags: response.commonTags
    };

    return request;
  } else {
    const request: ExpiredRequest = {
      matched: response.matched,
      durationMins: respToDurationMins(response.duration),
      userId: response.userId,
      tags: response.tags,
      dateRanges: response.dateRanges.map(respToDateRange),
      minPeople: response.minPeople,
      maxPeople: response.maxPeople,
      matchRandom: response.matchRandom,
      matchRecents: response.matchRecents
    };

    return request;
  }
};

const respToDateRange = (response: DateRangeResponse): DateRange => (
  {
    start: new Date(response.start),
    end: new Date(response.end)
  }
);

const respToDurationMins = (response: DurationResponse): number => (
  (response.seconds / 60) + (response.nanos / (60 * 1000))
);