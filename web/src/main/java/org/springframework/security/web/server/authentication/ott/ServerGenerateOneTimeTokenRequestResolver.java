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

import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.web.server.ServerWebExchange;

/**
 * A strategy for resolving a {@link GenerateOneTimeTokenRequest} from the
 * {@link ServerWebExchange}.
 *
 * @author Max Batischev
 * @since 6.5
 */
public interface ServerGenerateOneTimeTokenRequestResolver {

	/**
	 * Resolves {@link GenerateOneTimeTokenRequest} from {@link ServerWebExchange}
	 * @param exchange {@link ServerWebExchange} to resolve
	 * @return {@link GenerateOneTimeTokenRequest}
	 */
	Mono<GenerateOneTimeTokenRequest> resolve(ServerWebExchange exchange);

}
