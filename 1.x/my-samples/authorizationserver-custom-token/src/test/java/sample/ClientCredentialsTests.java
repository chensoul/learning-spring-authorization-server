package sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClientCredentialsTests {
	private static final Logger LOG = LoggerFactory.getLogger(ClientCredentialsTests.class);

	@LocalServerPort
	private int serverPort;

	@Test
	void start() throws JsonProcessingException {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		httpHeaders.setBasicAuth("messaging-client", "secret");
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.put(OAuth2ParameterNames.GRANT_TYPE,
			Collections.singletonList(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()));
		requestBody.put(OAuth2ParameterNames.SCOPE,
			Collections.singletonList("openid profile message.read message.write"));
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

		Map map = restTemplate.postForObject(String.format("http://127.0.0.1:%d/oauth2/token", serverPort), httpEntity,
			Map.class);

		assertNotNull(map);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

		LOG.info("token:\n{}", objectWriter.writeValueAsString(map));

		assertNotNull(map.get(OAuth2ParameterNames.ACCESS_TOKEN));
		assertNotNull(map.get(OAuth2ParameterNames.SCOPE));
		assertNotNull(map.get(OAuth2ParameterNames.TOKEN_TYPE));
		assertNotNull(map.get(OAuth2ParameterNames.EXPIRES_IN));

		String accessToken = map.get(OAuth2ParameterNames.ACCESS_TOKEN).toString();

		String[] split = accessToken.split("\\.");
		assertEquals(split.length, 3);

		String payloadEncode = split[1];
		String payloadDecode = new String(Base64.getDecoder().decode(payloadEncode));
		Map payload = objectMapper.readValue(payloadDecode, Map.class);
		LOG.info("payload:\n{}", objectWriter.writeValueAsString(payload));

		assertNotNull(payload.get(OAuth2TokenIntrospectionClaimNames.SUB));
		assertNotNull(payload.get(OAuth2TokenIntrospectionClaimNames.AUD));
		assertNotNull(payload.get(OAuth2TokenIntrospectionClaimNames.NBF));
		assertNotNull(payload.get(OAuth2TokenIntrospectionClaimNames.SCOPE));
		assertNotNull(payload.get(OAuth2TokenIntrospectionClaimNames.ISS));
		assertNotNull(payload.get(OAuth2TokenIntrospectionClaimNames.EXP));
		assertNotNull(payload.get(OAuth2TokenIntrospectionClaimNames.IAT));
		assertNotNull(payload.get("authorities"));

		// 凭证式模式：
		// sub：代表用户名，由于凭证式是自己给自己授权，所以 sub 和 aud 相同，都是 客户ID
		// aud：代表客户ID
		assertEquals(payload.get(OAuth2TokenIntrospectionClaimNames.SUB), "messaging-client");
		assertEquals(payload.get(OAuth2TokenIntrospectionClaimNames.AUD), "messaging-client");
	}

}
