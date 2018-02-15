package ru.urvanov.javaexamples.niofilecommander;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

/**
 *
 * @author fedya
 */
class FindByRegexFrame extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8350369985003185575L;
	
	private JLabel regexLabel;
    private JEditorPane regexEditorPane;
    private JCheckBox canonEqCheckBox;
    private JCheckBox caseInsensitiveCheckBox;
    private JCheckBox commentsCheckBox;
    private JCheckBox dotallCheckBox;
    private JCheckBox literalCheckBox;
    private JCheckBox multilineCheckBox;
    private JCheckBox unicodeCaseCheckBox;
    private JCheckBox unixLinesCheckBox;
    private JList<String> resultsList;
    private JScrollPane resultsScrollPane;
    private GridBagLayout gbl;

    private JButton selectByRegexButton;
    private JButton findByRegexButton;
    
    FindByRegexFrame() {
        gbl = new GridBagLayout();
        Container container = this.getContentPane();
        container.setLayout(gbl);
        
        GridBagConstraints normalConstraints = new GridBagConstraints();
        normalConstraints.anchor = GridBagConstraints.NORTHWEST;
        
        GridBagConstraints fillHorizontalConstraints = new GridBagConstraints();
        fillHorizontalConstraints.anchor = GridBagConstraints.NORTHWEST;
        fillHorizontalConstraints.fill = GridBagConstraints.HORIZONTAL;
        fillHorizontalConstraints.gridwidth = GridBagConstraints.REMAINDER;
        fillHorizontalConstraints.weightx = 0.1;
        
        regexLabel = new JLabel("regex pattern");
        container.add(regexLabel);
        gbl.setConstraints(regexLabel, fillHorizontalConstraints);
        
        regexEditorPane = new JEditorPane();
        regexEditorPane.setMinimumSize(new Dimension(100,100));
        container.add(regexEditorPane);
        gbl.setConstraints(regexEditorPane, fillHorizontalConstraints);
        
        canonEqCheckBox = new JCheckBox("Pattern.CANON_EQ");
        container.add(canonEqCheckBox);
        gbl.setConstraints(canonEqCheckBox, fillHorizontalConstraints);
        
        caseInsensitiveCheckBox = new JCheckBox("Pattern.CASE_INSENSITIVE");
        container.add(caseInsensitiveCheckBox);
        gbl.setConstraints(caseInsensitiveCheckBox, fillHorizontalConstraints);
        
        commentsCheckBox = new JCheckBox("Pattern.COMMENTS");
        container.add(commentsCheckBox);
        gbl.setConstraints(commentsCheckBox, fillHorizontalConstraints);
        
        dotallCheckBox = new JCheckBox("Pattern.DOTALL");
        container.add(dotallCheckBox);
        gbl.setConstraints(dotallCheckBox, fillHorizontalConstraints);
        
        literalCheckBox = new JCheckBox("Pattern.LITERAL");
        container.add(literalCheckBox);
        gbl.setConstraints(literalCheckBox, fillHorizontalConstraints);
        
        multilineCheckBox = new JCheckBox("Pattern.MULTILINE");
        container.add(multilineCheckBox);
        gbl.setConstraints(multilineCheckBox, fillHorizontalConstraints);
        
        unicodeCaseCheckBox = new JCheckBox("Pattern.UNICODE_CASE");
        container.add(unicodeCaseCheckBox);
        gbl.setConstraints(unicodeCaseCheckBox, fillHorizontalConstraints);
        
        unixLinesCheckBox = new JCheckBox("Pattern.UNIX_LINES");
        container.add(unixLinesCheckBox);
        gbl.setConstraints(unixLinesCheckBox, fillHorizontalConstraints);
        
        setSize(400, 500);
        setTitle("FindByRegex");
        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        
        resultsList = new JList<>(new DefaultListModel<String>());
        resultsScrollPane = new JScrollPane(resultsList);
        GridBagConstraints pnl1Constraints = new GridBagConstraints();
        pnl1Constraints.anchor = GridBagConstraints.NORTHWEST;
        pnl1Constraints.gridwidth = GridBagConstraints.REMAINDER;
        pnl1Constraints.weightx = 0.1;
        pnl1Constraints.fill = GridBagConstraints.BOTH;
        pnl1Constraints.weighty = 0.1;
        container.add(resultsScrollPane);
        gbl.setConstraints(resultsScrollPane, pnl1Constraints);
        
        JPanel pnl2 = new JPanel();
        GridBagConstraints pnl2Constraints = new GridBagConstraints();
        pnl2Constraints.anchor = GridBagConstraints.NORTHWEST;
        pnl2Constraints.weightx = 2;
        pnl2Constraints.fill = GridBagConstraints.HORIZONTAL;
        container.add(pnl2);
        gbl.setConstraints(pnl2, pnl2Constraints);
        
        selectByRegexButton = new JButton("select by regex");
        selectByRegexButton.addActionListener(e -> {
            int flags = getFlags();
            Pattern pattern = Pattern.compile(regexEditorPane.getText(), flags);
            FileTableModel model = FileTable.getCurrentFileTable().getModel();
            List<Integer> rowsToSelect = new ArrayList<>();
            for (int n = 0; n < model.getRowCount(); n++) {
                FileInfo fileInfo = model.getRow(n);
                Matcher matcher = pattern.matcher(fileInfo.getFileName());
                if (matcher.matches()) {
                    rowsToSelect.add(FileTable.getCurrentFileTable().convertRowIndexToView(n));
                }
            }
            FileTable.getCurrentFileTable().clearSelection();
            rowsToSelect.stream().forEach(n -> {FileTable.getCurrentFileTable().addRowSelectionInterval(n, n);});
        });
        container.add(selectByRegexButton);
        gbl.setConstraints(selectByRegexButton, normalConstraints);
        
        findByRegexButton = new JButton("find by regex");
        findByRegexButton.addActionListener(e -> {
            findByRegex();
        });
        container.add(findByRegexButton);
        gbl.setConstraints(findByRegexButton, normalConstraints);
    }
    
    private int getFlags() {
        int flags = 0;
        if (canonEqCheckBox.isSelected()) flags |= Pattern.CANON_EQ;
        if (caseInsensitiveCheckBox.isSelected()) flags |= Pattern.CASE_INSENSITIVE;
        if (commentsCheckBox.isSelected()) flags |= Pattern.COMMENTS;
        if (dotallCheckBox.isSelected()) flags |= Pattern.DOTALL;
        if (literalCheckBox.isSelected()) flags |= Pattern.LITERAL;
        if (multilineCheckBox.isSelected()) flags |= Pattern.MULTILINE;
        if (unicodeCaseCheckBox.isSelected()) flags |= Pattern.UNICODE_CASE;
        if (unixLinesCheckBox.isSelected()) flags |= Pattern.UNIX_LINES;
        return flags;
    }

    private void findByRegex() {
        findByRegexButton.setEnabled(false);
        resultsList.removeAll();
        final int flags = getFlags();
        final String regex = regexEditorPane.getText();
        final Path path = FileTable.getCurrentFileTable().getModel().getPath();
        
        SwingWorker<Boolean, FileInfo> worker = new SwingWorker<Boolean, FileInfo>(){
            
            @Override
            protected Boolean doInBackground() throws Exception {
                Pattern pattern = Pattern.compile(regex, flags);
                SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>(){
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        String fileName = file.getFileName().toString();
                        Matcher matcher = pattern.matcher(fileName);
                        if (matcher.matches()) {
                            FileInfo fileInfo = new FileInfo(file, attrs);
                            publish(fileInfo);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                };
                Files.walkFileTree(path, visitor);
                return true;
            }
            
            @Override
            protected void process(List<FileInfo> fileInfos) {
                
                fileInfos.stream().forEach(fi->{
                    ((DefaultListModel<String>) resultsList.getModel())
                            .addElement(fi.getPath().toAbsolutePath()
                                    .toString());
                });
            }
            
            @Override
            protected void done() {
                findByRegexButton.setEnabled(true);
            }
        };
        worker.execute();
    }
}
