import React from "react";

/**
 * A hook that fetches JSON data from a URL using a GET request
 *
 * @param url: URL to fetch data from
 * @param initial: Initial value, returned when no data was fetched yet
 * @returns a pair: data and function that causes the data to be fetched again when called
 */
export function useFetch(url: string, initial: any = null): [any, () => void] {
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
  }];
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
 * Sends a POST request to the specified URL, passing specified data as parameters
 *
 * @param url: URL to send the POST request to
 * @param data: Data to pass as urlencoded parameters
 */
export async function postData(url: string, data: Map<string, string>) {
  const requestBody = new URLSearchParams();

  data.forEach((value, key) => {
    requestBody.append(key, value);
  });

  await fetch(url, {
    "method": "POST",
    body: requestBody,
    headers: {
      "Content-Type": "application/x-www-form-urlencoded"
    }
  });
}
