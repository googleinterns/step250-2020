import React from "react";
import "./App.css";
import {MainPage} from "./pages/MainPage";
import {NavBar} from "./components/NavBar";
import { MuiPickersUtilsProvider } from "@material-ui/pickers"
import DateFnsUtils from "@date-io/date-fns"
import {HashRouter as Router, Switch, Route} from "react-router-dom";
import {useFetchOnce} from "./util/fetch";
import {AuthState} from "./entity/AuthState";
import {GroupListPage} from "./pages/GroupListPage";
import {GroupInfoPage} from "./pages/GroupInfoPage";

function App() {
  // this will automatically redirect to the login page if not logged in
  const authState: AuthState | null = useFetchOnce("/api/auth");

  if (authState == null) {
    return null;
  }

  return (
      <Router>
        <NavBar logoutUrl={authState?.logoutUrl}/>
        <Switch>
          <Route path="/groups">
            <GroupListPage/>
          </Route>

          <Route path="/group/:groupId">
            <GroupInfoPage/>
          </Route>

          <Route path="/">
            <MuiPickersUtilsProvider utils={DateFnsUtils}>
              <MainPage/>
            </MuiPickersUtilsProvider>
          </Route>
        </Switch>
      </Router>
  );
}

export default App;
