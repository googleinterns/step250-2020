import React from 'react';
import {render} from '@testing-library/react';
import {MainPage} from './MainPage';

test('renders Coffee Chats title', () => {
  const { getByText } = render(<MainPage />);
  const title = getByText(/coffee chats/i);
  expect(title).toBeInTheDocument();
});
