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

package org.springframework.security.messaging.access.expression;

import java.util.function.Supplier;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.messaging.Message;
import org.springframework.security.access.expression.AbstractSecurityExpressionHandler;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

/**
 * The default implementation of {@link SecurityExpressionHandler} which uses a
 * {@link MessageSecurityExpressionRoot}.
 *
 * @param <T> the type for the body of the Message
 * @author Rob Winch
 * @author Evgeniy Cheban
 * @since 4.0
 */
public class DefaultMessageSecurityExpressionHandler<T> extends AbstractSecurityExpressionHandler<Message<T>> {

	private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

	@Override
	public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication, Message<T> message) {
		MessageSecurityExpressionRoot root = createSecurityExpressionRoot(authentication, message);
		StandardEvaluationContext ctx = new StandardEvaluationContext(root);
		ctx.setBeanResolver(getBeanResolver());
		return ctx;
	}

	@Override
	protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication,
			Message<T> invocation) {
		return createSecurityExpressionRoot(() -> authentication, invocation);
	}

	private MessageSecurityExpressionRoot createSecurityExpressionRoot(Supplier<Authentication> authentication,
			Message<T> invocation) {
		MessageSecurityExpressionRoot root = new MessageSecurityExpressionRoot(authentication, invocation);
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setTrustResolver(this.trustResolver);
		root.setRoleHierarchy(getRoleHierarchy());
		return root;
	}

	public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
		Assert.notNull(trustResolver, "trustResolver cannot be null");
		this.trustResolver = trustResolver;
	}

}
