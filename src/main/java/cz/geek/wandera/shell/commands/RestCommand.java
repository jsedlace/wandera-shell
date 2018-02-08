package cz.geek.wandera.shell.commands;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import cz.geek.wandera.shell.rest.RestService;

@ShellComponent
public class RestCommand {

	private static final List<MediaType> JSON_TYPE = asList(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));

	private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	private final RestService service;

	public RestCommand(RestService service) {
		this.service = requireNonNull(service);
	}

	@ShellMethod("Issue GET request")
	public String get(String uri) throws IOException {
		ResponseEntity<byte[]> response = service.get(uri, byte[].class);
		return processResponse(response, null, false);
	}

	private String processResponse(ResponseEntity<byte[]> response, Path target, boolean raw) throws IOException {
		if (response.getBody() == null) {
			return "Status: " + response.getStatusCode();
		} else if (target != null) {
			try (OutputStream output = Files.newOutputStream(target)) {
				final int length = IOUtils.copy(new ByteArrayInputStream(response.getBody()), output);
				return "Status: " + response.getStatusCode() + " " + target.toAbsolutePath() + ": " + length + " bytes";
			}
		} else if (!raw && isCompatible(response.getHeaders())) {
			final JsonNode tree = mapper.readTree(response.getBody());
			return mapper.writeValueAsString(tree);
		} else {
			return new String(response.getBody());
		}
	}

	private static boolean isCompatible(final HttpHeaders headers) {
		MediaType contentType = headers.getContentType();
		return JSON_TYPE.stream()
				.anyMatch(mediaType -> mediaType.isCompatibleWith(contentType));
	}

}