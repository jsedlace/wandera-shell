package cz.geek.wandera.shell.rest;

import org.springframework.stereotype.Component;

@Component
public class WanderaKeys {

	private String apiKey;

	private String secretKey;

	public WanderaKeys() {
	}

	public WanderaKeys(String apiKey, String secretKey) {
		this.apiKey = apiKey;
		this.secretKey = secretKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getApiKey() {
		return apiKey;
	}
}