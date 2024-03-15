// File managed by WebFX (DO NOT EDIT MANUALLY)
package java.util;

import java.util.Iterator;
import java.util.logging.Logger;
import dev.webfx.platform.util.function.Factory;

public class ServiceLoader<S> implements Iterable<S> {

    public static <S> ServiceLoader<S> load(Class<S> serviceClass) {
        switch (serviceClass.getName()) {
            case "dev.webfx.extras.filepicker.spi.FilePickerProvider": return new ServiceLoader<S>(dev.webfx.extras.filepicker.spi.impl.gwt.GwtFilePickerProvider::new);
            case "dev.webfx.kit.launcher.spi.WebFxKitLauncherProvider": return new ServiceLoader<S>(dev.webfx.kit.launcher.spi.impl.gwtj2cl.GwtJ2clWebFxKitLauncherProvider::new);
            case "dev.webfx.kit.mapper.peers.javafxmedia.spi.WebFxKitMediaMapperProvider": return new ServiceLoader<S>(dev.webfx.kit.mapper.peers.javafxmedia.spi.gwtj2cl.GwtJ2clWebFxKitMediaMapperProvider::new);
            case "dev.webfx.kit.mapper.spi.WebFxKitMapperProvider": return new ServiceLoader<S>(dev.webfx.kit.mapper.spi.impl.gwtj2cl.GwtJ2clWebFxKitHtmlMapperProvider::new);
            case "dev.webfx.platform.blob.spi.BlobProvider": return new ServiceLoader<S>(dev.webfx.platform.blob.spi.impl.gwtj2cl.GwtJ2clBlobProvider::new);
            case "dev.webfx.platform.boot.spi.ApplicationBooterProvider": return new ServiceLoader<S>(dev.webfx.platform.boot.spi.impl.gwt.GwtApplicationBooterProvider::new);
            case "dev.webfx.platform.boot.spi.ApplicationJob": return new ServiceLoader<S>();
            case "dev.webfx.platform.boot.spi.ApplicationModuleBooter": return new ServiceLoader<S>(dev.webfx.kit.launcher.WebFxKitLauncherModuleBooter::new, dev.webfx.platform.boot.spi.impl.ApplicationJobsBooter::new, dev.webfx.platform.resource.spi.impl.gwt.GwtResourceModuleBooter::new);
            case "dev.webfx.platform.console.spi.ConsoleProvider": return new ServiceLoader<S>(dev.webfx.platform.console.spi.impl.gwtj2cl.GwtJ2clConsoleProvider::new);
            case "dev.webfx.platform.file.spi.FileProvider": return new ServiceLoader<S>(dev.webfx.platform.file.spi.impl.gwtj2cl.GwtJ2clFileProvider::new);
            case "dev.webfx.platform.resource.spi.ResourceProvider": return new ServiceLoader<S>(dev.webfx.platform.resource.spi.impl.gwt.GwtResourceProvider::new);
            case "dev.webfx.platform.resource.spi.impl.gwt.GwtResourceBundle": return new ServiceLoader<S>(webfx.demo.files.application.gwt.embed.EmbedResourcesBundle.ProvidedGwtResourceBundle::new);
            case "dev.webfx.platform.scheduler.spi.SchedulerProvider": return new ServiceLoader<S>(dev.webfx.platform.uischeduler.spi.impl.gwtj2cl.GwtJ2clUiSchedulerProvider::new);
            case "dev.webfx.platform.shutdown.spi.ShutdownProvider": return new ServiceLoader<S>(dev.webfx.platform.shutdown.spi.impl.gwtj2cl.GwtJ2clShutdownProvider::new);
            case "dev.webfx.platform.uischeduler.spi.UiSchedulerProvider": return new ServiceLoader<S>(dev.webfx.platform.uischeduler.spi.impl.gwtj2cl.GwtJ2clUiSchedulerProvider::new);
            case "dev.webfx.platform.useragent.spi.UserAgentProvider": return new ServiceLoader<S>(dev.webfx.platform.useragent.spi.impl.gwtj2cl.GwtJ2clUserAgentProvider::new);
            case "javafx.application.Application": return new ServiceLoader<S>(dev.webfx.demo.files.FilesApplication::new);

            // UNKNOWN SPI
            default:
                Logger.getLogger(ServiceLoader.class.getName()).warning("Unknown " + serviceClass + " SPI - returning no provider");
                return new ServiceLoader<S>();
        }
    }

    private final Factory[] factories;

    public ServiceLoader(Factory... factories) {
        this.factories = factories;
    }

    public Iterator<S> iterator() {
        return new Iterator<S>() {
            int index = 0;
            @Override
            public boolean hasNext() {
                return index < factories.length;
            }

            @Override
            public S next() {
                return (S) factories[index++].create();
            }
        };
    }
}