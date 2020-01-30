package com.fedex.geopolitical.custom.annotation;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class StringDeserializer extends JsonDeserializer<String>{


	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = p.readValueAsTree();
		if(node.asText().isEmpty()){
			return null;
		}
		return node.toString().substring(1, node.toString().length()-1);
	}

}
