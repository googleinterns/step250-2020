import {User} from "./User";
import React from "react";

export interface AuthState {
  logoutUrl: string;
  user: User;
}

// Yes, there's an ugly cast, but the state will never be used uninitialized
export const AuthStateContext = React.createContext<AuthState>({} as any);
