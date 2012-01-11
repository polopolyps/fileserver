package example.fileserver.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystemUtils
{
    private static final String CLASS = FileSystemUtils.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);

    private FileSystemUtils(){}
    
    public static byte[] getBytesFromFile(String filename) throws IOException
    {
        File file = new File(filename);
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            // File is too large
            LOG.log(Level.FINE, "File is too large");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + filename);
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    public static boolean deleteDirectory(File path)
    {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public static void copyFile(String from, String to)
    {
        File inputFile = new File(from);
        File outputFile = new File(to);

        BufferedInputStream bufferedInputStream;
        BufferedOutputStream bufferedOutputStream;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(inputFile));
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

            int c;
            while ((c = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(c);
            }
            bufferedInputStream.close();
            bufferedOutputStream.close();
        } catch (IOException e) {
            LOG.log(Level.INFO, "Failed to copy file", e);
        }
    }

    public static void createDirectory(String path)
    {
        File directory = new File(path);
        if (directory.exists() != true) {
            LOG.log(Level.FINE, "Creating directory: " + path);
            directory.mkdirs();
        }
    }

}
