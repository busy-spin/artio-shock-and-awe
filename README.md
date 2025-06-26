# Unix Perf Commands

## htop for single process
htop -p <pid>


# Start up commands

## Media Driver

```shell
java -jar ~/apps/media-driver.jar
```

```shell
java --add-opens java.base/sun.nio.ch=ALL-UNNAMED -Dfix.core.receiver_buffer_size=1048576 -Dfix.core.sender_socket_buffer_size=16777216 -Dfix.core.receiver_socket_buffer_size=16777216 -jar ~/apps/fix-engine.jar
```

```shell
java -jar ~/apps/artio-initiator.jar
```


 

