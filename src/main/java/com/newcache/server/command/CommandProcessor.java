package com.newcache.server.command;

import com.newcache.server.command.handler.CommandHandler;
import com.newcache.server.command.handler.CommandHandlerFactory;
import com.newcache.server.constants.Constants;
import com.newcache.server.exception.InvalidCommandException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Processes each command and returns response.
 */
public final class CommandProcessor {

    private final static Logger LOGGER = Logger.getLogger(CommandProcessor.class);

    /**
     * Fetch appropriate CommandHandler implementation based on input command.
     * Process command using handler.
     * Handle client exceptions and construct response.
     *
     * @param command
     * @return List<String>
     */
    public final List<String> processCommand(String command) {
        LOGGER.debug("Processing command: " + command);
        CommandHandlerFactory commandHandlerFactory = new CommandHandlerFactory();
        List<String> commandResponse = new ArrayList<>();
        try {
            CommandHandler commandHandler = commandHandlerFactory.getCommandHandler(command);
            commandResponse = commandHandler.processCommand(command);
        } catch (InvalidCommandException ex) {
            LOGGER.error(ex);
            commandResponse.add(ex.getMessage());
        } catch (Exception e) {
            LOGGER.error(e);
            commandResponse.add(Constants.SERVER_ERROR + e.getMessage());
        }
        return commandResponse;
    }
}
