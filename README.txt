How to Build and Run
——————————----------
The project is based on gradle and can be run as follows:

 In the root folder of the project, execute 
./gradlew run

Connecting to the server
———————————-------------
The TCP server is listening at 11211. You can send commands to the server as follows:

telnet localhost 11211
Connected to localhost.
Escape character is '^]'.
set A 1 0 3 4
eru
STORED
get A
VALUE A 1 3
eru
END


