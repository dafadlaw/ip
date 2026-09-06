package nob.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Displays one user or Nob message as a chatbot dialog bubble.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    /** Loads and configures a dialog box for the specified speaker. */
    private DialogBox(String text, Image avatarImage, String fallbackAvatar, boolean isUser) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box.", exception);
        }

        dialog.setText(text);
        dialog.getStyleClass().add(isUser ? "user-bubble" : "nob-bubble");
        Node avatar = createAvatar(avatarImage, fallbackAvatar);
        setAlignment(isUser ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        if (isUser) {
            getChildren().setAll(dialog, avatar);
        } else {
            getChildren().setAll(avatar, dialog);
        }
    }

    /** Returns a circular image avatar, or an emoji avatar when the image is unavailable. */
    private Node createAvatar(Image avatarImage, String fallbackAvatar) {
        if (avatarImage == null) {
            Label avatarLabel = new Label(fallbackAvatar);
            avatarLabel.getStyleClass().add("avatar-fallback");
            return avatarLabel;
        }

        ImageView avatarView = new ImageView(avatarImage);
        avatarView.setFitWidth(44);
        avatarView.setFitHeight(44);
        avatarView.setPreserveRatio(false);
        avatarView.setClip(new Circle(22, 22, 22));
        return avatarView;
    }

    /**
     * Returns a right-aligned dialog bubble for the user.
     *
     * @param text The user's message.
     * @param avatarImage The user's avatar, or {@code null} to use the fallback emoji.
     * @return The formatted user dialog.
     */
    public static DialogBox getUserDialog(String text, Image avatarImage) {
        return new DialogBox(text, avatarImage, "🙂", true);
    }

    /**
     * Returns a left-aligned dialog bubble for Nob.
     *
     * @param text Nob's response.
     * @param avatarImage Nob's avatar, or {@code null} to use the fallback emoji.
     * @return The formatted Nob dialog.
     */
    public static DialogBox getNobDialog(String text, Image avatarImage) {
        return new DialogBox(text, avatarImage, "🤖", false);
    }
}
