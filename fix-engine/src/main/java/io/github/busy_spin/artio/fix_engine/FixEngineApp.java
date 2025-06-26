package io.github.busy_spin.artio.fix_engine;

import io.aeron.CommonContext;
import org.agrona.concurrent.NoOpIdleStrategy;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.agrona.concurrent.SigInt;
import uk.co.real_logic.artio.MonitoringAgentFactory;
import uk.co.real_logic.artio.engine.DefaultEngineScheduler;
import uk.co.real_logic.artio.engine.EngineConfiguration;
import uk.co.real_logic.artio.engine.FixEngine;
import uk.co.real_logic.artio.validation.AuthenticationStrategy;
import uk.co.real_logic.artio.validation.MessageValidationStrategy;

import java.util.Collections;

public class FixEngineApp {
    public static void main(String[] args) {

        boolean isAcceptorEngine = "true".equals(System.getProperty("artio_demo.acceptor.engine", "false"));

        EngineConfiguration configuration = new EngineConfiguration()
                .monitoringAgentFactory(MonitoringAgentFactory.none())
                .logOutboundMessages(false)
                .logInboundMessages(false)
                .monitoringAgentFactory(MonitoringAgentFactory.none())
                .framerIdleStrategy(new NoOpIdleStrategy())
                .libraryAeronChannel(CommonContext.IPC_CHANNEL)
                .scheduler(new DefaultEngineScheduler());

        if (isAcceptorEngine) {

            configuration
                    .logFileDir("logs_acceptor")
                    .deleteLogFileDirOnStart(true)
                    .bindTo("0.0.0.0", 2134)
                    .authenticationStrategy(
                            AuthenticationStrategy.of(
                                    MessageValidationStrategy.targetCompId("EXCHANGE")
                                            .and(MessageValidationStrategy.senderCompId(Collections.singletonList("TAKER_FIRM")))));
            configuration.aeronContext().aeronDirectoryName(CommonContext.getAeronDirectoryName() + "-acceptor");
        } else {
            configuration
                    .logFileDir("logs_initiator")
                    .bindTo("0.0.0.0", 2135)
                    .authenticationStrategy(
                            AuthenticationStrategy.of(
                                    MessageValidationStrategy.targetCompId("EXCHANGE_1")
                                            .and(MessageValidationStrategy.senderCompId(Collections.singletonList("TAKER_FIRM_1")))));
            configuration.aeronContext().aeronDirectoryName(CommonContext.getAeronDirectoryName());
        }

        FixEngine fixEngine = FixEngine.launch(configuration);

        if (isAcceptorEngine) {
            System.out.println("Acceptor Fix Engine Started !!!");
        } else {
            System.out.println("Initiator Fix Engine Started !!!");
        }

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
