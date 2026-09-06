package nob;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Loads and displays Nob's JavaFX chatbot window.
 */
public class GuiNob extends Application {
    /** Loads the main-window FXML resource and displays it. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(GuiNob.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = loader.load();
            stage.setTitle("Nob — Your task companion");
            stage.setMinWidth(460);
            stage.setMinHeight(540);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Nob's main window.", exception);
        }
    }
}
