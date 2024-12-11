package io.github.busy_spin.media_driver;

import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.NoOpIdleStrategy;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.agrona.concurrent.SigInt;

public class MediaDriverApp {

    public static void main(String[] args) {
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();

        final MediaDriver.Context ctx = new MediaDriver.Context()
                .termBufferSparseFile(false)
                .useWindowsHighResTimer(true)
                .threadingMode(ThreadingMode.DEDICATED)
                .conductorIdleStrategy(BusySpinIdleStrategy.INSTANCE)
                .receiverIdleStrategy(NoOpIdleStrategy.INSTANCE)
                .senderIdleStrategy(NoOpIdleStrategy.INSTANCE);

        SigInt.register(() -> {
            System.out.println("Shutdown signal received");
            barrier.signal();
        });

        try (MediaDriver mediaDriver = MediaDriver.launch(ctx)) {
            barrier.await();
        } finally {
            System.out.println("Exiting the program !!!");
        }
    }
}
