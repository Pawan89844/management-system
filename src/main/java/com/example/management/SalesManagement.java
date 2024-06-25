package com.example.management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

public class SalesManagement {
    private final String DATABASE_URL = "jdbc:sqlite:store.db";
    private Connection conn;
    private ObservableList<Sale> sales;
    private TableView<Sale> tableView;

    public SalesManagement() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DATABASE_URL);
            setupDatabase();
            sales = FXCollections.observableArrayList();
            loadSales();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public VBox getView() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Button addButton = new Button("Add Sale");
        addButton.setOnAction(e -> showAddSaleDialog());

        tableView = new TableView<>();
        TableColumn<Sale, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Sale, Integer> productIdColumn = new TableColumn<>("Product ID");
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        TableColumn<Sale, Integer> customerIdColumn = new TableColumn<>("Customer ID");
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        TableColumn<Sale, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<Sale, String> saleDateColumn = new TableColumn<>("Sale Date");
        saleDateColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(productIdColumn);
        tableView.getColumns().add(customerIdColumn);
        tableView.getColumns().add(quantityColumn);
        tableView.getColumns().add(saleDateColumn);

        tableView.setItems(sales);

        vbox.getChildren().addAll(addButton, tableView);
        return vbox;
    }

    private void setupDatabase() {
        String createSalesTable = "CREATE TABLE IF NOT EXISTS sales ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "product_id INTEGER NOT NULL,"
                + "customer_id INTEGER NOT NULL,"
                + "quantity INTEGER NOT NULL,"
                + "sale_date TEXT NOT NULL,"
                + "FOREIGN KEY (product_id) REFERENCES products(id),"
                + "FOREIGN KEY (customer_id) REFERENCES customers(id));";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createSalesTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSales() {
        String sql = "SELECT * FROM sales";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Sale sale = new Sale(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("quantity"),
                        rs.getString("sale_date")
                );
                sales.add(sale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddSaleDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Sale");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        Label productIdLabel = new Label("Product ID:");
        TextField productIdField = new TextField();
        Label customerIdLabel = new Label("Customer ID:");
        TextField customerIdField = new TextField();
        Label quantityLabel = new Label("Quantity:");
        TextField quantityField = new TextField();

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            addSale(Integer.parseInt(productIdField.getText()), Integer.parseInt(customerIdField.getText()), Integer.parseInt(quantityField.getText()));
            dialog.close();
        });

        grid.add(productIdLabel, 0, 0);
        grid.add(productIdField, 1, 0);
        grid.add(customerIdLabel, 0, 1);
        grid.add(customerIdField, 1, 1);
        grid.add(quantityLabel, 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(addButton, 1, 3);

        Scene scene = new Scene(grid, 300, 200);
        dialog.setScene(scene);
        dialog.show();
    }

    private void addSale(int productId, int customerId, int quantity) {
        String sql = "INSERT INTO sales(product_id, customer_id, quantity, sale_date) VALUES(?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String saleDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pstmt.setInt(1, productId);
            pstmt.setInt(2, customerId);
            pstmt.setInt(3, quantity);
            pstmt.setString(4, saleDate);
            pstmt.executeUpdate();
            System.out.println("Sale added successfully.");

            // Add the sale to the ObservableList
            String getLastInsertedId = "SELECT last_insert_rowid()";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(getLastInsertedId)) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Sale sale = new Sale(id, productId, customerId, quantity, saleDate);
                    sales.add(sale);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
