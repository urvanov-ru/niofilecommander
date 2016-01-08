/**
 * Главный файл niofilecommander. С него начинается выполнение.
 * @author Урванов Федор
 * @see <a href="http://urvanov.ru">http://urvanov.ru</a>
 */

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

public class MainClass {

    public static void main(String[] args) throws Exception {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setTitle("niofilecommander");
        frame.setLocationByPlatform(true);
        frame.setLayout(new BorderLayout());

        Path path = FileSystems.getDefault().getPath("");

        final FilePanel leftPanel = new FilePanel(path);
        final FilePanel rightPanel = new FilePanel(path);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                false, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        Container container = frame.getContentPane();
        container.add(splitPane, BorderLayout.CENTER);

        frame.setVisible(true);

        JToolBar toolbar = new JToolBar();
        container.add(toolbar, BorderLayout.NORTH);
        JButton moveButton = new JButton("move");
        moveButton.addActionListener((ActionEvent e) -> {
            try {
                Collection<FileInfo> leftFileInfos = leftPanel
                        .getFileTable().getSelectedFileInfos();
                Collection<FileInfo> rightFileInfos = rightPanel
                        .getFileTable().getSelectedFileInfos();
                List<Path> fileSources = new ArrayList<Path>();
                Path fileDestination = null;
                if (FileTable.getCurrentFileTable() == leftPanel
                        .getFileTable()) {
                    for (FileInfo fi : leftFileInfos) {
                        if (fi.getPath() != null) {
                            fileSources.add(fi.getPath());
                        }
                    }
                    fileDestination = rightPanel.getFileTable().getModel()
                            .getPath();
                } else {
                    for (FileInfo fi : rightFileInfos) {
                        if (fi.getPath() != null) {
                            fileSources.add(fi.getPath());
                        }
                    }
                    fileDestination = leftPanel.getFileTable().getModel()
                            .getPath();
                }
                if (fileSources.size() == 1) {
                    fileDestination = fileDestination.resolve(fileSources
                            .iterator().next().getFileName());
                }
                MoveFrame moveFrame = new MoveFrame();
                moveFrame.setMainFrame(frame);
                moveFrame.setSources(fileSources);
                moveFrame.setDestination(fileDestination);
                moveFrame.setVisible(true);
            } catch (Exception ioex) {
                ioex.printStackTrace();
            }
        });
        toolbar.add(moveButton);
        JButton copyButton = new JButton("copy");
        copyButton.addActionListener((ActionEvent e) -> {
            try {
                Collection<FileInfo> leftFileInfos = leftPanel
                        .getFileTable().getSelectedFileInfos();
                Collection<FileInfo> rightFileInfos = rightPanel
                        .getFileTable().getSelectedFileInfos();
                List<Path> fileSources = new ArrayList<Path>();
                Path fileDestination = null;
                if (FileTable.getCurrentFileTable() == leftPanel
                        .getFileTable()) {
                    for (FileInfo fi : leftFileInfos) {
                        if (fi.getPath() != null) {
                            fileSources.add(fi.getPath());
                        }
                    }
                    fileDestination = rightPanel.getFileTable().getModel()
                            .getPath();
                } else {
                    for (FileInfo fi : rightFileInfos) {
                        if (fi.getPath() != null) {
                            fileSources.add(fi.getPath());
                        }
                    }
                    fileDestination = leftPanel.getFileTable().getModel()
                            .getPath();
                }
                if (fileSources.size() == 1) {
                    fileDestination = fileDestination.resolve(fileSources
                            .iterator().next().getFileName());
                }
                CopyFrame copyFrame = new CopyFrame();
                copyFrame.setMainFrame(frame);
                copyFrame.setSources(fileSources);
                copyFrame.setDestination(fileDestination);
                copyFrame.setVisible(true);
            } catch (Exception ioex) {
                JOptionPane.showMessageDialog(frame, ioex.toString(), "",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        toolbar.add(copyButton);
        JButton createFolderButton = new JButton("create folder");
        createFolderButton.addActionListener((ActionEvent e) -> {
            try {
                Path parentPath = null;
                if (FileTable.getCurrentFileTable() == leftPanel
                        .getFileTable()) {
                    parentPath = leftPanel.getFileTable().getModel()
                            .getPath().toAbsolutePath();
                } else {
                    parentPath = rightPanel.getFileTable().getModel()
                            .getPath().toAbsolutePath();
                }
                String directoryName = JOptionPane.showInputDialog(frame,
                        "Directory name");
                Path path1 = parentPath.resolve(directoryName.trim());
                Files.createDirectory(path1);
            }catch (IOException ioex) {
                JOptionPane.showMessageDialog(frame, ioex.toString(), "",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        toolbar.add(createFolderButton);
        JButton deleteButton = new JButton("delete");
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Path path = null;
                    Collection<FileInfo> fileInfos = null;
                    if (FileTable.getCurrentFileTable() == leftPanel
                            .getFileTable()) {
                        fileInfos = leftPanel.getFileTable()
                                .getSelectedFileInfos();
                    } else {
                        fileInfos = rightPanel.getFileTable()
                                .getSelectedFileInfos();
                    }
                    if (fileInfos.size() == 1) {
                        FileInfo fileInfo = null;
                        for (FileInfo fi : fileInfos) {
                            fileInfo = fi;
                        }
                        path = fileInfo == null ? null : fileInfo.getPath();
                        if (path != null) {
                            if (JOptionPane.showConfirmDialog(
                                    frame,
                                    "Are you really want to delete \""
                                            + path.toString() + "\"?",
                                    "Warning", JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                                List<Path> paths = new ArrayList();
                                paths.add(path);
                                deletePaths(paths);
                            }
                        }
                    } else {
                        if (JOptionPane.showConfirmDialog(
                                frame,
                                "Are you really want to delete "
                                        + fileInfos.size() + " files?",
                                "Warning", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                            List<Path> paths = new ArrayList();
                            for (FileInfo fileInfo : fileInfos) {
                                path = fileInfo.getPath();
                                paths.add(path);
                            }
                            deletePaths(paths);
                        }
                    }
                } catch (IOException ioex) {
                    JOptionPane.showMessageDialog(frame, ioex.toString(), "",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            private void deletePaths(final List<Path> initialDeletePaths) throws IOException {
                final List<Path> paths = new ArrayList<>();
                final ProgressFrame progressFrame = new ProgressFrame();
                class DeleteInfo {
                    DeleteInfo(Path path, int deletedFilesCount) {
                        this.path = path;
                        this.deletedFilesCount = deletedFilesCount;
                    }

                    Path path;
                    int deletedFilesCount;
                }
                final SwingWorker<Boolean, DeleteInfo> deleteWorker = new SwingWorker<Boolean, DeleteInfo>() {

                    @Override
                    protected Boolean doInBackground() throws Exception {
                        int n = 0;
                        for (Path path : paths) {
                            if (isCancelled()) {
                                break;
                            }
                            try {
                                Files.delete(path);
                                n++;
                                publish(new DeleteInfo(path, n));
                            } catch (IOException ioex) {
                                ioex.printStackTrace();
                            }
                        }
                        return true;
                    }

                    @Override
                    protected void process(List<DeleteInfo> chunks) {
                        DeleteInfo deleteInfo = chunks.get(chunks.size() - 1);
                        progressFrame.setValue(deleteInfo.deletedFilesCount);
                        progressFrame.setInfo("Deleted ("
                                + deleteInfo.deletedFilesCount + "/"
                                + paths.size() + "): " + deleteInfo.path);
                    }

                    @Override
                    protected void done() {
                        progressFrame.dispose();
                    }
                };
                final Thread deleteThread = new Thread(deleteWorker);
                final SwingWorker<Boolean, Path> collectWorker = new SwingWorker<Boolean, Path>() {

                    @Override
                    protected Boolean doInBackground() throws Exception {
                        FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {

                            @Override
                            public FileVisitResult preVisitDirectory(Path dir,
                                    BasicFileAttributes attrs) {
                                if (isCancelled()) {
                                    return FileVisitResult.SKIP_SIBLINGS;
                                } else {
                                    return FileVisitResult.CONTINUE;
                                }
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir,
                                    IOException ioex) throws IOException {

                                if (isCancelled()) {
                                    return FileVisitResult.SKIP_SIBLINGS;
                                } else {
                                    publish(dir);
                                    return FileVisitResult.CONTINUE;
                                }
                            }

                            @Override
                            public FileVisitResult visitFile(Path file,
                                    BasicFileAttributes attrs)
                                    throws IOException {
                                if (isCancelled()) {
                                    return FileVisitResult.SKIP_SIBLINGS;
                                } else {
                                    publish(file);
                                    return FileVisitResult.CONTINUE;
                                }
                            }
                        };
                        for (Path path : initialDeletePaths) {
                            if (Files.isDirectory(path)) {
                                Files.walkFileTree(path, fileVisitor);
                            } else {
                                paths.add(path);
                            }
                        }
                        
                        return true;
                    }

                    @Override
                    protected void process(List<Path> chunks) {
                        Path path = chunks.get(chunks.size() - 1);
                        paths.addAll(chunks);
                        progressFrame.setInfo("Collecting: " + path);
                    }

                    @Override
                    protected void done() {
                        progressFrame.setMaximum(paths.size());
                        deleteThread.start();
                    }
                };
                final Thread collectThread = new Thread(collectWorker);
                progressFrame.setTitle("Deleting... ");
                progressFrame.setVisible(true);
                progressFrame.addCancelButtonListener((ActionEvent e) -> {
                    collectWorker.cancel(false);
                    deleteWorker.cancel(false);
                });

                collectThread.start();
            }
        });
        toolbar.add(deleteButton);

        JButton metaDataButton = new JButton("meta data");
        metaDataButton.addActionListener((ActionEvent e) -> {
            FileTable fileTable = FileTable.getCurrentFileTable();
            FileInfo fileInfo = fileTable.getSelectedFileInfo();
            if (fileInfo != null) {
                FileMetaDataFrame fileMetaDataFrame = new FileMetaDataFrame(
                        fileInfo.getPath());
                fileMetaDataFrame.setVisible(true);
            }
        });
        toolbar.add(metaDataButton);

        JButton createSymbolicLinkButton = new JButton("create symbolic link");
        createSymbolicLinkButton.addActionListener((ActionEvent e) -> {
            CreateLinkFrame createLinkFrame = new CreateLinkFrame();
            FileTable fileTable = FileTable.getCurrentFileTable();
            FileInfo fileInfo = fileTable.getSelectedFileInfo();
            if (fileInfo != null) {
                createLinkFrame.setTarget(fileInfo.getPath());
            }
            createLinkFrame.setLinkType(CreateLinkFrame.LinkType.SYMBOLIC);
            createLinkFrame.setVisible(true);
        });
        toolbar.add(createSymbolicLinkButton);

        JButton createHardLinkButton = new JButton("create hard link");
        createHardLinkButton.addActionListener((ActionEvent e) -> {
            CreateLinkFrame createLinkFrame = new CreateLinkFrame();
            FileTable fileTable = FileTable.getCurrentFileTable();
            FileInfo fileInfo = fileTable.getSelectedFileInfo();
            if (fileInfo != null) {
                createLinkFrame.setTarget(fileInfo.getPath());
            }
            createLinkFrame.setLinkType(CreateLinkFrame.LinkType.HARD);
            createLinkFrame.setVisible(true);
        });
        toolbar.add(createHardLinkButton);
        
        JButton findByRegexButton = new JButton("find by regex");
        findByRegexButton.addActionListener((e) ->{
            FindByRegexFrame findByRegexFrame = new FindByRegexFrame();
            findByRegexFrame.setVisible(true);
        });
        toolbar.add(findByRegexButton);
    }

}
