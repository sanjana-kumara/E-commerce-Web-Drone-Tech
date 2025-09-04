/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hibernate;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Sanjana
 */
@Entity
@Table(name = "order_items")
public class OrderItems implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "qty", nullable = false)
    private int qty;

    @Column(name = "rating", nullable = false)
    private int rating;

    @ManyToOne
    @JoinColumn(name = "order_status_id", nullable = false)
    private OrderStatus orderStatus;
    
    @ManyToOne
    @JoinColumn(name = "orders_id", nullable = false)
    private Orders orders;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "dilivery_type_id", nullable = false)
    private DiliveryType diliveryType;

    public OrderItems(int id, int qty, int rating, OrderStatus orderStatus, Orders orders, Product product, DiliveryType diliveryType) {
        this.id = id;
        this.qty = qty;
        this.rating = rating;
        this.orderStatus = orderStatus;
        this.orders = orders;
        this.product = product;
        this.diliveryType = diliveryType;
    }

    public OrderItems() {
    }

        
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public DiliveryType getDiliveryType() {
        return diliveryType;
    }

    public void setDiliveryType(DiliveryType diliveryType) {
        this.diliveryType = diliveryType;
    }

}
