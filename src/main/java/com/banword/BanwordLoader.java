package com.banword;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class BanwordLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> loadBanword(String filePath) throws Exception {
        if (filePath.endsWith(".json")) {
            return loadFromJson(filePath);
//        } else if (filePath.endsWith(".xml")) {
//            return loadFromXml(filePath);
        } else if (filePath.endsWith(".txt")) {
            return loadFromTxt(filePath);
        }

        throw new IllegalArgumentException("unsupported file type : " + filePath.endsWith("."));
    }

    private List<String> loadFromJson(String filePath) throws IOException {
        LinkedHashMap<String, List<String>> config = objectMapper.readValue(new File(filePath), LinkedHashMap.class);
        return config.getOrDefault("banwords", new ArrayList<>());
    }

//    private List<String> loadFromXml(String filePath) throws Exception {
//        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//        Document doc = dBuilder.parse(new File(filePath));
//        doc.getDocumentElement().normalize();
//        List<String> banwords = new ArrayList<>();
//        NodeList banword = doc.getElementsByTagName("banword");
//
//    }

    private List<String> loadFromTxt(String filePath) throws IOException {
        return Files.readAllLines(new File(filePath).toPath());
    }
}
