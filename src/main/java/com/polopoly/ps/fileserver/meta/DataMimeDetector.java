package com.polopoly.ps.fileserver.meta;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Singleton;
import com.polopoly.ps.fileserver.mime.MimeDetector;
import com.polopoly.ps.fileserver.repository.FileResource;

import eu.medsea.mimeutil.MimeException;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;

@Singleton
public class DataMimeDetector implements MimeDetector
{
    private static final String CLASS = DataMimeDetector.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);

    private MimeUtil2 mimeUtil;

    public DataMimeDetector() {
        mimeUtil = new MimeUtil2();
        mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
    }

    @Override
    public String getMimeType(FileResource fileResource)
    {
        try {
            @SuppressWarnings("unchecked")
            Collection<MimeType> mimeTypes = mimeUtil.getMimeTypes(fileResource.getData());
            if (mimeTypes != null) {
                Iterator<MimeType> mimeTypeIterator = mimeTypes.iterator();
                if (mimeTypeIterator.hasNext()) {
                    return mimeTypeIterator.next().toString();
                }
            }
        } catch (MimeException e) {
            LOG.log(Level.INFO, "Failed to detect mime type from file data", e);
        }
        return MimeUtil2.UNKNOWN_MIME_TYPE.toString();
    }
}
