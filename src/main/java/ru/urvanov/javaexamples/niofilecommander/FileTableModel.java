package ru.urvanov.javaexamples.niofilecommander;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

class FileTableModel implements TableModel {
    private Path path;
    private WatchService watchService;
    private static final int COLUMN_COUNT = 5;
    Class<?>[] columnClasses = new Class<?>[] { String.class, FileType.class,
            Boolean.class, FileTime.class, FileTime.class, FileTime.class };
    String[] columnNames = new String[] { "name", "type", "symbolicLink",
            "modified", "size" };
    Set<TableModelListener> listeners = new HashSet<TableModelListener>();
    Set<ActionListener> pathListeners = new HashSet<ActionListener>();

    private List<FileInfo> data = new ArrayList<FileInfo>();

    @Override
    public void addTableModelListener(TableModelListener arg0) {
        listeners.add(arg0);
    }

    @Override
    public Class<?> getColumnClass(int index) {
        return columnClasses[index];
    }

    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public String getColumnName(int index) {
        return columnNames[index];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= data.size()) {
            System.out.println("NO");
            return null;
        }
        FileInfo fileInfo = data.get(rowIndex);
        switch (columnIndex) {
        case 0:
            return fileInfo.getFileName();
        case 1:
            return fileInfo.getFileType();
        case 2:
            return fileInfo.isSymbolicLink();
        case 3:
            return fileInfo.getLastModifiedTime();
        case 4:
            return fileInfo.getSize();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public boolean isCellEditable(int arg0, int arg1) {
        return false;
    }

    @Override
    public void removeTableModelListener(TableModelListener arg0) {
        listeners.remove(arg0);
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        FileInfo fileInfo = null;
        if (rowIndex < data.size()) {
            fileInfo = data.get(rowIndex);
        } else {
            fileInfo = new FileInfo();
            data.add(rowIndex, fileInfo);
        }
        switch (columnIndex) {
        case 0:
            fileInfo.setFileName((String) value);
            break;
        case 1:
            fileInfo.setFileType((FileType) value);
            break;
        case 2:
            fileInfo.setSymbolicLink((Boolean) value);
            break;
        case 3:
            fileInfo.setLastModifiedTime((FileTime) value);
            break;
        case 4:
            fileInfo.setSize((Long) value);
            break;
        }
        throw new IndexOutOfBoundsException();
    }

    private void addRow(FileInfo fileInfo, boolean sort) {
        data.add(fileInfo);
        if (sort) {
            TableModelEvent e = new TableModelEvent(this, 0, data.size() - 1,
                    TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
            for (TableModelListener l : listeners) {
                l.tableChanged(e);
            }
        }
    }

    public void addRow(FileInfo fileInfo) {
        addRow(fileInfo, true);
    }

    public FileInfo getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path.toAbsolutePath();
        for (ActionListener listener : pathListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, ""));
        }
    }

    void fill() throws IOException {
        if (this.watchService != null)
            this.watchService.close();
        int rowCount = this.getRowCount();
        clear();
        if (rowCount > 0) {
            TableModelEvent e = new TableModelEvent(this, 0, rowCount - 1,
                    TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
            for (TableModelListener l : listeners) {
                l.tableChanged(e);
            }
        }
        FileInfo backFileInfo = new FileInfo();
        backFileInfo.setFileName("..");
        backFileInfo.setUpDirectory(true);
        backFileInfo.setFileType(FileType.DIRECTORY);
        addRow(backFileInfo, false);
        Files.walkFileTree(path, new FileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file,
                    BasicFileAttributes attrs) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setPath(file);
                fileInfo.setFileName(file.getFileName().toString());
                fileInfo.setFileType(FileType.FILE);
                fileInfo.setLastModifiedTime(attrs.lastModifiedTime());
                fileInfo.setSize(attrs.size());
                fileInfo.setSymbolicLink(attrs.isSymbolicLink());
                addRow(fileInfo, false);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                    BasicFileAttributes attrs) throws IOException {
                if (dir.equals(path))
                    return FileVisitResult.CONTINUE;
                FileInfo fileInfo = new FileInfo();
                fileInfo.setPath(dir);
                fileInfo.setFileName(dir.getFileName().toString());
                fileInfo.setFileType(FileType.DIRECTORY);
                fileInfo.setLastModifiedTime(attrs.lastModifiedTime());
                fileInfo.setSymbolicLink(attrs.isSymbolicLink());
                addRow(fileInfo, false);
                return FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc)
                    throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
        if (getRowCount() > 0) {
            TableModelEvent e = new TableModelEvent(this, 0,
                    this.getRowCount() - 1, TableModelEvent.ALL_COLUMNS,
                    TableModelEvent.INSERT);
            for (TableModelListener l : listeners) {
                l.tableChanged(e);
            }
        }
        initializeWatchService();
    }

    private void initializeWatchService() throws IOException {
        this.watchService = path.getFileSystem().newWatchService();
        final WatchKey watchKey = path.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    for (;;) {
                        WatchKey takedWatchKey = FileTableModel.this.watchService
                                .take();
                        if (takedWatchKey == watchKey) {
                            List<WatchEvent<?>> events = takedWatchKey
                                    .pollEvents();
                            for (WatchEvent<?> event : events) {
                                WatchEvent.Kind<?> kind = event.kind();
                                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                                    final Path path = FileTableModel.this
                                            .getPath().resolve(
                                                    (Path) event.context());
                                    try {
                                        final FileInfo fileInfo = new FileInfo(
                                                path);
                                        SwingUtilities
                                                .invokeLater(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        FileTableModel.this
                                                                .addOrUpdate(fileInfo);
                                                    }
                                                });
                                    } catch (IOException ioex) {
                                        ioex.printStackTrace();
                                    }
                                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                                    final Path path = FileTableModel.this
                                            .getPath().resolve(
                                                    (Path) event.context());
                                    SwingUtilities.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            FileTableModel.this
                                                    .removeRowByPath(path);
                                        }
                                    });
                                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                    final Path path = FileTableModel.this
                                            .getPath().resolve((Path)event.context());
                                    try {
                                        final FileInfo fileInfo = new FileInfo(path);
                                        SwingUtilities.invokeLater(new Runnable() {

                                            @Override
                                            public void run() {
                                                FileTableModel.this.updateRowByFileInfo(fileInfo);
                                            }
                                        });
                                    } catch (IOException ioex) {
                                        ioex.printStackTrace();
                                    }
                                    
                                }
                            }
                        }
                        boolean keyValid = watchKey.reset();
                        if (!keyValid) {
                            break;
                        }
                    }
                } catch (ClosedWatchServiceException cwse) {

                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }

        });
        thread.start();
    }

    protected void removeRowByPath(Path removedPath) {
        FileInfo fi = null;
        ListIterator<FileInfo> it = data.listIterator();
        while (it.hasNext()) {
            fi = it.next();
            if (fi.getPath() != null && fi.getPath().equals(removedPath)) {
                it.remove();
                TableModelEvent e = new TableModelEvent(this, 0,
                        it.previousIndex(), TableModelEvent.ALL_COLUMNS,
                        TableModelEvent.DELETE);
                for (TableModelListener l : listeners) {
                    l.tableChanged(e);
                }
                return;
            }
        }
    }

    protected void addOrUpdate(FileInfo fileInfo) {
        FileInfo fi = null;
        ListIterator<FileInfo> it = data.listIterator();
        while (it.hasNext()) {
            fi = it.next();
            if (fi.getPath() != null && fi.getPath().equals(fileInfo.getPath())) {
                it.set(fileInfo);
                TableModelEvent e = new TableModelEvent(this, 0,
                        it.previousIndex(), TableModelEvent.ALL_COLUMNS,
                        TableModelEvent.UPDATE);
                for (TableModelListener l : listeners) {
                    l.tableChanged(e);
                }
                return;
            }
        }
        addRow(fileInfo);
    }

    public void clear() {
        data.clear();
    }

    void addPathListener(ActionListener listener) {
        pathListeners.add(listener);
    }

    void removePathListener(ActionListener listener) {
        pathListeners.remove(listener);
    }

    private void updateRowByFileInfo(FileInfo updateFileInfo) {
        FileInfo fi = null;
        Path updatePath = updateFileInfo.getPath();
        ListIterator<FileInfo> it = data.listIterator();
        while (it.hasNext()) {
            fi = it.next();
            if (fi.getPath() != null && fi.getPath().equals(updatePath)) {
                it.set(updateFileInfo);
                TableModelEvent e = new TableModelEvent(this, 0,
                        it.previousIndex(), TableModelEvent.ALL_COLUMNS,
                        TableModelEvent.UPDATE);
                for (TableModelListener l : listeners) {
                    l.tableChanged(e);
                }
                return;
            }
        }
    }
}
