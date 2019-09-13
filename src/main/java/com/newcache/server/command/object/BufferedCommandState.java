package com.newcache.server.command.object;

/**
 * Wrapper class around a ThreadLocal variable which
 * holds the set command state in memory while waiting for the
 * data block in the subsequent command.
 */
public final class BufferedCommandState {

    public static final ThreadLocal<CommandObject> commandObject = new ThreadLocal<>();

    public static void set(CommandObject cmdObj) {
        commandObject.set(cmdObj);
    }

    public static void unset() {
        commandObject.remove();
    }

    public static CommandObject get() {
        return commandObject.get();
    }
}
