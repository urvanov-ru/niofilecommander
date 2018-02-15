package ru.urvanov.javaexamples.niofilecommander;


import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

class CreateLinkFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -4884724995244007060L;

    enum LinkType {
        SYMBOLIC, HARD
    }

    private JLabel linkLabel;
    private JTextField linkTextField;
    private JLabel targetLabel;
    private JTextField targetTextField;
    private JButton okButton;
    private JButton cancelButton;
    private LinkType linkType;

    CreateLinkFrame() {
        setSize(400, 100);
        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GridBagLayout gbl = new GridBagLayout();
        Container container = getContentPane();

        container.setLayout(gbl);

        GridBagConstraints normalConstraints = new GridBagConstraints();
        normalConstraints.anchor = GridBagConstraints.NORTHWEST;
        GridBagConstraints fillHorizontalConstraints = new GridBagConstraints();
        fillHorizontalConstraints.anchor = GridBagConstraints.NORTHWEST;
        fillHorizontalConstraints.gridwidth = GridBagConstraints.REMAINDER;
        fillHorizontalConstraints.weightx = 0.1;
        fillHorizontalConstraints.fill = GridBagConstraints.HORIZONTAL;

        linkLabel = new JLabel("link");
        container.add(linkLabel);
        gbl.setConstraints(linkLabel, normalConstraints);

        linkTextField = new JTextField();
        container.add(linkTextField);
        gbl.setConstraints(linkTextField, fillHorizontalConstraints);

        targetLabel = new JLabel("target");
        container.add(targetLabel);
        gbl.setConstraints(targetLabel, normalConstraints);

        targetTextField = new JTextField();
        container.add(targetTextField);
        gbl.setConstraints(targetTextField, fillHorizontalConstraints);

        JPanel pnlFill = new JPanel();
        GridBagConstraints fillConstraints = new GridBagConstraints();
        fillConstraints.anchor = GridBagConstraints.NORTHWEST;
        fillConstraints.fill = GridBagConstraints.BOTH;
        fillConstraints.gridwidth = GridBagConstraints.REMAINDER;
        fillConstraints.weightx = 0.1;
        fillConstraints.weighty = 0.1;
        container.add(pnlFill);
        gbl.setConstraints(pnlFill, fillConstraints);

        JPanel pnl1 = new JPanel();
        GridBagConstraints pnl1Constraints = new GridBagConstraints();
        pnl1Constraints.anchor = GridBagConstraints.NORTHWEST;
        pnl1Constraints.weightx = 0.1;
        pnl1Constraints.fill = GridBagConstraints.HORIZONTAL;
        pnl1Constraints.gridwidth = 2;
        container.add(pnl1);
        gbl.setConstraints(pnl1, pnl1Constraints);

        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (linkTextField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(CreateLinkFrame.this, "Enter link path.");
                } else {
                    createLink();
                }
            }
        });
        container.add(okButton);
        gbl.setConstraints(okButton, normalConstraints);

        cancelButton = new JButton("CANCEL");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CreateLinkFrame.this.dispose();
            }

        });
        container.add(cancelButton);
        gbl.setConstraints(cancelButton, normalConstraints);
    }

    protected void createLink() {
        try {
            Path linkPath = Paths.get(linkTextField.getText());
            Path targetPath = Paths.get(targetTextField.getText());
            switch (linkType) {
            case SYMBOLIC:
                Files.createSymbolicLink(linkPath, targetPath);
                break;
            case HARD:
                Files.createLink(linkPath, targetPath);
                break;
            }
            dispose();
        } catch (IOException ioex) {
            JOptionPane.showMessageDialog(this, ioex.toString(), "",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setLink(Path path) {
        linkTextField.setText(path.toAbsolutePath().toString());
    }

    public void setTarget(Path path) {
        targetTextField.setText(path.toAbsolutePath().toString());
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }
}
