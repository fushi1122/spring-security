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

package org.springframework.security.authorization.method;

import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AnnotationTemplateExpressionDefaults;
import org.springframework.util.Assert;

/**
 * A {@link ReactiveAuthorizationManager} which can determine if an {@link Authentication}
 * has access to the returned object from the {@link MethodInvocation} by evaluating an
 * expression from the {@link PostAuthorize} annotation.
 *
 * @author Evgeniy Cheban
 * @since 5.8
 */
public final class PostAuthorizeReactiveAuthorizationManager
		implements ReactiveAuthorizationManager<MethodInvocationResult>, MethodAuthorizationDeniedHandler {

	private final PostAuthorizeExpressionAttributeRegistry registry = new PostAuthorizeExpressionAttributeRegistry();

	public PostAuthorizeReactiveAuthorizationManager() {
		this(new DefaultMethodSecurityExpressionHandler());
	}

	public PostAuthorizeReactiveAuthorizationManager(MethodSecurityExpressionHandler expressionHandler) {
		Assert.notNull(expressionHandler, "expressionHandler cannot be null");
		this.registry.setExpressionHandler(expressionHandler);
	}

	/**
	 * Configure pre/post-authorization template resolution
	 * <p>
	 * By default, this value is <code>null</code>, which indicates that templates should
	 * not be resolved.
	 * @param defaults - whether to resolve pre/post-authorization templates parameters
	 * @since 6.4
	 */
	public void setTemplateDefaults(AnnotationTemplateExpressionDefaults defaults) {
		this.registry.setTemplateDefaults(defaults);
	}

	public void setApplicationContext(ApplicationContext context) {
		this.registry.setApplicationContext(context);
	}

	/**
	 * Determines if an {@link Authentication} has access to the returned object from the
	 * {@link MethodInvocation} by evaluating an expression from the {@link PostAuthorize}
	 * annotation.
	 * @param authentication the {@link Mono} of the {@link Authentication} to check
	 * @param result the {@link MethodInvocationResult} to check
	 * @return a Mono of the {@link AuthorizationDecision} or an empty {@link Mono} if the
	 * {@link PostAuthorize} annotation is not present
	 */
	@Override
	public Mono<AuthorizationResult> authorize(Mono<Authentication> authentication, MethodInvocationResult result) {
		MethodInvocation mi = result.getMethodInvocation();
		ExpressionAttribute attribute = this.registry.getAttribute(mi);
		if (attribute == null) {
			return Mono.empty();
		}

		MethodSecurityExpressionHandler expressionHandler = this.registry.getExpressionHandler();
		// @formatter:off
		return authentication
				.map((auth) -> expressionHandler.createEvaluationContext(auth, mi))
				.doOnNext((ctx) -> expressionHandler.setReturnObject(result.getResult(), ctx))
				.flatMap((ctx) -> ReactiveExpressionUtils.evaluate(attribute.getExpression(), ctx))
				.cast(AuthorizationResult.class);
		// @formatter:on
	}

	@Override
	public @Nullable Object handleDeniedInvocation(MethodInvocation methodInvocation,
			AuthorizationResult authorizationResult) {
		ExpressionAttribute attribute = this.registry.getAttribute(methodInvocation);
		PostAuthorizeExpressionAttribute postAuthorizeAttribute = (PostAuthorizeExpressionAttribute) attribute;
		return postAuthorizeAttribute.getHandler().handleDeniedInvocation(methodInvocation, authorizationResult);
	}

	@Override
	public @Nullable Object handleDeniedInvocationResult(MethodInvocationResult methodInvocationResult,
			AuthorizationResult authorizationResult) {
		ExpressionAttribute attribute = this.registry.getAttribute(methodInvocationResult.getMethodInvocation());
		PostAuthorizeExpressionAttribute postAuthorizeAttribute = (PostAuthorizeExpressionAttribute) attribute;
		return postAuthorizeAttribute.getHandler()
			.handleDeniedInvocationResult(methodInvocationResult, authorizationResult);
	}

}
