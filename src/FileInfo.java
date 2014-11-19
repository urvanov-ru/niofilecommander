

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

class FileInfo {
    private boolean isUpDirectory;
    private Path path;
    private String fileName;
    private FileType fileType;
    private boolean isSymbolicLink;
    private FileTime lastModifiedTime;
    private Long size;

    public FileInfo(Path path) throws IOException {
        this(path, Files.readAttributes(path, BasicFileAttributes.class,
                LinkOption.NOFOLLOW_LINKS));

    }

    public FileInfo(Path path, BasicFileAttributes attrs) {
        this.path = path;
        fileName = path.getFileName().toString();
        fileType = attrs.isDirectory() ? FileType.DIRECTORY : FileType.FILE;
        isSymbolicLink = attrs.isSymbolicLink();
        lastModifiedTime = attrs.lastModifiedTime();
        if (fileType == FileType.FILE)
            size = attrs.size();
    }

    public FileInfo() {

    }

    public boolean isUpDirectory() {
        return isUpDirectory;
    }

    public void setUpDirectory(boolean isUpDirectory) {
        this.isUpDirectory = isUpDirectory;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public boolean isSymbolicLink() {
        return isSymbolicLink;
    }

    public void setSymbolicLink(boolean isSymbolicLink) {
        this.isSymbolicLink = isSymbolicLink;
    }

    public FileTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(FileTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

}