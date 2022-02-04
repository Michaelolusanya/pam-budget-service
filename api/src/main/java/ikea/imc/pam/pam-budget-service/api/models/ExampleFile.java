package org.imc.pam.boilerplate.api.models;

import io.swagger.annotations.ApiModel;
import java.time.LocalDateTime;

@ApiModel(description = "ExampleFileModel")
public class ExampleFile {

    private String fileName;
    private String filePath;
    private String mimeType;
    private LocalDateTime created;
    private LocalDateTime changed;
    private int sizeInKb;

    public ExampleFile(
            String fileName,
            String filePath,
            String mimeType,
            LocalDateTime created,
            LocalDateTime changed,
            int sizeInKb) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.mimeType = mimeType;
        this.created = created;
        this.changed = changed;
        this.sizeInKb = sizeInKb;
    }

    public ExampleFile() {}

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(LocalDateTime localDateTime) {
        this.created = localDateTime;
    }

    public LocalDateTime getChanged() {
        return this.changed;
    }

    public void setChanged(LocalDateTime localDateTime) {
        this.changed = localDateTime;
    }

    public int getSizeInKb() {
        return this.sizeInKb;
    }

    public void setSizeInKb(long l) {
        this.sizeInKb = (int) l;
    }
}
