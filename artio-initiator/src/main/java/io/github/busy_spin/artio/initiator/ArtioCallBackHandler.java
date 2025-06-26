package io.github.busy_spin.artio.initiator;

import io.aeron.logbuffer.ControlledFragmentHandler;
import org.HdrHistogram.Histogram;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.SystemNanoClock;
import uk.co.real_logic.artio.builder.TestRequestEncoder;
import uk.co.real_logic.artio.decoder.HeartbeatDecoder;
import uk.co.real_logic.artio.library.FixLibrary;
import uk.co.real_logic.artio.library.LibraryConnectHandler;
import uk.co.real_logic.artio.library.OnMessageInfo;
import uk.co.real_logic.artio.library.SessionAcquireHandler;
import uk.co.real_logic.artio.library.SessionAcquiredInfo;
import uk.co.real_logic.artio.library.SessionConfiguration;
import uk.co.real_logic.artio.library.SessionExistsHandler;
import uk.co.real_logic.artio.library.SessionHandler;
import uk.co.real_logic.artio.messages.DisconnectReason;
import uk.co.real_logic.artio.session.Session;
import uk.co.real_logic.artio.util.MutableAsciiBuffer;

import java.nio.ByteBuffer;

public class ArtioCallBackHandler implements LibraryConnectHandler, SessionAcquireHandler, SessionHandler, SessionExistsHandler {

    private String testReqId  = "ABC";

    private Session session;

    private final TestRequestEncoder encoder = new TestRequestEncoder();

    private final HeartbeatDecoder decoder = new HeartbeatDecoder();

    private final ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);

    private final MutableAsciiBuffer mutableAsciiBuffer = new MutableAsciiBuffer();

    Histogram histogram = new Histogram(1, 200_000_000, 3);

    private long counter = 0;

    private long delta = 0;

    @Override
    public void onConnect(FixLibrary fixLibrary) {
        System.out.println("Connected to library " + fixLibrary.libraryId());
        fixLibrary.initiate(SessionConfiguration.builder()
                        .address("localhost", 2134)
                        .targetCompId("EXCHANGE")
                        .senderCompId("TAKER_FIRM")
                .build());
    }

    @Override
    public void onDisconnect(FixLibrary fixLibrary) {
        System.out.println("Disconnected from library " + fixLibrary.libraryId());
    }

    @Override
    public SessionHandler onSessionAcquired(Session session, SessionAcquiredInfo sessionAcquiredInfo) {
        this.session = session;
        return this;
    }

    public void sendTestRequest() {
        if (session != null && session.isConnected()) {
            long sendTime = SystemNanoClock.INSTANCE.nanoTime();
            encoder.testReqID(String.valueOf(sendTime));
            session.trySend(encoder);
        }
    }

    public void printAndResetCounter() {
        delta += (histogram.getTotalCount() - counter);
        System.out.printf("""
                        >>>
                        Send-count  = [%d], receive count = [%d], send-rsv-delta = [%d], p100 = [%d], p99.999 = [%d], p99.99 = [%d]
                       
                        """,
                histogram.getTotalCount(),
                counter,
                delta,
                histogram.getValueAtPercentile(100),
                histogram.getValueAtPercentile(99.999),
                histogram.getValueAtPercentile(99.99));
        histogram.reset();
        counter = 0;
    }

    @Override
    public ControlledFragmentHandler.Action onMessage(DirectBuffer buffer,
                                                      int offset,
                                                      int length,
                                                      int libraryId,
                                                      Session session,
                                                      int sequenceIndex,
                                                      long messageType,
                                                      long timestampInNs,
                                                      long position,
                                                      OnMessageInfo messageInfo) {
        if (messageType == HeartbeatDecoder.MESSAGE_TYPE) {
            mutableAsciiBuffer.wrap(buffer, offset, length);
            decoder.decode(mutableAsciiBuffer, 0, length);

            if (decoder.hasTestReqID()) {
                long startTime = Long.parseLong(decoder.testReqIDAsString());
                long endTime = SystemNanoClock.INSTANCE.nanoTime();
                histogram.recordValue((endTime - startTime) / 1000);
                counter++;
            }
        }

        return ControlledFragmentHandler.Action.CONTINUE;
    }

    @Override
    public void onTimeout(int libraryId, Session session) {
        System.out.println("Session timed out");
    }

    @Override
    public void onSlowStatus(int libraryId, Session session, boolean hasBecomeSlow) {
        System.out.println("Slow session detected " + hasBecomeSlow);
    }

    @Override
    public ControlledFragmentHandler.Action onDisconnect(int libraryId, Session session,
                                                         DisconnectReason disconnectReason) {
        System.out.println("Disconnected from session");
        return ControlledFragmentHandler.Action.CONTINUE;
    }

    @Override
    public void onSessionStart(Session session) {
        System.out.println("Session started");
        this.session = session;
    }

    @Override
    public void onSessionExists(
            final FixLibrary library,
            final long surrogateSessionId,
            final String localCompId,
            final String localSubId,
            final String localLocationId,
            final String remoteCompId,
            final String remoteSubId,
            final String remoteLocationId, final int logonReceivedSequenceNumber, final int logonSequenceIndex) {

        if (localCompId.equals("TAKER_FIRM")) {
            System.out.println("Session exists, requesting session " + localCompId + "->" + remoteCompId);
            library.requestSession(surrogateSessionId, FixLibrary.NO_MESSAGE_REPLAY,
                    FixLibrary.NO_MESSAGE_REPLAY, 10_000);
        }

    }
}
