# Unix Perf Commands

## htop for single process
htop -p <pid>

## Context switches

pidstat -wt 1

-w: show voluntary (cswch/s) and non-voluntary (nvcswch/s) context switches.
-t: show per-thread info.
1: updates every second.


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


 

