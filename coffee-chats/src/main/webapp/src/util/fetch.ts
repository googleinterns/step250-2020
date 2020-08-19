import React from "react";

/**
 * A hook that fetches JSON data from a URL using a GET request
 *
 * @param url: URL to fetch data from
 */
export function useFetch(url: string): any {
  const [data, setData] = React.useState(null);
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
  }, [url]);

  return data;
}
