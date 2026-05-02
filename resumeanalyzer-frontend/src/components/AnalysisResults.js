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
          <Chip label={`Match: ${resumeScore}/100`} sx={{ backgroundColor: 'rgba(245, 158, 11, 0.1)', color: '#f59e0b', fontWeight: 'bold', border: '1px solid rgba(245, 158, 11, 0.3)' }} />
          <Chip label={`ATS: ${atsEval}`} sx={{ backgroundColor: 'rgba(129, 208, 239, 0.1)', color: '#81D0EF', fontWeight: 'bold', border: '1px solid rgba(129, 208, 239, 0.3)' }} />
          <Chip label={`Recruiter: ${recruiterEval}`} sx={{ backgroundColor: 'rgba(129, 208, 239, 0.1)', color: '#81D0EF', fontWeight: 'bold', border: '1px solid rgba(129, 208, 239, 0.3)' }} />
          <Chip label={`Shortlist: ${shortlistEval}`} sx={{ backgroundColor: 'rgba(129, 208, 239, 0.1)', color: '#81D0EF', fontWeight: 'bold', border: '1px solid rgba(129, 208, 239, 0.3)' }} />
          <Chip label={verdict} sx={{ backgroundColor: 'rgba(169, 91, 108, 0.1)', color: '#A95B6C', fontWeight: 'bold', border: '1px solid rgba(169, 91, 108, 0.3)' }} />
        </Box>

        {/* General Feedback Box */}
        {generalFeedback && (
          <Box sx={{ p: 3, mb: 4, backgroundColor: 'rgba(255, 255, 255, 0.03)', borderRadius: 2 }}>
            <Typography variant="body1" sx={{ color: '#cbd5e1', lineHeight: 1.8 }}>
              {generalFeedback}
            </Typography>
          </Box>
        )}

        {/* Exact Fixes Section */}
        {exactFixes.length > 0 && (
          <Box>
            <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#f1f5f9', mb: 3 }}>
              Exact fixes for this role
            </Typography>

            {exactFixes.map((fix, index) => (
              <Box key={index} sx={{ mb: 4, borderLeft: '2px solid rgba(129, 208, 239, 0.2)', pl: 3 }}>
                <Typography variant="subtitle1" sx={{ fontWeight: 'bold', color: '#f1f5f9', mb: 1, display: 'flex', alignItems: 'center', gap: 1 }}>
                  {fix.type === 'replace' ? <span style={{color: '#ef4444'}}>✗</span> : <span style={{color: '#f59e0b'}}>!</span>}
                  {fix.title}
                </Typography>
                
                {fix.location && (
                  <Typography variant="caption" sx={{ color: '#94a3b8', display: 'block', mb: 1.5, fontWeight: 600, textTransform: 'uppercase' }}>
                    Location: {fix.location}
                  </Typography>
                )}

                {fix.type === 'replace' && fix.originalText && (
                  <Box sx={{ 
                    backgroundColor: 'rgba(239, 68, 68, 0.05)', color: '#fca5a5', 
                    p: 1.5, textDecoration: 'line-through', fontFamily: 'monospace', mb: 0.5, borderRadius: 1, border: '1px solid rgba(239, 68, 68, 0.1)'
                  }}>
                    {fix.originalText}
                  </Box>
                )}

                {fix.newText && (
                  <Box sx={{ 
                    backgroundColor: 'rgba(34, 197, 94, 0.05)', color: '#86efac', 
                    p: 1.5, fontFamily: 'monospace', mb: 2, borderRadius: 1, border: '1px solid rgba(34, 197, 94, 0.1)'
                  }}>
                    {fix.type === 'add' ? 'Add: ' : 'Replace with: '}
                    {fix.newText}
                  </Box>
                )}

                {fix.keywords && fix.keywords.length > 0 && (
                  <Box sx={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap', gap: 1 }}>
                    <Typography variant="caption" sx={{ color: '#64748b', fontWeight: 'bold', mr: 1 }}>KEYWORDS:</Typography>
                    {fix.keywords.map(kw => (
                      <Chip key={kw} size="small" label={kw} sx={{ backgroundColor: 'rgba(129, 208, 239, 0.05)', color: '#81D0EF', borderRadius: 1, fontSize: '0.7rem' }} />
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
