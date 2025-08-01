/*
 * Copyright 2004-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.oauth2.client.oidc.web.logout;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.TestClientRegistrations;
import org.springframework.security.oauth2.core.oidc.user.TestOidcUsers;
import org.springframework.security.oauth2.core.user.TestOAuth2Users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link OidcClientInitiatedLogoutSuccessHandler}
 */
@ExtendWith(MockitoExtension.class)
public class OidcClientInitiatedLogoutSuccessHandlerTests {

	// @formatter:off
	ClientRegistration registration = TestClientRegistrations
			.clientRegistration()
			.providerConfigurationMetadata(Collections.singletonMap("end_session_endpoint", "https://endpoint"))
			.build();
	// @formatter:on

	ClientRegistrationRepository repository = new InMemoryClientRegistrationRepository(this.registration);

	MockHttpServletRequest request;

	MockHttpServletResponse response;

	OidcClientInitiatedLogoutSuccessHandler handler;

	@BeforeEach
	public void setup() {
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
		this.handler = new OidcClientInitiatedLogoutSuccessHandler(this.repository);
	}

	@Test
	public void logoutWhenOidcRedirectUrlConfiguredThenRedirects() throws IOException, ServletException {
		OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(TestOidcUsers.create(),
				AuthorityUtils.NO_AUTHORITIES, this.registration.getRegistrationId());
		this.request.setUserPrincipal(token);
		this.handler.onLogoutSuccess(this.request, this.response, token);
		assertThat(this.response.getRedirectedUrl()).isEqualTo("https://endpoint?id_token_hint=id-token");
	}

	@Test
	public void logoutWhenNotOAuth2AuthenticationThenDefaults() throws IOException, ServletException {
		Authentication token = mock(Authentication.class);
		this.request.setUserPrincipal(token);
		this.handler.setDefaultTargetUrl("https://default");
		this.handler.onLogoutSuccess(this.request, this.response, token);
		assertThat(this.response.getRedirectedUrl()).isEqualTo("https://default");
	}

	@Test
	public void logoutWhenNotOidcUserThenDefaults() throws IOException, ServletException {
		OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(TestOAuth2Users.create(),
				AuthorityUtils.NO_AUTHORITIES, this.registration.getRegistrationId());
		this.request.setUserPrincipal(token);
		this.handler.setDefaultTargetUrl("https://default");
		this.handler.onLogoutSuccess(this.request, this.response, token);
		assertThat(this.response.getRedirectedUrl()).isEqualTo("https://default");
	}

	@Test
	public void logoutWhenClientRegistrationHasNoEndSessionEndpointThenDefaults() throws Exception {
		ClientRegistration registration = TestClientRegistrations.clientRegistration().build();
		ClientRegistrationRepository repository = new InMemoryClientRegistrationRepository(registration);
		OidcClientInitiatedLogoutSuccessHandler handler = new OidcClientInitiatedLogoutSuccessHandler(repository);
		OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(TestOidcUsers.create(),
				AuthorityUtils.NO_AUTHORITIES, registration.getRegistrationId());
		this.request.setUserPrincipal(token);
		handler.setDefaultTargetUrl("https://default");
		handler.onLogoutSuccess(this.request, this.response, token);
		assertThat(this.response.getRedirectedUrl()).isEqualTo("https://default");
	}

	@Test
	public void logoutWhenUsingPostLogoutBaseUrlRedirectUriTemplateThenBuildsItForRedirect()
			throws IOException, ServletException {
		OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(TestOidcUsers.create(),
				AuthorityUtils.NO_AUTHORITIES, this.registration.getRegistrationId());
		this.handler.setPostLogoutRedirectUri("{baseUrl}");
		this.request.setScheme("https");
		this.request.setServerPort(443);
		this.request.setServerName("rp.example.org");
		this.request.setUserPrincipal(token);
		this.handler.onLogoutSuccess(this.request, this.response, token);
		assertThat(this.response.getRedirectedUrl()).isEqualTo(
				"https://endpoint?" + "id_token_hint=id-token&" + "post_logout_redirect_uri=https://rp.example.org");
	}

	@Test
	public void logoutWhenUsingPostLogoutRedirectUriTemplateThenBuildsItForRedirect()
			throws IOException, ServletException {
		OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(TestOidcUsers.create(),
				AuthorityUtils.NO_AUTHORITIES, this.registration.getRegistrationId());
		this.handler.setPostLogoutRedirectUri("{baseScheme}://{baseHost}{basePort}{basePath}");
		this.request.setScheme("https");
		this.request.setServerPort(443);
		this.request.setServerName("rp.example.org");
		this.request.setUserPrincipal(token);
		this.handler.onLogoutSuccess(this.request, this.response, token);
		assertThat(this.response.getRedirectedUrl()).isEqualTo(
				"https://endpoint?" + "id_token_hint=id-token&" + "post_logout_redirect_uri=https://rp.example.org");
	}

	@Test
	public void logoutWhenUsingPostLogoutRedirectUriTemplateWithOtherPortThenBuildsItForRedirect()
			throws IOException, ServletException {
		OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(TestOidcUsers.create(),
				AuthorityUtils.NO_AUTHORITIES, this.registration.getRegistrationId());
		this.handler.setPostLogoutRedirectUri("{baseScheme}://{baseHost}{basePort}{basePath}");
		this.request.setScheme("https");
		this.request.setServerPort(400);
		this.request.setServerName("rp.example.org");
		this.request.setUserPrincipal(token);
		this.handler.onLogoutSuccess(this.request, this.response, token);
		assertThat(this.response.getRedirectedUrl()).isEqualTo("https://endpoint?" + "id_token_hint=id-token&"
				+ "post_logout_redirect_uri=https://rp.example.org:400");
	}

	@Test
	public void logoutWhenUsingPostLogoutRedirectUriTemplateThenBuildsItForRedirectExpanded()
			throws IOException, ServletException {
		OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(TestOidcUsers.create(),
				AuthorityUtils.NO_AUTHORITIES, this.registration.getRegistrationId());
		this.handler.setPostLogoutRedirectUri("{baseUrl}/{registrationId}");
		this.request.setScheme("https");
		this.request.setServerPort(443);
		this.request.setServerName("rp.example.org");
		this.request.setUserPrincipal(token);
		this.handler.onLogoutSuccess(this.request, this.response, token);
		assertThat(this.response.getRedirectedUrl()).isEqualTo(String.format(
				"https://endpoint?" + "id_token_hint=id-token&" + "post_logout_redirect_uri=https://rp.example.org/%s",
				this.registration.getRegistrationId()));
	}

	// gh-9511
	@Test
	public void logoutWhenUsingPostLogoutRedirectUriWithQueryParametersThenBuildsItForRedirect()
			throws IOException, ServletException {
		OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(TestOidcUsers.create(),
				AuthorityUtils.NO_AUTHORITIES, this.registration.getRegistrationId());
		this.handler.setPostLogoutRedirectUri("https://rp.example.org/context?forwardUrl=secured%3Fparam%3Dtrue");
		this.request.setUserPrincipal(token);
		this.handler.onLogoutSuccess(this.request, this.response, token);
		assertThat(this.response.getRedirectedUrl()).isEqualTo("https://endpoint?id_token_hint=id-token&"
				+ "post_logout_redirect_uri=https://rp.example.org/context?forwardUrl%3Dsecured%253Fparam%253Dtrue");
	}

	@Test
	public void setPostLogoutRedirectUriTemplateWhenGivenNullThenThrowsException() {
		assertThatIllegalArgumentException().isThrownBy(() -> this.handler.setPostLogoutRedirectUri((String) null));
	}

}
