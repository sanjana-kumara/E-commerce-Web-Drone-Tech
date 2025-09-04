/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hibernate;

import java.io.Serializable;
import java.util.Date;
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
@Table(name = "product")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title", length = 45, nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "qty", nullable = false)
    private int qty;

    @Column(name = "filght_time", length = 45, nullable = false)
    private String filght_time;

    @Column(name = "`range`", length = 45, nullable = false)
    private String range;

    @Column(name = "feautures", nullable = false)
    private String feautures;

    @Column(name = "warranty", length = 45, nullable = false)
    private String warranty;

    @Column(name = "created_at", nullable = false)
    private Date created_at;

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = true)
    private Model model_id;

    @ManyToOne
    @JoinColumn(name = "categories_id", nullable = true)
    private Categories categories_id;

    @ManyToOne
    @JoinColumn(name = "color_id", nullable = true)
    private Color color_id;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = true)
    private Brand brand_id;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = true)
    private Status status_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user_id;
    
    
    public Status getStatus_id() {
        return status_id;
    }

    public void setStatus_id(Status status_id) {
        this.status_id = status_id;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getFilght_time() {
        return filght_time;
    }

    public void setFilght_time(String filght_time) {
        this.filght_time = filght_time;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getFeautures() {
        return feautures;
    }

    public void setFeautures(String feautures) {
        this.feautures = feautures;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Model getModel_id() {
        return model_id;
    }

    public void setModel_id(Model model_id) {
        this.model_id = model_id;
    }

    public Categories getCategories_id() {
        return categories_id;
    }

    public void setCategories_id(Categories categories_id) {
        this.categories_id = categories_id;
    }

    public Color getColor_id() {
        return color_id;
    }

    public void setColor_id(Color color_id) {
        this.color_id = color_id;
    }

    public Brand getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(Brand brand_id) {
        this.brand_id = brand_id;
    }


}
