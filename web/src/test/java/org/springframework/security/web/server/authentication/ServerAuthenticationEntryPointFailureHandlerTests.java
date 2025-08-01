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

package org.springframework.security.web.server.authentication;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;

/**
 * @author Rob Winch
 * @since 5.0
 */
@ExtendWith(MockitoExtension.class)
public class ServerAuthenticationEntryPointFailureHandlerTests {

	@Mock
	private ServerAuthenticationEntryPoint authenticationEntryPoint;

	@Mock
	private ServerWebExchange exchange;

	@Mock
	private WebFilterChain chain;

	@InjectMocks
	private WebFilterExchange filterExchange;

	@InjectMocks
	private ServerAuthenticationEntryPointFailureHandler handler;

	@Test
	public void constructorWhenNullEntryPointThenException() {
		assertThatIllegalArgumentException().isThrownBy(() -> new ServerAuthenticationEntryPointFailureHandler(null));
	}

	@Test
	public void onAuthenticationFailureWhenInvokedThenDelegatesToEntryPoint() {
		Mono<Void> result = Mono.empty();
		BadCredentialsException e = new BadCredentialsException("Failed");
		given(this.authenticationEntryPoint.commence(this.exchange, e)).willReturn(result);
		assertThat(this.handler.onAuthenticationFailure(this.filterExchange, e)).isEqualTo(result);
	}

	@Test
	void onAuthenticationFailureWhenRethrownFalseThenAuthenticationServiceExceptionSwallowed() {
		AuthenticationServiceException e = new AuthenticationServiceException("fail");
		this.handler.setRethrowAuthenticationServiceException(false);
		given(this.authenticationEntryPoint.commence(this.exchange, e)).willReturn(Mono.empty());
		this.handler.onAuthenticationFailure(this.filterExchange, e).block();
	}

	@Test
	void handleWhenDefaultsThenAuthenticationServiceExceptionRethrown() {
		AuthenticationServiceException e = new AuthenticationServiceException("fail");
		assertThatExceptionOfType(AuthenticationServiceException.class)
			.isThrownBy(() -> this.handler.onAuthenticationFailure(this.filterExchange, e).block());
	}

}
