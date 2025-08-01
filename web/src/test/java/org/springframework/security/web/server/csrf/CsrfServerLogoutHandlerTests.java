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

package org.springframework.security.web.server.csrf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.web.server.WebFilterChain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author Eric Deandrea
 * @since 5.1
 */
@ExtendWith(MockitoExtension.class)
public class CsrfServerLogoutHandlerTests {

	@Mock
	private ServerCsrfTokenRepository csrfTokenRepository;

	@Mock
	private WebFilterChain filterChain;

	private MockServerWebExchange exchange;

	private WebFilterExchange filterExchange;

	private CsrfServerLogoutHandler handler;

	@BeforeEach
	public void setup() {
		this.exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());
		this.filterExchange = new WebFilterExchange(this.exchange, this.filterChain);
		this.handler = new CsrfServerLogoutHandler(this.csrfTokenRepository);
	}

	@Test
	public void constructorNullCsrfTokenRepository() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new CsrfServerLogoutHandler(null))
			.withMessage("csrfTokenRepository cannot be null")
			.withNoCause();
	}

	@Test
	public void logoutRemovesCsrfToken() {
		given(this.csrfTokenRepository.saveToken(this.exchange, null)).willReturn(Mono.empty());
		this.handler.logout(this.filterExchange, new TestingAuthenticationToken("user", "password", "ROLE_USER"))
			.block();
		verify(this.csrfTokenRepository).saveToken(this.exchange, null);
	}

}
