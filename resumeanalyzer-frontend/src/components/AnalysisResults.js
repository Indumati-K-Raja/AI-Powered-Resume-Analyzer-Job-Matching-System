import React from 'react';
import { Box, Typography, Chip, Paper, Divider } from '@mui/material';
import { motion } from 'framer-motion';

const AnalysisResults = ({ analysis }) => {
  if (!analysis) return null;

  const {
    resumeScore = 0,
    atsEval = 'Unknown',
    recruiterEval = 'Unknown',
    shortlistEval = 'Unknown',
    verdict = 'Unknown',
    generalFeedback = '',
    exactFixes = []
  } = analysis;

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <Paper className="glass-panel" sx={{ p: 4, mt: 4 }}>
        
        {/* Top Badges Row */}
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mb: 4 }}>
          <Chip label={`Match: ${resumeScore}/100`} sx={{ backgroundColor: '#fef3c7', color: '#d97706', fontWeight: 'bold' }} />
          <Chip label={`ATS: ${atsEval}`} sx={{ backgroundColor: '#e0f2fe', color: '#0369a1', fontWeight: 'bold' }} />
          <Chip label={`Recruiter: ${recruiterEval}`} sx={{ backgroundColor: '#e0f2fe', color: '#0369a1', fontWeight: 'bold' }} />
          <Chip label={verdict} sx={{ backgroundColor: '#fdf2f2', color: '#991b1b', fontWeight: 'bold' }} />
        </Box>

        {/* General Feedback Box */}
        {generalFeedback && (
          <Box sx={{ p: 3, mb: 4, backgroundColor: '#f8fafc', borderRadius: 2, border: '1px solid #e2e8f0' }}>
            <Typography variant="body1" sx={{ color: '#334155', lineHeight: 1.8 }}>
              {generalFeedback}
            </Typography>
          </Box>
        )}

        {/* Exact Fixes Section */}
        {exactFixes.length > 0 && (
          <Box>
            <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#1e293b', mb: 3 }}>
              Exact fixes for this role
            </Typography>

            {exactFixes.map((fix, index) => (
              <Box key={index} sx={{ mb: 4, borderLeft: '3px solid #e2e8f0', pl: 3 }}>
                <Typography variant="subtitle1" sx={{ fontWeight: 'bold', color: '#1e293b', mb: 1 }}>
                  {fix.type === 'replace' ? '❌ ' : '⚠️ '}
                  {fix.title}
                </Typography>
                
                {fix.location && (
                  <Typography variant="caption" sx={{ color: '#64748b', display: 'block', mb: 1.5, fontWeight: 700 }}>
                    LOCATION: {fix.location}
                  </Typography>
                )}

                {fix.type === 'replace' && fix.originalText && (
                  <Box sx={{ 
                    backgroundColor: '#fff1f2', color: '#e11d48', 
                    p: 1.5, textDecoration: 'line-through', fontFamily: 'monospace', mb: 1, borderRadius: 1
                  }}>
                    {fix.originalText}
                  </Box>
                )}

                {fix.newText && (
                  <Box sx={{ 
                    backgroundColor: '#f0fdf4', color: '#166534', 
                    p: 1.5, fontFamily: 'monospace', mb: 2, borderRadius: 1, fontWeight: 600
                  }}>
                    {fix.type === 'add' ? 'ADD: ' : 'REPLACE WITH: '}
                    {fix.newText}
                  </Box>
                )}
              </Box>
            ))}
          </Box>
        )}
      </Paper>
    </motion.div>
  );
};

export default AnalysisResults;
