const tagsURL = '/api/tags';

export const fetchTags = () => {
  return fetch(tagsURL)
  .then((response) => response.json())
  .then((data) => (
    data as string[]
  ))
  .catch((reason) => {
    console.log("Tag fetch failed: " + reason.toString());
    return [];
  })
};