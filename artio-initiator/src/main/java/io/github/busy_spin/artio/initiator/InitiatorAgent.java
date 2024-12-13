package io.github.busy_spin.artio.initiator;

import io.aeron.Aeron;
import io.aeron.CommonContext;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.SystemEpochClock;
import uk.co.real_logic.artio.library.FixLibrary;
import uk.co.real_logic.artio.library.LibraryConfiguration;

import java.util.Collections;

public class InitiatorAgent implements Agent {

    private final ArtioCallBackHandler handler = new ArtioCallBackHandler();

    private final long testReqIntervalInMs = 5_000;

    private long lastInterval = SystemEpochClock.INSTANCE.time();

    private FixLibrary library;

    @Override
    public void onStart() {
        LibraryConfiguration configuration = new LibraryConfiguration()
                .libraryConnectHandler(handler)
                .libraryAeronChannels(Collections.singletonList(CommonContext.IPC_CHANNEL))
                .libraryName("initiator-app").defaultHeartbeatIntervalInS(2)
                .sessionAcquireHandler(handler);
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
        if (SystemEpochClock.INSTANCE.time() > testReqIntervalInMs + lastInterval) {
            System.out.println("Sending test request !!!");
            lastInterval = SystemEpochClock.INSTANCE.time();
            handler.sendTestRequest();
        }
        return library.poll(10);
    }

    @Override
    public String roleName() {
        return "initiator";
    }
}
