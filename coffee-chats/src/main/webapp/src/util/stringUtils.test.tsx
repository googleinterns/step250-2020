import {capitaliseEachWord} from "./stringUtils";

test('capitalising empty string is empty', () => {
  const input = '';
  const output = capitaliseEachWord(input);
  expect(output).toBe('');
});

test('capitalising single word does not affect other letters', () => {
  const input = 'testWord';
  const output = capitaliseEachWord(input);
  expect(output).toBe('TestWord');
})

test('capitalizes every space-separated word', () => {
  const input = 'hyphen-separated Words and a number 3.';
  const output = capitaliseEachWord(input);
  expect(output).toBe('Hyphen-separated Words And A Number 3.')
})
