import React from "react";

interface ResponseRaceDetect {
  response: Response;
  json: any;
  raceOccurred: boolean;
}

const fetchAndDetectRaces: (url: string) => Promise<ResponseRaceDetect> = (() => {
  let requestId = 0;

  return async (url: string) => {
    const currentRequestId = ++requestId;

    const response = await fetch(url);
    const json = await response.json();
    const raceOccurred = requestId != currentRequestId;

    return {response, json, raceOccurred};
  }
})();

/**
 * A hook that fetches JSON data from a URL using a GET request
 *
 * @param url: URL to fetch data from
 */
export function useFetch(url: string): any {
  const [data, setData] = React.useState(null);

  React.useEffect(() => {
    (async () => {
      const {response, json, raceOccurred} = await fetchAndDetectRaces(url);

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
