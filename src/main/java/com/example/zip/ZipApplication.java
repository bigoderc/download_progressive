package com.example.zip;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.zip.ZipApplication;

import org.springframework.beans.factory.annotation.Value;
@SpringBootApplication
@RestController

@EnableAsync
public class ZipApplication {
	
	@Value("${teste.url}")
    private String testeUrl;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ZipApplication.class, args);
        
        // Acesse a propriedade 'teste.url' usando o contexto da aplicação
        String testeUrl = context.getEnvironment().getProperty("teste.url");
        System.out.println("Valor de teste.url: " + testeUrl);
        
        // Você também pode acessar diretamente a variável do objeto da aplicação
        ZipApplication app = context.getBean(ZipApplication.class);
        System.out.println("Valor de teste.url a partir do objeto: " + app.getTesteUrl());
    }
    
    public String getTesteUrl() {
        return testeUrl;
    }
	@GetMapping("/")
    public String home(@RequestParam(value = "name", defaultValue = "World") String name) {
      return String.format("Hello %s!", name);
    }
}
