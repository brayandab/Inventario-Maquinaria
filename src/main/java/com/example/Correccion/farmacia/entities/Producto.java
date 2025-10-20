package com.example.Correccion.farmacia.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "maquinaria")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_maquinaria")
    private Long idMaquinaria;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "proveedor")
    private String proveedor;

    @Column(name = "lote")
    private String lote;

    // Eliminamos la anotación @DateTimeFormat

    @Column(name = "stock")
    private int stock;

    @Column(name = "precio_unitario")
    private double precioUnitario;

    @Column(name = "foto")
    private String foto;



    // Getters y Setters

    public Long getIdMaquinaria() {
        return idMaquinaria;
    }

    public void setIdMaquinaria(Long idMedicamento) {
        this.idMaquinaria = idMaquinaria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}