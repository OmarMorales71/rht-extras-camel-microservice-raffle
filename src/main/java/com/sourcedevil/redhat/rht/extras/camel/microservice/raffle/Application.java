/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package com.sourcedevil.redhat.rht.extras.camel.microservice.raffle;

import java.net.InetAddress;
import java.net.URL;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * A spring-boot application that includes a Camel route builder to setup the
 * Camel routes
 */
@SpringBootApplication
@ImportResource({ "classpath:spring/camel-context.xml" })
public class Application extends RouteBuilder {

	// must have a main method spring-boot can run
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void configure() throws Exception {
/*
		rest("/iptest").get("{name}").produces("application/json")

				.to("direct:sayHello");
		from("direct:sayHello").routeId("iptest").setBody()
				.simple("{\n" + " ipTest, ${header.name}\n" + " server: "
						+ InetAddress.getLocalHost().getHostAddress() + " - " + InetAddress.getLocalHost().getHostName()
						+ " - " + System.getenv("HOSTNAME") + "\n" + "}\n");

	*/	
		rest("/raffle").get("go/{nombre}/{nick}").produces("application/json")
         .route().routeId("champions-api")
         .to("sql:insert into redhat_champions values ( :#${header.nombre} , :#${header.nick} )  ")
         .log("SIGA PARTICIPANDO ${header.nombre}")
        
         .endRest()
         
         .get("ganador").produces("application/json")
         .route().routeId("ganador")
         .to("sql:SELECT * FROM redhat_champions ORDER BY RAND() LIMIT 0,1 ")
         .log("NO PUEDE SER !!!! === ${header.nombre}")
         .endRest()
         
         .get("test").produces("application/json")
         .route().routeId("exec")
         .process(new Processor() {
				
				@Override
				public void process(Exchange exchange) throws Exception {
					// TODO Auto-generated method stub
					exchange.getIn().getBody();
					(new URL("http://172.25.254.223:8080/raffle/go/su/s")).openConnection().getContent();
					
				
					
				}
			});
	}
}
