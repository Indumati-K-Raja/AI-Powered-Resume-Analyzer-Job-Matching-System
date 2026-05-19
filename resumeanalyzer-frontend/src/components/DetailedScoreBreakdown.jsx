import React from 'react';
import { Paper, Typography, Grid, Box } from '@mui/material';

export const ScoreBreakdown = ({ scoreComponents }) => {
  if (!scoreComponents) return null;

  // Handle both camelCase and snake_case keys from backend
  const entries = Object.entries(scoreComponents).filter(([key]) => key !== 'overall');

  return (
    <Paper elevation={2} sx={{ padding: 3, mt: 3, borderRadius: 2 }}>
      <Typography variant="h5" gutterBottom fontWeight="bold">Score Breakdown</Typography>
      <Grid container spacing={2} sx={{ mt: 1 }}>
        {entries.map(([key, value]) => (
          <Grid item xs={6} sm={3} key={key}>
            <Paper variant="outlined" sx={{ padding: 2, textAlign: 'center', borderRadius: 2 }}>
              <Typography variant="caption" color="textSecondary" sx={{ textTransform: 'uppercase' }}>
                {key.replace(/_/g, ' ')}
              </Typography>
              <Typography variant="h6" color={value >= 15 ? 'success.main' : 'warning.main'} fontWeight="bold">
                {Math.round(value)}
              </Typography>
            </Paper>
          </Grid>
        ))}
      </Grid>
    </Paper>
  );
};

export default ScoreBreakdown;
