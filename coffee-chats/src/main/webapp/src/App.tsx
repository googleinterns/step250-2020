import React, {useEffect, useState} from "react";
import {MainPage} from "./pages/MainPage";
import {NavBar} from "./components/NavBar";
import {MuiPickersUtilsProvider} from "@material-ui/pickers"
import DateFnsUtils from "@date-io/date-fns"
import {HashRouter as Router, Switch, Route} from "react-router-dom";
import {getFetchErrorPage, hasFetchFailed, useFetch} from "./util/fetch";
import {AuthState, AuthStateContext} from "./entity/AuthState";
import {GroupListPage} from "./pages/GroupListPage";
import {GroupInfoPage} from "./pages/GroupInfoPage";
import {GroupEditPage} from "./pages/GroupEditPage";
import {OAuthDialog} from "./components/OAuthDialog";
import { RequestsPage } from "./pages/RequestsPage";

function App() {
  // this will automatically redirect to the login page if not logged in
  const authState = useFetch<AuthState>("/api/auth");
  const [oauthDialogOpen, setOAuthDialogOpen] = useState(false);

  useEffect(() => {
    if (authState.value && !authState.value.oauthAuthorized) {
      setOAuthDialogOpen(true);
    }
  }, [authState.value]);

  if (hasFetchFailed(authState)) {
    return getFetchErrorPage(authState);
  }

  return (
      <AuthStateContext.Provider value={authState.value}>
        <Router>
          <NavBar openOAuthDialog={() => setOAuthDialogOpen(true)} />
          <OAuthDialog open={oauthDialogOpen} setOpen={setOAuthDialogOpen} />
          <Switch>
            <Route path="/groups">
              <GroupListPage />
            </Route>

            <Route path="/group/:groupId/edit">
              <GroupEditPage />
            </Route>

            <Route path="/group/:groupId">
              <GroupInfoPage />
            </Route>

            <Route path="/requests">
              <RequestsPage />
            </Route>

            <Route path="/">
              <MuiPickersUtilsProvider utils={DateFnsUtils}>
                <MainPage />
              </MuiPickersUtilsProvider>
            </Route>
          </Switch>
        </Router>
      </AuthStateContext.Provider>
  );
}

export default App;
