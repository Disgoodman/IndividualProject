package com.example.models.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "bytea", nullable = false)
    private byte[] data;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorcycle_id", foreignKey = @ForeignKey(name = "fk_image_motorcycle"))
    private Motorcycle motorcycle;

    // region boilerplate

    public Image() {
    }

    public Image(byte[] data, String name, Motorcycle motorcycle) {
        this.data = data;
        this.name = name;
        this.motorcycle = motorcycle;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Motorcycle getMotorcycle() {
        return motorcycle;
    }

    public void setMotorcycle(Motorcycle motorcycle) {
        this.motorcycle = motorcycle;
    }

    //endregion
}
