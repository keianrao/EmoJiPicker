
all: main tests

main:
	javac *.java

tests:
	javac tests/*.java

clean:
	rm *.class
	rm tests/*.class

run-main:
	java SwingGUI

run-tests:
	java tests.EmojiDataLoaderTests
	java tests.BackendTests

.PHONY: all main tests clean run-main run-tests
