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

package org.springframework.security.concurrent;

import org.junit.jupiter.api.BeforeEach;

import org.springframework.security.core.context.SecurityContext;

/**
 * Tests Explicitly specifying the {@link SecurityContext} on
 * {@link DelegatingSecurityContextScheduledExecutorService}
 *
 * @author Rob Winch
 * @since 3.2
 *
 */
public class ExplicitDelegatingSecurityContextScheduledExecutorServiceTests
		extends AbstractDelegatingSecurityContextScheduledExecutorServiceTests {

	@BeforeEach
	public void setUp() throws Exception {
		this.explicitSecurityContextSetup();
	}

	@Override
	protected DelegatingSecurityContextScheduledExecutorService create() {
		return new DelegatingSecurityContextScheduledExecutorService(this.delegate, this.securityContext);
	}

}
