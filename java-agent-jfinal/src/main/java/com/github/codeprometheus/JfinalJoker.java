package com.github.codeprometheus;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;

public class JfinalJoker extends JFinalConfig {
  /**
   -javaagent:/Users/zhouzixin/observability/opentelemetry-java-instrumentation/javaagent/build/libs/opentelemetry-javaagent-2.13.0-SNAPSHOT.jar
   -Dotel.javaagent.debug=true -Dotel.traces.exporter=zipkin -Dotel.logs.exporter=logging -Dotel.metrics.exporter=logging -Dotel.resource.attributes=service.name=zhouzixin
   */
  public static void main(String[] args) {
    UndertowServer undertowServer = UndertowServer.create(JfinalJoker.class);
    undertowServer.start();
  }
  
  void stop() {

  }

  @Override public void configConstant(Constants constants) {
    
  }

  @Override
  public void configRoute(Routes routes) {
    routes.add("/", TestController.class);
  }

  @Override public void configEngine(Engine engine) {

  }

  @Override public void configPlugin(Plugins plugins) {

  }

  @Override public void configInterceptor(Interceptors interceptors) {

  }

  @Override public void configHandler(Handlers handlers) {

  }

  public static class TestController extends Controller {
    public void index() {
      String requestURI = getRequest().getRequestURI();
      handleURI(requestURI);
    }

    private void handleURI(String requestURI) {
      if ("success".equals(requestURI)) {
        renderText("200", "success");
      } else {
        renderText("200", "success-else");
      }
    }

    public void test() {
      renderText("Hello JFinal Test.");
    }
  }
}

