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

package org.springframework.security.test.context.annotation;

import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SecurityTestExecutionListeners
public class SecurityTestExecutionListenerTests {

	@WithMockUser
	@Test
	public void withSecurityContextTestExecutionListenerIsRegistered() {
		assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("user");
	}

	@WithMockUser
	@Test
	public void reactorContextTestSecurityContextHolderExecutionListenerTestIsRegistered() {
		Mono<String> name = ReactiveSecurityContextHolder.getContext()
			.map(SecurityContext::getAuthentication)
			.map(Principal::getName);
		StepVerifier.create(name).expectNext("user").verifyComplete();
	}

}
