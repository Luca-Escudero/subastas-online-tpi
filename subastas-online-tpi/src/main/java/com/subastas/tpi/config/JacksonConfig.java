package com.subastas.tpi.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class JacksonConfig {

    @Bean
    public com.fasterxml.jackson.databind.Module localDateTimeModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new UtcLocalDateTimeDeserializer());
        module.addSerializer(LocalDateTime.class, new UtcLocalDateTimeSerializer());
        return module;
    }

    public static class UtcLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getText();
            if (text == null || text.trim().isEmpty()) {
                return null;
            }
            try {
                // Intenta parsear con offset (ej: 2026-06-30T19:58:00-03:00 o 2026-06-30T22:58:00Z)
                OffsetDateTime odt = OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                // Si tiene offset, lo convierte a UTC y devuelve el LocalDateTime en UTC
                return odt.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
            } catch (DateTimeParseException e1) {
                try {
                    // Si no tiene offset, intenta parsear como LocalDateTime plano (ej: 2026-06-30T19:58:00)
                    LocalDateTime localDateTime = LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    // Asumimos que viene en la zona horaria del sistema y la convertimos a UTC
                    return localDateTime.atZone(ZoneId.systemDefault())
                                        .withZoneSameInstant(ZoneOffset.UTC)
                                        .toLocalDateTime();
                } catch (DateTimeParseException e2) {
                    throw new IOException("Error al parsear fecha: " + text, e2);
                }
            }
        }
    }

    public static class UtcLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value != null) {
                // Dado que nuestras fechas internas están en UTC, les agregamos la 'Z' al serializarlas
                // para que el cliente (front/Swagger) sepa que están en UTC y pueda convertirlas localmente.
                String formatted = value.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                gen.writeString(formatted);
            }
        }
    }
}
