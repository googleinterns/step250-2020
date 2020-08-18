import React from "react";
import "./App.css";
import {MainPage} from "./components/MainPage";
import {NavBar} from "./components/NavBar";
import {HashRouter as Router, Switch, Route} from "react-router-dom";
import {GroupListPage} from "./components/GroupListPage";
import {useFetch} from "./util/fetch";
import {AuthState} from "./entity/AuthState";

function App() {
  // this will automatically redirect to the login page if not logged in
  const authState: AuthState | null = useFetch("/api/auth")[0];

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

          <Route path="/">
            <MainPage/>
          </Route>
        </Switch>
      </Router>
  );
}

export default App;
