package com.ayalait.gesventas.controller;

 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ayalait.response.*;

import java.util.List;

@Controller
public class MailController {


    @GetMapping({ "/mailbox" })
    public String mailBox(Model modelo) {
        if (LoginController.session.getToken() != null) {
            modelo.addAttribute("user",LoginController.session.getUser());

            ResponseDowloadMail response = LoginController.conMail.descargarMail(LoginController.session.getUser().getEmail(),LoginController.session.getUser().getPassword());
            if(response.isStatus()){
                List<Correo> mail=response.getLstMail().getCorreo();
             modelo.addAttribute("mail", mail);
            }



            return "mailbox";
        }else{
            return "login";
        }


    }
    
    @GetMapping({ "/read-mail" })
    public String readMail(Model modelo) {
        if (LoginController.session.getToken() != null) {
            modelo.addAttribute("user",LoginController.session.getUser());

            return "read-mail";
        }else{
            return "login";
        }


    }

}
