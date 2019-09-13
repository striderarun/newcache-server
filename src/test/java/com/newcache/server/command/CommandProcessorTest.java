package com.newcache.server.command;

import com.newcache.server.cache.NewCache;
import com.newcache.server.command.object.BufferedCommandState;
import com.newcache.server.command.object.CommandObject;
import com.newcache.server.constants.Constants;
import org.junit.After;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommandProcessorTest {

    CommandProcessor commandProcessor = new CommandProcessor();

    @After
    public void cleanUp() {
        BufferedCommandState.unset();
        NewCache.getInstance().invalidateAll();
    }

    @Test
    public void testInvalidCommand() {
        String command = "wrongcommand\n";
        List<String> commandResponse = commandProcessor.processCommand(command);
        assertEquals(Constants.ERROR, commandResponse.get(0));
    }

    @Test
    public void testBadSetCommandWithNoParameters() {
        String command = "set \n";
        List<String> commandResponse = commandProcessor.processCommand(command);
        assertEquals(Constants.ERROR, commandResponse.get(0));
    }

    @Test
    public void testBadSetCommandWithOnlyKeyParameter() {
        String command = "set x\n";
        List<String> commandResponse = commandProcessor.processCommand(command);
        assertEquals(Constants.ERROR, commandResponse.get(0));
    }

    @Test
    public void testBadSetCommandWithOnlyKeyAndFlagParameters() {
        String command = "set x 10\n";
        List<String> commandResponse = commandProcessor.processCommand(command);
        assertEquals(Constants.ERROR, commandResponse.get(0));
    }

    @Test
    public void testBadSetCommandWithOnlyKeyFlagAndExpiryParameters() {
        String command = "set x 10 0\n";
        List<String> commandResponse = commandProcessor.processCommand(command);
        assertEquals(Constants.ERROR, commandResponse.get(0));
    }

    @Test
    public void testBadSetCommandWithExtraParameters() {
        String command = "set x 10 0 5 7 10\n";
        List<String> commandResponse = commandProcessor.processCommand(command);
        assertEquals(Constants.ERROR, commandResponse.get(0));
    }

    @Test
    public void testBadSetCommandWithNonNumericFlagParameter() {
        String command = "set x a 0 5\n";
        List<String> commandResponse = commandProcessor.processCommand(command);
        assertEquals(Constants.CLIENT_ERROR_BAD_FORMAT, commandResponse.get(0));
    }

    @Test
    public void testBadSetCommandWithNonNumericExpiryParameter() {
        String command = "set x 10 y 5\n";
        List<String> commandResponse = commandProcessor.processCommand(command);
        assertEquals(Constants.CLIENT_ERROR_BAD_FORMAT, commandResponse.get(0));
    }

    @Test
    public void testBadSetCommandWithNonNumericBytesParameter() {
        String command = "set x 10 0 z\n";
        List<String> commandResponse = commandProcessor.processCommand(command);
        assertEquals(Constants.CLIENT_ERROR_BAD_FORMAT, commandResponse.get(0));
    }

    @Test
    public void testBadSetCommandWithAllNonNumericParameters() {
        String command = "set w x y z\n";
        List<String> commandResponse = commandProcessor.processCommand(command);
        assertEquals(Constants.CLIENT_ERROR_BAD_FORMAT, commandResponse.get(0));
    }

    @Test
    public void testBadGetCommandWithNoKey() {
        String command = "get \n";
        List<String> commandResponse = commandProcessor.processCommand(command);
        assertEquals(Constants.ERROR, commandResponse.get(0));
    }

    @Test
    public void testValidSetCommandWithoutNoReplyParameters() {
        String command = "set X 1 0 3\n";
        List<String> commandResponse = commandProcessor.processCommand(command);

        //Retrieve the set command state from memory
        CommandObject commandObject = BufferedCommandState.get();

        assertEquals("X", commandObject.getKey());
        assertEquals(1, commandObject.getFlags());
        assertEquals(0, commandObject.getExpiryTime());
        assertEquals(3, commandObject.getBytes());
        assertEquals(null, commandObject.getNoReply());
        assertEquals(0, commandResponse.size());

    }

    @Test
    public void testValidSetCommandWithNoReplyParameters() {
        String command = "set X 1 0 3 noreply\n";
        List<String> commandResponse = commandProcessor.processCommand(command);

        //Retrieve the set command state from memory
        CommandObject commandObject = BufferedCommandState.get();

        assertEquals("X", commandObject.getKey());
        assertEquals(1, commandObject.getFlags());
        assertEquals(0, commandObject.getExpiryTime());
        assertEquals(3, commandObject.getBytes());
        assertEquals("noreply", commandObject.getNoReply());
        assertEquals(0, commandResponse.size());

    }

    @Test
    public void testValidStorageInCacheWithNoReplyParameters() {
        String command1 = "set X 1 0 5 noreply\n";
        List<String> commandResponse1 = commandProcessor.processCommand(command1);

        String command2 = "cache";
        List<String> commandResponse2 = commandProcessor.processCommand(command2);

        NewCache cache = NewCache.getInstance();
        CommandObject commandObject = cache.get("X");

        assertEquals("cache", commandObject.getValue());
        assertEquals(0, commandResponse1.size());
        assertEquals(Constants.STORED, commandResponse2.get(0));

    }

    @Test
    public void testValidStorageInCacheWithoutNoReplyParameters() {
        String command1 = "set X 1 0 5\n";
        List<String> commandResponse1 = commandProcessor.processCommand(command1);

        String command2 = "cache";
        List<String> commandResponse2 = commandProcessor.processCommand(command2);

        NewCache cache = NewCache.getInstance();
        CommandObject commandObject = cache.get("X");

        assertEquals("cache", commandObject.getValue());
        assertEquals(0, commandResponse1.size());
        assertEquals(Constants.STORED, commandResponse2.get(0));

    }

    @Test
    public void testValidStorageInCacheWithZeroBytes() {
        String command1 = "set X 1 0 0\n";
        List<String> commandResponse1 = commandProcessor.processCommand(command1);

        String command2 = "\n";
        List<String> commandResponse2 = commandProcessor.processCommand(command2);

        NewCache cache = NewCache.getInstance();
        CommandObject commandObject = cache.get("X");

        assertEquals("", commandObject.getValue());
        assertEquals(0, commandResponse1.size());
        assertEquals(Constants.STORED, commandResponse2.get(0));

    }

    @Test
    public void testInValidStorageInCacheWithIncorrectBytes() {
        String command1 = "set X 1 0 3\n";
        List<String> commandResponse1 = commandProcessor.processCommand(command1);

        String command2 = "cache";
        List<String> commandResponse2 = commandProcessor.processCommand(command2);

        NewCache cache = NewCache.getInstance();
        CommandObject commandObject = cache.get("X");

        assertEquals(null, commandObject);
        assertEquals(0, commandResponse1.size());
        assertEquals(Constants.CLIENT_ERROR_BAD_CHUNK, commandResponse2.get(0));
    }

    @Test
    public void testRetrievalFromCacheWithExistingValue() {
        String command1 = "set X 1 0 5 noreply\n";
        List<String> commandResponse1 = commandProcessor.processCommand(command1);

        String command2 = "cache";
        List<String> commandResponse2 = commandProcessor.processCommand(command2);

        String command3 = "get X";
        List<String> commandResponse3 = commandProcessor.processCommand(command3);

        assertEquals(0, commandResponse1.size());
        assertEquals(Constants.STORED, commandResponse2.get(0));
        assertEquals(2, commandResponse3.size());
        assertEquals("VALUE X 1 5\ncache", commandResponse3.get(0));
        assertEquals("END\n", commandResponse3.get(1));
    }

    @Test
    public void testRetrievalFromCacheWithNonExistingValue() {
        String command1 = "set X 1 0 5 noreply\n";
        List<String> commandResponse1 = commandProcessor.processCommand(command1);

        String command2 = "cache";
        List<String> commandResponse2 = commandProcessor.processCommand(command2);

        String command3 = "get Y";
        List<String> commandResponse3 = commandProcessor.processCommand(command3);

        assertEquals(0, commandResponse1.size());
        assertEquals(Constants.STORED, commandResponse2.get(0));
        assertEquals(1, commandResponse3.size());
        assertEquals("END\n", commandResponse3.get(0));
    }


}
