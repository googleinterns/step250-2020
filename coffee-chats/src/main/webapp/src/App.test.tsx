import React from 'react';
import { render } from '@testing-library/react';
import App from './App';

test('renders Hello World message', () => {
  const { getByText } = render(<App />);
  const title = getByText(/coffee chats/i);
  expect(title).toBeInTheDocument();
});
