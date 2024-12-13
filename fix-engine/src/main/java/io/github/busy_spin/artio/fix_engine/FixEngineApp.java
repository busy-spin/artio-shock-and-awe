package io.github.busy_spin.artio.fix_engine;

import io.aeron.CommonContext;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.agrona.concurrent.SigInt;
import uk.co.real_logic.artio.MonitoringAgentFactory;
import uk.co.real_logic.artio.engine.DefaultEngineScheduler;
import uk.co.real_logic.artio.engine.EngineConfiguration;
import uk.co.real_logic.artio.engine.FixEngine;
import uk.co.real_logic.artio.library.FixLibrary;
import uk.co.real_logic.artio.validation.AuthenticationStrategy;
import uk.co.real_logic.artio.validation.MessageValidationStrategy;

import java.util.Collections;

public class FixEngineApp {
    public static void main(String[] args) {
        EngineConfiguration configuration = new EngineConfiguration()
                .monitoringAgentFactory(MonitoringAgentFactory.none())
                .logOutboundMessages(false)
                .logInboundMessages(false)
                .monitoringAgentFactory(MonitoringAgentFactory.none())
                .libraryAeronChannel(CommonContext.IPC_CHANNEL)
                .bindTo("0.0.0.0", 2134)
                .authenticationStrategy(AuthenticationStrategy.of(
                        MessageValidationStrategy.targetCompId("EXCHANGE")
                                .and(MessageValidationStrategy.senderCompId(Collections.singletonList("TAKER_FIRM")))))
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
