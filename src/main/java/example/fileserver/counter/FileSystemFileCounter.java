package example.fileserver.counter;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class FileSystemFileCounter implements FileCounter {
    private static final String CLASS = FileSystemFileCounter.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);

    private String repositoryDirectory;

    private final AtomicLong counter;


    @Inject
    public FileSystemFileCounter(@Named("RepositoryDirectory") String repositoryDirectory) {
        this.repositoryDirectory = repositoryDirectory;
        long valueFromDisk = readCurrentFromDisk();
        LOG.log(Level.INFO, "counter initialized at: " + valueFromDisk);
        counter = new AtomicLong(valueFromDisk);
    }

    @Override
    public long getNextFileCounter() {
        return counter.incrementAndGet();
    }

    private synchronized long readCurrentFromDisk() {
        File dir = new File(repositoryDirectory);
        File max = findLargestIndexFileInDir(dir);
        if (max == null) {
            LOG.log(Level.WARNING, "Empty filesystem");
            return 0;
        }
        return Long.parseLong(max.getName());
    }

    private File findLargestIndexFileInDir(File node) {
        LOG.log(Level.FINE, "Looking in " + repositoryDirectory + node.getName());
        if (node.isFile()) {
            return node;
        } else {
            long max = 0;
            String[] children = node.list();
            if (children == null) {
                LOG.log(Level.FINE, "Empty directory " + node.getName());
                return null;
            }
            String chosen = null;
            String potentialChosen = null;
            for (int i = 0; i < children.length; ++i) {
                String[] range = children[i].split("-");
                long potentialMax = 0;
                try {
                    if (!range[0].endsWith(".metadata")) {
                        potentialMax = Long.parseLong(range[0]);
                        potentialChosen = children[i];
                    }
                } catch (NumberFormatException e) {
                    LOG.log(Level.WARNING, "Garbage file " + children[i] + " in found in the repository.");
                    potentialMax = -1;
                }
                if (potentialMax >= max) {
                    max = potentialMax;
                    chosen = potentialChosen;
                }
            }
            return findLargestIndexFileInDir(new File(node.getPath() + File.separator + chosen));
        }
    }
}
