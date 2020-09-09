import React from "react";
import "./App.css";
import {MainPage} from "./pages/MainPage";
import {NavBar} from "./components/NavBar";
import {MuiPickersUtilsProvider} from "@material-ui/pickers"
import DateFnsUtils from "@date-io/date-fns"
import {HashRouter as Router, Switch, Route} from "react-router-dom";
import {useFetch} from "./util/fetch";
import {AuthState, AuthStateContext} from "./entity/AuthState";
import {GroupListPage} from "./pages/GroupListPage";
import {GroupInfoPage} from "./pages/GroupInfoPage";
import {GroupEditPage} from "./pages/GroupEditPage";

function App() {
  // this will automatically redirect to the login page if not logged in
  const authState = useFetch<AuthState>("/api/auth");

  if (authState.result.data === null) {
    return null;
  }

  return (
      <AuthStateContext.Provider value={authState.value}>
        <Router>
          <NavBar />
          <Switch>
            <Route path="/groups">
              <GroupListPage/>
            </Route>

            <Route path="/group/:groupId/edit">
              <GroupEditPage/>
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
      </AuthStateContext.Provider>
  );
}

export default App;
