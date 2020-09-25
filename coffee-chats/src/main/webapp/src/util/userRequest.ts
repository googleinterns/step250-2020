import { User } from "../entity/User";

const usersInfoURL = (userIds: string[]) => `/api/users-info?userIds=[${userIds.toString()}]`;

export const fetchUsersInfo = (userIds: string[], setUsers: (users: User[]) => void) => {
  fetch(usersInfoURL(userIds))
  .then(resp => {
    if (resp.ok) {
      return resp.json();
    }
  })
  .then(data => data as User[])
  .then(users => {
    setUsers(users);
  })
}