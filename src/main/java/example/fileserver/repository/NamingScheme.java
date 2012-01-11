package example.fileserver.repository;

import java.io.FileNotFoundException;

/**
 * A file system naming scheme.
 */
public interface NamingScheme
{
    /**
     * @return a String id of a new file.
     */
    String newFile();

    /**
     * @return a directory path to the file id. Note that this excludes the id itself.
     */
    String getPath(String id) throws FileNotFoundException;
}
