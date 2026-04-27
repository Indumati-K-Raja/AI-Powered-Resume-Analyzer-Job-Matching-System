import React from "react";
import { ThemeProvider, CssBaseline } from "@mui/material";
import { theme } from "./theme/DesignSystem";
import ResumeUploader from "./components/ResumeUploader";
import ErrorBoundary from "./components/ErrorBoundary";
import "./index.css";

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <ErrorBoundary>
        <ResumeUploader />
      </ErrorBoundary>
    </ThemeProvider>
  );
}

export default App;
