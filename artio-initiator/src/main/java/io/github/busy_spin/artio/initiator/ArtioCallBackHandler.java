package io.github.busy_spin.artio.initiator;

import io.aeron.logbuffer.ControlledFragmentHandler;
import org.agrona.DirectBuffer;
import uk.co.real_logic.artio.builder.TestRequestEncoder;
import uk.co.real_logic.artio.decoder.HeartbeatDecoder;
import uk.co.real_logic.artio.library.*;
import uk.co.real_logic.artio.messages.DisconnectReason;
import uk.co.real_logic.artio.session.Session;
import uk.co.real_logic.artio.util.MutableAsciiBuffer;

import java.nio.ByteBuffer;

public class ArtioCallBackHandler implements LibraryConnectHandler, SessionAcquireHandler, SessionHandler {

    private String testReqId  = "ABC";

    private Session session;

    private final TestRequestEncoder encoder = new TestRequestEncoder();

    private final HeartbeatDecoder decoder = new HeartbeatDecoder();

    private final ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);

    private final MutableAsciiBuffer mutableAsciiBuffer = new MutableAsciiBuffer();

    private long counter = 0;

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
        return this;
    }

    public void sendTestRequest() {
        if (session != null) {
            byteBuffer.clear();
            byteBuffer.putLong(System.currentTimeMillis());
            encoder.testReqID(testReqId);
            session.trySend(encoder);
        }
    }

    public void printAndResetCounter() {
        System.out.println("Count " + counter);
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
}
