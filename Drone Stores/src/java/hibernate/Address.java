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
@Table(name = "addres")
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "line1", nullable = false)
    private String line1;

    @Column(name = "line2", nullable = false)
    private String line2;

    @Column(name = "postal_code", length = 10, nullable = false)
    private String postal_code;

    @Column(name = "mobile_no", length = 45, nullable = false)
    private String mobile_no;

    @ManyToOne
    @JoinColumn(name = "province_id", nullable = false)
    private Province province_id;

    @ManyToOne
    @JoinColumn(name = "distric_id", nullable = false)
    private Distric distric_id;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public Province getProvince_id() {
        return province_id;
    }

    public void setProvince_id(Province province_id) {
        this.province_id = province_id;
    }

    public Distric getDistric_id() {
        return distric_id;
    }

    public void setDistric_id(Distric distric_id) {
        this.distric_id = distric_id;
    }

    public City getCity_id() {
        return city_id;
    }

    public void setCity_id(City city_id) {
        this.city_id = city_id;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

}
