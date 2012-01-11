package example.fileserver.mocks;

import example.fileserver.counter.FileCounter;

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
