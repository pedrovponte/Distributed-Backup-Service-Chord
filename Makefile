all: mkdir
	javac -d build/ src/*.java src/*/*.java

mkdir:
	@mkdir -p build/

clean:
	@rm -rf build/

rmi:
	rmiregistry &

REP_DEG = 1
NEW_SIZE = 0
FILEPATH = "file.pdf"
ACCESS_POINT = "ap14661"

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