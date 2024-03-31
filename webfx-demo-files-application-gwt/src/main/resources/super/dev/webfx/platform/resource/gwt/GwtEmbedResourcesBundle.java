// File managed by WebFX (DO NOT EDIT MANUALLY)
package dev.webfx.platform.resource.gwt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import dev.webfx.platform.resource.spi.impl.gwt.GwtResourceBundleBase;

public interface GwtEmbedResourcesBundle extends ClientBundle {

    GwtEmbedResourcesBundle R = GWT.create(GwtEmbedResourcesBundle.class);
    @Source("com/chrisnewland/demofx/text/greetings.txt")
    TextResource r1();

    @Source("dev/webfx/platform/meta/exe/exe.properties")
    TextResource r2();



    final class ProvidedGwtResourceBundle extends GwtResourceBundleBase {
        public ProvidedGwtResourceBundle() {
            registerResource("com/chrisnewland/demofx/text/greetings.txt", R.r1());
            registerResource("dev/webfx/platform/meta/exe/exe.properties", R.r2());

        }
    }
}
