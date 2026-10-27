package su.nightexpress.nightcore.bridge.text.event;

import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.bridge.common.NightKey;
import su.nightexpress.nightcore.bridge.common.NightNbtHolder;

public interface WrappedPayload {

    record Text(@NonNull String value) implements WrappedPayload {
    }

    record Int(int integer) implements WrappedPayload {
    }

    record Custom(@NonNull NightKey key, @NonNull NightNbtHolder nbt) implements WrappedPayload {
    }


    static WrappedPayload.@NonNull Text string(@NonNull String value) {
        return new Text(value);
    }

    static WrappedPayload.@NonNull Int integer(int integer) {
        return new Int(integer);
    }


    // BinaryTagHolder nbt
    static WrappedPayload.@NonNull Custom custom(@NonNull NightKey key, @NonNull NightNbtHolder nbt) {
        return new Custom(key, nbt);
    }
}
