
package com.example.begzug;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path="/index")
public class WebController {
    @RequestMapping(path="/")
    public String ServeIndex () {
        return "forward:/index2.html";
    }
}
