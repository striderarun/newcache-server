package com.newcache.server.command.object;

public class CommandObject {

    private String key;
    private String value;
    private int flags;
    private int expiryTime;
    private int bytes;
    private String noReply;

    public CommandObject(String key, int flags, int expiryTime, int bytes, String noReply) {
        this.key = key;
        this.flags = flags;
        this.expiryTime = expiryTime;
        this.bytes = bytes;
        this.noReply = noReply;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public int getFlags() {
        return flags;
    }

    public int getExpiryTime() {
        return expiryTime;
    }

    public int getBytes() {
        return bytes;
    }

    public String getNoReply() {
        return noReply;
    }
}
