import React, { useEffect } from "react";
import { getUsers } from "../api/userService";

function Users() {
  useEffect(() => {
    getUsers()
      .then(res => console.log(res.data))
      .catch(err => console.error(err));
  }, []);

  return <div>Users</div>;
}

export default Users;
