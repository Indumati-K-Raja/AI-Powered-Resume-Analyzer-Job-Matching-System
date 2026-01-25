import React, { useState } from "react";
import axios from "axios";
import { useDropzone } from "react-dropzone";
import { LinearProgress, Box, Chip, Typography } from "@mui/material";

function ResumeUploader() {
  const [file, setFile] = useState(null);
  const [jd, setJD] = useState("");
  const [result, setResult] = useState(null);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop: (acceptedFiles) => setFile(acceptedFiles[0]),
    accept: { "application/pdf": [".pdf"] },
    multiple: false,
  });

  const handleUpload = async (withJD = false) => {
    if (!file) return alert("Please select a file");
    if (file.type !== "application/pdf") return alert("Only PDF files allowed");

    const formData = new FormData();
    formData.append("file", file);
    if (withJD) formData.append("jd", jd);

    try {
      const url = withJD
        ? "http://localhost:8080/resume/analyzeWithJD"
        : "http://localhost:8080/resume/analyze";

      const res = await axios.post(url, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      setResult(res.data);
    } catch (err) {
      console.error(err);
      alert("Error uploading resume");
    }
  };

  // 🔹 Normalize response (KEY FIX)
  const skills =
    result?.resume_skills || result?.skills_found || [];

  const resumeScore =
    result?.resume_score ?? result?.score;

  return (
    <Box sx={{ maxWidth: 700, margin: "20px auto", padding: 3, boxShadow: 3, borderRadius: 2 }}>
      <Typography variant="h4" gutterBottom>
        Resume Analyzer
      </Typography>

      {/* Drag & Drop */}
      <Box
        {...getRootProps()}
        sx={{
          border: "2px dashed #1976d2",
          padding: 20,
          textAlign: "center",
          background: isDragActive ? "#e3f2fd" : "#f5f5f5",
          cursor: "pointer",
          mb: 2,
        }}
      >
        <input {...getInputProps()} />
        {file ? file.name : isDragActive ? "Drop the file here ..." : "Drag & drop PDF or click to select"}
      </Box>

      {/* Job Description */}
      <textarea
        placeholder="Paste Job Description here (optional)"
        value={jd}
        onChange={(e) => setJD(e.target.value)}
        rows={5}
        style={{ width: "100%", marginBottom: 10, padding: 8 }}
      />

      {/* Buttons */}
      <Box sx={{ mb: 2 }}>
        <button onClick={() => handleUpload(false)}>Analyze Resume Only</button>
        <button onClick={() => handleUpload(true)} style={{ marginLeft: 10 }}>
          Analyze with JD
        </button>
      </Box>

      {/* Results */}
      {result && (
        <Box sx={{ mt: 3 }}>
          {/* Skills */}
          {skills.length > 0 && (
            <>
              <Typography variant="h6">Skills Found:</Typography>
              {skills.map((skill) => (
                <Chip key={skill} label={skill} color="success" sx={{ mr: 1, mb: 1 }} />
              ))}
            </>
          )}

          {/* Missing Skills */}
          {result.missing_skills && result.missing_skills.length > 0 && (
            <>
              <Typography variant="h6" sx={{ mt: 2 }}>
                Missing Skills:
              </Typography>
              {result.missing_skills.map((skill) => (
                <Chip key={skill} label={skill} color="error" sx={{ mr: 1, mb: 1 }} />
              ))}
            </>
          )}

          {/* Resume Score */}
          {resumeScore !== undefined && (
            <Box sx={{ mt: 2 }}>
              <Typography>Resume Score: {resumeScore}%</Typography>
              <LinearProgress variant="determinate" value={resumeScore} />
            </Box>
          )}

          {/* Job Fit Score */}
          {result.job_fit_score !== undefined && (
            <Box sx={{ mt: 2 }}>
              <Typography>Job Fit Score: {result.job_fit_score}%</Typography>
              <LinearProgress variant="determinate" value={result.job_fit_score} />
            </Box>
          )}
        </Box>
      )}
    </Box>
  );
}

export default ResumeUploader;
