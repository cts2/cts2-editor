package edu.mayo.cts2.framework.plugin.web.editor;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class StaticHttpService extends GenericServlet implements
		BundleActivator {
	
	private static final long serialVersionUID = -420835724412643550L;
	private ServiceTracker httpTracker;

	public void start(final BundleContext context) throws Exception {
		this.httpTracker = new ServiceTracker(context,
				HttpService.class.getName(), 
				new ServiceTrackerCustomizer(){

					public Object addingService(ServiceReference reference) {
						HttpService httpService = (HttpService) context.getService(reference);

						try {
							httpService.registerServlet("/editor", StaticHttpService.this, null, null);

							httpService.registerResources("/editor/resources", "/resources", null);
						} catch (Exception e) {
							throw new RuntimeException(e);
						} 
						
						return context.getService(reference);
					}

					public void modifiedService(ServiceReference reference,
							Object service) {
						//
					}

					public void removedService(ServiceReference reference,
							Object service) {
						//
					}
			
		});
		this.httpTracker.open();
	}

	public void stop(BundleContext context) throws Exception {
		this.httpTracker.close();
	}

	public void service(final ServletRequest req, final ServletResponse res)
			throws ServletException, IOException {
		// don't really expect to be called within a non-HTTP environment
		service((HttpServletRequest) req, (HttpServletResponse) res);

		// ensure response has been sent back and response is committed
		// (we are authorative for our URL space and no other servlet should
		// interfere)
		res.flushBuffer();
	}

	private void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// check whether we are not at .../{webManagerRoot}
		final String pathInfo = request.getPathInfo();

		if (pathInfo.equals("/console")) {
			InputStream in = this.getClass().getResourceAsStream("/index.html");

			IOUtils.copy(in, response.getWriter());
		}

	}

}
