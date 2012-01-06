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
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class StaticHttpService extends GenericServlet implements
		BundleActivator {

	private static final long serialVersionUID = -420835724412643550L;
	private ServiceTracker httpTracker;

	public void start(BundleContext context) throws Exception {
		this.httpTracker = new ServiceTracker(context,
				HttpService.class.getName(), null);
		this.httpTracker.open();

		HttpService httpService = (HttpService) httpTracker.getService();

		httpService.registerServlet("/editor", this, null, null);

		httpService.registerResources("/editor/resources", "/resources", null);

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
