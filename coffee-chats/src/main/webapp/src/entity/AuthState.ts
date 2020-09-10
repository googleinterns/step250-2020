import {User} from "./User";
import React from "react";

export interface AuthState {
  logoutUrl: string;
  user: User;
}

export const AuthStateContext = React.createContext<AuthState>({
  logoutUrl: "",
  user: {
    id: ""
  }
});

export interface OAuthState {
  authorised: boolean,
  authLink: string
}
