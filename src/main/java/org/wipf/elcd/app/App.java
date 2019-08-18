/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.wipf.elcd.app;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.wipf.elcd.rest.Rest;

public class App {

	private static final URI BASE_URI = URI.create("http://192.168.2.43:8080/wipf/");
	// private static final URI BASE_URI =
	// URI.create("http://localhost:8080/wipf/");

	public static void main(String[] args) {
		try {
			System.out.println("Start");

			final ResourceConfig resourceConfig = new ResourceConfig(Rest.class);
			final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false);
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					server.shutdownNow();
				}
			}));
			server.start();

			System.out.println(String.format("Server aktiv.\n%s\n", BASE_URI));
			Thread.currentThread().join();
		} catch (IOException | InterruptedException ex) {
			Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
