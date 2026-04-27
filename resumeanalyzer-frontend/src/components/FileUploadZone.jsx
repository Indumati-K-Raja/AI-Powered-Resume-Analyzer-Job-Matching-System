import React from 'react';
import { Box, Typography } from '@mui/material';
import { useDropzone } from 'react-dropzone';
import { motion } from 'framer-motion';
import { UploadCloud, FileText } from 'lucide-react';

export const FileUploadZone = ({ file, setFile }) => {
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop: (acceptedFiles) => setFile(acceptedFiles[0]),
    accept: { "application/pdf": [".pdf"] },
    multiple: false,
  });

  return (
    <Box
      {...getRootProps()}
      sx={{
        border: "2px dashed",
        borderColor: isDragActive ? "primary.main" : "secondary.main",
        borderRadius: 3,
        padding: 6,
        textAlign: "center",
        backgroundColor: isDragActive ? "rgba(169, 91, 108, 0.05)" : "transparent",
        cursor: "pointer",
        transition: "all 0.3s ease",
        "&:hover": {
          backgroundColor: "rgba(129, 208, 239, 0.1)",
          borderColor: "primary.light",
        }
      }}
    >
      <input {...getInputProps()} />
      <motion.div whileHover={{ scale: 1.1 }}>
        {file ? (
          <FileText size={64} color="#532841" style={{ margin: "0 auto" }} />
        ) : (
          <UploadCloud size={64} color="#81D0EF" style={{ margin: "0 auto" }} />
        )}
      </motion.div>
      <Typography variant="h6" sx={{ mt: 2, color: "primary.main" }}>
        {file ? file.name : isDragActive ? "Drop the PDF here" : "Drag & drop PDF here, or click to browse"}
      </Typography>
      {file && (
        <Typography variant="body2" color="text.secondary">
          {(file.size / (1024 * 1024)).toFixed(2)} MB
        </Typography>
      )}
      {!file && (
        <Typography variant="body2" color="text.secondary">
          Only .pdf files are supported (Max 5MB)
        </Typography>
      )}
    </Box>
  );
};

export default FileUploadZone;
