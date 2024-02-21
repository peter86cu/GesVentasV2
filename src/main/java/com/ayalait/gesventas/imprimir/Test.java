package com.ayalait.gesventas.imprimir;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.itextpdf.html2pdf.HtmlConverter;

public class Test {

	public static void main(String[] args) {
		 try{
		        Date date=new Date();
		        SimpleDateFormat fecha=new SimpleDateFormat("dd/MM/yyyy");
		        SimpleDateFormat hora=new SimpleDateFormat("hh:mm:ss aa");
		        Ticket ticket = new Ticket();
		        ticket.AddCabecera("             SANDALS RESTAURANT");
		        ticket.AddCabecera(ticket.DarEspacio());
		        ticket.AddCabecera("     tlf: 222222  r.u.c: 22222222222");
		        ticket.AddCabecera(ticket.DarEspacio());
		        ticket.AddSubCabecera(ticket.DibujarLinea(40));
		        ticket.AddSubCabecera(ticket.DarEspacio());
		        ticket.AddSubCabecera("     Ticket Factura No:'003-000011'");
		        ticket.AddSubCabecera(ticket.DarEspacio());
		        ticket.AddSubCabecera("        "+fecha.format(date) + " " + hora.format(date));
		        ticket.AddSubCabecera(ticket.DarEspacio());
		        ticket.AddSubCabecera("         Mesa "+12+" Mozo  Pers ");
		        ticket.AddSubCabecera(ticket.DarEspacio());
		        ticket.AddSubCabecera(ticket.DibujarLinea(40));
		        ticket.AddSubCabecera(ticket.DarEspacio());
		        ticket.AddSubCabecera("cant      producto         p.u     total");
		        ticket.AddSubCabecera(ticket.DarEspacio());
		        ticket.AddSubCabecera(ticket.DibujarLinea(40));
		        ticket.AddSubCabecera(ticket.DarEspacio());
		       for(int y=0;y<2;y++){
		           //cantidad de decimales
		           NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
		           DecimalFormat form = (DecimalFormat)nf;
		           form.applyPattern("#,###.00");
		           //cantidad
		           String cantidad="2";
		           if(cantidad.length()<4){
		               int cant=4-cantidad.length();String can="";
		               for(int f=0;f<cant;f++){can+=" ";}cantidad+=can;
		           }
		            //items
		            String item="2";
		            if(item.length()>17){item=item.substring(0,16)+".";}
		            else{
		                int c=17-item.length();String comple="";
		                for(int y1=0;y1<c;y1++){comple+=" ";}item+=comple;
		            }
		            //precio
		            String precio="50";
		            double pre1=Double.parseDouble(precio);
		            precio=form.format(pre1);
		            if(precio.length()<8){
		                int p=8-precio.length();String pre="";
		                for(int y1=0;y1<p;y1++){pre+=" ";}precio=pre+precio;
		            }
		            //total
		            String total="150";
		            total=form.format(Double.parseDouble(total));
		            if(total.length()<8){
		                int t=8-total.length();String tota="";
		                for(int y1=0;y1<t;y1++){tota+=" ";}total=tota+total;
		            }
		            //agrego los items al detalle
		            ticket.AddItem(cantidad,item,precio,total);
		            //ticket.AddItem("","","",ticket.DarEspacio());
		        }
		        ticket.AddItem(ticket.DibujarLinea(40),"","","");
		        ticket.AddTotal("",ticket.DarEspacio());
		        ticket.AddTotal("total                   ","45");
		        ticket.AddTotal("",ticket.DarEspacio());
		        ticket.AddTotal("Igv                     ","25");
		        ticket.AddTotal("",ticket.DarEspacio());
		        ticket.AddTotal("total venta             ","150");
		        ticket.AddTotal("",ticket.DarEspacio());
		        ticket.AddTotal("paga con                ","Efectivo");
		        ticket.AddTotal("",ticket.DarEspacio());
		        ticket.AddTotal("vuelto                  ","10");
		        ticket.AddPieLinea(ticket.DarEspacio());
		        ticket.AddPieLinea("Gracias por su Preferencia");
//		        OutputStream fileOutputStream = new FileOutputStream(pathOrden);
//				HtmlConverter.convertToPdf(ticket, fileOutputStream);
				String pathOrden=System.getProperty("user.dir")+"\\src\\main\\resources\\static\\pdf\\ticket\\ticket_"+45+".pdf";

		        ticket.ImprimirDocumento(pathOrden,false);
		    }catch(Exception e){System.out.print("\nerror "+e.toString());}

	}
	
}


