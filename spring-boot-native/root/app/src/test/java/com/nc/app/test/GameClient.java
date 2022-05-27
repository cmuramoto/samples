package com.nc.app.test;

import org.junit.Assert;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.nc.domain.frontend.api.v1.ApiConstraints;
import com.nc.domain.frontend.api.v1.ApiRound;
import com.nc.domain.frontend.api.v1.ApiUser;
import com.nc.domain.frontend.api.v1.FinishMatchRequest;
import com.nc.domain.frontend.api.v1.FinishMatchResponse;
import com.nc.domain.frontend.api.v1.FinishRoundRequest;
import com.nc.domain.frontend.api.v1.FinishRoundResponse;
import com.nc.domain.frontend.api.v1.GetOrStartGameRequest;
import com.nc.domain.frontend.api.v1.GetOrStartGameResponse;
import com.nc.domain.frontend.api.v1.TopPlayerRequest;
import com.nc.domain.frontend.api.v1.TopPlayerResponse;
import com.nc.domain.frontend.services.v1.MovieGameService;
import com.nc.domain.frontend.stable.JwtRequest;
import com.nc.domain.frontend.stable.JwtResponse;
import com.nc.domain.internal.DomainException;
import com.nc.utils.json.JSON;

public class GameClient implements MovieGameService {

	static HttpMessageConverter<?> byObjecFieldConverter() {
		var converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(JSON.mapper());
		return converter;

	}

	private final String user;
	private final String pass;
	private final TestRestTemplate template;
	private final String authenticationURL;
	private final String baseURL;
	private final String apiDocsURL;
	private final String openApiURL;

	private String jwtToken;

	public GameClient(String user, String pass, int port) {
		this(user, pass, "http", "localhost", port);
	}

	public GameClient(String user, String pass, String protocol, String host, int port) {
		super();
		this.user = user;
		this.pass = pass;
		this.template = new TestRestTemplate();
		this.authenticationURL = String.format("%s://%s:%d/v1/authenticate", protocol, host, port);
		this.baseURL = String.format("%s://%s:%d/v1/game", protocol, host, port);
		this.apiDocsURL = String.format("%s://%s:%d/v3/api-docs", protocol, host, port);
		this.openApiURL = String.format("%s://%s:%d/swagger-ui/index.html", protocol, host, port);
	}

	public String apiDocs() {
		return getOrThrow(this.template.getForEntity(apiDocsURL, String.class));
	}

	private void authenticate() {
		if (jwtToken == null) {
			var req = new JwtRequest();
			req.setUsername(user);
			req.setPassword(pass);
			var entity = template.postForEntity(this.authenticationURL, createRequest(req, false), JwtResponse.class);

			if (entity.getStatusCode().is2xxSuccessful()) {
				this.jwtToken = entity.getBody().getJwtToken();
			} else {
				Assert.fail(String.format("Could not authenticate with (%s,%s)", user, pass));
			}
		}
	}

	@Override
	public ApiConstraints changeConstraints(ApiConstraints request) {
		var path = baseURL + "/constraints";

		var entity = this.template.postForEntity(path, createRequest(request), ApiConstraints.class);

		return getOrThrow(entity);
	}

	<T> HttpEntity<T> createRequest(T request) {
		return createRequest(request, true);
	}

	<T> HttpEntity<T> createRequest(T request, boolean authenticated) {
		var headers = new HttpHeaders();
		if (authenticated) {
			this.authenticate();
			Assert.assertNotNull(jwtToken);
			headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
		}
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		var entity = new HttpEntity<>(request, headers);

		return entity;
	}

	@Override
	public ApiRound current() {
		var path = baseURL + "/round/current";

		return getOrThrow(template.exchange(path, HttpMethod.GET, createRequest(null), ApiRound.class));
	}

	@Override
	public FinishMatchResponse finish(FinishMatchRequest request) {
		var path = baseURL + "/finish";

		var entity = this.template.postForEntity(path, createRequest(request), FinishMatchResponse.class);

		return getOrThrow(entity);
	}

	@Override
	public FinishRoundResponse finish(FinishRoundRequest request) {
		var path = baseURL + "/round/finish";

		var entity = this.template.postForEntity(path, createRequest(request), FinishRoundResponse.class);

		return getOrThrow(entity);
	}

	@Override
	public ApiConstraints getConstraints() {
		var path = baseURL + "/constraints";

		return getOrThrow(template.exchange(path, HttpMethod.GET, createRequest(null), ApiConstraints.class));
	}

	<T> T getOrThrow(ResponseEntity<T> entity) {

		if (entity.getStatusCode().is2xxSuccessful()) {
			return entity.getBody();
		}

		if (entity.getStatusCodeValue() == 417) {
			throw new DomainException("Business Rule violated", null);
		}

		throw new RuntimeException("Unexpected Error " + entity.getStatusCodeValue());
	}

	@Override
	public ApiUser me() {
		var path = baseURL + "/me";

		return getOrThrow(template.exchange(path, HttpMethod.GET, createRequest(null), ApiUser.class));
	}

	@Override
	public GetOrStartGameResponse start(GetOrStartGameRequest request) {
		var path = baseURL + "/start";

		var entity = this.template.postForEntity(path, createRequest(request), GetOrStartGameResponse.class);

		return getOrThrow(entity);
	}

	public String swaggerUI(String subpath) {
		var uri = subpath == null || subpath.isBlank() ? openApiURL : openApiURL + subpath;

		return getOrThrow(this.template.getForEntity(uri, String.class));
	}

	@Override
	public TopPlayerResponse top(TopPlayerRequest request) {
		var path = baseURL + "/top";

		var entity = this.template.postForEntity(path, createRequest(request), TopPlayerResponse.class);

		return getOrThrow(entity);
	}

}
