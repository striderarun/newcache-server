package com.newcache.server.command.handler;

import com.newcache.server.command.object.CommandObject;

import java.util.Arrays;
import java.util.List;

/**
 * Empty command handler implementation.
 * Returns empty list as response.
 */
public class EmptyCommandHandler implements CommandHandler {

    @Override
    public CommandObject parseCommand(String command) {
        return null;
    }

    @Override
    public List<String> processCommand(String command) {
        return Arrays.asList(command);
    }
}
