import React from 'react';
import { Box, Typography, Chip, LinearProgress, Paper, Divider } from '@mui/material';
import { motion } from 'framer-motion';

const AnalysisResults = ({ analysis }) => {
  if (!analysis) return null;

  const skills = analysis.extractedSkills || [];
  const missingSkills = analysis.missingSkills || [];
  const resumeScore = analysis.resumeScore !== undefined ? analysis.resumeScore : 0;
  const jobFitScore = analysis.jdMatchScore;
  const suggestions = analysis.suggestions || [];

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <Paper className="glass-panel" sx={{ p: 4, mt: 4, borderRadius: 3 }}>
        <Typography variant="h5" color="primary" gutterBottom>
          Analysis Report
        </Typography>
        <Divider sx={{ mb: 3 }} />

        {resumeScore !== undefined && (
          <Box sx={{ mb: 4 }}>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              Resume Score
            </Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <LinearProgress 
                variant="determinate" 
                value={resumeScore} 
                sx={{ flexGrow: 1, height: 10, borderRadius: 5, backgroundColor: '#F1D5F1' }} 
                color={resumeScore >= 70 ? 'success' : resumeScore >= 40 ? 'warning' : 'error'}
              />
              <Typography variant="h6" color="primary.main">{resumeScore}%</Typography>
            </Box>
          </Box>
        )}

        {jobFitScore !== undefined && jobFitScore > 0 && (
          <Box sx={{ mb: 4 }}>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              Job Fit Score
            </Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <LinearProgress 
                variant="determinate" 
                value={jobFitScore} 
                sx={{ flexGrow: 1, height: 10, borderRadius: 5, backgroundColor: '#F1D5F1' }} 
                color={jobFitScore >= 75 ? 'success' : jobFitScore >= 50 ? 'warning' : 'error'}
              />
              <Typography variant="h6" color="primary.main">{jobFitScore}%</Typography>
            </Box>
          </Box>
        )}

        <Box sx={{ mb: 4 }}>
          <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
            Identified Skills
          </Typography>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
            {skills.map((skill, index) => (
              <motion.div key={skill} initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ delay: index * 0.05 }}>
                <Chip label={skill} sx={{ backgroundColor: '#81D0EF', color: '#000', fontWeight: 'bold' }} />
              </motion.div>
            ))}
            {skills.length === 0 && <Typography variant="body2" color="text.secondary">No skills detected.</Typography>}
          </Box>
        </Box>

        {missingSkills.length > 0 && (
          <Box sx={{ mb: 4 }}>
            <Typography variant="subtitle1" fontWeight="bold" color="error.main" gutterBottom>
              Missing Skills (Based on JD)
            </Typography>
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
              {missingSkills.map((skill, index) => (
                <motion.div key={skill} initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ delay: index * 0.05 }}>
                  <Chip label={skill} color="error" variant="outlined" />
                </motion.div>
              ))}
            </Box>
          </Box>
        )}

        {suggestions.length > 0 && (
          <Box>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              AI Suggestions & Advice
            </Typography>
            <Paper elevation={0} sx={{ p: 2, backgroundColor: 'rgba(241, 213, 241, 0.3)', borderRadius: 2 }}>
              <ul style={{ paddingLeft: 20, margin: 0 }}>
                {suggestions.map((suggestion, index) => (
                  <li key={index} style={{ marginBottom: 8 }}>
                    <Typography variant="body2" color="text.secondary">{suggestion}</Typography>
                  </li>
                ))}
              </ul>
            </Paper>
          </Box>
        )}
      </Paper>
    </motion.div>
  );
};

export default AnalysisResults;
