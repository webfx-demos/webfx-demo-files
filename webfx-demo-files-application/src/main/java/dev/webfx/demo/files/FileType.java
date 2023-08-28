package dev.webfx.demo.files;

/**
 * @author Bruno Salmon
 */
enum FileType {
    TEXT ("text/*", "application/json", "application/xml", "application/rtf", "application/x-sh", "application/xhtml+xml", "application/vnd.mozilla.xul+xml"),
    IMAGE ("image/*"),
    AUDIO ("audio/*"),
    VIDEO ("video/*"),
    OTHER ();

    private final String[] mimePatterns;

    FileType(String... mimePatterns) {
        this.mimePatterns = mimePatterns;
    }

    static FileType fromMimeType(String mimeType) {
        if (mimeType != null)
            for (FileType fileType : FileType.values()) {
                for (String mimePattern : fileType.mimePatterns) {
                    if (mimeType.equals(mimePattern) || mimePattern.endsWith("*") && mimeType.startsWith(mimePattern.replace("*", "")))
                        return fileType;
                }
            }
        return OTHER;
    }
}
