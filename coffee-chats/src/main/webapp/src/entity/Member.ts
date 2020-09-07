import {User} from "./User";

export type MembershipStatus = "NOT_A_MEMBER" | "REGULAR_MEMBER" | "ADMINISTRATOR" | "OWNER";

export interface Member {
  user: User;
  status: MembershipStatus
}
