package dev.webfx.platform.boot.j2cl;

import dev.webfx.platform.boot.ApplicationBooter;
import dev.webfx.platform.boot.spi.ApplicationBooterProvider;
import org.treblereel.j2cl.processors.annotations.GWT3EntryPoint;

import static dev.webfx.platform.service.gwtj2cl.ServiceRegistry.*;

public final class J2clEntryPoint implements ApplicationBooterProvider {

    @GWT3EntryPoint
    public void entryPoint() {
        registerArrayConstructors();
        registerServiceProviders();
        ApplicationBooter.start(this, null);
    }

    public static void registerArrayConstructors() {

    }

    public static void registerServiceProviders() {
        register(dev.webfx.extras.filepicker.spi.FilePickerProvider.class, dev.webfx.extras.filepicker.spi.impl.gwtj2cl.GwtJ2clFilePickerProvider::new);
        register(dev.webfx.kit.launcher.spi.WebFxKitLauncherProvider.class, dev.webfx.kit.launcher.spi.impl.gwtj2cl.GwtJ2clWebFxKitLauncherProvider::new);
        register(dev.webfx.kit.mapper.peers.javafxmedia.spi.WebFxKitMediaMapperProvider.class, dev.webfx.kit.mapper.peers.javafxmedia.spi.gwtj2cl.GwtJ2clWebFxKitMediaMapperProvider::new);
        register(dev.webfx.kit.mapper.spi.WebFxKitMapperProvider.class, dev.webfx.kit.mapper.spi.impl.gwtj2cl.GwtJ2clWebFxKitHtmlMapperProvider::new);
        register(dev.webfx.platform.blob.spi.BlobProvider.class, dev.webfx.platform.blob.spi.impl.gwtj2cl.GwtJ2clBlobProvider::new);
        register(dev.webfx.platform.boot.spi.ApplicationModuleBooter.class, dev.webfx.kit.launcher.WebFxKitLauncherModuleBooter::new, dev.webfx.platform.boot.spi.impl.ApplicationJobsInitializer::new, dev.webfx.platform.boot.spi.impl.ApplicationJobsStarter::new, dev.webfx.platform.resource.spi.impl.j2cl.J2clResourceModuleBooter::new);
        register(dev.webfx.platform.console.spi.ConsoleProvider.class, dev.webfx.platform.console.spi.impl.gwtj2cl.GwtJ2clConsoleProvider::new);
        register(dev.webfx.platform.file.spi.FileProvider.class, dev.webfx.platform.file.spi.impl.gwtj2cl.GwtJ2clFileProvider::new);
        register(dev.webfx.platform.resource.spi.ResourceProvider.class, dev.webfx.platform.resource.spi.impl.j2cl.J2clResourceProvider::new);
        register(dev.webfx.platform.resource.spi.impl.j2cl.J2clResourceBundle.class, dev.webfx.platform.resource.j2cl.J2clEmbedResourcesBundle.ProvidedJ2clResourceBundle::new);
        register(dev.webfx.platform.scheduler.spi.SchedulerProvider.class, dev.webfx.platform.uischeduler.spi.impl.gwtj2cl.GwtJ2clUiSchedulerProvider::new);
        register(dev.webfx.platform.shutdown.spi.ShutdownProvider.class, dev.webfx.platform.shutdown.spi.impl.gwtj2cl.GwtJ2clShutdownProvider::new);
        register(dev.webfx.platform.uischeduler.spi.UiSchedulerProvider.class, dev.webfx.platform.uischeduler.spi.impl.gwtj2cl.GwtJ2clUiSchedulerProvider::new);
        register(dev.webfx.platform.useragent.spi.UserAgentProvider.class, dev.webfx.platform.useragent.spi.impl.gwtj2cl.GwtJ2clUserAgentProvider::new);
        register(javafx.application.Application.class, dev.webfx.demo.files.FilesApplication::new);
    }
}