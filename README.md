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

## Media Driver

```shell
java -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -Xmx2g -Xms2g -jar ~/apps/media-driver.jar
taskset -ca 0-2 java -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -Xmx2g -Xms2g -jar ~/apps/media-driver.jar
```

```shell
java -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -Xmx2g -Xms2g --add-opens java.base/sun.nio.ch=ALL-UNNAMED -Dfix.core.receiver_buffer_size=1048576 -Dfix.core.sender_socket_buffer_size=16777216 -Dfix.core.receiver_socket_buffer_size=16777216 -jar ~/apps/fix-engine.jar
taskset -ca 3-5 java -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -Xmx2g -Xms2g --add-opens java.base/sun.nio.ch=ALL-UNNAMED -Dfix.core.receiver_buffer_size=1048576 -Dfix.core.sender_socket_buffer_size=16777216 -Dfix.core.receiver_socket_buffer_size=16777216 -jar ~/apps/fix-engine.jar
```

```shell
java -jar ~/apps/artio-initiator.jar
taskset -ca 6-8 java -jar ~/apps/artio-initiator.jar
```


 

