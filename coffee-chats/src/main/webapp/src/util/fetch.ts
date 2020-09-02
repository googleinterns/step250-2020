import React from "react";

function useFetchImpl<T>(url: string, initial: T): [T, () => void, (arg: T) => void] {
  const [data, setData] = React.useState(initial);
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

      if (!response.ok && json.loginUrl) {
        window.location.href = json.loginUrl;
      } else {
        setData(json);
      }
    })();
  }, [url, forceUpdate]);

  return [data, () => {
    setForceUpdate(forceUpdate + 1);
  }, setData];
}

/**
 * A hook that fetches JSON data from a URL using a GET request
 *
 * @param url: URL to fetch data from
 * @param initial: Initial value, returned when no data was fetched yet
 * @returns a pair: data and function that causes the data to be fetched again when called
 */
export function useFetch(url: string, initial: any = null): [any, () => void] {
  const [data, forceUpdate] = useFetchImpl(url, initial);
  return [data, forceUpdate];
}

/**
 * A hook that fetches JSON data from a URL using a GET request.
 * Unlike useFetch it doesn't return a function that forces the data to be fetched again.
 *
 * @param url: URL to fetch data from
 * @param initial: Initial value, returned when no data was fetched yet
 * @returns JSON-decoded data
 */
export function useFetchOnce(url: string, initial: any = null): any {
  const [data] = useFetch(url, initial);
  return data;
}

/**
 * A hook that fetches JSON data from a URL using a GET request.
 * It fetches the data once and returns a state, like the useState hook does.
 *
 * @param url: URL to fetch data from
 * @param initial: Initial value, returned when no data was fetched yet
 * @returns a pair: data and setter function for this data.
 */
export function useStatefulFetch<T>(url: string, initial: T | null = null): [T | null, (arg: T) => void] {
  const [data,, setData] = useFetchImpl(url, initial);
  return [data, setData];
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
