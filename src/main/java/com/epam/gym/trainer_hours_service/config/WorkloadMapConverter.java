package com.epam.gym.trainer_hours_service.config;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class WorkloadMapConverter implements AttributeConverter<Map<Integer, Map<Integer, Integer>>, String> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Map<Integer, Map<Integer, Integer>> map) {
		try {
			return objectMapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			// Log the error
			return null;
		}
	}

	@Override
	public Map<Integer, Map<Integer, Integer>> convertToEntityAttribute(String json) {
		if (json == null) {
			return null;
		}
		try {
			return objectMapper.readValue(json, new TypeReference<Map<Integer, Map<Integer, Integer>>>() {
			});
		} catch (JsonProcessingException e) {
			// Log the error
			return null;
		}
	}
}
