package io.github.busy_spin.artio.initiator;

import io.aeron.CommonContext;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.SystemEpochClock;
import uk.co.real_logic.artio.library.FixLibrary;
import uk.co.real_logic.artio.library.LibraryConfiguration;

import java.util.Collections;

public class InitiatorAgent implements Agent {

    private final ArtioCallBackHandler handler = new ArtioCallBackHandler();

    private final long houseKeepInterval = 1_000;

    private final int messagesPerMs;
    private int lastMsCount = 0;
    private long currentTimestamp = System.currentTimeMillis();

    private long lastInterval = SystemEpochClock.INSTANCE.time();


    private FixLibrary library;

    public InitiatorAgent() {
        int throughput = 1000;
        String throughputStr = System.getProperty("artio_demo.throughput", String.valueOf(throughput));
        try {
            throughput = Integer.parseInt(throughputStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid value for artio_demo.throughput: " + throughputStr);
            System.out.println("Setting default throughput to " + throughput);
        }

        messagesPerMs = throughput / 1000;

    }

    @Override
    public void onStart() {
        LibraryConfiguration configuration = new LibraryConfiguration()
                .libraryConnectHandler(handler)
                .libraryAeronChannels(Collections.singletonList(CommonContext.IPC_CHANNEL))
                .libraryName("initiator-app").defaultHeartbeatIntervalInS(2)
                .sessionAcquireHandler(handler)
                .sessionExistsHandler(handler);
        configuration.aeronContext().aeronDirectoryName(CommonContext.getAeronDirectoryName());
        library = FixLibrary.connect(configuration);
        System.out.println("Library Id " + library.libraryId());
    }

    @Override
    public void onClose() {
        library.close();
    }

    @Override
    public int doWork() throws Exception {
        library.poll(10);
        if (SystemEpochClock.INSTANCE.time() > houseKeepInterval + lastInterval) {
            lastInterval = SystemEpochClock.INSTANCE.time();
            handler.printAndResetCounter();
            handler.checkStatus();
        }

        if (SystemEpochClock.INSTANCE.time() > currentTimestamp) {
            currentTimestamp = SystemEpochClock.INSTANCE.time();
            lastMsCount = 0;
        } else {
            if (lastMsCount < messagesPerMs) {
                lastMsCount++;
                handler.sendTestRequest();
            }
        }

        return 1;
    }

    @Override
    public String roleName() {
        return "initiator";
    }
}
