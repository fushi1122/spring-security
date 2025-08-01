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

package org.springframework.security.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class PostProcessedMockUserDetailsService implements UserDetailsService {

	private String postProcessorWasHere;

	public PostProcessedMockUserDetailsService() {
		this.postProcessorWasHere = "Post processor hasn't been yet";
	}

	public String getPostProcessorWasHere() {
		return this.postProcessorWasHere;
	}

	public void setPostProcessorWasHere(String postProcessorWasHere) {
		this.postProcessorWasHere = postProcessorWasHere;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		throw new UnsupportedOperationException("Not for actual use");
	}

}
