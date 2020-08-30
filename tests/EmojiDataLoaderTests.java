
import java.util.List;
import java.util.LinkedList;

public class EmojiDataLoaderTests {

public static void testAll() {
    testParseUnicodeScalar();
    testFilterForGroupCommentsAndDataLines();
    testLoadEmojiData();
}

public static void testParseUnicodeScalar() {
    // Some examples from emoji-test..
    // Correct answers are copied from GNU bc
    assert EmojiDataLoader.parseUnicodeScalar("1F600") == 128512;
    assert EmojiDataLoader.parseUnicodeScalar("263A") == 9786;
    assert EmojiDataLoader.parseUnicodeScalar("FE0F") == 65039;

    // Sanity checks..
    assert EmojiDataLoader.parseUnicodeScalar("0") == 0;
    assert EmojiDataLoader.parseUnicodeScalar("00") == 0;

    // We won't ask parseUnicodeScalar to reject invalid Unicode scalars.
    // We'll just test that it understands this syntax we're reading.
}

public static void testFilterForGroupCommentsAndDataLines() {
    List<String> lines = new LinkedList<String>();
    List<String> response1, response2, response3;

    // Add random things that should all be ignored
    lines.add("# For documentation and usage");
    lines.add("# subgroup: face-smiling");
    lines.add("#randomcomment");
    lines.add("");
    lines.add("  ");
    response1 = EmojiDataLoader.filterForGroupCommentsAndDataLines(lines);
    assert response1.size() == 0;

    // Add data-line-like lines that shouldn't be ignored
    lines.add(2, "1F448");
    lines.add("1F448 ; fully-qualified");
    lines.add("1F448 2708");
    lines.add("1F92A ; minimally-qualified");
    response2 = EmojiDataLoader.filterForGroupCommentsAndDataLines(lines);
    assert response2.size() == 4;

    // Add group comment lines
    lines.add(4, "# group: Test I");
    lines.add(6, "# group:Test II");
    lines.add("#group: Test III");
    response3 = EmojiDataLoader.filterForGroupCommentsAndDataLines(lines);
    assert response3.size() == 7;

    // Okay, final clarification..
    List<String> exampleUnfilteredFile = new LinkedList<String>();
    List<String> filteredExampleFile = new LinkedList<String>();
    List<String> response4;
    for (String l: new String[] {
        "#   • The file is in CLDR order, not codepoint order.",
        "#   • The groups and subgroups are illustrative.",
        " ",
        "# group: Smileys & Emotion",
        "",
        "# subgroup: face-smiling",
        "1F600                                      ; fully-qualified",
        "1F603                                      ; fully-qualified",
        "",
        "# subgroup: face-tongue",
        "1F60B                                      ; fully-qualified",
        "",
        "# group: People & Body",
        "",
        "# subgroup: hand-fingers-open",
        "1F44B                                      ; fully-qualified",
        "1F44B 1F3FB                                ; fully-qualified",
        "1F44B 1F3FC                                ; fully-qualified"
    }) exampleUnfilteredFile.add(l);
    for (String l: new String[] {
        "# group: Smileys & Emotion",
        "1F600                                      ; fully-qualified",
        "1F603                                      ; fully-qualified",
        "1F60B                                      ; fully-qualified",
        "# group: People & Body",
        "1F44B                                      ; fully-qualified",
        "1F44B 1F3FB                                ; fully-qualified",
        "1F44B 1F3FC                                ; fully-qualified"
    }) filteredExampleFile.add(l);
    response4 = EmojiDataLoader
        .filterForGroupCommentsAndDataLines(exampleUnfilteredFile);
    assert response4.equals(filteredExampleFile);
}

public static void testLoadEmojiData() {

}



//  \\  //  \\  //  \\  //  \\  //  \\  //  \\

public static void main(String[] args) {
    testAll();
}

}
