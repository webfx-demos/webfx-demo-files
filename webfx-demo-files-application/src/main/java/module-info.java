// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.demo.files.application {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires webfx.extras.filepicker;
    requires webfx.platform.async;
    requires webfx.platform.file;
    requires webfx.platform.util;

    // Exported packages
    exports dev.webfx.demo.files;

    // Provided services
    provides javafx.application.Application with dev.webfx.demo.files.FilesApplication;

}