package dev.webfx.demo.files;

import com.chrisnewland.demofx.DemoConfig;
import com.chrisnewland.demofx.DemoFX;
import com.chrisnewland.demofx.effect.effectfactory.IEffectFactory;
import com.chrisnewland.demofx.effect.spectral.Equaliser;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.kit.util.properties.ObservableLists;
import dev.webfx.platform.file.File;
import dev.webfx.platform.file.FileReader;
import dev.webfx.platform.util.Dates;
import dev.webfx.platform.util.collection.Collections;
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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static dev.webfx.demo.files.Shared.*;

/**
 * @author Bruno Salmon
 */
public class FilesView {

    private final static ZoneOffset LOCAL_ZONE_OFFSET = ZoneId.systemDefault().getRules().getOffset(Instant.now());
    private final static Image quaver2 = new Image("dev/webfx/demo/files/quaver2.png", true);

    private final BorderPane mainContainer = new BorderPane();
    private final VBox fileTilesListView = new VBox();
    private final FlowPane fileTilesGridView = new FlowPane();
    private final ScrollPane fileTilesListScrollPane = createFileTilesScrollPane(fileTilesListView);
    private final ScrollPane fileTilesGridScrollPane = createFileTilesScrollPane(fileTilesGridView);
    public final BooleanProperty showFileTilesListViewProperty = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            mainContainer.setCenter(get() ? fileTilesListScrollPane : fileTilesGridScrollPane);
        }
    };

    private final ObservableList<FileInfo> fileInfos = FXCollections.observableArrayList();

    public FilesView() {
        ObservableLists.bindConverted(fileTilesListView.getChildren(), fileInfos, FilesView.FileInfo::getFileListTile);
        ObservableLists.bindConverted(fileTilesGridView.getChildren(), fileInfos, FilesView.FileInfo::getFileGridTile);
        showFileTilesListViewProperty.set(true);
    }

    public Node getView() {
        return mainContainer;
    }

    void populateFiles(List<File> files) {
        fileInfos.addAll(files.stream().map(FilesView.FileInfo::new).collect(Collectors.toList()));
    }

    void ascendingSort() {
        fileInfos.sort(Comparator.comparing(fi -> fi.file.getName()));
    }

    void descendingSort() {
        fileInfos.sort((fi1, fi2) -> fi2.file.getName().compareTo(fi1.file.getName()));
    }

    void clearList() {
        fileInfos.clear();
    }

    private static ScrollPane createFileTilesScrollPane(Node fileTilesView) {
        ScrollPane fileTilesScrollPane = new ScrollPane(fileTilesView);
        fileTilesScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent");
        fileTilesScrollPane.setFitToWidth(true);
        fileTilesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        BorderPane.setMargin(fileTilesScrollPane, new Insets(0, 0, 5, 10));
        return fileTilesScrollPane;
    }

    private long openTime;

    private void openFileView(Node fileView, boolean closeOnMouseClick, Runnable onClose) {
        BorderPane borderPane = new BorderPane(fileView);
        setBackgroundFill(borderPane, ROOT_BACKGROUND_COLOR);
        borderPane.setCursor(Cursor.HAND);
        borderPane.getProperties().put("onClose", onClose);
        if (closeOnMouseClick)
            borderPane.setOnMouseClicked(e -> closeFileView());
        getRootChildren().add(borderPane);
        openTime = System.currentTimeMillis();
    }

    private void closeFileView() {
        if (System.currentTimeMillis() - openTime < 300)
            return;
        ObservableList<Node> rootChildren = getRootChildren();
        while (rootChildren.size() > 1) {
            Node toRemove = rootChildren.get(rootChildren.size() - 1);
            if (rootChildren.remove(toRemove)) {
                Runnable onClose = (Runnable) toRemove.getProperties().get("onClose");
                if (onClose != null)
                    onClose.run();
            }
        }
    }

    private ObservableList<Node> getRootChildren() {
        return ((Pane) mainContainer.getParent().getParent()).getChildren();
    }

    class FileInfo {
        private final File file;
        private final FileType fileType;

        public FileInfo(File file) {
            this.file = file;
            fileType = FileType.fromMimeType(file.getMimeType());
        }

        private BorderPane fileGridTile;

        private Node getFileGridTile() {
            if (fileGridTile == null) {
                fileGridTile = new BorderPane(createFileIcon());
                BorderPane.setAlignment(fileGridTile.getCenter(), Pos.CENTER_LEFT);
                BorderPane.setMargin(fileGridTile.getCenter(), new Insets(10));
                armTileNode(fileGridTile);
            }
            return fileGridTile;
        }

        private Pane fileListTile;
        private Node getFileListTile() {
            if (fileListTile == null) {
                BorderPane sizeTextBorderPane;
                BorderPane borderPane = new BorderPane(new VBox(15,
                        createWhiteText(file.getName(), 28),
                        new HBox(10,
                                sizeTextBorderPane = new BorderPane(createGrayText(readableFileSize(file.length()), 20)),
                                createGrayText(Dates.format(LocalDateTime.ofEpochSecond(file.lastModified() / 1000, 0, LOCAL_ZONE_OFFSET), "dd/MM/yyyy HH:mm:ss"), 20)
                        )
                ));
                sizeTextBorderPane.setMinWidth(100);
                borderPane.setMinWidth(0);
                BorderPane.setAlignment(sizeTextBorderPane.getCenter(), Pos.CENTER_LEFT);
                BorderPane.setAlignment(borderPane.getCenter(), Pos.CENTER_LEFT);
                BorderPane.setMargin(borderPane.getCenter(), new Insets(20, 0, 0, 5));
                borderPane.setLeft(createFileIcon());
                BorderPane.setAlignment(borderPane.getLeft(), Pos.CENTER);
                BorderPane.setMargin(borderPane.getLeft(), new Insets(5));
                setBackgroundFill(borderPane, ROOT_BACKGROUND_COLOR_LIGHTER);
                armTileNode(borderPane);
                Rectangle clip = new Rectangle();
                clip.widthProperty().bind(borderPane.widthProperty());
                clip.heightProperty().bind(borderPane.heightProperty());
                borderPane.setClip(clip);
                fileListTile = new BorderPane(borderPane); // Embedding it so the clip doesn't prevent the shadow effect
                fileListTile.setEffect(CYAN_DROP_SHADOW);
                fileListTile.setCursor(Cursor.HAND);
                VBox.setMargin(fileListTile, new Insets(5, 15, 10, 5));
            }
            return fileListTile;
        }

        private TouchPoint pressedTouchPoint;
        private void armTileNode(Node tileNode) {
            tileNode.setCursor(Cursor.HAND);
            tileNode.setOnMouseClicked(e -> openFileAction());
            tileNode.setOnTouchPressed(e -> pressedTouchPoint = e.getTouchPoint());
            tileNode.setOnTouchReleased(e -> {
                if (Math.abs(e.getTouchPoint().getSceneY() - pressedTouchPoint.getSceneY()) < 20)
                    openFileAction();
            });
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
            textArea.setFont(Font.font(18)); // Ignored in the browser for any reason
            textArea.setPadding(new Insets(10));
            BorderPane borderPane = new BorderPane(textArea);
            BorderPane.setMargin(textArea, new Insets(10));
            Button closeButton = new Button("Close");
            setBackgroundFill(closeButton, Color.RED);
            closeButton.setTextFill(Color.WHITE);
            closeButton.setFont(Font.font(18));
            closeButton.setPadding(new Insets(10));
            closeButton.setOnAction(e -> closeFileView());
            HBox buttonBar = new HBox(closeButton);
            buttonBar.setAlignment(Pos.CENTER);
            buttonBar.setPadding(new Insets(0, 0, 20, 0));
            borderPane.setBottom(buttonBar);
            openFileView(borderPane, false, null);
        }

        private void openImageFile() {
            openFileView(getFullSizeImageView(), true, null);
        }

        private boolean userRequestedStop;
        private void openAudioFile() {
            DemoConfig demoConfig = new DemoConfig(mainContainer.getWidth(), mainContainer.getHeight());
            demoConfig.setAudioFilename(file.getURLPath());
            DemoFX equaliserDemoFX = new DemoFX(demoConfig, (IEffectFactory) config -> Collections.listOf(new Equaliser(config)));
            BorderPane equaliserPane = equaliserDemoFX.getPane();
            userRequestedStop = false;
            openFileView(equaliserPane, true, () -> {
                userRequestedStop = true;
                equaliserDemoFX.stopDemo();
            });
            equaliserDemoFX.setOnCompleted(() -> {
                if (!userRequestedStop)
                    openNextSameTypeFile(true);
            });
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
                    if (++fileIndex >= fileInfos.size()) {
                        if (fileType == FileType.AUDIO) // No loop for audio files
                            return;
                        fileIndex = 0;
                    }
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
}
