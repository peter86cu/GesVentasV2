package com.ayalait.gesventas.imprimir;


 

import java.util.List;

import com.ayalait.modelo.Empresa;
import com.ayalait.modelo.Proveedor;

public class ResponseOrdenImprimir {

    private boolean status;

    private String fecha;

    private int code;

    private double total;

    private String resultado;

    private int idOrden;

    private String aprobadoPor;

    private List<ItemOrden> lstItems;

    private Empresa empresa;

    private Proveedor proveedor;


    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<ItemOrden> getLstItems() {
        return lstItems;
    }

    public void setLstItems(List<ItemOrden> lstItems) {
        this.lstItems = lstItems;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(String aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }
}
