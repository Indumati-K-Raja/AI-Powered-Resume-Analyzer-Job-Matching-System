import { createTheme } from "@mui/material/styles";

export const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: "#A95B6C", // Rose Gold
    },
    secondary: {
      main: "#81D0EF", // Soft Blue
    },
    background: {
      default: "#fcfaf2", // Warm Beige
      paper: "#ffffff",
    },
    text: {
      primary: "#1e293b",
      secondary: "#64748b",
    },
  },
  typography: {
    fontFamily: "'Inter', sans-serif",
    h3: {
      fontWeight: 800,
      color: "#1e293b",
    },
  },
  shape: {
    borderRadius: 16,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          padding: "10px 24px",
          borderRadius: 30,
        },
      },
    },
  },
});
