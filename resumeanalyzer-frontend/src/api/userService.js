import axios from "./axiosConfig";

// 👇 THIS IS WHERE axios.get(...) BELONGS
export const getUsers = () => {
  return axios.get("/users");
};
