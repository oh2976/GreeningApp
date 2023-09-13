package com.example.greeningapp;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryParent {
    private String orderDate;
    private ArrayList<OrderHistoryChild> orderHistoryChildList;

    public OrderHistoryParent() {}

    public String getOrderDate() { return orderDate; }

    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public ArrayList<OrderHistoryChild> getOrderHistoryChildList() {
        return orderHistoryChildList;
    }

    public void setOrderHistoryChildList(ArrayList<OrderHistoryChild> orderHistoryChildList) {
        this.orderHistoryChildList = orderHistoryChildList;
    }
}
