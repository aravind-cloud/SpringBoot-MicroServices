package com.example.sbc.Service1.controller;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class WelcomeMessage {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    Environment environment;


    @RequestMapping("/client/greeting")
    @HystrixCommand(fallbackMethod = "fallbackWelcomeMsg",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value="60")
            }
    )
    public String constructWelcomeMsg(){
        // Internal Microservice communiation via API GateWay
        String res1 = restTemplate.getForObject("http://localhost:9900/api/micro2/hello/service", String.class);
        String res2 = restTemplate.getForObject("http://localhost:9900/api/micro3/aravind/service", String.class);

        //Internal Microservice communication via EUREKHA
//        String res1 = restTemplate.getForObject("http://service2/hello/service", String.class);
//        String res2 = restTemplate.getForObject("http://service3/aravind/service", String.class);
        String host ="";
        String hostName ="";
        String port;
        port = environment.getProperty("local.server.port");

        try {
         host = InetAddress.getLocalHost().getHostAddress();
            hostName  = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        return res1+" " + res2+" from "+host +" "+ port;
       // return "Hello Zuul";
    }

    public String fallbackWelcomeMsg(Throwable exp)  {

        return "Hello Aravind - this is the fallback";

    }
}
