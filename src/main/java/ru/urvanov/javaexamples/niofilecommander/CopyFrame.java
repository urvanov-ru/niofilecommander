package ru.urvanov.javaexamples.niofilecommander;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

class CopyFrame extends JFrame {

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
    private JCheckBox copyAttributesCheckBox;
    private JCheckBox noFollowLinksCheckBox;
    private GridBagLayout gbl;
    private JButton okButton;
    private JButton cancelButton;

    CopyFrame() {
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
        copyAttributesCheckBox = new JCheckBox(
                StandardCopyOption.COPY_ATTRIBUTES.toString());
        noFollowLinksCheckBox = new JCheckBox(
                LinkOption.NOFOLLOW_LINKS.toString());
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
        container.add(copyAttributesCheckBox);
        gbl.setConstraints(copyAttributesCheckBox, fillHorizontalConstraints);
        container.add(noFollowLinksCheckBox);
        gbl.setConstraints(noFollowLinksCheckBox, fillHorizontalConstraints);
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
            final class CopyFileInfo {

                private Path sourcePath;
                private Path destinationPath;
                private BasicFileAttributes attributes;

                CopyFileInfo(Path sourcePath, Path destinationPath, BasicFileAttributes sourceAttributes) {
                    this.sourcePath = sourcePath;
                    this.destinationPath = destinationPath;
                    this.attributes = sourceAttributes;
                }

                Path getSourcePath() {
                    return this.sourcePath;
                }

                Path getDestinationPath() {
                    return this.destinationPath;
                }

                long getSize() {
                    return this.attributes.size();
                }

                public boolean isDirectory() {
                    return this.attributes.isDirectory();
                }

                public BasicFileAttributes getAttributes() {
                    return this.attributes;
                }
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (sourcePaths.size() == 1) {
                        Path sourcePath = Paths.get(sourceTextField.getText());
                        sourcePaths.clear();
                        sourcePaths.add(sourcePath);
                    }
                    int copyOptionsLength = 0;
                    int linkOptionsLength = 0;
                    if (replaceExistingCheckBox.isSelected()) {
                        copyOptionsLength++;
                    }
                    if (copyAttributesCheckBox.isSelected()) {
                        copyOptionsLength++;
                    }
                    if (noFollowLinksCheckBox.isSelected()) {
                        copyOptionsLength++;
                        linkOptionsLength++;
                    }
                    final CopyOption[] copyOptions = new CopyOption[copyOptionsLength];
                    LinkOption[] linkOptions = new LinkOption[linkOptionsLength];
                    int n = 0;
                    int m = 0;
                    if (replaceExistingCheckBox.isSelected()) {
                        copyOptions[n++] = StandardCopyOption.REPLACE_EXISTING;
                    }
                    if (copyAttributesCheckBox.isSelected()) {
                        copyOptions[n++] = StandardCopyOption.COPY_ATTRIBUTES;
                    }
                    if (noFollowLinksCheckBox.isSelected()) {
                        copyOptions[n] = LinkOption.NOFOLLOW_LINKS;
                        linkOptions[m++] = LinkOption.NOFOLLOW_LINKS;
                    }
                    List<CopyFileInfo> copyFileInfos = new ArrayList<>();
                    for (final Path sourcePath : sourcePaths) {
                        destinationPath = Paths.get(destinationTextField
                                .getText());
                        if (sourcePaths.size() > 1) {
                            destinationPath = destinationPath
                                    .resolve(sourcePath.getFileName());
                        }

                        BasicFileAttributeView view = Files.getFileAttributeView(sourcePath, BasicFileAttributeView.class, linkOptions);
                        BasicFileAttributes attributes = view.readAttributes();
                        copyFileInfos.add(new CopyFileInfo(sourcePath, destinationPath, attributes));

                    }
                    Path destinationPath = Paths.get(destinationTextField
                            .getText());
                    if (copyFileInfos.size() == 1) {
                        destinationPath = destinationPath.getParent();
                    }
                    copyFiles(copyFileInfos, FileTable.getCurrentFileTable().getModel().getPath(), destinationPath, copyOptions, linkOptions, replaceExistingCheckBox.isSelected(), copyAttributesCheckBox.isSelected(), noFollowLinksCheckBox.isSelected());
                    CopyFrame.this.dispose();
                } catch (IOException ioex) {
                    JOptionPane.showMessageDialog(
                            CopyFrame.this.getMainFrame(), ioex.toString(), "",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            private void copyFiles(List<CopyFileInfo> copyFileInfosArg, Path sourcePath, Path destinationPath, CopyOption[] copyOptions, LinkOption[] linkOptions, boolean replaceExisting, boolean copyAttributes, boolean noFollowLinks) {
                final ProgressFrame progressFrame = new ProgressFrame();
                class GlobalCopyInfo {

                    private BigInteger totalCopyBytes;
                    private BigInteger currentCopiedBytes;

                    public BigInteger getTotalCopyBytes() {
                        return this.totalCopyBytes;
                    }

                    public void setTotalCopyBytes(BigInteger totalCopyBytes) {
                        this.totalCopyBytes = totalCopyBytes;
                    }

                    public BigInteger getCurrentCopiedBytes() {
                        return this.currentCopiedBytes;
                    }

                    public void setCurrentCopiedBytes(BigInteger currentCopiedBytes) {
                        this.currentCopiedBytes = currentCopiedBytes;
                    }
                ;
            };
                final GlobalCopyInfo globalCopyInfo = new GlobalCopyInfo();
                final List<CopyFileInfo> copyFileInfos = new ArrayList<CopyFileInfo>();
                class CopyInfo {

                    private Path sourcePath;
                    private long size;

                    CopyInfo(Path sourcePath, long size) {
                        this.sourcePath = sourcePath;
                        this.size = size;
                    }

                    Path getSourcePath() {
                        return this.sourcePath;
                    }

                    long getSize() {
                        return this.size;
                    }
                }
                final SwingWorker<Boolean, CopyInfo> copyWorker = new SwingWorker<Boolean, CopyInfo>() {
                    final int BUFFER_SIZE = 1024 * 1024;

                    @Override
                    protected Boolean doInBackground() throws Exception {
                        byte[] buff = new byte[BUFFER_SIZE];
                        for (CopyFileInfo cfi : copyFileInfos) {
                            if (isCancelled()) {
                                return true;
                            }

                            if (cfi.getSize() < 100000L || cfi.isDirectory() || cfi.getAttributes().isSymbolicLink()) {

                                try {
                                    Files.copy(cfi.getSourcePath(),
                                            cfi.getDestinationPath(),
                                            copyOptions);
                                } catch (IOException ioex) {
                                    ioex.printStackTrace();
                                }
                            } else {
                                OpenOption[] openOptions = null;
                                if (replaceExisting) {
                                    openOptions = new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.WRITE};
                                } else {
                                    openOptions = new OpenOption[]{StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE};
                                }
                                try (InputStream is = Files.newInputStream(cfi.getSourcePath()); OutputStream os = Files.newOutputStream(cfi.getDestinationPath(), openOptions);) {
                                    int readed = 0;
                                    while ((readed = is.read(buff)) != -1) {
                                        os.write(buff, 0, readed);
                                        publish(new CopyInfo(cfi.getSourcePath(), readed));
                                        if (isCancelled()) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        return true;
                    }

                    @Override
                    public void process(List<CopyInfo> chunks) {
                        CopyInfo copyInfo = chunks.get(chunks.size() - 1);
                        Path path = copyInfo.getSourcePath();
                        BigInteger currentCopiedBytes = globalCopyInfo.getCurrentCopiedBytes();
                        for (CopyInfo ci : chunks) {
                            currentCopiedBytes = currentCopiedBytes.add(BigInteger.valueOf(ci.getSize()));
                        }
                        globalCopyInfo.setCurrentCopiedBytes(currentCopiedBytes);
                        progressFrame.setInfo("Copy ("
                                + currentCopiedBytes + "/"
                                + globalCopyInfo.getTotalCopyBytes() + "): " + path);
                        int curProgressValue = currentCopiedBytes.multiply(BigInteger.valueOf(Integer.MAX_VALUE)).divide(globalCopyInfo.getTotalCopyBytes()).intValue();
                        progressFrame.setValue(curProgressValue);
                    }

                    @Override
                    public void done() {
                        progressFrame.dispose();
                    }
                };

                final SwingWorker<Boolean, CopyFileInfo> collectWorker = new SwingWorker<Boolean, CopyFileInfo>() {

                    @Override
                    protected Boolean doInBackground() throws Exception {
                        SimpleFileVisitor<Path> collectFileVisitor = new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) {
                                if (isCancelled()) {
                                    return FileVisitResult.SKIP_SIBLINGS;
                                } else {
                                    Path dst = destinationPath
                                            .resolve(sourcePath.relativize(path));
                                    publish(new CopyFileInfo(path, dst, attrs));
                                    return FileVisitResult.CONTINUE;
                                }
                            }

                            @Override
                            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                                if (isCancelled()) {
                                    return FileVisitResult.SKIP_SIBLINGS;
                                } else {
                                    Path dst = destinationPath
                                            .resolve(sourcePath.relativize(path));
                                    publish(new CopyFileInfo(path, dst, attrs));
                                    return FileVisitResult.CONTINUE;
                                }
                            }
                        };
                        for (CopyFileInfo cfi : copyFileInfosArg) {
                            if (isCancelled()) {
                                return true;
                            }
                            if (cfi.isDirectory()) {
                                Set<FileVisitOption> fileVisitOptions = new HashSet<>();
                                if (!noFollowLinks) {
                                    fileVisitOptions.add(FileVisitOption.FOLLOW_LINKS);
                                }
                                Files.walkFileTree(cfi.getSourcePath(), fileVisitOptions, Integer.MAX_VALUE, collectFileVisitor);
                            } else {
                                copyFileInfos.add(new CopyFileInfo(cfi.getSourcePath(), cfi.getDestinationPath(),
                                        cfi.getAttributes()));
                            }
                        }
                        return true;
                    }

                    @Override
                    public void process(List<CopyFileInfo> chunks) {
                        Path path = chunks.get(chunks.size() - 1).getSourcePath();
                        copyFileInfos.addAll(chunks);
                        progressFrame.setInfo("Collecting: " + path);
                    }

                    @Override
                    public void done() {

                        BigInteger totalCopyBytes = BigInteger.valueOf(0);
                        for (CopyFileInfo cfi : copyFileInfos) {
                            totalCopyBytes = totalCopyBytes.add(BigInteger.valueOf(cfi.getSize()));
                        }
                        globalCopyInfo.setTotalCopyBytes(totalCopyBytes);
                        globalCopyInfo.setCurrentCopiedBytes(BigInteger.valueOf(0));
                        progressFrame.setMaximum(Integer.MAX_VALUE);

                        copyWorker.execute();
                    }
                };

                progressFrame.setTitle("Копирование");
                progressFrame.setVisible(true);
                progressFrame.addCancelButtonListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        collectWorker.cancel(false);
                        copyWorker.cancel(false);
                    }
                });
                collectWorker.execute();

            }
        });
        container.add(okButton);
        gbl.setConstraints(okButton, alignRightConstraints);
        cancelButton = new JButton("CANCEL");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CopyFrame.this.dispose();
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
        destinationTextField.setText(fileDestination.toString());
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

}
