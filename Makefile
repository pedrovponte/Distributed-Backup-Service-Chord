REP_DEG = 1
NEW_SIZE = 0
FILEPATH = "file.pdf"
ACCESS_POINT = "1923"

all: mkdir
	cd src; javac -d build/ *.java #src/*/*.java src/*/*/*.java

mkdir:
	@mkdir -p src/build/

clean:
	@rm -rf src/build/

rmi:
	cd src/build; rmiregistry &

# PEERS
peer1:
	cd src/build; java Peer 1.0 1 Peer1 6001

peer2:
	cd src/build; java Peer 1.0 2 Peer2 6002 127.0.1.1 6001

peer3:
	cd src/build; java Peer 1.0 3 Peer3 6003 127.0.1.1 6002

# CLIENT
backup:
	@bash scripts/backup.sh $(ACCESS_POINT) $(FILEPATH) $(REP_DEG)

restore:
	@bash scripts/restore.sh $(ACCESS_POINT) $(FILEPATH)

delete:
	@bash scripts/delete.sh $(ACCESS_POINT) $(FILEPATH)

reclaim:
	@bash scripts/reclaim.sh $(ACCESS_POINT) $(NEW_SIZE)

state:
	@bash scripts/state.sh $(ACCESS_POINT)

