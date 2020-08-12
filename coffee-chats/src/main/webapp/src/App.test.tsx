import React from 'react';
import { render } from '@testing-library/react';
import App from './App';

test('renders Coffee Chats title', () => {
  const { getByText } = render(<App />);
  const title = getByText(/coffee chats/i);
  expect(title).toBeInTheDocument();
});
