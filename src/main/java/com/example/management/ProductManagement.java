package com.example.management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProductManagement {
    private final String DATABASE_URL = "jdbc:sqlite:store.db";
    private Connection conn;
    private ObservableList<Product> products;
    private TableView<Product> tableView;

    public ProductManagement() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DATABASE_URL);
            setupDatabase();
            products = FXCollections.observableArrayList();
            loadProducts();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public VBox getView() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Button addButton = new Button("Add Product");
        addButton.setOnAction(e -> showAddProductDialog());

        tableView = new TableView<>();
        TableColumn<Product, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(priceColumn);
        tableView.getColumns().add(quantityColumn);

        tableView.setItems(products);

        vbox.getChildren().addAll(addButton, tableView);
        return vbox;
    }

    private void setupDatabase() {
        String createProductsTable = "CREATE TABLE IF NOT EXISTS products ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "price REAL NOT NULL,"
                + "quantity INTEGER NOT NULL);";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createProductsTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadProducts() {
        String sql = "SELECT * FROM products";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddProductDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Product");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label priceLabel = new Label("Price:");
        TextField priceField = new TextField();
        Label quantityLabel = new Label("Quantity:");
        TextField quantityField = new TextField();

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            addProduct(nameField.getText(), Double.parseDouble(priceField.getText()), Integer.parseInt(quantityField.getText()));
            dialog.close();
        });

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(priceLabel, 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(quantityLabel, 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(addButton, 1, 3);

        Scene scene = new Scene(grid, 300, 200);
        dialog.setScene(scene);
        dialog.show();
    }

    private void addProduct(String name, double price, int quantity) {
        String sql = "INSERT INTO products(name, price, quantity) VALUES(?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, quantity);
            pstmt.executeUpdate();
            System.out.println("Product added successfully.");

            // Add the product to the ObservableList
            String getLastInsertedId = "SELECT last_insert_rowid()";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(getLastInsertedId)) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Product product = new Product(id, name, price, quantity);
                    products.add(product);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
