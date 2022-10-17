package dev.webfx.demo.files;

import dev.webfx.extras.filepicker.FilePicker;
import dev.webfx.platform.file.File;
import dev.webfx.platform.uischeduler.UiScheduler;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FilesApplication extends Application {
    private final BorderPane mainContainer = new BorderPane();

    @Override
    public void start(Stage stage) {
        FilePicker filePicker = FilePicker.create();
        filePicker.setGraphic(createFilePickerGraphic());
        filePicker.setMultiple(true);
        FilesView filesView = new FilesView();
        filePicker.getSelectedFilesObservableList().addListener((InvalidationListener) observable ->
                filesView.populateFiles(filePicker.getSelectedFilesObservableList()));
        ToggleButton toggleButton = new ToggleButton("List");
        filesView.showFileTilesListViewProperty.bind(toggleButton.selectedProperty());
        toggleButton.setSelected(true);
        Button clearButton = new Button("C");
        clearButton.setOnAction(e -> filesView.clearList());
        Button ascSortButton = new Button("A");
        ascSortButton.setOnAction(e -> filesView.ascendingSort());
        Button descSortButton = new Button("D");
        descSortButton.setOnAction(e -> filesView.descendingSort());
        Region hGrow = new Region();
        hGrow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(hGrow, Priority.ALWAYS);
        HBox buttonBar = new HBox(10, filePicker.getView(), hGrow, toggleButton, clearButton, ascSortButton, descSortButton);
        buttonBar.setAlignment(Pos.CENTER);
        mainContainer.setTop(buttonBar);
        mainContainer.setCenter(filesView.getView());
        BorderPane.setMargin(mainContainer.getTop(), new Insets(10));
        Shared.setBackgroundFill(mainContainer, Shared.ROOT_BACKGROUND_COLOR);
        updateBorder(false);
        StackPane root = new StackPane(mainContainer);
        root.getChildren().setAll(mainContainer);
        Scene scene = new Scene(root, 800, 600);
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
                    filesView.populateFiles(File.createFileList(platformFiles));
                    updateBorder(false);
                    mainContainer.setCursor(Cursor.DEFAULT);
                }, 5);
            }
        });
    }

    private static Node createFilePickerGraphic() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M10 0l-5.2 4.9h3.3v5.1h3.8v-5.1h3.3l-5.2-4.9zm9.3 11.5l-3.2-2.1h-2l3.4 2.6h-3.5c-.1 0-.2.1-.2.1l-.8 2.3h-6l-.8-2.2c-.1-.1-.1-.2-.2-.2h-3.6l3.4-2.6h-2l-3.2 2.1c-.4.3-.7 1-.6 1.5l.6 3.1c.1.5.7.9 1.2.9h16.3c.6 0 1.1-.4 1.3-.9l.6-3.1c.1-.5-.2-1.2-.7-1.5z");
        svgPath.setFill(Color.WHITE);
        Text chooseText = Shared.createWhiteText("Choose files...", 24);
        HBox hBox = new HBox(10, svgPath, chooseText);
        hBox.setAlignment(Pos.CENTER);
        Shared.setBackgroundFill(hBox, Color.RED);
        hBox.setPadding(new Insets(10));
        hBox.setMaxWidth(250);
        hBox.setEffect(Shared.CYAN_DROP_SHADOW);
        return hBox;
    }

    private void updateBorder(boolean drag) {
        Shared.setBorderStroke(mainContainer, drag ? Color.RED : Shared.ROOT_BACKGROUND_COLOR);
    }

}