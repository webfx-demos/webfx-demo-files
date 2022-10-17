package dev.webfx.demo.files;

/**
 * @author Bruno Salmon
 */
enum FileType {
    TEXT ("text/"),
    IMAGE ("image/"),
    AUDIO ("audio/"),
    VIDEO ("video/"),
    OTHER (null);

    private final String mimePrefix;

    FileType(String mimePrefix) {
        this.mimePrefix = mimePrefix;
    }

    static FileType fromMimeType(String mimeType) {
        if (mimeType != null)
            for (FileType fileType : FileType.values())
                if (fileType != OTHER && mimeType.startsWith(fileType.mimePrefix))
                    return fileType;
        return OTHER;
    }
}
