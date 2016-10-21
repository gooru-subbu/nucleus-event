package org.gooru.nucleus.handlers.events.constants;

public final class MessagebusEndpoints {
    /*
     * Any change here in end points should be done in the gateway side as well,
     * as both sender and receiver should be in sync
     */
    public static final String MBEP_EVENT = "org.gooru.nucleus.message.bus.publisher.event";

    private MessagebusEndpoints() {
        throw new AssertionError();
    }
}
