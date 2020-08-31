
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

class SwingGUI implements ActionListener {

//  Interface   //  \\  //  \\  //  \\  //  \\  //  \\

public JPanel getPanel() {
    return mainpanel;
}

public void setFrameVisible(boolean visibility) {
    mainframe.setVisible(visibility);
}

public void displayEmojiGroup(String groupID) {
    List<Backend.Emoji> emojiGroup = backend.getEmojiGroup(groupID);

    for (Backend.Emoji emoji: emojiGroup) {
        EmojiButton button = new EmojiButton(emoji);
        emojiButtonsPanel.add(button);
        button.addActionListener(this);
    }
    emojiButtonsPanel.revalidate();
}




//  Private classes     //  \\  //  \\  //  \\  //  \\

class EmojiButton extends JButton {
    final Backend.Emoji emoji;

    EmojiButton(Backend.Emoji emoji) {
        super(emoji.qualifiedSequence);

        this.emoji = emoji;

        setMargin(new Insets(0, 0, 0, 0));
        setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        setBorder(BorderFactory.createRaisedBevelBorder());
    }
}

class EmojiGroupButton extends JToggleButton {
    final String groupID;

    EmojiGroupButton(String groupID) {
        this.groupID = groupID;

        char initialLetter = groupID.isEmpty() ? ' ' : groupID.charAt(0);
        setText(Character.toString(initialLetter));
        // For now, we go with this. Later on we'll set our text as
        // the first emoji in the emoji group.

        setMargin(new Insets(0, 0, 0, 0));
        setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
    }
}




//  Private constants   //  \\  //  \\  //  \\  //  \\

private static final int BUTTON_WIDTH = 24;
private static final int BUTTON_HEIGHT = 24;
private static final int BUTTONPANEL_HGAP = 2;
private static final int BUTTONPANEL_VGAP = 2;



//  Private members     //  \\  //  \\  //  \\  //  \\

Backend backend;

JFrame mainframe;
JPanel mainpanel;

JPanel upperPanel;
JTextField pickupField;
JPanel emojiGroupButtonsBar;
JPanel emojiButtonsPanel;

String currentlySelectedGroupID = null;



//  Private methods     //  \\  //  \\  //  \\  //  \\

private void syncWithBackend() {
    pickupField.setText("");

    emojiGroupButtonsBar.removeAll();
    List<String> groupIDs = backend.getEmojiGroupIDs();
    ButtonGroup buttonGroup = new ButtonGroup();
    for (String groupID: groupIDs) {
        EmojiGroupButton button = new EmojiGroupButton(groupID);
        button.setSelected(groupID.equals(currentlySelectedGroupID));
        buttonGroup.add(button);
        emojiGroupButtonsBar.add(button);
        button.addActionListener(this);
    }
    emojiGroupButtonsBar.validate();
}

public void actionPerformed(ActionEvent e) {
    if (e.getSource() instanceof EmojiButton) {
        EmojiButton emojiButton = (EmojiButton)e.getSource();
        pickupField.setText(
            pickupField.getText()
            + emojiButton.emoji.qualifiedSequence
        );
    }
    else if (e.getSource() instanceof EmojiGroupButton) {
        EmojiGroupButton emojiGroupButton = (EmojiGroupButton)e.getSource();
        currentlySelectedGroupID = emojiGroupButton.groupID;
        displayEmojiGroup(emojiGroupButton.groupID);
    }
}



//  Constructors    \\  //  \\  //  \\  //  \\  //  \\

SwingGUI(Backend backend) {
    this.backend = backend;

    mainframe = new JFrame("Emoji Picker");
    // Frame title is intentionally generic
    mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainframe.setSize(340, 512);

    pickupField = new JTextField();
    emojiGroupButtonsBar = new JPanel();
    upperPanel = new JPanel();
    upperPanel.setLayout(new GridLayout(2, 0));
    upperPanel.add(pickupField);
    upperPanel.add(emojiGroupButtonsBar);
    upperPanel.setPreferredSize(new Dimension(0, (int)(BUTTON_HEIGHT * 2.5)));

    emojiButtonsPanel = new JPanel();
    emojiButtonsPanel.setLayout(new FlowLayout(
        FlowLayout.LEFT,
        BUTTONPANEL_HGAP, BUTTONPANEL_VGAP
    ));

    mainpanel = new JPanel();
    mainpanel.setBorder(
        BorderFactory.createEmptyBorder(
            BUTTONPANEL_VGAP, BUTTONPANEL_HGAP,
            BUTTONPANEL_VGAP, BUTTONPANEL_HGAP
        )
    );
    mainpanel.setLayout(new BorderLayout());
    mainpanel.add(upperPanel, BorderLayout.NORTH);
    mainpanel.add(emojiButtonsPanel, BorderLayout.CENTER);
    mainframe.setContentPane(mainpanel);

    syncWithBackend();
}



//  Main    \\  //  \\  //  \\  //  \\  //  \\  //  \\

public static void main(String... args) throws FileNotFoundException, IOException {
    Backend backend = new Backend();

    EmojiDataLoader dataLoader = new EmojiDataLoader();
    try {
        dataLoader.loadEmojiData(backend);
    }
    catch (FileNotFoundException eFnf) {
        showMissingEmojiDataFileDialog();
        throw eFnf;
    }
    catch (IOException eIo) {
        showIOExceptionDialog();
        throw eIo;
    }

    SwingGUI gui = new SwingGUI(backend);

    gui.setFrameVisible(true);
}

private static void showMissingEmojiDataFileDialog() {
    final String MISSING_EMOJI_FILE_DIALOGUE_MESSAGE = (
        "EmoJiPicker loads emojis from a certain data file. " +
        "It is called 'emoji-test.txt', and is provided by Unicode. " +
        "Our data loader said that it couldn't find that file at " +
        "where it expects in the current directory.\n\n" +

        "Without the file the program knows no emojis, so " +
        "it cannot show any buttons for them in the picker.\n\n" +

        "One of two things is probably happening. " +
        "First is, the file is genuinely missing, and you'd need to " +
        "download it and place it where this program expects it. " +
        "(That is probably in the 'data' folder inside where " +
        "this program resides.) " +
        "Second is, the current directory when this program is running (now) " +
        "isn't where the program is, but rather something like your " +
        "\"home directory\". This case is more likely and, to fix it, " +
        "you need to navigate to the folder the program is in then " +
        "start the program from there.\n\n" +

        "As mentioned, this program has no emoji to show, so " +
        "we'll just quit for now. Apologies for the trouble.."
    );
    JOptionPane.showMessageDialog(null, MISSING_EMOJI_FILE_DIALOGUE_MESSAGE);
}

private static void showIOExceptionDialog() {
    final String IO_EXCEPTION_DIALOGUE_MESSAGE = (
        "We found the emoji data file and tried to load it, but " +
        "then we got an IO exception. Which is a very rare type of problem. " +
        "The program hasn't really started yet so it should be safe for " +
        "us to do the most appropriate course of action, which is quit.\n\n" +

        "Try opening the program again after this, it might resolve.. " +
        "If it doesn't, then there is some issue with the data file. " +
        "Apologies about this.."
    );
    JOptionPane.showMessageDialog(null, IO_EXCEPTION_DIALOGUE_MESSAGE);
}

}
