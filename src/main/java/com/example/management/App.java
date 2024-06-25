package com.example.management;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @SuppressWarnings("exports")
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Store Management Software");

        TabPane tabPane = new TabPane();

        Tab productTab = new Tab("Products", new ProductManagement().getView());
        Tab customerTab = new Tab("Customers", new CustomerManagement().getView());
        Tab salesTab = new Tab("Sales", new SalesManagement().getView());

        tabPane.getTabs().add(productTab);
        tabPane.getTabs().add(customerTab);
        tabPane.getTabs().add(salesTab);

        Scene scene = new Scene(tabPane, 800, 600);
        stage.setScene(scene);
        stage.show();
        // scene = new Scene(loadFXML("primary"), 640, 480);
        // stage.setScene(scene);
        // stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}