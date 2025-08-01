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

package org.springframework.security.acls.model;

import java.util.List;

/**
 * Allow customization of the logic for determining whether a permission or permissions
 * are granted to a particular sid or sids by an {@link Acl}.
 *
 * @author Luke Taylor
 * @since 3.0.2
 */
public interface PermissionGrantingStrategy {

	/**
	 * Returns true if the supplied strategy decides that the supplied {@code Acl} grants
	 * access based on the supplied list of permissions and sids.
	 */
	boolean isGranted(Acl acl, List<Permission> permission, List<Sid> sids, boolean administrativeMode);

}
