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

package org.springframework.security.web.authentication.logout;

import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Luke Taylor
 */
public class SimpleUrlLogoutSuccessHandlerTests {

	@Test
	public void doesntRedirectIfResponseIsCommitted() throws Exception {
		SimpleUrlLogoutSuccessHandler lsh = new SimpleUrlLogoutSuccessHandler();
		lsh.setDefaultTargetUrl("/target");
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setCommitted(true);
		lsh.onLogoutSuccess(request, response, mock(Authentication.class));
		assertThat(request.getSession(false)).isNull();
		assertThat(response.getRedirectedUrl()).isNull();
		assertThat(response.getForwardedUrl()).isNull();
	}

	@Test
	public void absoluteUrlIsSupported() throws Exception {
		SimpleUrlLogoutSuccessHandler lsh = new SimpleUrlLogoutSuccessHandler();
		lsh.setDefaultTargetUrl("https://someurl.com/");
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		lsh.onLogoutSuccess(request, response, mock(Authentication.class));
		assertThat(response.getRedirectedUrl()).isEqualTo("https://someurl.com/");
	}

}
