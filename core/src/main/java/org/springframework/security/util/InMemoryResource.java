/*
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
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

package org.springframework.security.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.jspecify.annotations.Nullable;

import org.springframework.core.io.AbstractResource;
import org.springframework.util.Assert;

/**
 * An in memory implementation of Spring's {@link org.springframework.core.io.Resource}
 * interface.
 * <p>
 * Used to create a bean factory from an XML string, rather than a file.
 * </p>
 *
 * @author Luke Taylor
 */
public class InMemoryResource extends AbstractResource {

	private final byte[] source;

	private final String description;

	public InMemoryResource(String source) {
		this(source.getBytes());
	}

	public InMemoryResource(byte[] source) {
		this(source, null);
	}

	public InMemoryResource(byte[] source, @Nullable String description) {
		Assert.notNull(source, "source cannot be null");
		this.source = source;
		this.description = (description != null) ? description : "";
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(this.source);
	}

	@Override
	public boolean equals(@Nullable Object res) {
		if (!(res instanceof InMemoryResource)) {
			return false;
		}
		return Arrays.equals(this.source, ((InMemoryResource) res).source);
	}

	@Override
	public int hashCode() {
		return 1;
	}

}
