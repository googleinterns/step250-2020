import {User} from "./User";

export interface AuthState {
  logoutUrl: string;
  user: User;
}
