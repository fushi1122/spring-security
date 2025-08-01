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

package org.springframework.security.saml2.jackson2;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.springframework.security.saml2.core.Saml2Error;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationException;

/**
 * This mixin class is used to serialize/deserialize {@link Saml2AuthenticationException}.
 *
 * @author Ulrich Grave
 * @since 5.7
 * @see Saml2AuthenticationException
 * @see Saml2Jackson2Module
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE,
		isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true, value = { "cause", "stackTrace", "suppressedExceptions" })
abstract class Saml2AuthenticationExceptionMixin {

	@JsonProperty("error")
	abstract Saml2Error getSaml2Error();

	@JsonProperty("detailMessage")
	abstract String getMessage();

	@JsonCreator
	Saml2AuthenticationExceptionMixin(@JsonProperty("error") Saml2Error error,
			@JsonProperty("detailMessage") String message) {
	}

}
