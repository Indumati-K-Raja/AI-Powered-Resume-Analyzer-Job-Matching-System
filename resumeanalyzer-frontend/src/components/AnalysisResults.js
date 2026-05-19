import React from 'react';
import { Box, Typography, Chip, Paper, Divider, Stack } from '@mui/material';
import { motion } from 'framer-motion';
import { ScoreBreakdown } from './DetailedScoreBreakdown';
import { ImprovementSuggestions } from './ImprovementSuggestions';

const AnalysisResults = ({ analysis }) => {
  if (!analysis) return null;

  const {
    resumeScore = 0,
    atsEval = 'Unknown',
    recruiterEval = 'Unknown',
    shortlistEval = 'Unknown',
    verdict = 'Unknown',
    generalFeedback = '',
    exactFixes = [],
    extractedSkills = [],
    missingSkills = [],
    suggestions = [],
    scoreBreakdown = null
  } = analysis || {};

  const safeFixes = Array.isArray(exactFixes) ? exactFixes : [];
  const safeSuggestions = Array.isArray(suggestions) ? suggestions : [];
  const safeExtractedSkills = Array.isArray(extractedSkills) ? extractedSkills : [];
  const safeMissingSkills = Array.isArray(missingSkills) ? missingSkills : [];
  
  // Calculate estimated improvement from suggestions
  const estimatedImprovement = safeSuggestions.reduce((acc, curr) => {
    const impact = parseInt(curr.impact?.split(' ')[0] || 0);
    return acc + impact;
  }, 0);

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <Paper className="glass-panel" sx={{ p: 4, mt: 4, borderRadius: 2 }}>
        
        {/* Top Badges Row */}
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mb: 4 }}>
          <Chip label={`Match: ${Math.round(resumeScore)}/100`} sx={{ backgroundColor: '#fef3c7', color: '#d97706', fontWeight: 'bold' }} />
          <Chip label={`ATS: ${atsEval}`} sx={{ backgroundColor: '#e0f2fe', color: '#0369a1', fontWeight: 'bold' }} />
          <Chip label={`Recruiter: ${recruiterEval}`} sx={{ backgroundColor: '#e0f2fe', color: '#0369a1', fontWeight: 'bold' }} />
          <Chip label={verdict} sx={{ backgroundColor: '#fdf2f2', color: '#991b1b', fontWeight: 'bold' }} />
        </Box>

        {/* General Feedback Box */}
        {generalFeedback && (
          <Box sx={{ p: 3, mb: 4, backgroundColor: '#f8fafc', borderRadius: 2, border: '1px solid #e2e8f0' }}>
            <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>AI Feedback</Typography>
            <Typography variant="body1" sx={{ color: '#334155', lineHeight: 1.8 }}>
              {generalFeedback}
            </Typography>
          </Box>
        )}

        {/* Skills Section */}
        {(safeExtractedSkills.length > 0 || safeMissingSkills.length > 0) && (
          <Box sx={{ mb: 4 }}>
            <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>Skill Analysis</Typography>
            
            {safeExtractedSkills.length > 0 && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="subtitle2" color="textSecondary" gutterBottom>Detected Skills</Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                  {safeExtractedSkills.map(skill => (
                    <Chip key={skill} label={skill} size="small" color="success" variant="outlined" />
                  ))}
                </Box>
              </Box>
            )}

            {safeMissingSkills.length > 0 && (
              <Box>
                <Typography variant="subtitle2" color="textSecondary" gutterBottom>Missing Skills (Critical for JD)</Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                  {safeMissingSkills.map(skill => (
                    <Chip key={skill} label={skill} size="small" color="error" variant="outlined" />
                  ))}
                </Box>
              </Box>
            )}
          </Box>
        )}

        {/* Score Breakdown */}
        {scoreBreakdown && <ScoreBreakdown scoreComponents={scoreBreakdown} />}

        {/* Improvement Suggestions */}
        {safeSuggestions.length > 0 && (
          <ImprovementSuggestions 
            suggestions={safeSuggestions} 
            estimatedImprovement={estimatedImprovement} 
          />
        )}

        {/* Exact Fixes Section */}
        {safeFixes.length > 0 && (
          <Box sx={{ mt: 4 }}>
            <Divider sx={{ mb: 4 }} />
            <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#1e293b', mb: 3 }}>
              Exact fixes for this role
            </Typography>

            {safeFixes.map((fix, index) => (
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
