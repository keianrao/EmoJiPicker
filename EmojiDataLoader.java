
public class EmojiDataLoader {

//  \\  //  \\  //  \\  //  \\  //  \\  //  \\

public void loadEmojiData(Backend backend) {
    // Warn: This method is not very testable.
}



//  \\  //  \\  //  \\  //  \\  //  \\  //  \\

public static int parseUnicodeScalar(String string) {
    assert string.matches("^[0-9a-fA-F]+$");
    // We can use \p{XDigit} but this is clearer
    // If this is failing for you, check that you didn't input O instead of 0

    try {
        return Integer.parseInt(string, 16);
    }
    catch (NumberFormatException eNf) {
        assert false;
        return 0;
    }
}

}
