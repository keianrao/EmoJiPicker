/*
* This file is part of EmoJiPicker.
* ©2020 Keian Rao <keian.rao@gmail.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

    emojiButtonsPanel.removeAll();
    emojiButtonsPanel.repaint();
    for (Backend.Emoji emoji: emojiGroup) {
        EmojiButton button = new EmojiButton(emoji);
        button.setFont(regularFont);
        emojiButtonsPanel.add(button);
        button.addActionListener(this);
    }
    emojiButtonsPanel.revalidate();
}




//  Private classes     //  \\  //  \\  //  \\  //  \\

private class EmojiButton extends JButton {
    final Backend.Emoji emoji;

    public void paintComponent(Graphics g) {
        Color backgroundColour = getBackground();

        g.setColor(adjustColour(backgroundColour, +15));
        g.fillArc(0, 0, getWidth(), getHeight(), 45, 180);
        g.setColor(adjustColour(backgroundColour, +30));
        g.fillArc(0, 0, getWidth(), getHeight(), 90, 90);
        g.setColor(adjustColour(backgroundColour, +45));
        g.fillArc(0, 0, getWidth(), getHeight(), 120, 30);

        g.setColor(adjustColour(backgroundColour, -15));
        g.fillArc(0, 0, getWidth(), getHeight(), 225, 180);
        g.setColor(adjustColour(backgroundColour, -30));
        g.fillArc(0, 0, getWidth(), getHeight(), 260, 90);
        g.setColor(adjustColour(backgroundColour, -45));
        g.fillArc(0, 0, getWidth(), getHeight(), 290, 30);

        g.setColor(getBackground());
        g.fillOval(
            BUTTON_BORDER_THICKNESS / 2,
            BUTTON_BORDER_THICKNESS / 2,
            getWidth() - BUTTON_BORDER_THICKNESS,
            getHeight() - BUTTON_BORDER_THICKNESS
        );

        super.paintComponent(g);
        /*
        * No one on the internet bothered discussing if this is legal.
        * (Calling super.paintComponent at the end)
        * Given what I know about what this method does, and the fact that
        * you can remove it outright safely.. this is probably fine.
        */
    }

    private Color adjustColour(Color colour, int change) {
        int r = colour.getRed() + change;
        int g = colour.getGreen() + change;
        int b = colour.getBlue() + change;
        if (r > 255) r = 255; if (r < 0) r = 0;
        if (g > 255) g = 255; if (g < 0) g = 0;
        if (b > 255) b = 255; if (b < 0) b = 0;
        return new Color(r, g, b);
    }

    EmojiButton(Backend.Emoji emoji) {
        super(emoji.qualifiedSequence);

        this.emoji = emoji;

        {
            setBackground(new Color(216, 216, 216));
            setContentAreaFilled(false);
            setBorderPainted(false);
            // These should be removed in the future
            // in favour of something less whimsical..
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setMargin(new Insets(0, 0, 0, 0));
        setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
    }
}

private class EmojiGroupButton extends JToggleButton {
    final String groupID;

    EmojiGroupButton(String groupID) {
        this.groupID = groupID;

        {
            List<Backend.Emoji> emojiGroup = backend.getEmojiGroup(groupID);
            if (emojiGroup.isEmpty()) {
                char initialLetter =
                    groupID.isEmpty() ? ' ' :  groupID.charAt(0);
                setText(Character.toString(initialLetter));
            }
            else {
                setText(emojiGroup.get(0).qualifiedSequence);
                /*
                * Our button widths have just enough for one rendered glyph.
                * But if you're on a system that doesn't have a powerful
                * emoji font, but instead a basic font that has text
                * representations of the emojis - like my own system -
                * then sequences with one modifier base and multiple modifiers
                * are going to be rendered as multiple glyphs.
                * And multiple glyphs won't fit into the button width,
                * so what happens is the button takes the first char of
                * qualifiedSequence, then adds an elipsis.
                * Two chars still don't quite fit into the button width,
                * and when the button takes the first char like that
                * rather than as many chars for one renderable glyph,
                * that first char is probably a surrogate character,
                * which can't be rendered so it just becomes a tofu.
                *
                * It's rather bad because it's not that the button is broken,
                * if you press it the whole qualifiedSequence is appended to
                * pickupField and it actually renders fine. But the button
                * labels don't work.
                *
                * I'm wondering what happens if you have a powerful emoji
                * font on the system. But I can't tell since I don't have that.
                */
            }
        }

        setFocusPainted(false);

        setMargin(new Insets(0, 0, 0, 0));
        setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
    }
}

private static abstract class ScrollableButtonPanel extends JPanel implements Scrollable {

    public Dimension getPreferredScrollableViewportSize() {
        /*
        * JScrollPane and FlowLayout hilariously don't go well together..
        * FlowLayout always sets its preferred size for a single row,
        * and JScrollPane obliges, so no matter what you do, the view
        * becomes one big horizontally scrollable one. ScrollPaneLayout
        * uses it so confidently that it *never* called this method!
        *
        * [This thread](https://stackoverflow.com/questions/48269796/java-jscrollpane-and-flowlayout) and it links to Rob Carnick's [WrapLayout](https://tips4java.wordpress.com/2008/11/06/wrap-layout/).
        * One can arrive at this conclusion independently, but it is an
        * expert level task, writing your own layout manager code.
        *
        * There is also a [OpenJDK bug](https://bugs.openjdk.java.net/browse/JDK-5082531), started and fully discussed within 2004. Interestingly,
        * everyone was in agreement that AWT's layout API should be
        * upgraded to support it. Yet it never was fixed that year.
        *
        * The approach I'll go for is to instead use a GridLayout and
        * dynamically adjust the number of columns during resize events.
        * It's not nice but, it gets us what we need..
        */
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return BUTTON_WIDTH + (2 * BUTTONPANEL_HGAP);
        }
        else {
            return BUTTON_HEIGHT + (2 * BUTTONPANEL_HGAP);
        }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        // We don't have blocks, so, make it up, I guess..
        return
            getScrollableUnitIncrement(visibleRect, orientation, direction)
            * 4;
    }
}




//  Private constants   //  \\  //  \\  //  \\  //  \\

private static final int BUTTON_WIDTH = 40;
private static final int BUTTON_HEIGHT = 40;
private static final int BUTTONPANEL_HGAP = 6;
private static final int BUTTONPANEL_VGAP = 6;
private static final int BUTTON_BORDER_THICKNESS = 6;



//  Private members     //  \\  //  \\  //  \\  //  \\

Backend backend;

JFrame mainframe;
JPanel mainpanel;

JPanel upperPanel;
JTextField pickupField;
JPanel emojiGroupButtonsBar;
JPanel emojiButtonsPanel;
JScrollPane emojiGroupButtonsBarScrollPane;
JScrollPane emojiButtonsPanelScrollPane;

String currentlySelectedGroupID = null;

Font regularFont;
Font largerFont;



//  Private methods     //  \\  //  \\  //  \\  //  \\

private void syncWithBackend() {
    pickupField.setText("");

    emojiGroupButtonsBar.removeAll();

    List<String> groupIDs = backend.getEmojiGroupIDs();
    ButtonGroup buttonGroup = new ButtonGroup();
    for (String groupID: groupIDs) {
        EmojiGroupButton button = new EmojiGroupButton(groupID);
        button.setSelected(groupID.equals(currentlySelectedGroupID));
        button.setFont(largerFont);
        buttonGroup.add(button);
        emojiGroupButtonsBar.add(button);
        button.addActionListener(this);
    }
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



private void setFont(Font font, float regularSize, float largerSize) {
    regularFont = font.deriveFont(regularSize);
    largerFont = font.deriveFont(largerSize);
    pickupField.setFont(largerFont);
    syncWithBackend();
    // Lazy hacky way to recreate emoji group buttons
}



//  Constructors    \\  //  \\  //  \\  //  \\  //  \\

SwingGUI(Backend backend) {
    this.backend = backend;

    mainframe = new JFrame("Emoji Picker");
    // Frame title is intentionally generic
    mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainframe.setSize(340, 512);

    pickupField = new JTextField();
    pickupField.setPreferredSize(new Dimension(0, BUTTON_HEIGHT));
    pickupField.setBorder(BorderFactory.createLoweredBevelBorder());
    emojiGroupButtonsBar = new ScrollableButtonPanel() {
        public boolean getScrollableTracksViewportHeight() { return true; }
        public boolean getScrollableTracksViewportWidth() { return false; }
    };
    emojiGroupButtonsBar.setLayout(new FlowLayout(
        FlowLayout.CENTER,
        BUTTONPANEL_HGAP, BUTTONPANEL_VGAP
    ));
    /*
    * Even though we said centre alignment here, JScrollPane is going to
    * give emojiGroupButtonsBar its preferred width as its final width.
    * So it's going to look left-aligned whenever the number of
    * emoji group buttons are less than would fill the window's width, etc.
    * It's not a big deal so I'll leave it unfixed.
    */
    emojiGroupButtonsBarScrollPane = new JScrollPane(
        emojiGroupButtonsBar,
        JScrollPane.VERTICAL_SCROLLBAR_NEVER,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
    );
    emojiGroupButtonsBarScrollPane.setPreferredSize(
        new Dimension(0, BUTTON_HEIGHT * 3)
    );
    emojiGroupButtonsBarScrollPane.setBorder(null);
    upperPanel = new JPanel();
    upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
    upperPanel.add(pickupField);
    upperPanel.add(emojiGroupButtonsBarScrollPane);
    upperPanel.setPreferredSize(new Dimension(0, (int)(BUTTON_HEIGHT * 3)));

    emojiButtonsPanel = new ScrollableButtonPanel() {
        public boolean getScrollableTracksViewportHeight() { return false; }
        public boolean getScrollableTracksViewportWidth() { return true; }
    };
    GridLayout emojiButtonsPanelLayout = new GridLayout(
        0, 1, BUTTONPANEL_HGAP, BUTTONPANEL_VGAP
    );
    emojiButtonsPanel.setLayout(emojiButtonsPanelLayout);
    emojiButtonsPanelScrollPane = new JScrollPane(
        emojiButtonsPanel,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );
    emojiButtonsPanelScrollPane.addComponentListener(
        new ComponentAdapter() {
            public void componentResized(ComponentEvent eC) {
                Dimension newSize = eC.getComponent().getSize();
                int unit = BUTTON_WIDTH + BUTTONPANEL_HGAP;
                int columns = newSize.width / unit;
                emojiButtonsPanelLayout.setColumns(columns);
            }
        }
    );
    emojiButtonsPanelScrollPane.setBorder(null);

    mainpanel = new JPanel();
    mainpanel.setLayout(new BorderLayout(
        BUTTONPANEL_HGAP, BUTTONPANEL_VGAP
    ));
    mainpanel.setBorder(BorderFactory.createEmptyBorder(
        BUTTONPANEL_VGAP, BUTTONPANEL_HGAP,
        BUTTONPANEL_VGAP, BUTTONPANEL_HGAP
    ));
    mainpanel.add(upperPanel, BorderLayout.NORTH);
    mainpanel.add(emojiButtonsPanelScrollPane, BorderLayout.CENTER);
    mainframe.setContentPane(mainpanel);

    setFont(mainpanel.getFont(), 22f, 22f);
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
        "EmoJiPicker loads emojis from a certain data file. \n" +
        "It is called 'emoji-test.txt', and is provided by Unicode. \n" +
        "Our data loader said that it couldn't find that file \n" +
        "at where it expects in the current directory.\n\n" +

        "Without the file the program knows no emojis, so \n" +
        "it cannot show any buttons for them in the picker.\n\n" +

        "One of two things is probably happening. \n" +
        "First is, the file is genuinely missing, and you'd need to \n" +
        "download it and place it where this program expects it. \n" +
        "(That is probably in the 'data' folder inside \n" +
        "where this program resides.) \n" +
        "Second is, the current directory when this program is \n" +
        "running (now) isn't where the program is, but rather \n" +
        "something like your \"home directory\". This case is \n" +
        "more likely and, to fix it, you need to navigate to the\n" +
        "folder the program is in then start the program from there.\n\n" +

        "As mentioned, this program has no emoji to show, so \n" +
        "we'll just quit for now. Apologies for the trouble..\n"
    );
    JOptionPane.showMessageDialog(null, MISSING_EMOJI_FILE_DIALOGUE_MESSAGE);
}

private static void showIOExceptionDialog() {
    final String IO_EXCEPTION_DIALOGUE_MESSAGE = (
        "We found the emoji data file and tried to load it, but \n" +
        "then we got an IO exception. Which is a very rare type of problem. \n" +
        "The program hasn't really started yet so it should be safe for \n" +
        "us to do the most appropriate course of action, which is quit.\n\n" +

        "Try opening the program again after this, it might resolve.. \n" +
        "If it doesn't, then there is some issue with the data file. \n" +
        "Apologies about this.."
    );
    JOptionPane.showMessageDialog(null, IO_EXCEPTION_DIALOGUE_MESSAGE);
}

}
