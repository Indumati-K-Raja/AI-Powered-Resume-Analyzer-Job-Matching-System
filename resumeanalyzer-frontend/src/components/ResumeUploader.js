import React, { useState } from "react";
import { Box, Typography, Button, CircularProgress, Paper, Snackbar, Alert } from "@mui/material";
import { motion } from "framer-motion";
import axiosInstance from "../api/axiosConfig";
import FileUploadZone from "./FileUploadZone";
import JDInput from "./JDInput";
import AnalysisResults from "./AnalysisResults";

function ResumeUploader() {
  const [file, setFile] = useState(null);
  const [jd, setJD] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const validateFile = () => {
    if (!file) {
      setError("Please select a resume file first.");
      return false;
    }
    if (file.type !== "application/pdf") {
      setError("Only PDF files are allowed.");
      return false;
    }
    if (file.size > 5 * 1024 * 1024) {
      setError("File size exceeds 5MB limit.");
      return false;
    }
    return true;
  };

  const handleUpload = async (withJD = false) => {
    if (!validateFile()) return;
    
    setLoading(true);
    setResult(null);
    setError("");

    const formData = new FormData();
    formData.append("file", file);
    if (withJD && jd.trim()) formData.append("jobDescription", jd);

    try {
      const url = withJD && jd.trim() ? "/resume/analyzeWithJD" : "/resume/analyze";
      
      const res = await axiosInstance.post(url, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      setResult(res.data);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.error || "Error analyzing resume. Please make sure the backend is running and the API key is valid.");
    } finally {
      setLoading(false);
    }
  };

  const handleCloseError = () => setError("");

  return (
    <Box sx={{ maxWidth: 800, margin: "0 auto", padding: 4 }}>
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <Typography variant="h3" align="center" color="primary" gutterBottom fontWeight="bold">
          AI Resume Analyzer
        </Typography>
        <Typography variant="body1" align="center" color="text.secondary" sx={{ mb: 4 }}>
          Upload your resume and get instant, AI-driven feedback to land your dream job.
        </Typography>
      </motion.div>

      <Paper className="glass-panel" sx={{ p: 4, mb: 4 }}>
        <FileUploadZone file={file} setFile={setFile} />
        <JDInput jd={jd} setJD={setJD} />

        <Box sx={{ mt: 4, display: "flex", gap: 2, justifyContent: "center" }}>
          <Button
            variant="outlined"
            color="primary"
            size="large"
            onClick={() => handleUpload(false)}
            disabled={!file || loading}
            sx={{ flex: 1 }}
          >
            {loading && !jd ? <CircularProgress size={24} sx={{ mr: 1 }} /> : "Analyze Resume Only"}
          </Button>
          <Button
            variant="contained"
            color="primary"
            size="large"
            onClick={() => handleUpload(true)}
            disabled={!file || !jd.trim() || loading}
            sx={{ flex: 1 }}
          >
            {loading && jd ? <CircularProgress size={24} color="inherit" sx={{ mr: 1 }} /> : "Analyze with JD"}
          </Button>
        </Box>
      </Paper>

      {loading && (
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 4 }}>
          <CircularProgress size={48} sx={{ mb: 2 }} />
          <Typography variant="body1" color="text.secondary">Analyzing your resume using Gemini AI...</Typography>
          <Typography variant="body2" color="text.secondary">This process involves deep text extraction and AI analysis, which may take up to 30 seconds. Please wait.</Typography>
        </Box>
      )}

      {!loading && <AnalysisResults analysis={result} />}

      <Snackbar open={!!error} autoHideDuration={6000} onClose={handleCloseError} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
        <Alert onClose={handleCloseError} severity="error" sx={{ width: '100%' }}>
          {error}
        </Alert>
      </Snackbar>
    </Box>
  );
}

export default ResumeUploader;
