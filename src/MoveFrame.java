

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

class MoveFrame extends JFrame {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1877233543712112161L;
    private JFrame mainFrame;
    private Collection<Path> sourcePaths;
    private Path destinationPath;
    private JLabel sourceLabel;
    private JTextField sourceTextField;
    private JLabel destinationLabel;
    private JTextField destinationTextField;
    private JCheckBox replaceExistingCheckBox;
    private JCheckBox atomicMoveCheckBox;
    private GridBagLayout gbl;
    private JButton okButton;
    private JButton cancelButton;

    MoveFrame() {
        setSize(600, 200);
        setLocationByPlatform(true);
        Container container = getContentPane();
        gbl = new GridBagLayout();
        sourceLabel = new JLabel("source");
        destinationLabel = new JLabel("destination");
        sourceTextField = new JTextField(100);
        destinationTextField = new JTextField(100);
        replaceExistingCheckBox = new JCheckBox(
                StandardCopyOption.REPLACE_EXISTING.toString());
        atomicMoveCheckBox = new JCheckBox(
                StandardCopyOption.ATOMIC_MOVE.toString());
        container.setLayout(gbl);
        GridBagConstraints firstColumnConstraints = new GridBagConstraints();
        firstColumnConstraints.weightx = 0.0;
        firstColumnConstraints.weighty = 0.0;
        firstColumnConstraints.anchor = GridBagConstraints.NORTHWEST;
        GridBagConstraints fillHorizontalConstraints = new GridBagConstraints();
        fillHorizontalConstraints.weightx = 0.1;
        fillHorizontalConstraints.fill = GridBagConstraints.HORIZONTAL;
        fillHorizontalConstraints.anchor = GridBagConstraints.NORTHWEST;
        fillHorizontalConstraints.gridwidth = GridBagConstraints.REMAINDER;
        container.add(sourceLabel);
        gbl.setConstraints(sourceLabel, firstColumnConstraints);
        container.add(sourceTextField);
        gbl.setConstraints(sourceTextField, fillHorizontalConstraints);
        container.add(destinationLabel);
        gbl.setConstraints(destinationLabel, firstColumnConstraints);
        container.add(destinationTextField);
        gbl.setConstraints(destinationTextField, fillHorizontalConstraints);
        container.add(replaceExistingCheckBox);
        gbl.setConstraints(replaceExistingCheckBox, fillHorizontalConstraints);
        container.add(atomicMoveCheckBox);
        gbl.setConstraints(atomicMoveCheckBox, fillHorizontalConstraints);
        JPanel panel = new JPanel();
        container.add(panel);
        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.anchor = GridBagConstraints.NORTHWEST;
        panelConstraints.weighty = 0.1;
        panelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(panel, panelConstraints);

        GridBagConstraints panel2Constraints = new GridBagConstraints();
        panel2Constraints.anchor = GridBagConstraints.NORTHEAST;
        panel2Constraints.weightx = 0.1;
        JPanel panel2 = new JPanel();
        container.add(panel2);
        gbl.setConstraints(panel2, panel2Constraints);

        GridBagConstraints alignRightConstraints = new GridBagConstraints();
        alignRightConstraints.anchor = GridBagConstraints.NORTHEAST;
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (sourcePaths.size() == 1) {
                        Path sourcePath = Paths.get(sourceTextField.getText());
                        sourcePaths.clear();
                        sourcePaths.add(sourcePath);
                    }
                    for (Path sourcePath : sourcePaths) {
                        destinationPath = Paths.get(destinationTextField
                                .getText());
                        if (sourcePaths.size() > 1)
                            destinationPath = destinationPath
                                    .resolve(sourcePath.getFileName());
                        int copyOptionsLength = 0;
                        if (replaceExistingCheckBox.isSelected())
                            copyOptionsLength++;
                        if (atomicMoveCheckBox.isSelected())
                            copyOptionsLength++;
                        CopyOption[] copyOptions = new CopyOption[copyOptionsLength];
                        int n = 0;
                        if (replaceExistingCheckBox.isSelected())
                            copyOptions[n++] = StandardCopyOption.REPLACE_EXISTING;
                        if (atomicMoveCheckBox.isSelected())
                            copyOptions[n++] = StandardCopyOption.ATOMIC_MOVE;
                        Files.move(sourcePath, destinationPath, copyOptions);
                    }
                    MoveFrame.this.dispose();
                } catch (IOException ioex) {
                    JOptionPane.showMessageDialog(
                            MoveFrame.this.getMainFrame(), ioex.toString(), "",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        container.add(okButton);
        gbl.setConstraints(okButton, alignRightConstraints);
        cancelButton = new JButton("CANCEL");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                MoveFrame.this.dispose();
            }

        });
        container.add(cancelButton);
        gbl.setConstraints(cancelButton, alignRightConstraints);
    }

    public void setSources(Collection<Path> fileSources) {
        this.sourcePaths = fileSources;
        if (fileSources.size() == 1) {
            sourceTextField.setText(fileSources.iterator().next().toString());
            sourceTextField.setEnabled(true);
        } else {
            sourceTextField.setText(fileSources.size() + " files...");
            sourceTextField.setEnabled(false);
        }

    }

    public void setDestination(Path fileDestination) {
        this.destinationPath = fileDestination;
        destinationTextField.setText(destinationPath.toString());
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

}
