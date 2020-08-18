import React from "react";

/**
 * A hook that fetches JSON data from a URL using a GET request
 *
 * @param url: URL to fetch data from
 */
export function useFetch(url: string): any {
  const [data, setData] = React.useState(null);

  React.useEffect(() => {
    (async () => {
      const response = await fetch(url);
      const json = await response.json();

      if (!response.ok && json.loginUrl) {
        window.location.href = json.loginUrl;
      } else {
        setData(json);
      }
    })();
  }, [url]);

  return data;
}
