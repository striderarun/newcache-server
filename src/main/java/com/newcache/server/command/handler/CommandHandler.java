package com.newcache.server.command.handler;

import com.newcache.server.command.object.CommandObject;
import com.newcache.server.exception.BadCommandException;
import com.newcache.server.exception.InvalidCommandException;

import java.util.List;

/**
 * Interface which defines the operations to be implemented
 * by a Command handler to parse and process the command.
 */
public interface CommandHandler {

    /**
     * Parse the command string and return a CommandObject that represents
     * the properties of a command.
     *
     * @param command String
     * @return CommandObject
     * @throws BadCommandException
     */
    CommandObject parseCommand(String command) throws BadCommandException, InvalidCommandException;

    /**
     * Process the command and return the command response.
     *
     * @param command
     * @return
     */
    List<String> processCommand(String command);

}
