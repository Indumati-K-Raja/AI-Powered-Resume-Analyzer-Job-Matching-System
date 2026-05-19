import React from 'react';
import { Paper, Typography, Alert, Stack, Box, Chip } from '@mui/material';

export const ImprovementSuggestions = ({ suggestions, estimatedImprovement }) => {
  if (!suggestions || suggestions.length === 0) return null;

  return (
    <Paper elevation={2} sx={{ padding: 3, mt: 3, borderRadius: 2 }}>
      <Typography variant="h5" gutterBottom fontWeight="bold">AI-Powered Suggestions</Typography>
      <Alert severity="info" sx={{ mt: 2, mb: 2 }}>
        Estimated score improvement: <strong>+{estimatedImprovement} points</strong>
      </Alert>
      
      <Stack spacing={2}>
        {suggestions.map((suggestion, idx) => (
          <Paper key={idx} variant="outlined" sx={{ padding: 2, borderRadius: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <Box>
                <Chip 
                  label={suggestion.category} 
                  size="small" 
                  color={suggestion.severity === 'high' ? 'error' : 'warning'}
                  sx={{ mr: 1, mb: 1 }}
                />
                <Typography variant="body2" sx={{ mt: 1 }}>
                  {suggestion.message}
                </Typography>
              </Box>
              <Typography variant="caption" color="success.main" fontWeight="bold">
                +{suggestion.impact}
              </Typography>
            </Box>
          </Paper>
        ))}
      </Stack>
    </Paper>
  );
};

export default ImprovementSuggestions;
