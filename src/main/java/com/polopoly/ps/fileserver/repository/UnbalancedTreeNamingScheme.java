package com.polopoly.ps.fileserver.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.inject.Inject;
import com.polopoly.ps.fileserver.counter.FileCounter;

public class UnbalancedTreeNamingScheme implements NamingScheme
{
    private static final String CLASS = UnbalancedTreeNamingScheme.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    private static final long FACTOR = 100;
    private FileCounter fileCounter;

    @Inject
    public UnbalancedTreeNamingScheme(FileCounter fileCounter) {
        this.fileCounter = fileCounter;
    }

    private boolean inInterval(long left, long right, long x)
    {
        return x >= left && x < right;
    }

    private String getRestOfPath(long left, long right, long interval, long x)
    {
        if ((x < left) || (x >= right)) {
            LOG.log(Level.SEVERE, "Invalid interval " + x + " should be [" + left + ", " + right + "]");
            throw new IllegalArgumentException();
        }
        if (interval == 1) {
            return "";
        } else {
            for (int i = 0; i < FACTOR; ++i) {
                if (inInterval(left + i * interval, left + (i + 1) * interval, x)) {
                    return ""
                           + (left + (i * interval))
                           + "-"
                           + (left + ((i + 1) * interval) - 1)
                           + File.separator
                           + getRestOfPath(left + i * interval, left + (i + 1) * interval,
                                           interval / FACTOR, x);
                }
            }
        }
        return "";
    }

    private String getFirstDirectoryPath(long left, long right, long x)
    {
        if ((x >= left) && (x < right)) {
            long interval = (right - left) / (FACTOR - 1);

            return "" + left + "-" + (right - 1) + File.separator + getRestOfPath(left, right, interval, x);
        } else {
            return getFirstDirectoryPath(right, right * FACTOR, x);
        }
    }

    @Override
    public String newFile()
    {
        return Long.toString(fileCounter.getNextFileCounter());
    }

    @Override
    public String getPath(String filename) throws FileNotFoundException
    {
        long index = 0;
        try {
            index = Long.parseLong(filename);
        } catch (NumberFormatException e) {
            throw new FileNotFoundException("Could not find " + filename);
        }
        long left = 0;
        long right = FACTOR;

        while (index > (right - 1)) {
            left = right;
            right = right * FACTOR;
        }
        return getFirstDirectoryPath(left, right, index);
    }
}
