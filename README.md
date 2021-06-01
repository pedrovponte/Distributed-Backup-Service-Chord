# Run

```shell
make all
make rmi # kill rmi if needed
make peer1
make peer2
make peer3
```

or

```shell
cd src/
javac -d build/ *.java
cd build/
rmiregistry &
java Peer 1.0 1 Peer1 6001
java Peer 1.0 2 Peer2 6002 127.0.1.1 6001
java Peer 1.0 3 Peer3 6003 127.0.1.1 6002
```
