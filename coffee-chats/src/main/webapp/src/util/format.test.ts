import {humanReadableJoin} from "./format";

test("humanReadableJoin empty array", () => {
  expect(humanReadableJoin([]))
      .toStrictEqual("");
});

test("humanReadableJoin one element", () => {
  expect(humanReadableJoin(["apple"]))
      .toStrictEqual("apple");
});

test("humanReadableJoin two elements", () => {
  expect(humanReadableJoin(["apple", "banana"]))
      .toStrictEqual("apple and banana");
});

test("humanReadableJoin three elements", () => {
  expect(humanReadableJoin(["apple", "banana", "orange"]))
      .toStrictEqual("apple, banana and orange");
});

test("humanReadableJoin five elements", () => {
  expect(humanReadableJoin(["apple", "banana", "cherry", "pineapple", "orange"]))
      .toStrictEqual("apple, banana, cherry, pineapple and orange");
});