package com.example.zip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/consultar")
public class PacienteController {

    @Autowired
    private PacienteService consultaArquivosService;

    @GetMapping("/arquivos")
    public List<Map<String, Object>> consultarArquivos() {
        return consultaArquivosService.consultarArquivos();
    }
    @GetMapping("/nomeBancoDeDados")
    public String consultarBancoDeDados() {
        String nomeBancoDeDados = consultaArquivosService.obterNomeBancoDeDados();
        return "Nome do banco de dados: " + nomeBancoDeDados;
    }
}
