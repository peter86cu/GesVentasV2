package com.ayalait.gesventas.imprimir;

public class OrdenTotal {
	char[] temp = new char[] { ' ' };

	public OrdenTotal(char delimit) {
		temp = new char[] { delimit };
	}

	public String GetTotalNombre(String totalItem) {
		String[] delimitado = totalItem.split("" + temp);
		return delimitado[0];
	}

	public String GetTotalCantidad(String totalItem) {
		String[] delimitado = totalItem.split("" + temp);
		return delimitado[1];
	}

	public String GeneraTotal(String Nombre, String precio) {
		return Nombre + temp[0] + temp[0] + temp[0] + temp[0] + precio;
	}
}
