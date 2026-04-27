import React from 'react';
import { Box, Typography, TextField } from '@mui/material';

export const JDInput = ({ jd, setJD }) => {
  return (
    <Box sx={{ mt: 4 }}>
      <Typography variant="subtitle1" fontWeight="bold" gutterBottom color="primary.main">
        Target Job Description (Optional)
      </Typography>
      <TextField
        multiline
        rows={4}
        fullWidth
        placeholder="Paste the job description here to see how well you match..."
        value={jd}
        onChange={(e) => setJD(e.target.value)}
        variant="outlined"
        sx={{
          backgroundColor: "rgba(255, 255, 255, 0.8)",
          borderRadius: 1,
        }}
      />
    </Box>
  );
};

export default JDInput;
