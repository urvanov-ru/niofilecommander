
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

class FileTable extends JTable {
    /**
     * 
     */
    private static final long serialVersionUID = 3612032723234837912L;

    private static FileTable currentFileTable;

    private FileTableModel fileTableModel;

    public FileTable(FileTableModel model) {
        super(model);
        this.setAutoCreateRowSorter(true);
        this.fileTableModel = model;
        TableColumnModel tableColumnModel = this.getColumnModel();
        TableColumn sizeColumn = tableColumnModel.getColumn(4);
        TableColumn fileNameColumn = tableColumnModel.getColumn(0);
        TableColumn fileTypeColumn = tableColumnModel.getColumn(1);
        TableColumn isSymbolicLinkColumn = tableColumnModel.getColumn(2);
        TableColumn lastModifiedColumn = tableColumnModel.getColumn(3);
        fileNameColumn.setPreferredWidth(100);
        fileTypeColumn.setPreferredWidth(80);
        fileTypeColumn.setMaxWidth(80);
        isSymbolicLinkColumn.setPreferredWidth(80);
        isSymbolicLinkColumn.setMaxWidth(80);
        lastModifiedColumn.setPreferredWidth(130);
        lastModifiedColumn.setMaxWidth(130);
        sizeColumn.setPreferredWidth(80);
        sizeColumn.setMaxWidth(80);
        TableCellRenderer fileTimeCellRenderer = new DefaultTableCellRenderer() {

            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            DateFormat formatter;

            public void setValue(Object value) {
                if (formatter == null) {
                    formatter = DateFormat.getDateTimeInstance();
                }
                setText((value == null) ? "" : formatter.format(new Date(
                        ((FileTime) value).toMillis())));
            }
        };
        lastModifiedColumn.setCellRenderer(fileTimeCellRenderer);

        TableCellRenderer sizeCellRenderer = new DefaultTableCellRenderer() {

            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            DecimalFormat formatter;

            public void setValue(Object value) {
                final long KiB = 1024;
                final long MiB = KiB * 1024;
                final long GiB = MiB * 1024;
                final long TiB = GiB * 1024;
                final long PiB = TiB * 1024;
                final long EiB = PiB * 1024;
                final long ZiB = EiB * 1024;
                final long YiB = ZiB * 1024;
                if (value != null) {
                    Double d = ((Long) value).doubleValue();
                    if (formatter == null)
                        formatter = new DecimalFormat("###,###.#");
                    if (d < KiB)
                        setText(formatter.format(d) + " B");
                    else if (d < MiB)
                        setText(formatter.format(d / KiB) + " KiB");
                    else if (d < GiB)
                        setText(formatter.format(d / MiB) + " MiB");
                    else if (d < TiB)
                        setText(formatter.format(d / GiB) + " GiB");
                    else if (d < PiB)
                        setText(formatter.format(d / TiB) + " TiB");
                    else if (d < EiB)
                        setText(formatter.format(d / PiB) + " PiB");
                    else if (d < ZiB)
                        setText(formatter.format(d / EiB) + " EiB");
                    else if (d < YiB)
                        setText(formatter.format(d / ZiB) + " ZiB");
                    else
                        setText(formatter.format(d / YiB) + " YiB");
                } else
                    setText("");
            }
        };
        sizeColumn.setCellRenderer(sizeCellRenderer);
    }

    @Override
    public FileTableModel getModel() {
        if (fileTableModel == null)
            return (FileTableModel) super.getModel();
        return fileTableModel;
    }

    public FileInfo getSelectedFileInfo() {
        int rowIndex = this.getSelectedRow();
        if (rowIndex == -1)
            return null;
        else
            return fileTableModel.getRow(rowIndex);
    }

    public static void setCurrentFileTable(FileTable fileTable) {
        FileTable.currentFileTable = fileTable;
    }

    public static FileTable getCurrentFileTable() {
        return currentFileTable;
    }

    public Collection<FileInfo> getSelectedFileInfos() {
        int[] rowIndexes = this.getSelectedRows();
        List<FileInfo> result = new ArrayList<FileInfo>();
        for (int n : rowIndexes) {
            result.add(fileTableModel.getRow(this.convertRowIndexToModel(n)));
        }
        return result;
    }
}