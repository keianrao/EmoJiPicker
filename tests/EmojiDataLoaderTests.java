
public class EmojiDataLoaderTests {

public static void testAll() {
    testParseUnicodeScalar();
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

public static void testLoadEmojiData() {

}

//  \\  //  \\  //  \\  //  \\  //  \\  //  \\

public static void main(String[] args) {
    testAll();
}

}
