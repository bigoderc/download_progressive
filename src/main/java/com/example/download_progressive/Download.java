package com.example.download_progressive;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class Download {
	private final JdbcTemplate jdbcTemplate;

    public Download(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
	@GetMapping("/downloadFile/{ids}")
    public ResponseEntity<StreamingResponseBody> consultarPacientes(@PathVariable String ids) {
    	String pathSql = "select * from filesystem";
        List<Map<String, Object>> paths = jdbcTemplate.queryForList(pathSql);
        Object diretorio = null; // Defina a variável fora do loop
        for (Map<String, Object> path : paths) {
        	diretorio = path.get("dirpath"); // Atribua o valor da coluna "nome"
            break; // Para no primeiro elemento, se for suficiente

            
        }
        
        String directory = diretorio.toString();
        String download_name = "mlaudos";
        if (ids != null && !ids.isEmpty()) {
            // Divida a string de IDs em um array
            String[] idArray = ids.split(",");
            if (idArray.length == 1) {
    	        String PacienteFindSql = "select mp.nome_paciente, p.pk, s.pk as study, count(distinct se.pk) as series_count, count(se.pk) as instancias_count from mm_paciente mp "
    	                + "inner join patient p on (p.pk = mp.cod_paciente) "
    	                + "inner join study s on (s.patient_fk = p.pk) "
    	                + "inner join series se on (se.study_fk = s.pk) "
    	                + "inner join instance i on (i.series_fk = se.pk) "
    	                + "inner join files f on (f.instance_fk = i.pk) "
    	                + "where p.pk in("+ids+")" // Use o parâmetro :ids na consulta
    	                + "group by p.pk, mp.nome_paciente, s.pk";
    	        List<Map<String, Object>> pacienteFind = jdbcTemplate.queryForList(PacienteFindSql);
    	        for (Map<String, Object> find : pacienteFind) {
    	        	download_name = find.get("nome_paciente").toString(); // Atribua o valor da coluna "nome"
    	            break; // Para no primeiro elemento, se for suficiente
    	
    	            
    	        }
            }
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+download_name+".zip");
        ResponseEntity<StreamingResponseBody> responseEntity = new ResponseEntity<>(outputStream -> {
            try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
               
                byte[] buffer = new byte[1024];
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
                        	FileInputStream fis = new FileInputStream(directory+'/'+file.get("filepath"));
                        	// Encontre a última ocorrência da barra invertida no caminho
                        	String filePath = file.get("filepath").toString();

                        	int lastIndex = filePath.lastIndexOf("/");
                        	String lastPart = null;
                        	if (lastIndex != -1) {
                        	    // Use substring para obter a parte do caminho após a última barra
                        	    lastPart = filePath.substring(lastIndex + 1);
                        	    
                        	} 
                            zipOut.putNextEntry(new ZipEntry(paciente.get("nome_paciente").toString()+"/"+serie.get("pk").toString()+"/"+lastPart));
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                zipOut.write(buffer, 0, length);
                            }
                            zipOut.closeEntry();
                            fis.close();
                            result.append(file).append("\n");
                        }
                    }
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, headers, HttpStatus.OK);

        return responseEntity;
    }
}
