package sample;

public class DocumentModel {
    private String fileName;
    private String path;

    public DocumentModel(String fileName, String path) {
        this.fileName = fileName;
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }
}
