package dev.webfx.demo.files;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
final class Shared {

    final static Color ROOT_BACKGROUND_COLOR = Color.web("#0D1117");
    final static Color ROOT_BACKGROUND_COLOR_LIGHTER = Color.web("#161B22");
    final static DropShadow CYAN_DROP_SHADOW = new DropShadow(5, 0, 5, Color.CYAN);

    /*******************************************************************************************************************
     Graphical utility methods
     ******************************************************************************************************************/

    static void setBackgroundFill(Region region, Paint fill) {
        region.setBackground(new Background(new BackgroundFill(fill, null, null)));
    }

    static void setBorderStroke(Region region, Paint stroke) {
        region.setBorder(new Border(new BorderStroke(stroke, BorderStrokeStyle.SOLID, null, BorderStroke.THICK)));
    }

    static Text createWhiteText(String content, double size) {
        Text text = new Text(content);
        text.setFill(Color.WHITE);
        text.setFont(Font.font(size));
        return text;
    }

    static SVGPath createWhiteSVGPath(String content) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(content);
        svgPath.setFill(Color.WHITE);
        return svgPath;
    }
}
