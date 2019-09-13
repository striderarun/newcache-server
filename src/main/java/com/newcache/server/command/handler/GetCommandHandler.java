package com.newcache.server.command.handler;

import com.newcache.server.cache.NewCache;
import com.newcache.server.command.object.CommandObject;
import com.newcache.server.constants.Constants;
import com.newcache.server.exception.BadCommandException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CommandHandler implementation to process the get command.
 */
public class GetCommandHandler implements CommandHandler {

    private final static Logger LOGGER = Logger.getLogger(GetCommandHandler.class);

    /**
     * Parse the get command and extract the key.
     *
     * @param command String
     * @return CommandObject
     * @throws BadCommandException
     */
    @Override
    public CommandObject parseCommand(String command) throws BadCommandException {
        Pattern p = Pattern.compile("^\\s*get\\s+(\\S+)\\s*$");
        Matcher m = p.matcher(command);

        if (m.find()) {
            return new CommandObject(m.group(1), 0, 0, 0, null);
        } else {
            LOGGER.error("Invalid command");
            throw new BadCommandException(Constants.ERROR);
        }
    }

    /**
     * Parse the command to extract tokens.
     * Retrieve the key from the cache.
     * Construct and return response.
     * Handle input exception if any.
     *
     * @param command String
     * @return List<String>
     */
    @Override
    public List<String> processCommand(String command) {
        List<String> response = new ArrayList<>();
        try {
            CommandObject commandObject = parseCommand(command);

            NewCache cache = NewCache.getInstance();
            CommandObject cachedObject = cache.get(commandObject.getKey());

            if (cachedObject != null) {
                LOGGER.debug("Retrieved key from cache");
                response.add(String.format("VALUE %s %d %d\n%s", cachedObject.getKey(), cachedObject.getFlags(),
                        cachedObject.getBytes(), cachedObject.getValue()));
            }
            response.add(Constants.END);
        } catch (BadCommandException ex) {
            LOGGER.error(ex);
            response.add(ex.getMessage());
        }
        return response;
    }
}
