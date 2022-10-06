package dev.webfx.demo.files;

import dev.webfx.extras.filepicker.FilePicker;
import dev.webfx.platform.file.File;
import dev.webfx.platform.util.Dates;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.value.WritableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

public class FilesApplication extends Application {

    private final static Interpolator EASE_OUT_INTERPOLATOR = Interpolator.SPLINE(0, .75, .25, 1);
    private final static Color ROOT_BACKGROUND_COLOR = Color.web("#0D1117");
    private final static ZoneOffset LOCAL_ZONE_OFFSET = ZoneId.systemDefault().getRules().getOffset(Instant.now());
    private final double MAX_SWIPE_TRANSLATE_X = 120;

    private final FilePicker filePicker = FilePicker.create();
    private final BorderPane root = new BorderPane();
    private final VBox filesVBox = new VBox();
    private final DropShadow dropShadow = new DropShadow(5, 0, 5, Color.CYAN);
    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage stage) {
        filePicker.setMultiple(true);
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M10 0l-5.2 4.9h3.3v5.1h3.8v-5.1h3.3l-5.2-4.9zm9.3 11.5l-3.2-2.1h-2l3.4 2.6h-3.5c-.1 0-.2.1-.2.1l-.8 2.3h-6l-.8-2.2c-.1-.1-.1-.2-.2-.2h-3.6l3.4-2.6h-2l-3.2 2.1c-.4.3-.7 1-.6 1.5l.6 3.1c.1.5.7.9 1.2.9h16.3c.6 0 1.1-.4 1.3-.9l.6-3.1c.1-.5-.2-1.2-.7-1.5z");
        svgPath.setFill(Color.WHITE);
        Text chooseText = createText("Choose files...", 24);
        HBox hBox = new HBox(10, svgPath, chooseText);
        hBox.setAlignment(Pos.CENTER);
        setBackgroundFill(hBox, Color.RED);
        hBox.setPadding(new Insets(10));
        hBox.setMaxWidth(250);
        hBox.setEffect(dropShadow);
        filePicker.setGraphic(hBox);
        root.setTop(filePicker.getView());
        BorderPane.setMargin(root.getTop(), new Insets(10));
/*
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        root.setCenter(textArea);
        filePicker.selectedFileProperty().addListener((observableValue, oldFile, file) -> {
            if (file != null)
                FileReader.create()
                        .readAsText(file)
                        .onSuccess(textArea::setText);
        });
*/
        filePicker.getSelectedFilesObservableList().addListener((InvalidationListener) observable -> populateFiles(filePicker.getSelectedFilesObservableList()));
        ScrollPane scrollPane = new ScrollPane(filesVBox);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setCenter(scrollPane);
        BorderPane.setMargin(scrollPane, new Insets(0, 0, 5, 10));
        setBackgroundFill(root, ROOT_BACKGROUND_COLOR);
        updateBorder(false);
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
        root.setOnDragOver(e -> {
            if (e.getDragboard().hasContent(DataFormat.FILES)) {
                e.acceptTransferModes(TransferMode.COPY);
                updateBorder(true);
            }
        });
        root.setOnDragExited(e -> updateBorder(false));
        root.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(DataFormat.FILES)) {
                Object platformFiles = db.getContent(DataFormat.FILES);
                populateFiles(File.createFileList(platformFiles));
                updateBorder(false);
            }
        });
    }

    private void updateBorder(boolean drag) {
        setBorderStroke(root, drag ? Color.RED : ROOT_BACKGROUND_COLOR);
    }

    private void populateFiles(List<File> files) {
        filesVBox.getChildren().addAll(files.stream().map(this::createFileView).collect(Collectors.toList()));
    }

    private Node createFileView(File file) {
        BorderPane borderPane = new BorderPane(new VBox(10,
                createText(file.getName(), 28),
                createText(readableFileSize(file.length()), 12),
                //createText(file.getURLPath(), 12),
                createText(Dates.format(LocalDateTime.ofEpochSecond(file.lastModified() / 1000, 0, LOCAL_ZONE_OFFSET), "dd/MM/yyyy HH:mm:ss"), 12)
                //createText(file.getMimeType(), 12)
        ));
        borderPane.getProperties().put("file", file);
        borderPane.setMinWidth(0);
        BorderPane.setAlignment(borderPane.getCenter(), Pos.CENTER_LEFT);
        BorderPane.setMargin(borderPane.getCenter(), new Insets(10));
        borderPane.setLeft(createFileIcon(file));
        //borderPane.setPadding(new Insets(10));
        setBackgroundFill(borderPane, Color.web("#161B22"));
        borderPane.setOnMousePressed(e -> startFileViewHorizontalSwipe(borderPane, e.getSceneX()));
        borderPane.setOnMouseDragged(e -> updateFileViewHorizontalSwipe(e.getSceneX()));
        borderPane.setOnMouseReleased(e -> stopFileViewHorizontalSwipe());
        borderPane.setOnTouchPressed(e -> startFileViewHorizontalSwipe(borderPane, e.getTouchPoint().getSceneX()));
        borderPane.setOnTouchMoved(e -> {
            updateFileViewHorizontalSwipe(e.getTouchPoint().getSceneX());
            e.consume();
        });
        borderPane.setOnTouchReleased(e -> stopFileViewHorizontalSwipe());
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(borderPane.widthProperty());
        clip.heightProperty().bind(borderPane.heightProperty());
        borderPane.setClip(clip);
        Pane underPane = new Pane();
        setBackgroundFill(underPane, LinearGradient.valueOf("to right, cyan, cyan 50%, red 50%, red"));
        StackPane stackPane = new StackPane(underPane, borderPane);
        stackPane.setCursor(Cursor.HAND);
        stackPane.setEffect(dropShadow);
        VBox.setMargin(stackPane, new Insets(5, 15,10 ,5));
        return stackPane;
    }

    private Canvas createFileIcon(File file) {
        Canvas canvas = new Canvas(100, 100);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.PURPLE);
        gc.fillRect(0, 0, 100, 100);
        return canvas;
    }

    private Node swipingFileView;
    private double swipingStartScreenX;
    private void startFileViewHorizontalSwipe(Node fileView, double x) {
        swipingFileView = fileView;
        swipingStartScreenX = x;
        if (mediaPlayer == null) {
            try {
                Media media = new Media("data:audio/mpeg;base64,SUQzBAAAAAABEVRYWFgAAAAtAAADY29tbWVudABCaWdTb3VuZEJhbmsuY29tIC8gTGFTb25vdGhlcXVlLm9yZwBURU5DAAAAHQAAA1N3aXRjaCBQbHVzIMKpIE5DSCBTb2Z0d2FyZQBUSVQyAAAABgAAAzIyMzUAVFNTRQAAAA8AAANMYXZmNTcuODMuMTAwAAAAAAAAAAAAAAD/80DEAAAAA0gAAAAATEFNRTMuMTAwVVVVVVVVVVVVVUxBTUUzLjEwMFVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVf/zQsRbAAADSAAAAABVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVf/zQMSkAAADSAAAAABVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
            } catch (Exception e) { }
        }
    }

    private void updateFileViewHorizontalSwipe(double x) {
        if (swipingFileView != null) {
            double translateX = x - swipingStartScreenX;
            translateX = Math.min(MAX_SWIPE_TRANSLATE_X, Math.max(-MAX_SWIPE_TRANSLATE_X, translateX));
            swipingFileView.setTranslateX(translateX);
        }
    }

    private void stopFileViewHorizontalSwipe() {
        if (swipingFileView != null) {
            double swipeTranslateX = swipingFileView.getTranslateX();
            if (swipeTranslateX == MAX_SWIPE_TRANSLATE_X)
                runFileAction((File) swipingFileView.getProperties().get("file"));
            if (swipeTranslateX != -MAX_SWIPE_TRANSLATE_X)
                animateProperty(swipingFileView.translateXProperty(), 0);
            else {
                StackPane stackPane = (StackPane) swipingFileView.getParent();
                stackPane.setMinHeight(0);
                stackPane.setPrefHeight(stackPane.getHeight());
                animateProperty(stackPane.prefHeightProperty(), 0)
                        .setOnFinished(e2 -> filesVBox.getChildren().remove(stackPane));
            };
            swipingFileView = null;
        }
    }

    public static <T> Timeline animateProperty(WritableValue<T> target, T finalValue) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), new KeyValue(target, finalValue, EASE_OUT_INTERPOLATOR)));
        timeline.play();
        return timeline;
    }

    private void runFileAction(File file) {
        String mimeType = file.getMimeType();
        if (mimeType != null && (mimeType.startsWith("audio/") || mimeType.startsWith("video/"))) {
            if (mediaPlayer != null)
                mediaPlayer.stop();
            String fileSource = file.getURLPath();
            if (mediaPlayer != null && mediaPlayer.getMedia().getSource().equals(fileSource))
                mediaPlayer = null;
            else {
                mediaPlayer = new MediaPlayer(new Media(fileSource));
                mediaPlayer.play();
            }
        }
    }

    private static Text createText(String content, double size) {
        Text text = new Text(content);
        text.setFill(Color.WHITE);
        text.setFont(Font.font(size));
        return text;
    }

    private static String readableFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        double value = size / Math.pow(1024, digitGroups);
        int intValue = (int) value, decValue = ((int) (value * 10)) % 10;
        return intValue + (decValue == 0 ? "" : "." + decValue) + " " + units[digitGroups];
    }

    private static void setBackgroundFill(Region region, Paint fill) {
        region.setBackground(new Background(new BackgroundFill(fill, null, null)));
    }

    private static void setBorderStroke(Region region, Paint stroke) {
        region.setBorder(new Border(new BorderStroke(stroke, BorderStrokeStyle.SOLID, null, BorderStroke.THICK)));
    }
}