import {User} from "./User";
import React from "react";

export interface AuthState {
  logoutUrl: string;
  user: User;
  oauthAuthorized: boolean;
  oauthLink: string | null;
}

export const AuthStateContext = React.createContext<AuthState>({
  logoutUrl: "",
  user: {
    id: "",
    email: "",
    name: "",
    avatarUrl: ""
  },
  oauthAuthorized: false,
  oauthLink: null
});
