# Unix Perf Commands

## htop for single process
htop -p <pid>

## Context switches

pidstat -wt 1

-w: show voluntary (cswch/s) and non-voluntary (nvcswch/s) context switches.
-t: show per-thread info.
1: updates every second.

pidstat -wt -p <pid> 1

## CPU affinity

taskset -acp <cpu_list> <pid>

```shell
taskset -acp 0-3 4456
```

# Start up commands

## Component initiator

```shell
java -XX:+UnlockExperimentalVMOptions -Xmx2g -Xms2g -jar ~/apps/media-driver.jar
taskset -ca 0-2 java -XX:+UnlockExperimentalVMOptions -Xmx2g -Xms2g -jar ~/apps/media-driver.jar
```

```shell
java -XX:+UnlockExperimentalVMOptions -Xmx2g -Xms2g --add-opens java.base/sun.nio.ch=ALL-UNNAMED -Dfix.core.receiver_buffer_size=1048576 -Dfix.core.sender_socket_buffer_size=16777216 -Dfix.core.receiver_socket_buffer_size=16777216 -jar ~/apps/fix-engine.jar
taskset -ca 3-5 java -XX:+UnlockExperimentalVMOptions -Xmx2g -Xms2g --add-opens java.base/sun.nio.ch=ALL-UNNAMED -Dfix.core.receiver_buffer_size=1048576 -Dfix.core.sender_socket_buffer_size=16777216 -Dfix.core.receiver_socket_buffer_size=16777216 -jar ~/apps/fix-engine.jar
```

```shell
java -jar ~/apps/artio-initiator.jar
taskset -ca 6-8 java -Dartio_demo.throughput=10000 -jar ~/apps/artio-initiator.jar
```


## Component acceptor

```shell
java -Dartio_demo.acceptor.engine=true -XX:+UnlockExperimentalVMOptions -Xmx2g -Xms2g -jar ~/apps/media-driver.jar
taskset -ca 15-17 java -Dartio_demo.acceptor.engine=true -XX:+UnlockExperimentalVMOptions -Xmx2g -Xms2g -jar ~/apps/media-driver.jar
```

```shell
java -Dartio_demo.acceptor.engine=true -XX:+UnlockExperimentalVMOptions -Xmx2g -Xms2g --add-opens java.base/sun.nio.ch=ALL-UNNAMED -Dfix.core.receiver_buffer_size=1048576 -Dfix.core.sender_socket_buffer_size=16777216 -Dfix.core.receiver_socket_buffer_size=16777216 -jar ~/apps/fix-engine.jar
taskset -ca 19-21 java -Dartio_demo.acceptor.engine=true -XX:+UnlockExperimentalVMOptions -Xmx2g -Xms2g --add-opens java.base/sun.nio.ch=ALL-UNNAMED -Dfix.core.receiver_buffer_size=1048576 -Dfix.core.sender_socket_buffer_size=16777216 -Dfix.core.receiver_socket_buffer_size=16777216 -jar ~/apps/fix-engine.jar
```

 

