import {User} from "./User";

export interface Member {
  user: User;
  status: "NOT_A_MEMBER" | "REGULAR_MEMBER" | "ADMINISTRATOR" | "OWNER"
}
