package com.example.zip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PacienteService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> consultarArquivos() {
        String query = "SELECT name,email FROM users";
        return jdbcTemplate.queryForList(query);
    }
    public String obterNomeBancoDeDados() {
        return jdbcTemplate.queryForObject("SELECT current_database()", String.class);
    }
}
