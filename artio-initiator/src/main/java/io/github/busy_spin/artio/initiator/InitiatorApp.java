package io.github.busy_spin.artio.initiator;

import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.agrona.concurrent.SigInt;
import org.agrona.concurrent.SleepingIdleStrategy;

public class InitiatorApp {
    public static void main(String[] args) {
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();

        SigInt.register(() -> {
            System.out.println("Shutdown signal received");
            barrier.signal();
        });

        AgentRunner agentRunner = new AgentRunner(new SleepingIdleStrategy(), Throwable::printStackTrace,
                null, new InitiatorAgent());

        AgentRunner.startOnThread(agentRunner);

        System.out.println("Fix application started !!!");

        barrier.await();

        agentRunner.close();
    }
}
