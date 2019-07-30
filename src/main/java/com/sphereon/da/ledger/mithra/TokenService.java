package com.sphereon.da.ledger.mithra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sphereon.da.ledger.mithra.utils.FatToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TokenService {
    private final String jsonConfigFolder;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper jsonMapper;

    private List<FatToken> tokens;

    public TokenService(@Value("${mithra.tokens.folder}") final String jsonConfigFolder,
                        final ResourceLoader resourceLoader,
                        final ObjectMapper jsonMapper) {
        this.jsonConfigFolder = jsonConfigFolder;
        this.resourceLoader = resourceLoader;
        this.jsonMapper = jsonMapper;
        this.tokens = new ArrayList<>();
    }

    @PostConstruct
    public void init() throws IOException {
        for (Resource jsonFile : loadResources(jsonConfigFolder)) {
            FatToken token = jsonMapper.readValue(jsonFile.getInputStream(), FatToken.class);
            tokens.add(token);
        }
    }

    public List<FatToken> getTokens() {
        return tokens;
    }

    private Resource[] loadResources(String pattern) throws IOException {
        return ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(pattern);
    }
}
