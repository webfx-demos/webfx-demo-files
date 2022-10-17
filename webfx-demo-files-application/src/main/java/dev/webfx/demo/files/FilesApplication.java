package dev.webfx.demo.files;

import com.chrisnewland.demofx.DemoConfig;
import com.chrisnewland.demofx.DemoFX;
import com.chrisnewland.demofx.effect.effectfactory.IEffectFactory;
import com.chrisnewland.demofx.effect.spectral.Equaliser;
import dev.webfx.extras.filepicker.FilePicker;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.ObservableLists;
import dev.webfx.platform.file.File;
import dev.webfx.platform.file.FileReader;
import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.platform.util.Dates;
import dev.webfx.platform.util.collection.Collections;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FilesApplication extends Application {
    private final static Color ROOT_BACKGROUND_COLOR = Color.web("#0D1117");
    private final FilePicker filePicker = FilePicker.create();
    private final VBox fileTilesListView = new VBox();
    private final FlowPane fileTilesGridView = new FlowPane();
    private final ScrollPane fileTilesListScrollPane = createFileTilesScrollPane(fileTilesListView);
    private final ScrollPane fileTilesGridScrollPane = createFileTilesScrollPane(fileTilesGridView);
    private final BorderPane mainContainer = new BorderPane();
    private final BooleanProperty showFileTilesListViewProperty = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            mainContainer.setCenter(get() ? fileTilesListScrollPane : fileTilesGridScrollPane);
        }
    };
    private final StackPane root = new StackPane(mainContainer);
    private final Scene scene = new Scene(root, 800, 600);
    private final DropShadow dropShadow = new DropShadow(5, 0, 5, Color.CYAN);

    @Override
    public void start(Stage stage) {
        filePicker.setGraphic(createFilePickerGraphic());
        filePicker.setMultiple(true);
        filePicker.getSelectedFilesObservableList().addListener((InvalidationListener) observable ->
                populateFiles(filePicker.getSelectedFilesObservableList()));
        ToggleButton toggleButton = new ToggleButton("List");
        toggleButton.setSelected(true);
        showFileTilesListViewProperty.bind(toggleButton.selectedProperty());
        Button ascSortButton = new Button("A");
        ascSortButton.setOnAction(e -> fileInfos.sort(Comparator.comparing(fi -> fi.file.getName())));
        Button descSortButton = new Button("D");
        descSortButton.setOnAction(e -> fileInfos.sort((fi1, fi2) -> fi2.file.getName().compareTo(fi1.file.getName())));
        mainContainer.setTop(new HBox(10, filePicker.getView(), toggleButton, ascSortButton, descSortButton));
        BorderPane.setMargin(mainContainer.getTop(), new Insets(10));
        setBackgroundFill(mainContainer, ROOT_BACKGROUND_COLOR);
        updateBorder(false);
        root.getChildren().setAll(mainContainer);
        stage.setScene(scene);
        stage.show();
        mainContainer.setOnDragOver(e -> {
            if (e.getDragboard().hasContent(DataFormat.FILES)) {
                e.acceptTransferModes(TransferMode.COPY);
                updateBorder(true);
            }
        });
        mainContainer.setOnDragExited(e -> updateBorder(false));
        mainContainer.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(DataFormat.FILES)) {
                mainContainer.setCursor(Cursor.WAIT);
                Object platformFiles = db.getContent(DataFormat.FILES);
                UiScheduler.scheduleInAnimationFrame (() -> {
                    populateFiles(File.createFileList(platformFiles));
                    updateBorder(false);
                    mainContainer.setCursor(Cursor.DEFAULT);
                }, 5);
            }
        });
    }

    private static ScrollPane createFileTilesScrollPane(Node fileTilesView) {
        ScrollPane fileTilesScrollPane = new ScrollPane(fileTilesView);
        fileTilesScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent");
        fileTilesScrollPane.setFitToWidth(true);
        fileTilesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        BorderPane.setMargin(fileTilesScrollPane, new Insets(0, 0, 5, 10));
        return fileTilesScrollPane;
    }

    private Node createFilePickerGraphic() {
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
        return hBox;
    }

    private void updateBorder(boolean drag) {
        setBorderStroke(mainContainer, drag ? Color.RED : ROOT_BACKGROUND_COLOR);
    }

    private void populateFiles(List<File> files) {
        fileInfos.addAll(files.stream().map(FileInfo::new).collect(Collectors.toList()));
    }

    private long openTime;

    private void openFileView(Node fileView, boolean closeOnMouseClick, Runnable onClose) {
        BorderPane borderPane = new BorderPane(fileView);
        setBackgroundFill(borderPane, ROOT_BACKGROUND_COLOR);
        borderPane.setCursor(Cursor.HAND);
        borderPane.getProperties().put("onClose", onClose);
        if (closeOnMouseClick)
            borderPane.setOnMouseClicked(e -> closeFileView());
        root.getChildren().add(borderPane);
        openTime = System.currentTimeMillis();
    }

    private void closeFileView() {
        if (System.currentTimeMillis() - openTime < 300)
            return;
        ObservableList<Node> rootChildren = root.getChildren();
        while (rootChildren.size() > 1) {
            Node toRemove = rootChildren.get(rootChildren.size() - 1);
            if (rootChildren.remove(toRemove)) {
                Runnable onClose = (Runnable) toRemove.getProperties().get("onClose");
                if (onClose != null)
                    onClose.run();
            }
        }
    }

    private final static ZoneOffset LOCAL_ZONE_OFFSET = ZoneId.systemDefault().getRules().getOffset(Instant.now());
    private final Image quaver2 = new Image("dev/webfx/demo/files/quaver2.png", true);

    private final ObservableList<FileInfo> fileInfos = FXCollections.observableArrayList();
    {
        ObservableLists.bindConverted(fileTilesListView.getChildren(), fileInfos, FileInfo::getFileListTile);
        ObservableLists.bindConverted(fileTilesGridView.getChildren(), fileInfos, FileInfo::getFileGridTile);
    }

    enum FileType { TEXT, IMAGE, AUDIO, VIDEO, OTHER }

    class FileInfo {
        private final File file;
        private final FileType fileType;

        public FileInfo(File file) {
            this.file = file;
            String mimeType = file.getMimeType();
            if (mimeType == null)
                fileType = FileType.OTHER;
            else if (mimeType.startsWith("text/"))
                fileType = FileType.TEXT;
            else if (mimeType.startsWith("image/"))
                fileType = FileType.IMAGE;
            else if (mimeType.startsWith("audio/"))
                fileType = FileType.AUDIO;
            else if (mimeType.startsWith("video/"))
                fileType = FileType.VIDEO;
            else
                fileType = FileType.OTHER;
        }

        private BorderPane fileGridTile;

        private Node getFileGridTile() {
            if (fileGridTile == null) {
                fileGridTile = new BorderPane(createFileIcon());
                BorderPane.setAlignment(fileGridTile.getCenter(), Pos.CENTER_LEFT);
                BorderPane.setMargin(fileGridTile.getCenter(), new Insets(10));
                fileGridTile.setCursor(Cursor.HAND);
                fileGridTile.setOnMouseClicked(e -> openFileAction());
            }
            return fileGridTile;
        }

        private Pane fileListTile;
        private Node getFileListTile() {
            if (fileListTile == null) {
                BorderPane borderPane = new BorderPane(new VBox(10,
                        createText(file.getName(), 28),
                        createText(readableFileSize(file.length()), 12),
                        createText(Dates.format(LocalDateTime.ofEpochSecond(file.lastModified() / 1000, 0, LOCAL_ZONE_OFFSET), "dd/MM/yyyy HH:mm:ss"), 12)
                ));
                borderPane.setMinWidth(0);
                BorderPane.setAlignment(borderPane.getCenter(), Pos.CENTER_LEFT);
                BorderPane.setMargin(borderPane.getCenter(), new Insets(10));
                borderPane.setLeft(createFileIcon());
                BorderPane.setAlignment(borderPane.getLeft(), Pos.CENTER);
                setBackgroundFill(borderPane, Color.web("#161B22"));
                borderPane.setOnMouseClicked(e -> runTileClickAction());
                Rectangle clip = new Rectangle();
                clip.widthProperty().bind(borderPane.widthProperty());
                clip.heightProperty().bind(borderPane.heightProperty());
                borderPane.setClip(clip);
                fileListTile = new BorderPane(borderPane); // Embedding it so the clip doesn't prevent the shadow effect
                fileListTile.setEffect(dropShadow);
                fileListTile.setCursor(Cursor.HAND);
                VBox.setMargin(fileListTile, new Insets(5, 15, 10, 5));
            }
            return fileListTile;
        }

        private void runTileClickAction() {
            openFileAction();
        }

        private void openFileAction() {
            switch (fileType) {
                case TEXT:  openTextFile();  break;
                case IMAGE: openImageFile(); break;
                case AUDIO: openAudioFile(); break;
                case VIDEO: openVideoFile(); break;
            }
        }

        private void openTextFile() {
            TextArea textArea = new TextArea();
            FileReader.create()
                    .readAsText(file)
                    .onSuccess(textArea::setText);
            BorderPane borderPane = new BorderPane(textArea);
            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> closeFileView());
            HBox buttonBar = new HBox(10, closeButton);
            buttonBar.setAlignment(Pos.CENTER);
            buttonBar.setPadding(new Insets(10));
            borderPane.setBottom(buttonBar);
            openFileView(borderPane, false, null);
        }

        private void openImageFile() {
            openFileView(getFullSizeImageView(), true, null);
        }

        private void openAudioFile() {
            DemoConfig demoConfig = new DemoConfig(scene.getWidth(), scene.getHeight());
            demoConfig.setAudioFilename(file.getURLPath());
            DemoFX equaliserDemoFX = new DemoFX(demoConfig, (IEffectFactory) config -> Collections.listOf(new Equaliser(config)));
            BorderPane equaliserPane = equaliserDemoFX.getPane();
            openFileView(equaliserPane, true, equaliserDemoFX::stopDemo);
            /*equaliserDemoFX.setOnCompleted(() -> {
                if (equaliserDemoFX.isRunning())
                    openNextSameTypeFile(true);
            });*/
            equaliserDemoFX.runDemo();
            equaliserPane.setOnSwipeLeft( e -> openNextSameTypeFile(true)); // Right to Left
            equaliserPane.setOnSwipeRight(e -> openNextSameTypeFile(false)); // Left to Right

        }

        private void openVideoFile() {
            openAudioFile();
        }

        private Image fileImage;
        double naturalFileImageWidth, naturalFileImageHeight;

        private void onFileImageLoaded(Runnable onLoaded) {
            if (fileImage == null)
                fileImage = new Image(file.getURLPath(), true);
            FXProperties.runNowAndOnPropertiesChange(() -> {
                if (fileImage.getProgress() >= 1) {
                    if (naturalFileImageWidth == 0)
                        naturalFileImageWidth = fileImage.getWidth();
                    if (naturalFileImageHeight == 0)
                        naturalFileImageHeight = fileImage.getHeight();
                    if (onLoaded != null)
                        onLoaded.run();
                }
            }, fileImage.progressProperty());
        }

        private Point2D bestImageFitDimension(double containerWidth, double containerHeight) {
            if (naturalFileImageWidth > 0 && naturalFileImageHeight > 0 & containerWidth > 0 && containerHeight > 0) {
                boolean isSvgImage = file.getName().toLowerCase().endsWith(".svg");
                double imageViewMaxWidth  = Math.min(isSvgImage ? Double.MAX_VALUE : naturalFileImageWidth,  containerWidth);
                double imageViewMaxHeight = Math.min(isSvgImage ? Double.MAX_VALUE : naturalFileImageHeight, containerHeight);
                double bestFitWidth = Math.min(imageViewMaxWidth, imageViewMaxHeight * naturalFileImageWidth / naturalFileImageHeight);
                double bestFitHeight = Math.min(imageViewMaxWidth * naturalFileImageHeight / naturalFileImageWidth, imageViewMaxHeight);
                return new Point2D(bestFitWidth, bestFitHeight);
            }
            return null;
        }

        private BorderPane fullSizeImageView;

        private BorderPane getFullSizeImageView() {
            if (fullSizeImageView == null) {
                ImageView imageView = new ImageView();
                fullSizeImageView = new BorderPane(imageView);
                imageView.setPreserveRatio(true);
                fullSizeImageView.setMinWidth(0);
                fullSizeImageView.setMinHeight(0);
                onFileImageLoaded(() -> FXProperties.runOnPropertiesChange(() -> {
                    Point2D dimension = bestImageFitDimension(fullSizeImageView.getWidth(), fullSizeImageView.getHeight());
                    if (dimension != null) {
                        imageView.setFitWidth(dimension.getX());
                        imageView.setFitHeight(dimension.getY());
                    }
                }, fileImage.widthProperty(), fileImage.heightProperty(), fullSizeImageView.widthProperty(), fullSizeImageView.heightProperty()));
                imageView.setImage(fileImage);
                fullSizeImageView.setOnSwipeLeft( e -> openNextSameTypeFile(true)); // Right to Left
                fullSizeImageView.setOnSwipeRight(e -> openNextSameTypeFile(false)); // Left to Right
            }
            return fullSizeImageView;
        }

        private void openNextSameTypeFile(boolean forward) {
            int fileIndex = fileInfos.indexOf(this);
            while (true) {
                if (forward) {
                    if (++fileIndex >= fileInfos.size())
                        fileIndex = 0;
                } else {
                    if (--fileIndex < 0)
                        fileIndex = fileInfos.size() - 1;
                }
                FileInfo fileInfo = fileInfos.get(fileIndex);
                if (fileType == fileInfo.fileType) {
                    if (fileInfo != this) {
                        closeFileView();
                        fileInfo.openFileAction();
                    }
                    break;
                }
            }
        }

        private Node createFileIcon() {
            Canvas canvas = new Canvas(100, 100);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.save();
            boolean mediaFile = fileType == FileType.AUDIO || fileType == FileType.VIDEO;
            if (mediaFile)
                gc.drawImage(quaver2, 50 - quaver2.getWidth() / 2, 50 - quaver2.getHeight() / 2);
            else {
                String name = file.getName();
                int p = name.lastIndexOf('.');
                String extension = p < 0 ? null : name.substring(p + 1).toUpperCase();
                double extensionBaseline = 60;
                // Making a clip to exclude the right top corner
                gc.moveTo(0, 0);
                gc.lineTo(65, 0);
                gc.lineTo(100, 35);
                gc.lineTo(100, 100);
                gc.lineTo(0, 100);
                gc.clip();
                if (extension != null && !mediaFile) {
                    // Drawing the red triangle to create a folding effect behind the red rectangle
                    gc.setFill(Color.RED);
                    gc.fillPolygon(new double[]{0, 50, 100}, new double[]{extensionBaseline - 20, 10, extensionBaseline - 20}, 3);
                }
                // Drawing the document frame
                gc.setFill(Color.web("#c7a575")); // Old paper color
                gc.fillRect(10, 2, 80, 96); // Filling
                gc.setStroke(Color.WHITE); // White border
                gc.setLineWidth(5);
                gc.strokeRect(10, 2, 80, 96);
                if (extension != null && !mediaFile) {
                    // Drawing the red rectangle
                    gc.setFill(Color.RED);
                    gc.fillRect(0, extensionBaseline - 20, 100, 40);
                    gc.fillRect(0, extensionBaseline - 20, 100, 40);
                    // Drawing the extension in the middle of the red rectangle with a black drop shadow
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.setTextBaseline(VPos.CENTER);
                    gc.setFont(Font.font(28));
                    gc.setFill(Color.WHITE);
                    gc.setEffect(new DropShadow(5, 5, 5, Color.BLACK));
                    gc.fillText(extension, 50, extensionBaseline, 100);
                }
                // Drawing the white text lines with a gray drop shadow
                gc.setEffect(new DropShadow(5, 0, 5, Color.GRAY));
                gc.setFill(Color.WHITE);
                if (mediaFile)
                    gc.drawImage(quaver2, 50 - quaver2.getWidth() / 2, 50 - quaver2.getHeight() / 2);
                else if (extension != null) {
                    gc.fillRect(25, 20, 50, 5); // line 1
                    gc.fillRect(25, 30, 50, 5); // line 2
                    gc.fillRect(25, 83, 50, 5); // line 3
                }
                // Drawing the white top right triangle with the same gray drop shadow
                gc.fillRect(65, 2, 25, 25); // the rectangle is clipped so this finally draws a triangle
            }
            gc.restore();
            if (fileType == FileType.IMAGE) {
                onFileImageLoaded(() -> {
                    Point2D dimension = bestImageFitDimension(100, 100);
                    if (dimension != null) {
                        double width = dimension.getX();
                        double height = dimension.getY();
                        gc.clearRect(0, 0, 100, 100);
                        gc.drawImage(fileImage, 50 - width / 2, 50 - height / 2, width, height);
                    }
                });
            }
            return canvas;
        }

        private String readableFileSize(long size) {
            if (size <= 0)
                return "0";
            final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
            int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
            double value = size / Math.pow(1024, digitGroups);
            int intValue = (int) value, decValue = ((int) (value * 10)) % 10;
            return intValue + (decValue == 0 ? "" : "." + decValue) + " " + units[digitGroups];
        }
    }

    /*******************************************************************************************************************
     Graphical utility methods
     ******************************************************************************************************************/

    private static void setBackgroundFill(Region region, Paint fill) {
        region.setBackground(new Background(new BackgroundFill(fill, null, null)));
    }

    private static void setBorderStroke(Region region, Paint stroke) {
        region.setBorder(new Border(new BorderStroke(stroke, BorderStrokeStyle.SOLID, null, BorderStroke.THICK)));
    }

    private static Text createText(String content, double size) {
        Text text = new Text(content);
        text.setFill(Color.WHITE);
        text.setFont(Font.font(size));
        return text;
    }
}