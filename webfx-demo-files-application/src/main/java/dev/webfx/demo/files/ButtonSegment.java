package dev.webfx.demo.files;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;

/**
 * @author Bruno Salmon
 */
final class ButtonSegment<T> {
    private final SVGPath graphic;
    private final T state;
    private final EventHandler<? super MouseEvent> actionHandler;

    public ButtonSegment(T state, EventHandler<? super MouseEvent> actionHandler, SVGPath graphic) {
        this.graphic = graphic;
        this.state = state;
        this.actionHandler = actionHandler;
    }

    public ButtonSegment(T state, EventHandler<? super MouseEvent> actionHandler, String svgPath) {
        this(state, actionHandler, Shared.createWhiteSVGPath(svgPath));
    }

    public SVGPath getGraphic() {
        return graphic;
    }

    public T getState() {
        return state;
    }

    public EventHandler<? super MouseEvent> getActionHandler() {
        return actionHandler;
    }
}
