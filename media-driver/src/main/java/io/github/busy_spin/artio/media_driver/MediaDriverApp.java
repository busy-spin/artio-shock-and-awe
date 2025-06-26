package io.github.busy_spin.artio.media_driver;

import io.aeron.CommonContext;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.NoOpIdleStrategy;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.agrona.concurrent.SigInt;

public class MediaDriverApp {

    public static void main(String[] args) {
        boolean isAcceptorEngine = "true".equals(System.getProperty("artio_demo.acceptor.engine", "false"));

        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();

        final MediaDriver.Context ctx = new MediaDriver.Context()
                .termBufferSparseFile(false)
                .useWindowsHighResTimer(true)
                .threadingMode(ThreadingMode.DEDICATED)
                .conductorIdleStrategy(BusySpinIdleStrategy.INSTANCE)
                .receiverIdleStrategy(NoOpIdleStrategy.INSTANCE)
                .senderIdleStrategy(NoOpIdleStrategy.INSTANCE);

        if (isAcceptorEngine) {
            ctx.aeronDirectoryName(CommonContext.getAeronDirectoryName() + "-acceptor");
        } else {
            ctx.aeronDirectoryName(CommonContext.getAeronDirectoryName());
        }

        SigInt.register(() -> {
            System.out.println("Shutdown signal received");
            barrier.signal();
        });

        try (MediaDriver mediaDriver = MediaDriver.launch(ctx)) {
            System.out.println("Media driver started " + ctx.aeronDirectoryName());
            barrier.await();
        } finally {
            System.out.println("Exiting the program !!!");
        }
    }
}
