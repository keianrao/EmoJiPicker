
OPTIONS=-encoding utf-8
# This program is explicitly a Unicode program (arguably a UTF16 one).
# Tests for EmojiDataLoader have hardcoded emoji in them.
# ..Some systems, like my own CDE environment which defaults to the 'C' locale,
# do not have some form of Unicode as their default character encoding.
# Then, as javac follows the system's default character encoding by default,
# it fails to compile.
# ..Hence mandatory '-encoding utf-8' here.


all: main tests

main:
	javac $(OPTIONS) *.java

tests:
	javac $(OPTIONS) tests/*.java

clean:
	rm *.class || :
	rm tests/*.class || :

run-main:
	java SwingGUI

run-tests:
	java -cp .:tests -ea EmojiDataLoaderTests
	java -cp .:tests -ea BackendTests

.PHONY: all main tests clean run-main run-tests
