import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    primary: { main: '#1565C0', light: '#5E92F3', dark: '#003C8F', contrastText: '#fff' },
    secondary: { main: '#00897B', light: '#4EBAAA', dark: '#005B4F', contrastText: '#fff' },
    background: { default: '#F5F7FA', paper: '#fff' },
    text: { primary: '#1F2937', secondary: '#4B5563' },
  },
  typography: { fontFamily: 'Roboto, "Helvetica Neue", Arial, sans-serif', h4: { fontWeight: 600 }, h5: { fontWeight: 600 }, h6: { fontWeight: 600 }, button: { textTransform: 'none', fontWeight: 600 } },
  shape: { borderRadius: 8 },
  components: {
    MuiButton: { styleOverrides: { root: { boxShadow: 'none', '&:hover': { boxShadow: 'none' } } } },
    MuiTable: { defaultProps: { size: 'small' }, styleOverrides: { root: { '& .MuiTableCell-head': { backgroundColor: '#F8FAFC', color: '#4B5563', fontWeight: 700 } } } },
    MuiPaper: { styleOverrides: { root: { backgroundImage: 'none' }, elevation1: { boxShadow: '0 1px 3px rgba(0,0,0,.1), 0 1px 2px rgba(0,0,0,.06)' } } },
  },
});
