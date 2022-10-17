package dev.webfx.demo.files;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Arrays;

import static dev.webfx.demo.files.Shared.ROOT_BACKGROUND_COLOR_LIGHTER;

/**
 * @author Bruno Salmon
 */
final class SegmentedButton<T> {

    private final static double segmentWidth = 40, height = 32;
    private final ButtonSegment<T>[] buttonSegments;
    private final BorderPane[] frames;
    private final HBox hBox;
    private final ObjectProperty<T> stateProperty = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            updateFrames();
        }
    };

    public SegmentedButton(ButtonSegment<T>... buttonSegments) {
        this(null, buttonSegments);
    }

    public SegmentedButton(T initialState, ButtonSegment<T>... buttonSegments) {
        this.buttonSegments = buttonSegments;
        frames = Arrays.stream(buttonSegments).map(this::createSegmentFrame).toArray(BorderPane[]::new);
        hBox = new HBox(frames);
        hBox.setAlignment(Pos.CENTER);
        setState(initialState);
    }

    public Node getView() {
        return hBox;
    }

    public Object getState() {
        return stateProperty.get();
    }

    public ObjectProperty<T> stateProperty() {
        return stateProperty;
    }

    public void setState(T state) {
        stateProperty.set(state);
    }

    private BorderPane createSegmentFrame(ButtonSegment<T> buttonSegment) {
        BorderPane frame = new BorderPane(buttonSegment.getGraphic());
        frame.setPrefWidth(segmentWidth);
        frame.setMaxHeight(height);
        frame.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, segmentRadii(buttonSegment), null, null)));
        if (buttonSegment != buttonSegments[0])
            frame.setTranslateX(-1);
        frame.setCursor(Cursor.HAND);
        frame.setOnMouseClicked(e -> {
            setState(buttonSegment.getState());
            if (buttonSegment.getActionHandler() != null)
                buttonSegment.getActionHandler().handle(e);
        });
        frame.setOnMousePressed(e -> updateFrames(buttonSegment.getState()));
        frame.setOnMouseReleased(e -> updateFrames());
        return frame;
    }

    private CornerRadii segmentRadii(ButtonSegment<T> buttonSegment) {
        boolean first = buttonSegment == buttonSegments[0];
        boolean last = buttonSegment == buttonSegments[buttonSegments.length - 1];
        return new CornerRadii(first ? 5 : 0, last ? 5 : 0, last ? 5 : 0, first ? 5 : 0, false);
    }

    private void updateFrames() {
        updateFrames(getState());
    }

    private void updateFrames(Object state) {
        for (int i = 0, n = buttonSegments.length; i < n; i++) {
            ButtonSegment<T> buttonSegment = buttonSegments[i];
            BorderPane frame = frames[i];
            if (state == null || !state.equals(buttonSegment.getState()))
                frame.setBackground(null);
            else
                frame.setBackground(new Background(new BackgroundFill(ROOT_BACKGROUND_COLOR_LIGHTER.brighter().brighter(), segmentRadii(buttonSegment), null)));
        }
    }
}
