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

package org.springframework.security.web.jackson2;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.security.web.csrf.DefaultCsrfToken;

/**
 * Jackson module for spring-security-web. This module register
 * {@link DefaultCsrfTokenMixin}, {@link PreAuthenticatedAuthenticationTokenMixin} and
 * {@link SwitchUserGrantedAuthorityMixIn}. If no default typing enabled by default then
 * it'll enable it because typing info is needed to properly serialize/deserialize
 * objects. In order to use this module just add this module into your ObjectMapper
 * configuration.
 *
 * <pre>
 *     ObjectMapper mapper = new ObjectMapper();
 *     mapper.registerModule(new WebJackson2Module());
 * </pre> <b>Note: use {@link SecurityJackson2Modules#getModules(ClassLoader)} to get list
 * of all security modules.</b>
 *
 * @author Jitendra Singh
 * @since 4.2
 * @see SecurityJackson2Modules
 */
@SuppressWarnings("serial")
public class WebJackson2Module extends SimpleModule {

	public WebJackson2Module() {
		super(WebJackson2Module.class.getName(), new Version(1, 0, 0, null, null, null));
	}

	@Override
	public void setupModule(SetupContext context) {
		SecurityJackson2Modules.enableDefaultTyping(context.getOwner());
		context.setMixInAnnotations(DefaultCsrfToken.class, DefaultCsrfTokenMixin.class);
		context.setMixInAnnotations(PreAuthenticatedAuthenticationToken.class,
				PreAuthenticatedAuthenticationTokenMixin.class);
		context.setMixInAnnotations(SwitchUserGrantedAuthority.class, SwitchUserGrantedAuthorityMixIn.class);
	}

}
