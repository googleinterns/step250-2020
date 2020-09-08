import React from 'react';
import {render} from '@testing-library/react';
import {MainPage} from './MainPage';
import {MuiPickersUtilsProvider} from '@material-ui/pickers';
import DateFnsUtils from '@date-io/date-fns';

test('renders Coffee Chats title', () => {
  const { getByText } = render(
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <MainPage/>
    </MuiPickersUtilsProvider>
  );
  const title = getByText(/coffee chats/i);
  expect(title).toBeInTheDocument();
});
