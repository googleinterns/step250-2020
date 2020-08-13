import React from "react";
import "./App.css";
import {MainPage} from "./components/MainPage";
import {NavBar} from "./components/NavBar";

function App() {
  return (
      <React.Fragment>
        <NavBar/>
        <MainPage/>
      </React.Fragment>
  );
}

export default App;
