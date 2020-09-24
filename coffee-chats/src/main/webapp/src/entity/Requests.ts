interface RequestParams {
  tags: string[],
  dateRanges: DateRange[],
  minPeople: number,
  maxPeople: number,
  durationMins: number,
  matchRandom: boolean,
  matchRecents: boolean,
  userId: string
}

/**
 * Exposed interfaces for using and managing entites
 */
export interface ChatRequest extends RequestParams {
  requestId: number
};

export interface MatchedRequest {
  matched: boolean,
  dateRange: DateRange,
  durationMins: number,
  userId: string,
  tags: string[],
  participants: string[],
  commonTags: string[]
};

export interface ExpiredRequest extends RequestParams {
  matched: boolean
};

export type CompletedRequest = ExpiredRequest | MatchedRequest;

export interface DateRange {
  start: Date,
  end: Date
};


/**
 * Internal interfaces for handling JSON format recieved via fetch requests.
 */
interface RequestParamsResponse {
  tags: string[],
  dateRanges: DateRangeResponse[],
  minPeople: number,
  maxPeople: number,
  duration: DurationResponse,
  matchRandom: boolean,
  matchRecents: boolean,
  userId: string
}

export interface ChatRequestResponse extends RequestParamsResponse{
  requestId: number
  hasRequestId: boolean
};

interface MatchedRequestResponse {
  matched: boolean,
  firstDateRange: DateRangeResponse,
  duration: DurationResponse,
  userId: string,
  tags: string[],
  participants: string[],
  commonTags: string[]
};

interface ExpiredRequestResponse extends RequestParamsResponse {
  matched: boolean,
  firstDateRange: DateRangeResponse
};

export type CompletedRequestResponse = MatchedRequestResponse | ExpiredRequestResponse;

interface DateRangeResponse {
  start: string,
  end: string
};

interface DurationResponse {
  seconds: number,
  nanos: number
};

/**
 * Identifies the given request as a pending request or not.
 * 
 * @param request - Request object of either ChatRequest or CompletedRequest type.
 * @returns type predicate identifying request as a ChatRequest (pending) or CompletedRequest.
 */
export const isPending = (request: ChatRequest | CompletedRequest): request is ChatRequest => {
  return (request as ChatRequest).requestId !== undefined;
};

/**
 * Identifies the given request as a matched request or not.
 * 
 * @param request - Request object of either ChatRequest, MatchedRequest or ExpiredRequest type.
 * @returns type predicate identifying request as a MatchedRequest or either a ChatRequest or 
 *          ExpiredRequest.
 */
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