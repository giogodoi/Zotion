package com.zotion.backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zotion.backend.models.Lembrete;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.zotion.backend.dto.GeminiLembreteDTO;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;


//OBS: Existe outra maneira de fazermos a integração utilizando o SDK oficial do Google
//Porém, optei por fazer via REST para manter o projeto mais simples e funcional para a nossa aplicação.
//Por questões de custo e token de requisição, creio que essa abordagem só irá funcionar em pdf's que possuam textos a serem extraidos
//Caso seja enviado um pdf para a LLM, o custo do modal pode ser muito alto e irá exceder o limite de tokens permitidos na requisição.
// Aqui tentei manter o DRY, delegando a função de extrair o texto para o serviço LeitorPdfService.

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final LeitorPdfService leitorPdfService;
    private final String API_URL_BASE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=";

    public GeminiService(LeitorPdfService leitorPdfService) {
        this.leitorPdfService = leitorPdfService;
    }

    public List<Lembrete> extrairLembretesDeArquivo(byte[] arquivoBytes) {
        String textoExtraido = leitorPdfService.extrairTextoDeBytes(arquivoBytes);
        List<GeminiLembreteDTO> dtos;

        if (textoExtraido != null && !textoExtraido.trim().isEmpty()) {
            System.out.println("Texto detectado localmente. Usando modo Texto.");
            dtos = processarViaTexto(textoExtraido);
        } else {
            System.out.println("PDF sem texto (imagem). Usando modo Multimodal.");
            dtos = processarViaMultimodal(arquivoBytes);
        }

        return converterParaEntidades(dtos);
    }

    private List<GeminiLembreteDTO> processarViaTexto(String texto) {
        String instrucao = "Extraia tarefas deste texto. Caso não encontre o horário, use 06:00:00. " +
                "Retorne APENAS um array JSON puro, sem markdown. " +
                "Modelo: [{\"nome\": \"...\", \"descricao\": \"...\", \"dataHora\": \"YYYY-MM-DDTHH:MM:SS\", \"prioridade\": 1 para leve, 2 para media e 3 para alta}] \n\n" + 
                "Texto: " + texto;

        var requestBody = Map.of(
            "contents", List.of(Map.of("parts", List.of(Map.of("text", instrucao))))
        );
        return chamarApi(requestBody);
    }

    private List<GeminiLembreteDTO> processarViaMultimodal(byte[] arquivoBytes) {
        String base64Arquivo = Base64.getEncoder().encodeToString(arquivoBytes);
        String instrucao = "Analise este documento e extraia tarefas em formato JSON puro. " +
                "Modelo: [{\"nome\": \"...\", \"descricao\": \"...\", \"dataHora\": \"YYYY-MM-DDTHH:MM:SS\", \"prioridade\": 1 para leve, 2 para media e 3 para alta}]";

        var requestBody = Map.of(
            "contents", List.of(Map.of("parts", List.of(
                Map.of("text", instrucao),
                Map.of("inline_data", Map.of("mime_type", "application/pdf", "data", base64Arquivo))
            )))
        );
        return chamarApi(requestBody);
    }

    private List<GeminiLembreteDTO> chamarApi(Map<String, ?> body) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        try {
            ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<Map<String, Object>>() {};

            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                API_URL_BASE + apiKey,
                org.springframework.http.HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(body),
                responseType
            );

            Map<String, Object> response = responseEntity.getBody();
            
            if (response == null || !response.containsKey("candidates")) {
                return new ArrayList<>();
            }

            List<?> candidates = (List<?>) response.get("candidates");
            Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
            Map<?, ?> content = (Map<?, ?>) candidate.get("content");
            List<?> parts = (List<?>) content.get("parts");
            Map<?, ?> part = (Map<?, ?>) parts.get(0);
            
            String jsonResposta = (String) part.get("text");
            jsonResposta = jsonResposta.replace("```json", "").replace("```", "").trim();

            return mapper.readValue(jsonResposta, new TypeReference<List<GeminiLembreteDTO>>() {});
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Lembrete> converterParaEntidades(List<GeminiLembreteDTO> dtos) {
        List<Lembrete> lembretes = new ArrayList<>();
        for (GeminiLembreteDTO dto : dtos) {
            Lembrete lembrete = new Lembrete();
            lembrete.setNome(dto.getNome());
            lembrete.setDescricao(dto.getDescricao());
            lembrete.setDataHora(dto.getDataHora());
            lembrete.setPrioridade(dto.getPrioridade());
            lembrete.setRealizada(false);
            lembretes.add(lembrete);
        }
        return lembretes;
    }
}