

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.nio.file.Path;

class FileTableMouseListener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            FileTable table = (FileTable) e.getSource();
            FileTable.setCurrentFileTable(table);
            if (e.getClickCount() == 2) {
                int rowIndex = table.convertRowIndexToModel(table
                        .getSelectedRow());
                table.clearSelection();
                FileTableModel tableModel = (FileTableModel) table.getModel();
                FileInfo fileInfo = tableModel.getRow(rowIndex);
                Path newPath = null;
                if (fileInfo.isUpDirectory()) {
                    Path parent = tableModel.getPath().getParent();
                    newPath = parent == null ? tableModel.getPath() : parent
                            .toAbsolutePath();
                } else if (fileInfo.getFileType().equals(FileType.DIRECTORY)) {
                    newPath = tableModel.getPath()
                            .resolve(fileInfo.getFileName()).toAbsolutePath();
                } else if (fileInfo.getFileType().equals(FileType.FILE)) {
                   
                }
                if (newPath != null) {
                    tableModel.setPath(newPath);
                    tableModel.fill();
                }
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        FileTable table = (FileTable) e.getSource();
        FileTable.setCurrentFileTable(table);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
}
