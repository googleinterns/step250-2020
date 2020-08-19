/**
 * Joins a list of strings to produce a human readable string:
 *
 * > humanReadableJoin(["apple", "banana", "orange"])
 * "apple, banana, and orange"
 */
export function humanReadableJoin(list: string[]) {
  list = list.slice();
  return list.concat(list.splice(-2, 2).join(" and ")).join(", ");
}
