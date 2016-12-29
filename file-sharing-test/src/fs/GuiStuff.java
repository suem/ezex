package fs;

import javax.swing.*;
import java.io.File;

public class GuiStuff {
    public static File chooseFile(int selectionMode) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(selectionMode);
        int result = fileChooser.showDialog(null, null);
        if (result == JFileChooser.APPROVE_OPTION) {
           return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }
}
