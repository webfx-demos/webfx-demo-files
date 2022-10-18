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
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

import static dev.webfx.demo.files.Shared.*;

public class FilesApplication extends Application {
    private final BorderPane mainContainer = new BorderPane();
    private SegmentedButton<Boolean> clearButton;

    @Override
    public void start(Stage stage) {
        // Creating an instance of FilePicker
        FilePicker filePicker = FilePicker.create();
        // Customizing the FilePicker button appearance
        filePicker.setGraphic(createFilePickerGraphic());
        // Allowing multiple file selection
        filePicker.setMultiple(true);
        FilesView filesView = new FilesView();
        // Reacting to the user files selection
        filePicker.getSelectedFiles().addListener((InvalidationListener) observable ->
                filesView.populateFiles(filePicker.getSelectedFiles()));
        SegmentedButton<Boolean> listGridButton = new SegmentedButton<>(true,
                new ButtonSegment<>(Boolean.TRUE,  e -> filesView.showFileTilesListViewProperty.set(true),  "m 10.12884,7.933619 c 0,-0.5526417 0.447886,-1.0005279 0.999703,-1.0005279 h 16.000045 c 0.552642,0 0.999704,0.4478862 0.999704,1.0005279 0,0.5518182 -0.447062,0.9997039 -0.999704,0.9997039 H 11.128543 c -0.551817,0 -0.999703,-0.4478857 -0.999703,-0.9997039 z m 18.000339,7.000295 -8.25e-4,-8.25e-4 c 0,0.265595 -0.104754,0.519638 -0.292812,0.707716 -0.187238,0.187238 -0.442123,0.292813 -0.706892,0.292813 H 11.128605 c -0.551817,0 -0.999704,-0.447888 -0.999704,-1.000529 0,-0.551818 0.447887,-0.999704 0.999704,-0.999704 h 16.000044 c 0.264771,0 0.519638,0.10558 0.706894,0.292812 0.18806,0.187238 0.292811,0.442123 0.292811,0.706892 z m 0,7.000297 -8.25e-4,-8.25e-4 c 0,0.264771 -0.104754,0.519637 -0.292812,0.706892 -0.187238,0.188062 -0.442123,0.292813 -0.706892,0.292813 H 11.128605 c -0.551817,0 -0.999704,-0.447063 -0.999704,-0.999705 0,-0.552642 0.447887,-0.999705 0.999704,-0.999705 h 16.000044 c 0.264771,0 0.519638,0.104753 0.706894,0.292812 0.18806,0.187239 0.292811,0.441277 0.292811,0.706893 z"),
                new ButtonSegment<>(Boolean.FALSE, e -> filesView.showFileTilesListViewProperty.set(false), "m 16.143013,7.8900285 h -2.488308 c -1.325696,0 -2.399959,1.0742794 -2.399959,2.3999575 v 2.4878 c 0,1.325695 1.074283,2.399959 2.399959,2.399959 h 2.488308 c 1.325037,0 2.399959,-1.07428 2.399959,-2.399959 v -2.4878 c 0,-1.3256949 -1.074942,-2.3999575 -2.399959,-2.3999575 z m 0.799772,4.8884335 v -6.6e-4 c 0,0.212477 -0.08447,0.415714 -0.234254,0.566179 -0.149792,0.149792 -0.353701,0.234252 -0.565518,0.234252 h -2.488308 c -0.441458,0 -0.799771,-0.358313 -0.799771,-0.800431 v -2.48782 c 0,-0.4421177 0.358313,-0.8004305 0.799771,-0.8004305 h 2.488308 c 0.211817,0 0.415714,0.084464 0.565518,0.234911 0.149792,0.1497917 0.234254,0.3530255 0.234254,0.5655195 z M 24.85469,7.8900285 h -2.4878 c -1.325697,0 -2.399959,1.0742794 -2.399959,2.3999575 v 2.4878 c 0,1.325695 1.074278,2.399959 2.399959,2.399959 h 2.4878 c 1.325692,0 2.399954,-1.07428 2.399954,-2.399959 v -2.4878 c 0,-1.3256949 -1.074278,-2.3999575 -2.399954,-2.3999575 z m 0.799772,4.8884335 7.8e-4,-6.6e-4 c 0,0.212477 -0.08447,0.415714 -0.234254,0.566179 -0.150432,0.149792 -0.353681,0.234252 -0.566158,0.234252 h -2.487801 c -0.442119,0 -0.800431,-0.358313 -0.800431,-0.800431 v -2.48782 c 0,-0.4421177 0.358312,-0.8004305 0.800431,-0.8004305 h 2.487801 c 0.212477,0 0.415714,0.084464 0.566178,0.234911 0.149791,0.1497917 0.234254,0.3530255 0.234254,0.5655195 z m -9.511485,3.823344 h -2.488304 c -1.325696,0 -2.399958,1.074938 -2.399958,2.399958 v 2.488306 c 0,1.325696 1.074278,2.399959 2.399958,2.399959 h 2.488304 c 1.325037,0 2.399959,-1.07428 2.399959,-2.399957 v -2.488308 c 0,-1.325036 -1.074938,-2.399958 -2.399959,-2.399958 z m 0.799772,4.888434 c 0,0.211818 -0.08446,0.415714 -0.23425,0.565519 -0.149791,0.150451 -0.3537,0.234253 -0.565522,0.234253 h -2.488304 c -0.441458,0 -0.799771,-0.357655 -0.799771,-0.799772 v -2.488307 c 0,-0.441459 0.358313,-0.799772 0.799771,-0.799772 h 2.488304 c 0.211822,0 0.415714,0.08446 0.565522,0.234252 0.149792,0.150451 0.23425,0.353701 0.23425,0.56552 z m 7.911905,-4.888434 h -2.4878 c -1.325693,0 -2.399959,1.074938 -2.399959,2.399958 v 2.488306 c 0,1.325696 1.074282,2.399958 2.399959,2.399958 h 2.4878 c 1.325697,0 2.399959,-1.074279 2.399959,-2.399958 v -2.488306 c 0,-1.325036 -1.074279,-2.399958 -2.399959,-2.399958 z m 0.800432,4.888434 c 0,0.211818 -0.08447,0.415714 -0.234254,0.565519 -0.150448,0.150451 -0.353701,0.234253 -0.566178,0.234253 h -2.4878 c -0.442119,0 -0.800432,-0.357655 -0.800432,-0.799772 v -2.488307 c 0,-0.441459 0.358313,-0.799772 0.800432,-0.799772 h 2.4878 c 0.212477,0 0.415714,0.08446 0.566178,0.234252 0.149792,0.150451 0.234254,0.353701 0.234254,0.56552 z")
        );
        SegmentedButton<Boolean> sortButton = new SegmentedButton<Boolean>(
                new ButtonSegment<>(Boolean.TRUE,  e -> filesView.ascendingSort(),  "m 28.843831,19.506115 q 0,-0.451389 -0.329861,-0.78125 l -7.777776,-7.777778 q -0.329861,-0.329861 -0.781251,-0.329861 -0.451389,0 -0.78125,0.329861 l -7.777779,7.777778 q -0.32986,0.329861 -0.32986,0.78125 0,0.451388 0.32986,0.78125 0.329861,0.329861 0.781251,0.329861 H 27.73272 q 0.451389,0 0.78125,-0.329861 0.329861,-0.329862 0.329861,-0.78125 z"),
                new ButtonSegment<>(Boolean.FALSE, e -> filesView.descendingSort(), "m 11.066054,11.728337 q 0,0.451389 0.329861,0.78125 l 7.777776,7.777777 q 0.329862,0.329862 0.781251,0.329862 0.451389,0 0.78125,-0.329862 l 7.77778,-7.777777 q 0.32986,-0.329861 0.32986,-0.78125 0,-0.451388 -0.32986,-0.78125 -0.329861,-0.329861 -0.781251,-0.329861 H 12.177165 q -0.451388,0 -0.78125,0.329861 -0.329861,0.329862 -0.329861,0.78125 z")
        );
        clearButton = new SegmentedButton<Boolean>(
                new ButtonSegment<>(Boolean.TRUE, e -> {
                    filesView.clearList();
                    clearButton.setState(null);
                }, "M 12.2965 11.2412 L 13.6364 23.3212 C 13.7864 24.7012 14.9665 25.7412 16.3665 25.7412 L 23.1864 25.7412 C 24.5864 25.7412 25.7665 24.7012 25.9165 23.3212 L 27.2565 11.2412 Z M 18.7765 21.7412 C 18.7765 22.2912 18.3265 22.7412 17.7765 22.7412 C 17.2265 22.7412 16.7765 22.2912 16.7765 21.7412 L 16.7765 15.7412 C 16.7765 15.1912 17.2265 14.7412 17.7765 14.7412 C 18.3265 14.7412 18.7765 15.1912 18.7765 15.7412 Z M 22.7765 21.7412 C 22.7765 22.2912 22.3265 22.7412 21.7765 22.7412 C 21.2265 22.7412 20.7765 22.2912 20.7765 21.7412 L 20.7765 15.7412 C 20.7765 15.1912 21.2265 14.7412 21.7765 14.7412 C 22.3265 14.7412 22.7765 15.1912 22.7765 15.7412 Z M 28.7765 8.74118 C 28.7765 9.29118 28.3265 9.74118 27.7765 9.74118 L 11.7765 9.74118 C 11.2265 9.74118 10.7765 9.29118 10.7765 8.74118 C 10.7765 8.19118 11.2265 7.74118 11.7765 7.74118 L 18.7765 7.74118 L 18.7765 6.74118 C 18.7765 6.19118 19.2265 5.74118 19.7765 5.74118 C 20.3265 5.74118 20.7765 6.19118 20.7765 6.74118 L 20.7765 7.74118 L 27.7765 7.74118 C 28.3265 7.74118 28.7765 8.19118 28.7765 8.74118 Z")
        );
        Region hGrow = new Region();
        hGrow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(hGrow, Priority.ALWAYS);
        HBox buttonBar = new HBox(5, filePicker.getView(), hGrow, listGridButton.getView(), sortButton.getView(), clearButton.getView());
        buttonBar.setAlignment(Pos.CENTER);
        mainContainer.setTop(buttonBar);
        mainContainer.setCenter(filesView.getView());
        BorderPane.setMargin(mainContainer.getTop(), new Insets(10));
        setBackgroundFill(mainContainer, ROOT_BACKGROUND_COLOR);
        updateBorder(false);
        StackPane root = new StackPane(mainContainer);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
        // When the user drags files over that node, we tell the system we are accepting them
        mainContainer.setOnDragOver(e -> {
            if (e.getDragboard().hasContent(DataFormat.FILES)) {
                e.acceptTransferModes(TransferMode.COPY);
                updateBorder(true);
            }
        });
        // When the user finally drops the files on that node, we get them and add them in our files view
        mainContainer.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(DataFormat.FILES)) {
                Object platformFiles = db.getContent(DataFormat.FILES);
                List<File> fileList = File.createFileList(platformFiles);
                mainContainer.setCursor(Cursor.WAIT);
                UiScheduler.scheduleInAnimationFrame (() -> {
                    filesView.populateFiles(fileList);
                    updateBorder(false);
                    mainContainer.setCursor(Cursor.DEFAULT);
                }, 5);
            }
        });
        mainContainer.setOnDragExited(e -> updateBorder(false));
    }

    private static Node createFilePickerGraphic() {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M10 0l-5.2 4.9h3.3v5.1h3.8v-5.1h3.3l-5.2-4.9zm9.3 11.5l-3.2-2.1h-2l3.4 2.6h-3.5c-.1 0-.2.1-.2.1l-.8 2.3h-6l-.8-2.2c-.1-.1-.1-.2-.2-.2h-3.6l3.4-2.6h-2l-3.2 2.1c-.4.3-.7 1-.6 1.5l.6 3.1c.1.5.7.9 1.2.9h16.3c.6 0 1.1-.4 1.3-.9l.6-3.1c.1-.5-.2-1.2-.7-1.5z");
        svgPath.setFill(Color.WHITE);
        Text chooseText = createWhiteText("Choose files...", 24);
        HBox hBox = new HBox(10, svgPath, chooseText);
        hBox.setAlignment(Pos.CENTER);
        setBackgroundFill(hBox, Color.RED);
        hBox.setPadding(new Insets(10));
        hBox.setMaxWidth(250);
        hBox.setEffect(CYAN_DROP_SHADOW);
        return hBox;
    }

    private void updateBorder(boolean drag) {
        setBorderStroke(mainContainer, drag ? Color.RED : ROOT_BACKGROUND_COLOR);
    }

}