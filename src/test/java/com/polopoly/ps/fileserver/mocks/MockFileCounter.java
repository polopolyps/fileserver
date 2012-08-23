package com.polopoly.ps.fileserver.mocks;

import com.polopoly.ps.fileserver.counter.FileCounter;

public class MockFileCounter implements FileCounter
{
    private long fileCounter;

    public MockFileCounter() {
        fileCounter = 0;
    }

    @Override
    public long getNextFileCounter()
    {
        return fileCounter++;
    }

}
