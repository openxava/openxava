package org.openxava.actions;
import java.io.Serializable;

public class CustomFileItem implements Serializable {
    private String fileName;

    private byte[] bytes;

    private String string;

    public CustomFileItem() {
    }

    public CustomFileItem(String fileName, byte[] bytes, String string) {
        this.fileName = fileName;
        this.bytes = bytes;
        this.string = string;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}