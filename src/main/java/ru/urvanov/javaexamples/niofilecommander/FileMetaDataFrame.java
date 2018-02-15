package ru.urvanov.javaexamples.niofilecommander;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

class FileMetaDataFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -6275208752453326297L;

    private JTabbedPane tabbedPane;

    private BasicFileAttributesPanel basicFileAttributesPanel;
    private DosFileAttributesPanel dosFileAttributesPanel;
    private PosixFileAttributesPanel posixFileAttributesPanel;
    private AclFileAttributesPanel aclFileAttributesPanel;
    private UserDefinedFileAttributesPanel userDefinedFileAttributesPanel;

    FileMetaDataFrame(Path path) {
        setSize(600, 400);
        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(path.getFileName().toString());
        tabbedPane = new JTabbedPane();
        add(tabbedPane);

        basicFileAttributesPanel = new BasicFileAttributesPanel();
        basicFileAttributesPanel.readAttributes(path);
        tabbedPane.addTab(BasicFileAttributes.class.getSimpleName(),
                basicFileAttributesPanel);

        dosFileAttributesPanel = new DosFileAttributesPanel();
        dosFileAttributesPanel.readAttributes(path);
        tabbedPane.addTab(DosFileAttributes.class.getSimpleName(),
                dosFileAttributesPanel);

        posixFileAttributesPanel = new PosixFileAttributesPanel();
        posixFileAttributesPanel.readAttributes(path);
        tabbedPane.addTab(PosixFileAttributes.class.getSimpleName(),
                posixFileAttributesPanel);

        aclFileAttributesPanel = new AclFileAttributesPanel();
        aclFileAttributesPanel.readAttributes(path);
        tabbedPane.addTab(AclFileAttributeView.class.getSimpleName(),
                aclFileAttributesPanel);

        userDefinedFileAttributesPanel = new UserDefinedFileAttributesPanel();
        userDefinedFileAttributesPanel.readAttributes(path);
        tabbedPane.addTab(UserDefinedFileAttributeView.class.getSimpleName(),
                userDefinedFileAttributesPanel);

    }
}

class BasicFileAttributesPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 6814509335080766749L;

    private JLabel lastModifiedLabel;
    private JSpinner lastModifiedSpinner;
    private JLabel lastAccessLabel;
    private JSpinner lastAccessSpinner;
    private JLabel creationLabel;
    private JSpinner creationSpinner;
    private JLabel sizeLabel;
    private JLabel sizeValueLabel;
    private JCheckBox regularFileCheckBox;
    private JCheckBox directoryCheckBox;
    private JCheckBox symbolicLinkCheckBox;
    private JCheckBox otherCheckBox;
    private JLabel fileKeyLabel;
    private JLabel fileKeyValueLabel;
    private JButton saveButton;
    private GridBagLayout gbl;
    private Path path;

    BasicFileAttributesPanel() {
        gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints normalConstraints = new GridBagConstraints();
        normalConstraints.anchor = GridBagConstraints.NORTHWEST;
        GridBagConstraints fillHorizontalConstraints = new GridBagConstraints();
        fillHorizontalConstraints.anchor = GridBagConstraints.NORTHWEST;
        fillHorizontalConstraints.fill = GridBagConstraints.HORIZONTAL;
        fillHorizontalConstraints.weightx = 0.1;
        fillHorizontalConstraints.gridwidth = GridBagConstraints.REMAINDER;

        lastModifiedLabel = new JLabel("last modified");
        add(lastModifiedLabel);
        gbl.setConstraints(lastModifiedLabel, normalConstraints);

        lastModifiedSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateTimeEditor = new JSpinner.DateEditor(
                lastModifiedSpinner);
        lastModifiedSpinner.setEditor(dateTimeEditor);
        add(lastModifiedSpinner);
        gbl.setConstraints(lastModifiedSpinner, fillHorizontalConstraints);

        lastAccessLabel = new JLabel("last access");
        add(lastAccessLabel);
        gbl.setConstraints(lastAccessLabel, normalConstraints);

        lastAccessSpinner = new JSpinner(new SpinnerDateModel());
        lastAccessSpinner.setEditor(new JSpinner.DateEditor(lastAccessSpinner));
        add(lastAccessSpinner);
        gbl.setConstraints(lastAccessSpinner, fillHorizontalConstraints);

        creationLabel = new JLabel("creation");
        add(creationLabel);
        gbl.setConstraints(creationLabel, normalConstraints);

        creationSpinner = new JSpinner(new SpinnerDateModel());
        creationSpinner.setEditor(new JSpinner.DateEditor(creationSpinner));
        add(creationSpinner);
        gbl.setConstraints(creationSpinner, fillHorizontalConstraints);

        sizeLabel = new JLabel("size");
        add(sizeLabel);
        gbl.setConstraints(sizeLabel, normalConstraints);

        sizeValueLabel = new JLabel();
        add(sizeValueLabel);
        gbl.setConstraints(sizeValueLabel, fillHorizontalConstraints);

        regularFileCheckBox = new JCheckBox("regular file");
        regularFileCheckBox.setEnabled(false);
        add(regularFileCheckBox);
        gbl.setConstraints(regularFileCheckBox, fillHorizontalConstraints);

        directoryCheckBox = new JCheckBox("directory");
        directoryCheckBox.setEnabled(false);
        add(directoryCheckBox);
        gbl.setConstraints(directoryCheckBox, fillHorizontalConstraints);

        symbolicLinkCheckBox = new JCheckBox("symbolic link");
        symbolicLinkCheckBox.setEnabled(false);
        add(symbolicLinkCheckBox);
        gbl.setConstraints(symbolicLinkCheckBox, fillHorizontalConstraints);

        otherCheckBox = new JCheckBox("other");
        otherCheckBox.setEnabled(false);
        add(otherCheckBox);
        gbl.setConstraints(otherCheckBox, fillHorizontalConstraints);

        fileKeyLabel = new JLabel("fileKey");
        add(fileKeyLabel);
        gbl.setConstraints(fileKeyLabel, normalConstraints);

        fileKeyValueLabel = new JLabel();
        add(fileKeyValueLabel);
        gbl.setConstraints(fileKeyValueLabel, fillHorizontalConstraints);

        GridBagConstraints lastPanelConstraints = new GridBagConstraints();
        lastPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        lastPanelConstraints.weightx = 0.0;
        lastPanelConstraints.weighty = 0.1;
        lastPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        lastPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        JPanel lastPanel = new JPanel();
        add(lastPanel);
        gbl.setConstraints(lastPanel, lastPanelConstraints);

        JPanel bottomPanel = new JPanel();
        add(bottomPanel);
        gbl.setConstraints(bottomPanel, fillHorizontalConstraints);

        GridBagLayout bottomPanelLayout = new GridBagLayout();
        bottomPanel.setLayout(bottomPanelLayout);

        GridBagConstraints bottomPanelInnerFillConstraints = new GridBagConstraints();
        bottomPanelInnerFillConstraints.anchor = GridBagConstraints.NORTHWEST;
        bottomPanelInnerFillConstraints.weightx = 0.2;

        JPanel bottomPanelInnerFillPanel = new JPanel();
        bottomPanel.add(bottomPanelInnerFillPanel);
        bottomPanelLayout.setConstraints(bottomPanelInnerFillPanel,
                bottomPanelInnerFillConstraints);

        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BasicFileAttributesPanel.this.save();
            }
        });
        bottomPanel.add(saveButton);
        gbl.setConstraints(saveButton, normalConstraints);

    }

    protected void save() {
        try {
            BasicFileAttributeView view = Files.getFileAttributeView(path,
                    BasicFileAttributeView.class);
            FileTime lastModifiedTime;
            FileTime lastAccessTime;
            FileTime createTime;
            Date lastModifiedDate = (Date) lastModifiedSpinner.getValue();
            Date lastAccessDate = (Date) lastAccessSpinner.getValue();
            Date createDate = (Date) creationSpinner.getValue();
            lastModifiedTime = lastModifiedDate == null ? null : FileTime
                    .fromMillis(lastModifiedDate.getTime());
            lastAccessTime = lastAccessDate == null ? null : FileTime
                    .fromMillis(lastAccessDate.getTime());
            createTime = createDate == null ? null : FileTime
                    .fromMillis(createDate.getTime());
            view.setTimes(lastModifiedTime, lastAccessTime, createTime);
            JOptionPane.showMessageDialog(this, "Saved successfully.");
        } catch (IOException ioex) {
            JOptionPane.showMessageDialog(this, ioex.toString(), "",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void readAttributes(Path path) {
        try {
            this.path = path;
            BasicFileAttributes basicFileAttributes = Files.readAttributes(
                    path, BasicFileAttributes.class);
            if (basicFileAttributes.lastModifiedTime() != null)
                lastModifiedSpinner.setValue(new Date(basicFileAttributes
                        .lastModifiedTime().toMillis()));
            if (basicFileAttributes.lastAccessTime() != null)
                lastAccessSpinner.setValue(new Date(basicFileAttributes
                        .lastAccessTime().toMillis()));
            if (basicFileAttributes.creationTime() != null)
                creationSpinner.setValue(new Date(basicFileAttributes
                        .creationTime().toMillis()));
            sizeValueLabel.setText(basicFileAttributes.size() + " B");
            regularFileCheckBox
                    .setSelected(basicFileAttributes.isRegularFile());
            directoryCheckBox.setSelected(basicFileAttributes.isDirectory());
            symbolicLinkCheckBox.setSelected(basicFileAttributes
                    .isSymbolicLink());
            otherCheckBox.setSelected(basicFileAttributes.isOther());
            fileKeyValueLabel.setText("" + basicFileAttributes.fileKey());
        } catch (Exception e) {
            this.removeAll();
            add(new JLabel(e.toString()));
        }

    }

}

class DosFileAttributesPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 2764369895011912956L;

    private JCheckBox readOnlyCheckBox;
    private JCheckBox hiddenCheckBox;
    private JCheckBox systemCheckBox;
    private JCheckBox archiveCheckBox;
    private Path path;
    private JButton saveButton;

    DosFileAttributesPanel() {
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);

        GridBagConstraints normalConstraints = new GridBagConstraints();
        normalConstraints.anchor = GridBagConstraints.NORTHWEST;
        GridBagConstraints fillHorizontalConstraints = new GridBagConstraints();
        fillHorizontalConstraints.anchor = GridBagConstraints.NORTHWEST;
        fillHorizontalConstraints.fill = GridBagConstraints.HORIZONTAL;
        fillHorizontalConstraints.weightx = 0.1;
        fillHorizontalConstraints.gridwidth = GridBagConstraints.REMAINDER;

        readOnlyCheckBox = new JCheckBox("read only");
        add(readOnlyCheckBox);
        gbl.setConstraints(readOnlyCheckBox, fillHorizontalConstraints);

        hiddenCheckBox = new JCheckBox("hidden");
        add(hiddenCheckBox);
        gbl.setConstraints(hiddenCheckBox, fillHorizontalConstraints);

        systemCheckBox = new JCheckBox("system");
        add(systemCheckBox);
        gbl.setConstraints(systemCheckBox, fillHorizontalConstraints);

        archiveCheckBox = new JCheckBox("archive");
        add(archiveCheckBox);
        gbl.setConstraints(archiveCheckBox, fillHorizontalConstraints);

        GridBagConstraints lastPanelConstraints = new GridBagConstraints();
        lastPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        lastPanelConstraints.weightx = 0.0;
        lastPanelConstraints.weighty = 0.1;
        lastPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        lastPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        JPanel lastPanel = new JPanel();
        add(lastPanel);
        gbl.setConstraints(lastPanel, lastPanelConstraints);

        JPanel bottomPanel = new JPanel();
        add(bottomPanel);
        gbl.setConstraints(bottomPanel, fillHorizontalConstraints);

        GridBagLayout bottomPanelLayout = new GridBagLayout();
        bottomPanel.setLayout(bottomPanelLayout);

        GridBagConstraints bottomPanelInnerFillConstraints = new GridBagConstraints();
        bottomPanelInnerFillConstraints.anchor = GridBagConstraints.NORTHWEST;
        bottomPanelInnerFillConstraints.weightx = 0.2;

        JPanel bottomPanelInnerFillPanel = new JPanel();
        bottomPanel.add(bottomPanelInnerFillPanel);
        bottomPanelLayout.setConstraints(bottomPanelInnerFillPanel,
                bottomPanelInnerFillConstraints);

        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DosFileAttributesPanel.this.save();
            }
        });
        bottomPanel.add(saveButton);
        gbl.setConstraints(saveButton, normalConstraints);

    }

    protected void save() {
        try {
            DosFileAttributeView view = Files.getFileAttributeView(path,
                    DosFileAttributeView.class);
            view.setReadOnly(readOnlyCheckBox.isSelected());
            view.setHidden(hiddenCheckBox.isSelected());
            view.setSystem(systemCheckBox.isSelected());
            view.setArchive(archiveCheckBox.isSelected());
            JOptionPane.showMessageDialog(this, "Saved successfully.");
        } catch (IOException ioex) {
            JOptionPane.showMessageDialog(this, ioex.toString(), "",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    void readAttributes(Path path) {
        try {
            this.path = path;
            DosFileAttributes attrs = Files.readAttributes(path,
                    DosFileAttributes.class);
            readOnlyCheckBox.setSelected(attrs.isReadOnly());
            hiddenCheckBox.setSelected(attrs.isHidden());
            systemCheckBox.setSelected(attrs.isSystem());
            archiveCheckBox.setSelected(attrs.isArchive());

        } catch (Exception e) {
            this.removeAll();
            add(new JLabel(e.toString()));
        }
    }
}

class PosixFileAttributesPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 2708475365017230392L;

    private JCheckBox ownerReadCheckBox;
    private JCheckBox ownerWriteCheckBox;
    private JCheckBox ownerExecuteCheckBox;
    private JCheckBox groupReadCheckBox;
    private JCheckBox groupWriteCheckBox;
    private JCheckBox groupExecuteCheckBox;
    private JCheckBox othersReadCheckBox;
    private JCheckBox othersWriteCheckBox;
    private JCheckBox othersExecuteCheckBox;
    private JLabel ownerLabel;
    private JTextField ownerTextField;
    private JLabel groupLabel;
    private JTextField groupTextField;
    private JButton saveButton;
    private Path path;

    PosixFileAttributesPanel() {
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);

        GridBagConstraints normalConstraints = new GridBagConstraints();
        normalConstraints.anchor = GridBagConstraints.NORTHWEST;
        GridBagConstraints fillHorizontalConstraints = new GridBagConstraints();
        fillHorizontalConstraints.anchor = GridBagConstraints.NORTHWEST;
        fillHorizontalConstraints.fill = GridBagConstraints.HORIZONTAL;
        fillHorizontalConstraints.weightx = 0.1;
        fillHorizontalConstraints.gridwidth = GridBagConstraints.REMAINDER;

        ownerReadCheckBox = new JCheckBox("OWNER_READ");
        add(ownerReadCheckBox);
        gbl.setConstraints(ownerReadCheckBox, fillHorizontalConstraints);

        ownerWriteCheckBox = new JCheckBox("OWNER_WRITE");
        add(ownerWriteCheckBox);
        gbl.setConstraints(ownerWriteCheckBox, fillHorizontalConstraints);

        ownerExecuteCheckBox = new JCheckBox("OWNER_EXECUTE");
        add(ownerExecuteCheckBox);
        gbl.setConstraints(ownerExecuteCheckBox, fillHorizontalConstraints);

        groupReadCheckBox = new JCheckBox("GROUP_READ");
        add(groupReadCheckBox);
        gbl.setConstraints(groupReadCheckBox, fillHorizontalConstraints);

        groupWriteCheckBox = new JCheckBox("GROUP_WRITE");
        add(groupWriteCheckBox);
        gbl.setConstraints(groupWriteCheckBox, fillHorizontalConstraints);

        groupExecuteCheckBox = new JCheckBox("GROUP_EXECUTE");
        add(groupExecuteCheckBox);
        gbl.setConstraints(groupExecuteCheckBox, fillHorizontalConstraints);

        othersReadCheckBox = new JCheckBox("OTHERS_READ");
        add(othersReadCheckBox);
        gbl.setConstraints(othersReadCheckBox, fillHorizontalConstraints);

        othersWriteCheckBox = new JCheckBox("OTHERS_WRITE");
        add(othersWriteCheckBox);
        gbl.setConstraints(othersWriteCheckBox, fillHorizontalConstraints);

        othersExecuteCheckBox = new JCheckBox("OTHERS_EXECUTE");
        add(othersExecuteCheckBox);
        gbl.setConstraints(othersExecuteCheckBox, fillHorizontalConstraints);

        ownerLabel = new JLabel("owner");
        add(ownerLabel);
        gbl.setConstraints(ownerLabel, normalConstraints);

        ownerTextField = new JTextField();
        add(ownerTextField);
        gbl.setConstraints(ownerTextField, fillHorizontalConstraints);

        groupLabel = new JLabel("group");
        add(groupLabel);
        gbl.setConstraints(groupLabel, normalConstraints);

        groupTextField = new JTextField();
        add(groupTextField);
        gbl.setConstraints(groupTextField, fillHorizontalConstraints);

        GridBagConstraints lastPanelConstraints = new GridBagConstraints();
        lastPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        lastPanelConstraints.weightx = 0.0;
        lastPanelConstraints.weighty = 0.1;
        lastPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        lastPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        JPanel lastPanel = new JPanel();
        add(lastPanel);
        gbl.setConstraints(lastPanel, lastPanelConstraints);

        JPanel bottomPanel = new JPanel();
        add(bottomPanel);
        gbl.setConstraints(bottomPanel, fillHorizontalConstraints);

        GridBagLayout bottomPanelLayout = new GridBagLayout();
        bottomPanel.setLayout(bottomPanelLayout);

        GridBagConstraints bottomPanelInnerFillConstraints = new GridBagConstraints();
        bottomPanelInnerFillConstraints.anchor = GridBagConstraints.NORTHWEST;
        bottomPanelInnerFillConstraints.weightx = 0.2;

        JPanel bottomPanelInnerFillPanel = new JPanel();
        bottomPanel.add(bottomPanelInnerFillPanel);
        bottomPanelLayout.setConstraints(bottomPanelInnerFillPanel,
                bottomPanelInnerFillConstraints);

        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PosixFileAttributesPanel.this.save();
            }
        });
        bottomPanel.add(saveButton);
        gbl.setConstraints(saveButton, normalConstraints);
    }

    public void readAttributes(Path path) {
        try {
            this.path = path;
            PosixFileAttributes attrs = Files.readAttributes(path,
                    PosixFileAttributes.class);
            Set<PosixFilePermission> permissions = attrs.permissions();

            for (PosixFilePermission permission : permissions) {
                switch (permission) {
                case OWNER_READ:
                    ownerReadCheckBox.setSelected(true);
                    break;
                case OWNER_WRITE:
                    ownerWriteCheckBox.setSelected(true);
                    break;
                case OWNER_EXECUTE:
                    ownerExecuteCheckBox.setSelected(true);
                    break;
                case GROUP_READ:
                    groupReadCheckBox.setSelected(true);
                    break;
                case GROUP_WRITE:
                    groupWriteCheckBox.setSelected(true);
                    break;
                case GROUP_EXECUTE:
                    groupExecuteCheckBox.setSelected(true);
                    break;
                case OTHERS_READ:
                    othersReadCheckBox.setSelected(true);
                    break;
                case OTHERS_WRITE:
                    othersWriteCheckBox.setSelected(true);
                    break;
                case OTHERS_EXECUTE:
                    othersExecuteCheckBox.setSelected(true);
                    break;
                }
            }
            UserPrincipal owner = attrs.owner();
            ownerTextField.setText(owner.getName());
            GroupPrincipal group = attrs.group();
            groupTextField.setText(group.getName());
        } catch (Exception e) {
            this.removeAll();
            add(new JLabel(e.toString()));
        }
    }

    protected void save() {
        try {
            Set<PosixFilePermission> permissions = new HashSet<>();
            if (ownerReadCheckBox.isSelected())
                permissions.add(PosixFilePermission.OWNER_READ);
            if (ownerWriteCheckBox.isSelected())
                permissions.add(PosixFilePermission.OWNER_WRITE);
            if (ownerExecuteCheckBox.isSelected())
                permissions.add(PosixFilePermission.OWNER_EXECUTE);
            if (groupReadCheckBox.isSelected())
                permissions.add(PosixFilePermission.GROUP_READ);
            if (groupWriteCheckBox.isSelected())
                permissions.add(PosixFilePermission.GROUP_WRITE);
            if (groupExecuteCheckBox.isSelected())
                permissions.add(PosixFilePermission.GROUP_EXECUTE);
            if (othersReadCheckBox.isSelected())
                permissions.add(PosixFilePermission.OTHERS_READ);
            if (othersWriteCheckBox.isSelected())
                permissions.add(PosixFilePermission.OTHERS_WRITE);
            if (othersExecuteCheckBox.isSelected())
                permissions.add(PosixFilePermission.OTHERS_EXECUTE);
            Files.setPosixFilePermissions(path, permissions);
            UserPrincipalLookupService userPrincipalLookupService = path
                    .getFileSystem().getUserPrincipalLookupService();
            Files.setOwner(path,

            userPrincipalLookupService.lookupPrincipalByName(ownerTextField
                    .getText()));
            PosixFileAttributeView view = Files.getFileAttributeView(path,
                    PosixFileAttributeView.class);
            view.setGroup(userPrincipalLookupService
                    .lookupPrincipalByGroupName(groupTextField.getText()));
            JOptionPane.showMessageDialog(this, "Saved successfully.");
        } catch (IOException ioex) {
            JOptionPane.showMessageDialog(this, ioex.toString(), "",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

class AclFileAttributesPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 2624949891625127415L;

    class AclInfo {
        private UserPrincipal userPrincipal;
        private AclEntryType type;
        private Set<AclEntryPermission> permissions;
        private Set<AclEntryFlag> flags;

        public UserPrincipal getUserPrincipal() {
            return userPrincipal;
        }

        public void setUserPrincipal(UserPrincipal userPrincipal) {
            this.userPrincipal = userPrincipal;
        }

        public AclEntryType getType() {
            return type;
        }

        public void setType(AclEntryType type) {
            this.type = type;
        }

        public Set<AclEntryPermission> getPermissions() {
            return permissions;
        }

        public void setPermissions(Set<AclEntryPermission> permissions) {
            this.permissions = permissions;
        }

        public Set<AclEntryFlag> getFlags() {
            return flags;
        }

        public void setFlags(Set<AclEntryFlag> flags) {
            this.flags = flags;
        }

    }

    private class AclTableModel implements TableModel {

        private Path path;
        private List<AclInfo> data = new ArrayList<AclInfo>();

        private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
        private static final int NORMAL_COLUMN_COUNT = 2;

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return 2 + AclEntryPermission.values().length
                    + AclEntryFlag.values().length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0)
                return "principal";
            else if (columnIndex == 1)
                return "type";
            else if (columnIndex < NORMAL_COLUMN_COUNT
                    + AclEntryPermission.values().length)
                return AclEntryPermission.values()[columnIndex
                        - NORMAL_COLUMN_COUNT].name();
            else
                return AclEntryFlag.values()[columnIndex
                        - AclEntryPermission.values().length
                        - NORMAL_COLUMN_COUNT].name();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0)
                return String.class;
            else if (columnIndex == 1)
                return String.class;
            else
                return Boolean.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            AclInfo aclInfo = data.get(rowIndex);
            if (columnIndex == 0) {
                UserPrincipal userPrincipal = aclInfo.getUserPrincipal();
                return userPrincipal == null ? null : userPrincipal.getName();
            } else if (columnIndex == 1)
                return aclInfo.getType();
            else if (columnIndex < AclEntryPermission.values().length
                    + NORMAL_COLUMN_COUNT)
                return aclInfo.getPermissions().contains(
                        AclEntryPermission.values()[columnIndex
                                - NORMAL_COLUMN_COUNT]);
            else
                return aclInfo.getFlags().contains(
                        AclEntryFlag.values()[columnIndex - NORMAL_COLUMN_COUNT
                                - AclEntryPermission.values().length]);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            AclInfo aclInfo = data.get(rowIndex);
            if (columnIndex == 0) {
                try {
                    aclInfo.setUserPrincipal(path.getFileSystem()
                            .getUserPrincipalLookupService()
                            .lookupPrincipalByName(aValue.toString()));
                } catch (IOException e) {
                    aclInfo.setUserPrincipal(null);
                }
            } else if (columnIndex == 1) {
                aclInfo.setType((AclEntryType) aValue);
            } else if (columnIndex < AclEntryPermission.values().length
                    + NORMAL_COLUMN_COUNT) {
                Boolean b = (Boolean) aValue;
                AclEntryPermission permission = AclEntryPermission.values()[columnIndex
                        - NORMAL_COLUMN_COUNT];
                if (b)
                    aclInfo.getPermissions().add(permission);
                else
                    aclInfo.getPermissions().remove(permission);
            } else {
                Boolean b = (Boolean) aValue;
                AclEntryFlag flag = AclEntryFlag.values()[columnIndex
                        - NORMAL_COLUMN_COUNT
                        - AclEntryPermission.values().length];
                if (b)
                    aclInfo.getFlags().add(flag);
                else
                    aclInfo.getFlags().remove(flag);
            }
            /*
             * TableModelEvent tableModelEvent = new TableModelEvent(this,
             * rowIndex, rowIndex, columnIndex, TableModelEvent.UPDATE); for
             * (TableModelListener l : listeners) {
             * l.tableChanged(tableModelEvent); }
             */
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }

        public void setPath(Path path) {
            this.path = path;
        }

        public void setData(List<AclInfo> aclInfoList) {
            int rowCount = data.size();
            data = aclInfoList;
            if (data.size() > 0) {
                TableModelEvent tableModelEvent = new TableModelEvent(this, 0,
                        rowCount - 1, TableModelEvent.ALL_COLUMNS,
                        TableModelEvent.DELETE);
                for (TableModelListener l : listeners) {
                    l.tableChanged(tableModelEvent);
                }
            }
            TableModelEvent tableModelEvent = new TableModelEvent(this, 0,
                    data.size() - 1, TableModelEvent.ALL_COLUMNS,
                    TableModelEvent.INSERT);
            for (TableModelListener l : listeners) {
                l.tableChanged(tableModelEvent);
            }
        }

        public void addRow() {
            AclInfo aclInfo = new AclInfo();
            aclInfo.setPermissions(new HashSet<AclEntryPermission>());
            data.add(aclInfo);
            TableModelEvent tableModelEvent = new TableModelEvent(this,
                    data.size() - 1, data.size() - 1,
                    TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
            for (TableModelListener l : listeners) {
                l.tableChanged(tableModelEvent);
            }
        }

        public List<AclInfo> getData() {
            return data;
        }

        public void removeRow(int selectedRow) {
            data.remove(selectedRow);
            TableModelEvent tableModelEvent = new TableModelEvent(this,
                    selectedRow, selectedRow, TableModelEvent.ALL_COLUMNS,
                    TableModelEvent.DELETE);
            for (TableModelListener l : listeners) {
                l.tableChanged(tableModelEvent);
            }
        }

    }

    class AclTable extends JTable {

        /**
         * 
         */
        private static final long serialVersionUID = 2857236054321543983L;

        AclTable(AclTableModel model) {
            super(model);
            TableColumnModel tableColumnModel = this.getColumnModel();
            JComboBox<AclEntryType> aclEntryTypeComboBox = new JComboBox<>();
            for (AclEntryType type : AclEntryType.values()) {
                aclEntryTypeComboBox.addItem(type);
            }
            tableColumnModel.getColumn(1).setCellEditor(
                    new DefaultCellEditor(aclEntryTypeComboBox));
            tableColumnModel.getColumn(0).setPreferredWidth(300);
            for (int n = 1; n < tableColumnModel.getColumnCount(); n++) {
                TableColumn tableColumn = tableColumnModel.getColumn(n);
                tableColumn.setPreferredWidth(100);
            }

        }
    }

    private JLabel ownerLabel;
    private JTextField ownerTextField;
    private AclTableModel aclTableModel;
    private AclTable aclTable;
    private JButton saveButton;
    private JButton addRowButton;
    private JButton removeRowButton;
    private Path path;

    AclFileAttributesPanel() {
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);

        GridBagConstraints normalConstraints = new GridBagConstraints();
        normalConstraints.anchor = GridBagConstraints.NORTHWEST;
        GridBagConstraints fillHorizontalConstraints = new GridBagConstraints();
        fillHorizontalConstraints.anchor = GridBagConstraints.NORTHWEST;
        fillHorizontalConstraints.fill = GridBagConstraints.HORIZONTAL;
        fillHorizontalConstraints.weightx = 0.1;
        fillHorizontalConstraints.gridwidth = GridBagConstraints.REMAINDER;

        ownerLabel = new JLabel("Owner");
        add(ownerLabel);
        gbl.setConstraints(ownerLabel, normalConstraints);

        ownerTextField = new JTextField();
        add(ownerTextField);
        gbl.setConstraints(ownerTextField, fillHorizontalConstraints);

        addRowButton = new JButton("add row");
        addRowButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                aclTableModel.addRow();
            }
        });
        add(addRowButton);
        gbl.setConstraints(addRowButton, normalConstraints);

        removeRowButton = new JButton("remove row");
        removeRowButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = aclTable.getSelectedRow();
                if (selectedRow != -1) {
                    aclTableModel.removeRow(selectedRow);
                }
            }
        });
        add(removeRowButton);
        gbl.setConstraints(removeRowButton, normalConstraints);

        JPanel pnl1 = new JPanel();
        add(pnl1);
        gbl.setConstraints(pnl1, fillHorizontalConstraints);

        aclTableModel = new AclTableModel();

        aclTable = new AclTable(aclTableModel);
        aclTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane tableScrollPane = new JScrollPane(aclTable);
        GridBagConstraints tableConstraints = new GridBagConstraints();
        tableConstraints.anchor = GridBagConstraints.NORTHWEST;
        tableConstraints.fill = GridBagConstraints.BOTH;
        tableConstraints.weightx = 0.1;
        tableConstraints.weighty = 0.1;
        tableConstraints.gridwidth = GridBagConstraints.REMAINDER;
        tableScrollPane.getViewport().setMinimumSize(new Dimension(1000, 50));
        add(tableScrollPane);
        gbl.setConstraints(tableScrollPane, tableConstraints);

        JPanel bottomPanel = new JPanel();
        add(bottomPanel);
        gbl.setConstraints(bottomPanel, fillHorizontalConstraints);

        GridBagLayout bottomPanelLayout = new GridBagLayout();
        bottomPanel.setLayout(bottomPanelLayout);

        GridBagConstraints bottomPanelInnerFillConstraints = new GridBagConstraints();
        bottomPanelInnerFillConstraints.anchor = GridBagConstraints.NORTHWEST;
        bottomPanelInnerFillConstraints.weightx = 0.2;

        JPanel bottomPanelInnerFillPanel = new JPanel();
        bottomPanel.add(bottomPanelInnerFillPanel);
        bottomPanelLayout.setConstraints(bottomPanelInnerFillPanel,
                bottomPanelInnerFillConstraints);

        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AclFileAttributesPanel.this.save();
            }
        });
        bottomPanel.add(saveButton);
        gbl.setConstraints(saveButton, normalConstraints);

    }

    protected void save() {
        try {
            AclFileAttributeView view = Files.getFileAttributeView(path,
                    AclFileAttributeView.class);
            List<AclEntry> aclEntryList = new ArrayList<AclEntry>();
            for (AclInfo aclInfo : aclTableModel.getData()) {
                if (aclInfo.getPermissions() != null
                        && aclInfo.getType() != null
                        && aclInfo.getUserPrincipal() != null
                        && aclInfo.getFlags() != null) {
                    AclEntry.Builder builder = AclEntry.newBuilder();
                    builder.setPrincipal(aclInfo.getUserPrincipal());
                    if (!aclInfo.getPermissions().isEmpty())
                        builder.setPermissions(aclInfo.getPermissions());
                    builder.setType(aclInfo.getType());
                    if (!aclInfo.getFlags().isEmpty())
                        builder.setFlags(aclInfo.getFlags());
                    aclEntryList.add(builder.build());
                }
            }
            view.setAcl(aclEntryList);
            JOptionPane.showMessageDialog(this, "Saved successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.toString(), "",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void readAttributes(Path path) {
        try {
            this.path = path;
            aclTableModel.setPath(path);
            AclFileAttributeView view = Files.getFileAttributeView(path,
                    AclFileAttributeView.class);
            UserPrincipal owner = view.getOwner();
            ownerTextField.setText(owner == null ? "" : owner.getName());
            List<AclEntry> aclList = view.getAcl();
            List<AclInfo> aclInfoList = new ArrayList<>();
            for (AclEntry entry : aclList) {
                AclInfo aclInfo = new AclInfo();
                aclInfo.setPermissions(entry.permissions());
                aclInfo.setUserPrincipal(entry.principal());
                aclInfo.setType(entry.type());
                aclInfo.setFlags(entry.flags());
                aclInfoList.add(aclInfo);
            }
            aclTableModel.setData(aclInfoList);
        } catch (Exception e) {
            this.removeAll();
            this.add(new JLabel(e.toString()));
        }
    }

}

class UserDefinedFileAttributesPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 6517371141435675891L;

    private JTable table;
    private JButton addRowButton;
    private JButton removeRowButton;
    private JButton saveButton;

    private Path path;

    UserDefinedFileAttributesPanel() {
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);

        GridBagConstraints normalConstraints = new GridBagConstraints();
        normalConstraints.anchor = GridBagConstraints.NORTHWEST;
        GridBagConstraints fillHorizontalConstraints = new GridBagConstraints();
        fillHorizontalConstraints.anchor = GridBagConstraints.NORTHWEST;
        fillHorizontalConstraints.fill = GridBagConstraints.HORIZONTAL;
        fillHorizontalConstraints.weightx = 0.1;
        fillHorizontalConstraints.gridwidth = GridBagConstraints.REMAINDER;

        addRowButton = new JButton("add row");
        addRowButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel tableModel = (DefaultTableModel) table
                        .getModel();
                tableModel.addRow(new Vector<String>(2));
            }

        });
        add(addRowButton);
        gbl.setConstraints(addRowButton, normalConstraints);

        removeRowButton = new JButton("remove row");
        removeRowButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int rowIndex = table.getSelectedRow();
                if (rowIndex != -1) {
                    DefaultTableModel tableModel = (DefaultTableModel) table
                            .getModel();
                    tableModel.removeRow(rowIndex);
                }
            }

        });
        add(removeRowButton);
        gbl.setConstraints(removeRowButton, normalConstraints);

        JPanel pnl1 = new JPanel();
        add(pnl1);
        gbl.setConstraints(pnl1, fillHorizontalConstraints);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("name");
        tableModel.addColumn("value");
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane);
        GridBagConstraints tableConstraints = new GridBagConstraints();
        tableConstraints.anchor = GridBagConstraints.NORTHWEST;
        tableConstraints.fill = GridBagConstraints.BOTH;
        tableConstraints.weightx = 0.1;
        tableConstraints.weighty = 0.1;
        tableConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(tableScrollPane, tableConstraints);

        JPanel bottomPanel = new JPanel();
        add(bottomPanel);
        gbl.setConstraints(bottomPanel, fillHorizontalConstraints);

        GridBagLayout bottomPanelLayout = new GridBagLayout();
        bottomPanel.setLayout(bottomPanelLayout);

        GridBagConstraints bottomPanelInnerFillConstraints = new GridBagConstraints();
        bottomPanelInnerFillConstraints.anchor = GridBagConstraints.NORTHWEST;
        bottomPanelInnerFillConstraints.weightx = 0.2;

        JPanel bottomPanelInnerFillPanel = new JPanel();
        bottomPanel.add(bottomPanelInnerFillPanel);
        bottomPanelLayout.setConstraints(bottomPanelInnerFillPanel,
                bottomPanelInnerFillConstraints);

        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UserDefinedFileAttributesPanel.this.save();
            }
        });
        bottomPanel.add(saveButton);
        gbl.setConstraints(saveButton, normalConstraints);
    }

    protected void save() {
        try {
            UserDefinedFileAttributeView view = Files.getFileAttributeView(
                    path, UserDefinedFileAttributeView.class);
            List<String> existNames = view.list();
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            @SuppressWarnings("unchecked")
            Vector<Vector<String>> data = tableModel.getDataVector();
            for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
                Vector<String> row = data.elementAt(rowIndex);
                String name = row.elementAt(0);
                String value = row.elementAt(1);
                view.write(name, Charset.defaultCharset().encode(value));
                existNames.remove(name);
            }
            for (String deleteName : existNames) {
                view.delete(deleteName);
            }
            JOptionPane.showMessageDialog(this, "Saved successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.toString(), "",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void readAttributes(Path path) {
        try {
            this.path = path;
            UserDefinedFileAttributeView view = Files.getFileAttributeView(
                    path, UserDefinedFileAttributeView.class);
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            List<String> names = view.list();
            tableModel.setRowCount(names.size());
            int row = 0;
            for (String name : names) {
                ByteBuffer buf = ByteBuffer.allocate(view.size(name));
                view.read(name, buf);
                buf.flip();
                String value = Charset.defaultCharset().decode(buf).toString();
                tableModel.setValueAt(name, row, 0);
                tableModel.setValueAt(value, row, 1);
            }

        } catch (Exception e) {
            this.removeAll();
            this.add(new JLabel(e.toString()));
        }
    }
}