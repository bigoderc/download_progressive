package com.example.zip;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;

@RestController
public class MyController {
	private final JdbcTemplate jdbcTemplate;

    public MyController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private static final Logger logger = LoggerFactory.getLogger(MyController.class);
    
    @Autowired
    private DataSource dataSource;
    @GetMapping("/verificarConexao")
    public String verificarConexao() {
    	try {
            Connection connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseName = metaData.getDatabaseProductName();
            connection.close();
            logger.info("Conexão bem-sucedida com o banco de dados: " + databaseName);
            return "Conexão com o banco de dados bem-sucedida. Banco de dados: " + databaseName;
        } catch (SQLException e) {
            logger.error("Erro ao conectar ao banco de dados: " + e.getMessage());
            return "Erro ao conectar ao banco de dados: " + e.getMessage();
        }
    }
    @GetMapping("/consultarPacientes/{ids}")
    public String consultarPacientes(@PathVariable String ids) {
       // Divida a string de IDs separados por vírgula em um array
       

        try {
        	// Construa a consulta SQL com base no array de IDs
            String sql = "select mp.nome_paciente, p.pk, s.pk as study, count(distinct se.pk) as series_count, count(se.pk) as instancias_count from mm_paciente mp "
                    + "inner join patient p on (p.pk = mp.cod_paciente) "
                    + "inner join study s on (s.patient_fk = p.pk) "
                    + "inner join series se on (se.study_fk = s.pk) "
                    + "inner join instance i on (i.series_fk = se.pk) "
                    + "inner join files f on (f.instance_fk = i.pk) "
                    + "where p.pk in("+ids+")" // Use o parâmetro :ids na consulta
                    + "group by p.pk, mp.nome_paciente, s.pk";

           
           
            List<Map<String, Object>> pacientes = jdbcTemplate.queryForList(sql);

            StringBuilder result = new StringBuilder();
            for (Map<String, Object> paciente : pacientes) {
                Object nome = paciente.get("nome_paciente"); // Obtenha o valor da coluna "nome"
                result.append("Nome do paciente: ").append(nome).append("\n");

                // Adicione a consulta adicional para obter séries com base no estudo do paciente
                String seriesSql = "select se.* from series se where se.study_fk = " + paciente.get("study");
                List<Map<String, Object>> series = jdbcTemplate.queryForList(seriesSql);

                result.append("Séries do paciente:\n");
                for (Map<String, Object> serie : series) {
                    // Adicione aqui o processamento da série (você pode adicionar as informações que deseja)
                	result.append(serie).append("\n");
                    // Adicione a consulta adicional para obter os arquivos com base na série
                    String filesSql = "select f.* from instance i "
                            + "inner join files f on (f.instance_fk = i.pk) "
                            + "where i.series_fk = " + serie.get("pk");
                    List<Map<String, Object>> files = jdbcTemplate.queryForList(filesSql);

                    result.append("Arquivos da série:\n");
                    for (Map<String, Object> file : files) {
                        // Adicione aqui o processamento dos arquivos (você pode adicionar as informações que deseja)
                        result.append(file).append("\n");
                    }
                }
            }

            System.out.println(result.toString()); // Imprime os nomes dos pacientes, séries e arquivos

            return "Consulta realizada com sucesso.";
        } catch (DataAccessException e) {
        	logger.error("Erro ao executar a consulta: " + e.getMessage(), e);
            return "Erro ao executar a consulta: " + e.getMessage();
        }
    }


}
