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

package org.springframework.security.web.server.authentication.ott;

import reactor.core.publisher.Mono;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ott.reactive.ReactiveOneTimeTokenService;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

/**
 * {@link WebFilter} implementation that process a One-Time Token generation request.
 *
 * @author Max Batischev
 * @since 6.4
 * @see ReactiveOneTimeTokenService
 */
public final class GenerateOneTimeTokenWebFilter implements WebFilter {

	private final ReactiveOneTimeTokenService oneTimeTokenService;

	private ServerWebExchangeMatcher matcher = ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/ott/generate");

	private ServerGenerateOneTimeTokenRequestResolver generateRequestResolver = new DefaultServerGenerateOneTimeTokenRequestResolver();

	private final ServerOneTimeTokenGenerationSuccessHandler oneTimeTokenGenerationSuccessHandler;

	public GenerateOneTimeTokenWebFilter(ReactiveOneTimeTokenService oneTimeTokenService,
			ServerOneTimeTokenGenerationSuccessHandler oneTimeTokenGenerationSuccessHandler) {
		Assert.notNull(oneTimeTokenGenerationSuccessHandler, "oneTimeTokenGenerationSuccessHandler cannot be null");
		Assert.notNull(oneTimeTokenService, "oneTimeTokenService cannot be null");
		this.oneTimeTokenGenerationSuccessHandler = oneTimeTokenGenerationSuccessHandler;
		this.oneTimeTokenService = oneTimeTokenService;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		// @formatter:off
		return this.matcher.matches(exchange)
				.filter(ServerWebExchangeMatcher.MatchResult::isMatch)
				.flatMap((result) -> this.generateRequestResolver.resolve(exchange))
				.switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
				.flatMap(this.oneTimeTokenService::generate)
				.flatMap((token) -> this.oneTimeTokenGenerationSuccessHandler.handle(exchange, token));
		// @formatter:on
	}

	/**
	 * Use the given {@link ServerWebExchangeMatcher} to match the request.
	 * @param matcher
	 */
	public void setRequestMatcher(ServerWebExchangeMatcher matcher) {
		Assert.notNull(matcher, "matcher cannot be null");
		this.matcher = matcher;
	}

	/**
	 * Use the given {@link ServerGenerateOneTimeTokenRequestResolver} to resolve the
	 * request, defaults to {@link DefaultServerGenerateOneTimeTokenRequestResolver}
	 * @param requestResolver {@link ServerGenerateOneTimeTokenRequestResolver}
	 * @since 6.5
	 */
	public void setGenerateRequestResolver(ServerGenerateOneTimeTokenRequestResolver requestResolver) {
		Assert.notNull(requestResolver, "requestResolver cannot be null");
		this.generateRequestResolver = requestResolver;
	}

}
