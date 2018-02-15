package ru.urvanov.javaexamples.niofilecommander;


import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

class ProgressFrame extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 7206289593780364792L;
    private JLabel infoLabel;
    private JProgressBar progressBar;
    private JButton cancelButton;

    ProgressFrame() {
        GridBagLayout gbl = new GridBagLayout();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationByPlatform(true);
        setSize(600, 100);
        Container container = getContentPane();
        container.setLayout(gbl);

        GridBagConstraints infoLabelConstraints = new GridBagConstraints();
        infoLabelConstraints.anchor = GridBagConstraints.NORTHWEST;
        infoLabelConstraints.weightx = 0.1;
        infoLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
        infoLabelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        infoLabel = new JLabel();
        container.add(infoLabel);
        gbl.setConstraints(infoLabel, infoLabelConstraints);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        GridBagConstraints progressBarConstraints = new GridBagConstraints();
        progressBarConstraints.anchor = GridBagConstraints.NORTHWEST;
        progressBarConstraints.weightx = 0.1;
        progressBarConstraints.fill = GridBagConstraints.HORIZONTAL;
        container.add(progressBar);
        gbl.setConstraints(progressBar, progressBarConstraints);

        cancelButton = new JButton("CANCEL");
        container.add(cancelButton);
        GridBagConstraints cancelConstraints = new GridBagConstraints();
        cancelConstraints.anchor = GridBagConstraints.NORTHWEST;
        cancelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(cancelButton, cancelConstraints);
    }
    
    public void setInfo(String info) {
        infoLabel.setText(info);
    }
    
    public void setMaximum(int maximum) {
        progressBar.setMaximum(maximum);
    }
    
    public void setValue(int value) {
        progressBar.setValue(value);
    }
    
    public void addCancelButtonListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }
}
