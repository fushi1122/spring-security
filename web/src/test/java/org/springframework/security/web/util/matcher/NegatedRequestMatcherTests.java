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

package org.springframework.security.web.util.matcher;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;

/**
 * @author Rob Winch
 *
 */
@ExtendWith(MockitoExtension.class)
public class NegatedRequestMatcherTests {

	@Mock
	private RequestMatcher delegate;

	@Mock
	private HttpServletRequest request;

	private RequestMatcher matcher;

	@Test
	public void constructorNull() {
		assertThatIllegalArgumentException().isThrownBy(() -> new NegatedRequestMatcher(null));
	}

	@Test
	public void matchesDelegateFalse() {
		given(this.delegate.matches(this.request)).willReturn(false);
		this.matcher = new NegatedRequestMatcher(this.delegate);
		assertThat(this.matcher.matches(this.request)).isTrue();
	}

	@Test
	public void matchesDelegateTrue() {
		given(this.delegate.matches(this.request)).willReturn(true);
		this.matcher = new NegatedRequestMatcher(this.delegate);
		assertThat(this.matcher.matches(this.request)).isFalse();
	}

}
