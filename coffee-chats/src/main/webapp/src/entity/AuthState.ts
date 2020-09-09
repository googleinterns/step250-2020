import {User} from "./User";

export interface AuthState {
  logoutUrl: string;
  user: User;
};

export interface CalAuthState {
  authorised: boolean,
  authLink: string
};