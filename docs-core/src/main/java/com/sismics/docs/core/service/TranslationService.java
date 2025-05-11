package com.sismics.docs.core.service;

import com.sismics.docs.core.constant.ConfigType;
import com.sismics.docs.core.util.ConfigUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

/**
 * DeepL translation service.
 */
public class TranslationService {
    private static final Logger log = LoggerFactory.getLogger(TranslationService.class);
    private static final String DEEPL_API_URL = "https://api.deepl.com/v2/translate";
    private static TranslationService instance;

    private TranslationService() {
    }

    public static TranslationService getInstance() {
        if (instance == null) {
            instance = new TranslationService();
        }
        return instance;
    }

    /**
     * Format language code for DeepL API.
     * DeepL API expects language codes in the format "EN", "DE", "ZH-CN", etc.
     *
     * @param languageCode Language code to format
     * @return Formatted language code
     */
    private String formatLanguageCode(String languageCode) {
        if (languageCode == null) {
            return null;
        }
        // 保持连字符格式，但确保其他部分大写
        String[] parts = languageCode.split("-");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                result.append("-");
            }
            result.append(parts[i].toUpperCase());
        }
        return result.toString();
    }

    /**
     * Translate text using DeepL API.
     *
     * @param text Text to translate
     * @param sourceLanguage Source language code
     * @param targetLanguage Target language code
     * @return Translated text
     * @throws IllegalStateException if API key is not configured
     * @throws RuntimeException if translation fails
     */
    public String translate(String text, String sourceLanguage, String targetLanguage) {
        if (text == null || text.trim().isEmpty()) {
            log.warn("Empty text provided for translation");
            return text;
        }

        try {
            String apiKey = ConfigUtil.getConfigStringValue(ConfigType.DEEPL_API_KEY);
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.error("DeepL API key not configured");
                throw new IllegalStateException("DeepL API key not configured");
            }

            // 格式化语言代码
            String formattedSourceLang = formatLanguageCode(sourceLanguage);
            String formattedTargetLang = formatLanguageCode(targetLanguage);
            if (formattedTargetLang.contains("-")) {
                formattedTargetLang = formattedTargetLang.split("-")[0];
            }

            log.info("Translating text from {} to {}", formattedSourceLang, formattedTargetLang);

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost request = new HttpPost(DEEPL_API_URL);
                request.setHeader("Authorization", "DeepL-Auth-Key " + apiKey);
                request.setHeader("Content-Type", "application/x-www-form-urlencoded");

                // 使用 URL 编码格式传递数据
                String requestParams = "text=" + URLEncoder.encode(text, StandardCharsets.UTF_8) +
                        "&source_lang=" + formattedSourceLang +
                        "&target_lang=" + formattedTargetLang;
                request.setEntity(new StringEntity(requestParams, StandardCharsets.UTF_8));

                try (CloseableHttpResponse response = client.execute(request)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    HttpEntity entity = response.getEntity();

                    if (entity != null) {
                        String result = EntityUtils.toString(entity);

                        if (statusCode == 200) {
                            JSONObject jsonResponse = new JSONObject(result);
                            String translatedText = jsonResponse.getJSONArray("translations")
                                    .getJSONObject(0)
                                    .getString("text");
                            log.info("Translation completed successfully");
                            return translatedText;
                        } else {
                            log.error("Translation failed with status code {}: {}", statusCode, result);
                            throw new RuntimeException("Translation failed with status code: " + statusCode + " and message: " + result);
                        }
                    } else {
                        log.error("Empty response from DeepL API");
                        throw new RuntimeException("Empty response from DeepL API");
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Error encoding the request parameters", e);
            throw new RuntimeException("Error encoding the request parameters", e);
        } catch (Exception e) {
            log.error("Error translating text", e);
            throw new RuntimeException("Translation failed: " + e.getMessage(), e);
        }
    }
}
