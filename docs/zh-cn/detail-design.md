# x-api-gateway

## Purpose

Build a high-performance API gateway

构建一个高性能网关

## Detailed design

### HttpServer

Multiple http servers are supported. default http Server is reactor-netty-http.

支持多种http服务器，默认http服务器是 reactor-netty-http

### HttpHandler

为了支持多种服务器，抽象一个`HttpHandler`用于处理http请求。不同的服务器，通过适配器进行适配即可。

**request & response wraper**

为了支持多种服务器，需要对http的Request和Response进行抽象

- interface HttpRequest.

```bash
interface HttpRequest {
		URL getURI();
		HttpMethod getMethod();
		HttpHeaders getHeaders();
		MultiValueMap<String, HttpCookie> getCookies();
		Flux<DataBuffer> getBody();
}
```

- interface HttpResponse.

```bash
interface HttpResponse {
		HttpStatus getStatus();
		HttpHeaders getHeaders();
		MultiValueMap<String, HttpCookie> getCookies();
		Mono<Void> writeWith(Publisher<DataBuffer> body);
}
```

HttpHandler完整实现：

```java
interface HttpHandler {
		void handle(HttpRequest request, HttpResponse response);
}
```

### ServerAdapter

Server默认适配器：ReactorHttpHandlerAdaptor  用于适配 reactor-netty-http服务器

HttpRequest默认实现： ReactorHttpRequest

HttpResponse默认实现： ReactorHttpResponse

### WebAPI

在http之上进行一个抽象web层

- WebHandler
- ServerCodecConfigurer
- ForwardedHeaderTransformer [TODO]

提供一个http request-response 交互契约， 包含一些额外的属性和特性。

ServerWebExchange

```bash
interface ServerWebExchange {
		HttpRequest getRequest();
		HttpResponse getResponse();
		Map<String, Object> getAttributes();
		Mono<MutiValueMap<String, String>> getFormData();
		<T> Mono<T> getJsonData();
}
```

WebHandler 

```bash
interface WebHandler {
		Mono<Void> handle(ServerWebExchange exchange);
}
```

### WebHandlerDecorator

WebHandlerDecorator 提供一个WebHandler的包装&代理

```bash
class WebHandlerDecorator implements WebHandler {
		private final WebHandler delegate;
		
		public WebHandlerDecorator(WebHandler delegate) {
		Assert.notNull(delegate, "'delegate' must not be null");
		this.delegate = delegate;
		}
		
		@Override
		public Mono<Void> handle(ServerWebExchange exchange) {
			return this.delegate.handle(exchange);
		}
}
```

可以通过继承此类并重写handle方法，对某一个WebHandler进行包装，提供额外处理功能。可以查看后续的 `ExceptionHandlingWebHandler`  HttpWebHandlerAdapter

**HttpWebHandlerAdapter**

通过一个Adapter，将HttpHandler和WebHandler的行为联系起来。即HttpHandler接收请求然后交由被委托的WebHandler进行处理。

```bash
class HttpWebHandlerAdapter extends WebHandlerDecorator implements HttpHandler {
	....
}
```

`**ExceptionHandlingWebHandler**`

对委托的WebHandler提供Exception处理能力

```bash
class ExceptionHandlingWebHandler extends WebHandlerDecorator {
		public ExceptionHandlingWebHandler(WebHandler delegate,
													 List<WebExceptionHandler> handlers) {
		....	
		}
} 
```

```bash
public interface WebExceptionHandler {

		Mono<Void> handle(ServerWebExchange exchange, Throwable ex);

}
```

### Route

Route由一个id，一个RoutePredicate，一组GatewayFilter以及一个Endpoint组成

| Route Component |  |
| --- | --- |
| id |  |
| RoutePredicate | 1 |
| GatewayFilter | 1..N |
| Endpoint | 1 |

### RoutePredicate

```java
interface RoutePredicate extends Ordered {
			
			Mono<Boolean> test(ServerWebExchange exchange);
		  
			default RoutePredicate or(RoutePredicate other){..}
			default RoutePredicate and(RoutePredicate ohter){..}
			default RoutePredicate negative(){..}
}
```

断言参数配置

```java
interface Configable {
		C <C extends Config> getConfig();
}

interface Config {
		
}
// 基础实现
class NameValueConfig implements Config{
		private String name;
		private String value;
}

class NameValuesConfig implements Config{
		private String name;
		private Set<String> values;
}
```

```java
abstract class AbstraceRoutePredicate<C extends Config> implements RoutePredicate, Configable {
		 private final C config;
		 public AbstraceRoutePredicate (C config){
				 this.config = config;
		 }
		 public C getConfig() {
				 return this.config;
		 }
}
```

举例说明：HttpMethodPredicate 判断请求方法

```java
class HttpMethodPredicate extends AbstraceRoutePredicate<NameValuesPredicateConfig> {
		public HttpMethodPredicate(NameValuesPredicateConfig config) {
				super(config);
		}
		public Mono<Boolean> test(ServerWebExchange exchange) {
				return Mono.just(exchange->{
								return getConfig().getValues()
												.contains(exchange.getRequest().getMethod().toString());
						});
		}
}
```

### **RoutePredicateWebHandler**

用于断言路由

```java
public class RoutePredicateWebHandler ****extends WebHandlerDecorator {

			private RouteLocator routeLocator;
			
			@Override
			public Mono<Void> handle(ServerWebExchange exchange) {
					return lookupRoute(exchange)
														 .switchIfEmpty(...)
														 .flatMap(route -> {
																  exchange.setAttribte(ROUTE_KEY, route);
																  return Mono.just(super.handle(exchange));
															});
															
			}
}
```

### GatewayFilter

```java
interface GatewayFilter {
		Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain);
}
```

```java
interface GatewayFilterChain {
		Mono<Void> filter(ServerWebExchange exchange);
}
```

```java
class DefaultGatewayFilterChain implements GatewayFilterChain {
		private List<GatewayFilter> filters;
		private int index = 0;
		public DefaultGatewayFilterChain(List<GatewayFilter> filters) {
				this.filters = filters;
		}
		
		Mono<Void> filter(ServerWebExchange exchange) {
				if(index < filters.length()){
						retrun filters.get(index++).filer(exchange, this);
				} else {
						retrun Mono.empty();
				}
		}
}
```

### FilteringWebHandler

用于触发GatewayFilter责任链

```java
class FilteringWebHandler implements WebHandler {
		
		private List<GatewayFilter> globalFilters;		

		Mono<Void> handle(ServerWebExchange exchange) {
				Route route = exchange.getRequiredAttribute(ROUTE_KEY);	
				List<GatewayFilter> filters = Route.getFilters();
				filters = List.combine(globalFilters, filters).sort();
				GatewayFilterChain chain = new DefaltGatewayFilterChain(filters);
				return chain.filter(exchange);
		}

}
```

### Endpoint

Endpoint代表了这个路由最终调用的端点

```java
interface Endpoint {
		Mono<Void> invoke(ServerWebExchange exchange);
}
```

### Protocol & Refrence

Endpoint默认实现由Protocol和Refrence组合而成

| Endpoint Component |  |
| --- | --- |
| Protocol | GRPC/HTTP/DUBBO/THRIFT |
| Refrence |  |

```java
enum Protocol {
	GRPC, HTTP, THRIFT, DUBBO;
}

interface Refrence {
		
}

abstract class AbstractEndpoint<R extends Refrence> {
		
		private final Portocol protocol;

		private final R refrence;
		
		public AbstractEndpoint(Protocol protocol, R refrence) {
				this.protocol = protocol;
				this.refrence = refrence;
		}
		
		public R getRefrence(){
				return this.refrence;
		}
}
```

以http endpoint为例：

```java
class HttpRefrence implements Refrence {
		private URL url;
		...
}
class HttpEndpoint extends AbstractEndpoint<HttpRefrence>{
		public HttpEndpoint(HttpRefrence refrence){
				super(Protocol.HTTP, refrence);
		}
		
		public Mono<Void> invoke(ServerWebExchange exchange){
				....
		}
}
```

### InvokeEndpointGatewayFilter

将endpoint调用嵌入到GatewayFilter调用链中

```java
class InvokeEndpointGatewayFilter implements GatewayFilter {
		private final Endpoint endpoint;

		public InvokeEndpointGatewayFilter(Endpoint endpoint) {
				this.endpoint = endpoint;
		}

		Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
					return endpoint.invoke(exchange).then(chain.filter(exchange));
		}

}
```

## Observability

可观测性作为一个应用系统很重要的一部分，网关在以下三个方面进行设计:

整个可观测性的API层使用Open-Telemetry（这里有个风险：与Open-Telemetry强耦合）

### Tracing

支持从http请求头中提取上下文trace信息， 如果没有提取到，则开启一个新的Trace链；最终会将Trace信息传递给下游的Endpoint系统（通过http请求头/ RPC context）

网关获取Span

```java
class TracingWebHandler extends WebHandlerDecorator {
		public TracingWebHandler(WebHandler webhandler){
				super(webhandler);
		}
		
		@Override
		public Mono<Void> handle(ServerWebExchange exchange) {
				return Mono.just(()->{
						Span span = SpanHeaderExtractor.extract(exchange.getRequest().getHeaders());
						if (span == null) {
								// new Trace
						} else {
								// new Span
						}
						exchange.setAttribte(SPAN_KEY, span);
				}).then(super.handle(exchange))
				.finialy(()->{
						Span span = exchange.getRequiredAttribute(SPAN_KEY)
						// finish span
				});
		}		
}
```

向下游传递Span，以Http为例：

```java
class HttpEndpoint extends AbstractEndpoint<HttpRefrence>{
		public HttpEndpoint(HttpRefrence refrence){
				super(Protocol.HTTP, refrence);
		}
		
		public Mono<Void> invoke(ServerWebExchange exchange){
				// Span信息注入，传递给下游
				SpanHeaderInjecter.inject(exchange.getRequest().getHeaders());
				....
		}
}
```

### Metric

我们的目标是打造一个高性能网关，如何知道当前API网关的性能呢？监控就必不可少了。在整个API处理过程中，我们预设了一些指标。

| Metric | Type | Labels | Desc |
| --- | --- | --- | --- |
| requestTime | Histogram | route_id, http_status  | 统计请求的耗时+请求数 |
| requestBodyBytes【TODO】 | Histogram | route_id | 统计请求体的大小 |
| routePredicateTime | Histogram | route_id | 统计路由断言耗时 |
| inprogressRequests | Gauge | route_id | 统计正在处理的请求数量 |
| invokeEndpointTime | Histogram | route_id | 统计调用后端endpoint的时间 |

```java
class MetricWebHandler extends WebHandlerDecorator {
		private Histogram requestTime;
    private Gauge inprogressRequests;

		public MetricWebHandler(WebHandler webhandler){
				super(webhandler);
		}
		
		@Override
		public Mono<Void> handle(ServerWebExchange exchange) {
				return Mono.just(()->{
						Timer timer = new Timer();
						exchange.setAttribte(REQ_TIMER, timer);
						// 记录指标
						inprogressRequests.inc();
				}).then(super.handle(exchange))
				.finialy(()->{
						Timer timer = exchange.getRequiredAttribute(REQ_TIMER)
						timer.end();
						// 记录指标
						requestTime.record(timer.toMills(), ...);
						// 记录指标
						inprogressRequests.dec();
				});
		}		
}
```

```java
public class RoutePredicateWebHandler ****extends WebHandlerDecorator {
			private Histogram routePredicateTime; 
			private RouteLocator routeLocator;
			
			@Override
			public Mono<Void> handle(ServerWebExchange exchange) {
					return lookupRoute(exchange)
														 .switchIfEmpty(...)
														 .flatMap(route -> {
																  exchange.setAttribte(ROUTE_KEY, route);
																  return Mono.just(super.handle(exchange));
															});
															
			}
			
			private Flux<Route> lookupRoute(ServerWebExchange exchange) {
				return Flux.just(()->{
									 Timer timer = new Timer();
									 exchange.setAttribute(PREDICATE_TIMER, timer);
							 }).then(...)
							 .finialy(()->{
									 Timer timer = exchange.getRequiredAttribute(PREDICATE_TIMER);
									 timer.end();
									 // 记录指标
									 routePredicateTime.record(timer.toMills, ...);
								})
			}
}
```

```java
class InvokeEndpointGatewayFilter implements GatewayFilter {
		private Histogram invokeEndpointTime;
		private final Endpoint endpoint;

		public InvokeEndpointGatewayFilter(Endpoint endpoint) {
				this.endpoint = endpoint;
		}

		Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
					return Mono.just(()->{
							Timer timer = new Timer();
							exchange.setAttribute(INVOKE_TIMER, timer);
					}).then(() -> {
              endpoint.invoke(exchange).then(chain.filter(exchange));
					}).finialy(() -> {
							Timer timer = exchange.getRequiredAttribute(INVOKE_TIMER);
							timer.end();
							// 记录指标
							invokeEndpointTime.record(timer.toMills(),...);
					});
		}

}
```

### Logging

定义一个AccessLog对象包含我们关注的所有日志内容

```java
class AccessLog {
		private String traceId;
		private long timestamp;
		private String clientIp;
		private String httpMethod;
		private String requestUrl;
		private String requestHeaders;
		private String requestBody;
		private int httpStatus;
		private String responseHeaders;
		private String responseBody;

}
```

```java
interface AccessLogCollector {
		void collect(AccessLog accessLog);
}
```

```java
class LoggingWebHandler extends WebHandlerDecorator {
		
		public LoggingWebHandler(WebHandler webhandler){
				super(webhandler);
		}
		
		@Override
		public Mono<Void> handle(ServerWebExchange exchange) {
				return Mono.just(()->{
						AccessLog accessLog = new AccessLog();
						// set log properties
						...
						exchange.setAttribte(ACCESS_LOG, accesslog);
				}).then(()->{super.handle(exchange)))
				.finialy(()->{
						AccessLog accessLog = exchange.getRequiredAttribute(ACCESS_LOG)
						// set log properties
						... 
						AccessLogCollectorManager.collect(accessLog);
				});
		}
}
```

## Summary

本文介绍了x-api-gateway的架构和设计，包括WebHandler、WebHandlerDecorator、HttpWebHandlerAdapter、Route、RoutePredicate、GatewayFilter、Endpoint、Protocol、Refrence等组件，并重点介绍了x-api-gateway的可观测性设计，包括Tracing、Metric和Logging。