import React from "react";
import "./App.css";
import {MainPage} from "./components/MainPage";
import {NavBar} from "./components/NavBar";
import { MuiPickersUtilsProvider } from "@material-ui/pickers"
import DateFnsUtils from "@date-io/date-fns"

function App() {
  return (
      <React.Fragment>
        <MuiPickersUtilsProvider utils={DateFnsUtils}>
          <NavBar/>
          <MainPage/>
        </MuiPickersUtilsProvider>
      </React.Fragment>
  )
}

export default App;
