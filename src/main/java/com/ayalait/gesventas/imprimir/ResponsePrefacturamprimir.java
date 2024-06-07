package com.ayalait.gesventas.imprimir;


 

import java.util.List;

import com.ayalait.modelo.Cliente;
import com.ayalait.modelo.Empresa;
import com.ayalait.modelo.Proveedor;

public class ResponsePrefacturamprimir {

    private boolean status;

    private String fecha;

    private int code;

    private double total;

    private String resultado;

    private String idPrefactura;

    private String aprobadoPor;

    private List<ItemOrden> lstItems;

    private Empresa empresa;

    private Cliente cliente;


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

	public String getIdPrefactura() {
		return idPrefactura;
	}

	public void setIdPrefactura(String idPrefactura) {
		this.idPrefactura = idPrefactura;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
}
