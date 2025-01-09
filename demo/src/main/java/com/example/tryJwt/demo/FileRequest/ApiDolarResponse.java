package com.example.tryJwt.demo.FileRequest;




public class ApiDolarResponse {
    private Double compra;
    private Double venta;
    private String casa;
    private String nombre;
    private String moneda;
    private String fechaActualizacion;

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Double getCompra() {
        return compra;
    }

    public void setCompra(Double compra) {
        this.compra = compra;
    }

    public Double getVenta() {
        return venta;
    }

    public void setVenta(Double venta) {
        this.venta = venta;
    }

    public String getCasa() {
        return casa;
    }

    public void setCasa(String casa) {
        this.casa = casa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "ApiDolarResponse{" +
                "compra=" + compra +
                ", venta=" + venta +
                ", casa='" + casa + '\'' +
                ", nombre='" + nombre + '\'' +
                ", moneda='" + moneda + '\'' +
                ", fechaActualizacion='" + fechaActualizacion + '\'' +
                '}';
    }
}
