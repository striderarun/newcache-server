package com.newcache.server.command.handler;

import com.newcache.server.command.object.BufferedCommandState;
import com.newcache.server.constants.Constants;
import com.newcache.server.exception.InvalidCommandException;

import static com.newcache.server.constants.Constants.GET_COMMAND;
import static com.newcache.server.constants.Constants.SET_COMMAND;

/**
 * Factory class which picks a CommandHandler implementation
 * depending on the command input.
 *
 */
public final class CommandHandlerFactory {

    /**
     * Returns appropriate CommandHandler implementation based
     * on command input string and current buffer state.
     *
     * @param command String
     * @return CommandHandler
     * @throws InvalidCommandException
     */
    public final CommandHandler getCommandHandler(String command) throws InvalidCommandException {
        // If an unfinished 'set' command is in buffer, redirect this command to SetCommandHandler as data block.
        if (BufferedCommandState.get() != null) {
            return getCommandHandlerInternal(SET_COMMAND);
        } else {
            return getCommandHandlerInternal(command);
        }
    }

    /**
     * Returns appropriate CommandHandler implementation based
     * on command input string.
     * Throws Exception if command is unknown.
     *
     * @param command String
     * @return CommandHandler
     * @throws InvalidCommandException
     */
    private CommandHandler getCommandHandlerInternal(String command) throws InvalidCommandException {
        if (command.trim().equals("")) {
            return new EmptyCommandHandler();
        } else if (command.trim().startsWith(GET_COMMAND)) {
            return new GetCommandHandler();
        } else if (command.trim().startsWith(SET_COMMAND)) {
            return new SetCommandHandler();
        } else {
            throw new InvalidCommandException(Constants.ERROR);
        }
    }

}
