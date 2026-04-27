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
      <Paper sx={{ 
        p: 4, mt: 4, borderRadius: 2, 
        backgroundColor: '#1E1E1E', color: '#E0E0E0',
        fontFamily: "'Inter', sans-serif"
      }}>
        
        {/* Top Badges Row */}
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mb: 3 }}>
          <Chip label={`Match: ${resumeScore}/100`} sx={{ backgroundColor: '#423B2A', color: '#E2B13B', fontWeight: 'bold' }} />
          <Chip label={`ATS: ${atsEval}`} sx={{ backgroundColor: '#423B2A', color: '#E2B13B', fontWeight: 'bold' }} />
          <Chip label={`Recruiter: ${recruiterEval}`} sx={{ backgroundColor: '#423B2A', color: '#E2B13B', fontWeight: 'bold' }} />
          <Chip label={`Shortlist: ${shortlistEval}`} sx={{ backgroundColor: '#423B2A', color: '#E2B13B', fontWeight: 'bold' }} />
          <Chip label={verdict} sx={{ backgroundColor: '#423B2A', color: '#E2B13B', fontWeight: 'bold' }} />
        </Box>

        {/* General Feedback Box */}
        {generalFeedback && (
          <Paper sx={{ p: 3, mb: 4, backgroundColor: '#2C2C2C', color: '#CCCCCC', borderRadius: 2, boxShadow: 'none' }}>
            <Typography variant="body1" sx={{ lineHeight: 1.6 }}>
              {generalFeedback}
            </Typography>
          </Paper>
        )}

        {/* Exact Fixes Section */}
        {exactFixes.length > 0 && (
          <Box>
            <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#FFFFFF', mb: 2 }}>
              Exact fixes for this role only
            </Typography>

            {exactFixes.map((fix, index) => (
              <Box key={index} sx={{ mb: 4 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 'bold', color: '#FFFFFF', display: 'flex', alignItems: 'center' }}>
                    {fix.type === 'replace' ? <span style={{color: '#E06C75', marginRight: 8}}>✗</span> : <span style={{color: '#E2B13B', marginRight: 8}}>!</span>}
                    {fix.title}
                  </Typography>
                </Box>
                
                {fix.location && (
                  <Chip size="small" label={fix.location} sx={{ backgroundColor: '#423B2A', color: '#E2B13B', mb: 1.5, borderRadius: 1 }} />
                )}

                {fix.type === 'replace' && fix.originalText && (
                  <Box sx={{ 
                    backgroundColor: '#3E2428', color: '#D47E84', 
                    p: 1.5, textDecoration: 'line-through', fontFamily: 'monospace', mb: 0.5, borderRadius: 1
                  }}>
                    {fix.originalText}
                  </Box>
                )}

                {fix.newText && (
                  <Box sx={{ 
                    backgroundColor: '#1E3A1E', color: '#8FBC8F', 
                    p: 1.5, fontFamily: 'monospace', mb: 2, borderRadius: 1
                  }}>
                    {fix.type === 'add' ? `After "${fix.originalText}" add: ` : ''}
                    {fix.newText}
                  </Box>
                )}

                {fix.keywords && fix.keywords.length > 0 && (
                  <Box sx={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap', gap: 1 }}>
                    <Typography variant="body2" sx={{ color: '#AAAAAA', fontWeight: 'bold', mr: 1 }}>Keywords to include:</Typography>
                    {fix.keywords.map(kw => (
                      <Chip key={kw} size="small" label={kw} sx={{ backgroundColor: '#21314B', color: '#6A92D4', borderRadius: 1 }} />
                    ))}
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
