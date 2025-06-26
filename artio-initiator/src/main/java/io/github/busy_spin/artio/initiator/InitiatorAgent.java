package io.github.busy_spin.artio.initiator;

import io.aeron.CommonContext;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.SystemEpochClock;
import uk.co.real_logic.artio.library.FixLibrary;
import uk.co.real_logic.artio.library.LibraryConfiguration;

import java.util.Collections;

public class InitiatorAgent implements Agent {

    private final ArtioCallBackHandler handler = new ArtioCallBackHandler();

    private final long reportInterval = 1_000;

    private long lastInterval = SystemEpochClock.INSTANCE.time();


    private FixLibrary library;

    public InitiatorAgent() {
        int throughput = 10_000;
        String throughputStr = System.getProperty("artio_demo.throughput", String.valueOf(throughput));
        try {
            throughput = Integer.parseInt(throughputStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid value for artio_demo.throughput: " + throughputStr);
            System.out.println("Setting default throughput to " + throughput);
        }

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
        handler.sendTestRequest();
        library.poll(10);
        if (SystemEpochClock.INSTANCE.time() > reportInterval + lastInterval) {
            lastInterval = SystemEpochClock.INSTANCE.time();
            handler.printAndResetCounter();
        }

        return 1;
    }

    @Override
    public String roleName() {
        return "initiator";
    }
}
