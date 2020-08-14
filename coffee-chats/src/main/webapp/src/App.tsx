import React from "react";
import "./App.css";
import {MainPage} from "./components/MainPage";
import {NavBar} from "./components/NavBar";
import {HashRouter as Router, Switch, Route} from "react-router-dom";
import {GroupListPage} from "./components/GroupListPage";

function App() {
  return (
      <Router>
        <NavBar/>
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
