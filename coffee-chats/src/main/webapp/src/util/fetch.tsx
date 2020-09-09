import React from "react";
import {Typography} from "@material-ui/core";

const LOADING = -1;

interface FetchResult<T> {
  errorCode: number; // error code from fetch, -1 if the fetch is not completed yet
  message: string; // error message
  data: T | null; // data, null if the fetch is not completed yet
}

interface FetchContext<T> {
  result: FetchResult<T>;

  reload: () => void; // when called forces the data to be fetched again
  value: T;
  set: (arg: T) => void;
}

/**
 * A hook that fetches JSON data from a URL using a GET request
 *
 * @param url: URL to fetch data from
 * @returns FetchContext<T>: an object, whose result field contains the result of the fetch
 */
export function useFetch<T>(url: string): FetchContext<T> {
  const [result, setResult] = React.useState<FetchResult<T>>({
    errorCode: LOADING,
    message: "Loading",
    data: null
  });

  const [forceUpdate, setForceUpdate] = React.useState(0);
  const requestId = React.useRef(0);

  React.useEffect(() => {
    (async () => {
      const currentRequestId = ++requestId.current;
      const response = await fetch(url);
      const json = await response.json();
      const raceOccurred = requestId.current !== currentRequestId;

      if (raceOccurred) {
        return;
      }

      if (!response.ok) {
        setResult({
          errorCode: json.errorCode,
          message: json.message,
          data: null
        });

        if (json.loginUrl) {
          window.location.href = json.loginUrl;
        }
      } else {
        setResult({
          errorCode: response.status,
          message: "",
          data: json
        });
      }
    })();
  }, [url, forceUpdate]);

  return {
    result,
    reload: () => setForceUpdate(forceUpdate + 1),
    value: result.data as T,
    set: (value) => {
      setResult({
        ...result, data: value
      })
    }
  };
}

/**
 * Accepts any number of FetchContexts and returns true if any of them
 * have either failed or are still loading.
 */
export function hasFetchFailed(...data: FetchContext<any>[]): boolean {
  return data.find(item => item.result.data === null) !== undefined;
}

/**
 * Accepts any number of FetchContexts and returns an appropriate error page,
 * assuming that at least one of them failed or is still loading.
 */
export function getFetchErrorPage(...data: FetchContext<any>[]) {
  const error = data.find(item => item.result.data === null);

  if (!error || error.result.errorCode === LOADING) {
    return null;
  }

  return (
      <Typography variant="h5">{error.result.message}</Typography>
  );
}

/**
 * Sends a POST request to the specified URL, passing specified data as parameters
 *
 * @param url: URL to send the POST request to
 * @param data: Data to pass as urlencoded parameters
 */
export function postData(url: string, data: Map<string, string>): Promise<Response> {
  const requestBody = new URLSearchParams();

  data.forEach((value, key) => {
    requestBody.append(key, value);
  });

  return fetch(url, {
    "method": "POST",
    body: requestBody,
    headers: {
      "Content-Type": "application/x-www-form-urlencoded"
    }
  });
}
