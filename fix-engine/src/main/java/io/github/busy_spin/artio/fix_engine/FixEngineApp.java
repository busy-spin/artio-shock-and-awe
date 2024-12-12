package io.github.busy_spin.artio.fix_engine;

import io.aeron.CommonContext;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.agrona.concurrent.SigInt;
import uk.co.real_logic.artio.MonitoringAgentFactory;
import uk.co.real_logic.artio.engine.DefaultEngineScheduler;
import uk.co.real_logic.artio.engine.EngineConfiguration;
import uk.co.real_logic.artio.engine.FixEngine;

public class FixEngineApp {
    public static void main(String[] args) {
        EngineConfiguration configuration = new EngineConfiguration()
                .logOutboundMessages(false)
                .logInboundMessages(false)
                .monitoringAgentFactory(MonitoringAgentFactory.none())
                .libraryAeronChannel(CommonContext.IPC_CHANNEL)
                .scheduler(new DefaultEngineScheduler());

        configuration.aeronContext().aeronDirectoryName(CommonContext.getAeronDirectoryName());

        FixEngine fixEngine = FixEngine.launch(configuration);

        System.out.println("Fix Engine Started !!!");

        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();

        SigInt.register(() -> {
            System.out.println("Shutdown signal received");
            barrier.signal();
        });

        barrier.await();
        System.out.println("Closing engine");
        fixEngine.close();
    }
}
