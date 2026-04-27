import axios from "axios";

const axiosInstance = axios.create({
  baseURL: process.env.REACT_APP_API_URL || "http://localhost:8080",
  timeout: parseInt(process.env.REACT_APP_API_TIMEOUT || '60000'),
  headers: {
    "Content-Type": "application/json",
  },
});

export default axiosInstance;
