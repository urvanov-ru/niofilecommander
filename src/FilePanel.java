
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

class FilePanel extends JPanel {
    /**
	 * 
	 */
    private static final long serialVersionUID = -2413185135871050363L;
    private JComboBox<Path> rootPathsComboBox;
    private JTextField pathTextField;
    private FileTable fileTable;
    private FileTableModel fileTableModel;
    private JToolBar toolBar;

    FilePanel(Path pathArg) throws IOException {
        Path path = pathArg.toAbsolutePath();
        fileTableModel = new FileTableModel();
        fileTableModel.setPath(path);
        fileTableModel.fill();

        fileTable = new FileTable(fileTableModel);
        JScrollPane fileTableScrollPane = new JScrollPane(fileTable);
        setLayout(new BorderLayout());
        add(fileTableScrollPane, BorderLayout.CENTER);
        toolBar = new JToolBar();
        rootPathsComboBox = new JComboBox<>();
        for (Path rootPath : FileSystems.getDefault().getRootDirectories()) {
            rootPathsComboBox.addItem(rootPath);
        }
        rootPathsComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fileTableModel.setPath((Path) rootPathsComboBox
                            .getSelectedItem());
                    fileTableModel.fill();
                } catch (IOException ioex) {
                    JOptionPane.showMessageDialog(FilePanel.this,
                            ioex.toString(), "", JOptionPane.ERROR_MESSAGE);
                }
            }

        });
        toolBar.add(rootPathsComboBox);
        pathTextField = new JTextField(path.toString());
        toolBar.add(pathTextField);
        add(toolBar, BorderLayout.NORTH);
        FileTableMouseListener fileTableMouseListener = new FileTableMouseListener();
        fileTable.addMouseListener(fileTableMouseListener);

        fileTableModel.addPathListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pathTextField.setText(fileTableModel.getPath().toString());
            }

        });

        pathTextField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        fileTableModel.setPath(Paths.get(pathTextField
                                .getText()));
                        fileTableModel.fill();
                    }
                } catch (IOException ioex) {
                    pathTextField.setText(fileTableModel.getPath().toString());
                    JOptionPane.showMessageDialog(FilePanel.this,
                            ioex.toString(), "", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public FileTable getFileTable() {
        return fileTable;
    }

}
