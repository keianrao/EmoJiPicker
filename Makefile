
COMPILE_OPTIONS=\
	-encoding utf-8 \
# This program is explicitly a Unicode program (arguably a UTF16 one).
# The test code for EmojiDataLoader have hardcoded emoji in them - but
# by default, javac follows the system's default character encoding. Then,
# some systems, like my own CDE environment which uses the 'C' locale,
# do not use some Unicode character encoding as their default. Which makes
# javac fail in compiling the tests. Therefore, '-encoding utf-8'.

RUN_OPTIONS=\
	-Dawt.useSystemAAFontSettings=gasp
# 

all: main tests

main:
	javac $(COMPILE_OPTIONS) *.java

tests:
	javac $(COMPILE_OPTIONS) tests/*.java

clean:
	rm *.class || :
	rm tests/*.class || :

run-main:
	java $(RUN_OPTIONS) SwingGUI

run-tests:
	java $(RUN_OPTIONS) -cp .:tests -ea EmojiDataLoaderTests
	java $(RUN_OPTIONS) -cp .:tests -ea BackendTests

.PHONY: all main tests clean run-main run-tests
