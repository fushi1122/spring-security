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

package org.springframework.security.web.session;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

/**
 * Performs a redirect to a fixed URL when an invalid requested session is detected by the
 * {@code SessionManagementFilter}.
 *
 * @author Luke Taylor
 */
public final class SimpleRedirectInvalidSessionStrategy implements InvalidSessionStrategy {

	private final Log logger = LogFactory.getLog(getClass());

	private final String destinationUrl;

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	private boolean createNewSession = true;

	public SimpleRedirectInvalidSessionStrategy(String invalidSessionUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl), "url must start with '/' or with 'http(s)'");
		this.destinationUrl = invalidSessionUrl;
	}

	@Override
	public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Starting new session (if required) and redirecting to '" + this.destinationUrl + "'");
		}
		if (this.createNewSession) {
			request.getSession();
		}
		this.redirectStrategy.sendRedirect(request, response, this.destinationUrl);
	}

	/**
	 * Determines whether a new session should be created before redirecting (to avoid
	 * possible looping issues where the same session ID is sent with the redirected
	 * request). Alternatively, ensure that the configured URL does not pass through the
	 * {@code SessionManagementFilter}.
	 * @param createNewSession defaults to {@code true}.
	 */
	public void setCreateNewSession(boolean createNewSession) {
		this.createNewSession = createNewSession;
	}

}
