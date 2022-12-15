package org.openxava.util;
import java.io.*;

/**
 * Serializable FileItem to use as alternative to FileItem from Apache Commons FileUpload.
 * 
 * @author Harsha Mahaswami
 * @since 7.0.4
 */
public class XFileItem implements Serializable {
    private String fileName;

    private byte[] bytes;

    private String string;

    public XFileItem() {
    }

    public XFileItem(String fileName, byte[] bytes, String string) {
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