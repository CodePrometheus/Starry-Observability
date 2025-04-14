package com.starry.webspring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/sp1")
    public String testSp1() {
        return "sp1";
    }

    @GetMapping("/sp2")
    public String testSp2() {
        return "sp2";
    }

    @GetMapping("/sp1/1")
    public void testSp11() {
        System.out.println("sp1/1");
    }

    @GetMapping("/sp1/2")
    public void testSp12() {
        System.out.println("sp1/2");
    }

    @GetMapping("/sp2/1")
    public void testSp21() {
        System.out.println("sp2/1");
    }
}
