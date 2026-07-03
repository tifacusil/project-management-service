package com.projectmanagement.project_management.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;

/**
 * Instrumentacion Prometheus para la tesis:
 * - pm_requests_total: throughput HTTP (todas las peticiones).
 * - pm_persist_latency_seconds: latencia de peticiones mutadoras (POST/PUT/DELETE),
 *   proxy de la latencia de persistencia. Histogram para histogram_quantile (p50/p90/p95/p99).
 * CPU/memoria residente los expone Micrometer automaticamente (process_*).
 */
@Configuration
public class MetricsConfig implements WebMvcConfigurer {

	private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

	private final Counter requestsTotal;
	private final Timer persistLatency;

	public MetricsConfig(MeterRegistry meterRegistry) {
		this.requestsTotal = Counter.builder("pm.requests")
				.description("Peticiones HTTP atendidas")
				.register(meterRegistry);
		this.persistLatency = Timer.builder("pm.persist.latency")
				.description("Latencia de peticiones mutadoras (persistencia)")
				.publishPercentileHistogram()
				.register(meterRegistry);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new MetricsInterceptor())
				.excludePathPatterns("/metrics", "/health", "/info", "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**");
	}

	private final class MetricsInterceptor implements HandlerInterceptor {
		private static final String START_ATTR = "pm.request.start";

		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
			request.setAttribute(START_ATTR, System.nanoTime());
			return true;
		}

		@Override
		public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
			requestsTotal.increment();
			Object start = request.getAttribute(START_ATTR);
			if (start instanceof Long startNanos && WRITE_METHODS.contains(request.getMethod())) {
				persistLatency.record(System.nanoTime() - startNanos, java.util.concurrent.TimeUnit.NANOSECONDS);
			}
		}
	}
}
