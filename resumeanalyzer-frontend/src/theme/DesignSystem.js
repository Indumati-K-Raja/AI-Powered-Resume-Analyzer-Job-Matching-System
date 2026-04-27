import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    primary: {
      main: '#532841', // Deep Purple
      light: '#A95B6C', // Muted Red
    },
    secondary: {
      main: '#81D0EF', // Sky Blue
      light: '#F1D5F1', // Light Lavender
    },
    background: {
      default: '#fafafa',
      paper: '#ffffff',
    },
    text: {
      primary: '#333333',
      secondary: '#666666',
    },
  },
  typography: {
    fontFamily: "'Inter', sans-serif",
    h1: {
      fontWeight: 700,
      color: '#532841',
    },
    h2: {
      fontWeight: 600,
      color: '#532841',
    },
    h3: {
      fontWeight: 600,
    },
    h4: {
      fontWeight: 600,
    },
    button: {
      textTransform: 'none',
      fontWeight: 600,
    },
  },
  shape: {
    borderRadius: 12,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          padding: '10px 24px',
          boxShadow: 'none',
          '&:hover': {
            boxShadow: '0 4px 12px rgba(83, 40, 65, 0.2)',
          },
        },
        containedPrimary: {
          background: 'linear-gradient(135deg, #532841 0%, #A95B6C 100%)',
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          boxShadow: '0 8px 32px 0 rgba(83, 40, 65, 0.08)',
          border: '1px solid rgba(241, 213, 241, 0.5)',
        },
      },
    },
  },
});
