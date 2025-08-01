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

package org.springframework.security.oauth2.client.registration;

import java.util.Iterator;
import java.util.function.Supplier;

import org.springframework.util.Assert;
import org.springframework.util.function.SingletonSupplier;

/**
 * A {@link ClientRegistrationRepository} that lazily calls to retrieve
 * {@link ClientRegistration}(s) when requested.
 *
 * @author Justin Tay
 * @since 6.2
 * @see ClientRegistrationRepository
 * @see ClientRegistration
 */
public final class SupplierClientRegistrationRepository
		implements ClientRegistrationRepository, Iterable<ClientRegistration> {

	private final Supplier<? extends ClientRegistrationRepository> repositorySupplier;

	/**
	 * Constructs an {@code SupplierClientRegistrationRepository} using the provided
	 * parameters.
	 * @param repositorySupplier the client registration repository supplier
	 */
	public <T extends ClientRegistrationRepository & Iterable<ClientRegistration>> SupplierClientRegistrationRepository(
			Supplier<T> repositorySupplier) {
		Assert.notNull(repositorySupplier, "repositorySupplier cannot be null");
		this.repositorySupplier = SingletonSupplier.of(repositorySupplier);
	}

	@Override
	public ClientRegistration findByRegistrationId(String registrationId) {
		Assert.hasText(registrationId, "registrationId cannot be empty");
		return this.repositorySupplier.get().findByRegistrationId(registrationId);
	}

	/**
	 * Returns an {@code Iterator} of {@link ClientRegistration}.
	 * @return an {@code Iterator<ClientRegistration>}
	 */
	@Override
	public Iterator<ClientRegistration> iterator() {
		return ((Iterable<ClientRegistration>) this.repositorySupplier.get()).iterator();
	}

}
