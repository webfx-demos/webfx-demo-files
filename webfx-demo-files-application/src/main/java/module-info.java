// File managed by WebFX (DO NOT EDIT MANUALLY)

module webfx.demo.files.application {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires webfx.extras.filepicker;
    requires webfx.extras.panes;
    requires webfx.kit.util;
    requires webfx.lib.demofx;
    requires webfx.platform.blob;
    requires webfx.platform.file;
    requires webfx.platform.uischeduler;
    requires webfx.platform.util;
    requires webfx.platform.util.time;

    // Exported packages
    exports dev.webfx.demo.files;

    // Resources packages
    opens dev.webfx.demo.files;

    // Provided services
    provides javafx.application.Application with dev.webfx.demo.files.FilesApplication;

}