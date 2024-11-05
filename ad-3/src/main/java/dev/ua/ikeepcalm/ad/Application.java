package dev.ua.ikeepcalm.ad;

import atlantafx.base.theme.PrimerDark;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("application.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        javafx.application.Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        stage.getIcons().add(new Image("https://static.vecteezy.com/system/resources/thumbnails/018/921/850/small/circle-button-symbol-png.png"));
        stage.setTitle("AD-3 - ikeepcalm");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}