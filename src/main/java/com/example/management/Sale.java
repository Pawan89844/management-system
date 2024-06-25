package com.example.management;

public class Sale {
    private int id;
    private int productId;
    private int customerId;
    private int quantity;
    private String saleDate;

    public Sale(int id, int productId, int customerId, int quantity, String saleDate) {
        this.id = id;
        this.productId = productId;
        this.customerId = customerId;
        this.quantity = quantity;
        this.saleDate = saleDate;
    }

    public int getId() {
        return id;
    }

    public int getProductId() {
        return productId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }
}
