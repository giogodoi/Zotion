package com.zotion.backend.services;

import java.io.IOException;

//Fiz uma analise de viabilidade para usarmos uma LLM nesse projeto e, como a ideia é enviar arquivos, o modal de envio para a LLM ficaria muito "Caro" em termos de tokens.
//Esse serviço serve para, se existir texto selecionavel no PDF, extrair esse texto e enviar para a LLM, ao invés de enviar o PDF inteiro.
//Isso permite usarmos um plano gratuito na LLM, reduzindo os custos do projeto.

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LeitorPdfService {
    
    public String extrairTexto(MultipartFile arquivo) {


        if (arquivo.isEmpty() || !arquivo.getContentType().equals("application/pdf")) {
            throw new RuntimeException("Arquivo inválido. Por favor, envie um arquivo PDF.");
        }

        try (PDDocument documento = Loader.loadPDF(arquivo.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(documento);
        } catch (IOException e) {
            throw new RuntimeException ("Erro ao ler o conteúdo do PDF." + e.getMessage());
        }

    }

    public String extrairTextoDeBytes(byte[] arquivoBytes) {
        try (PDDocument documento = Loader.loadPDF(arquivoBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(documento);
        } catch (IOException e) {
            return null;
        }
    }
}
