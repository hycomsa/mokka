package pl.hycom.mokka.util.http;

import java.io.IOException;
import java.sql.Timestamp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import pl.hycom.mokka.emulator.logs.LogManager;
import pl.hycom.mokka.emulator.logs.LogStatus;
import pl.hycom.mokka.emulator.logs.model.Log;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Service
@Slf4j
public class HttpHelper {

	@Autowired
	private LogManager logManager;

	@Async
	public void asyncCall(String url, String method, String contentType) {
		call(url, null, method, contentType, 0);
	}

	@Async
	public void asyncCall(String url, String body, String method, String contentType) {
		call(url, body, method, contentType, 0);
	}

	@Async
	public void asyncCall(String url, String method, String contentType, Integer delay) {
		call(url, null, method, contentType, delay);
	}

	@Async
	public void asyncCall(String url, String body, String method, String contentType, Integer delay) {
		call(url, body, method, contentType, delay);
	}

	@Async
	public void asyncCall(String url, String body, String method, String contentType, Integer delay, String username, String password) {
		call(url, body, method, contentType, delay, username, password);
	}

	public void call(String url, String method, String contentType) {
		call(url, null, method, contentType, 0);
	}

	public void call(String url, String body, String method, String contentType) {
		call(url, body, method, contentType, 0);
	}

	public void call(String url, String method, String contentType, Integer delay) {
		call(url, null, method, contentType, delay);
	}

	public void call(String url, String body, String method, String contentType, Integer delay) {
		call(url, null, method, contentType, delay, null, null);
	}

	public void call(String url, String body, String method, String contentType, Integer delay, String username, String password) {
		if (delay != null && delay > 0) {
			try {
				Thread.sleep(delay * 1000);
			} catch (InterruptedException e) {
				log.error("", e);
			}
		}

		Request request = null;

		if (StringUtils.equalsIgnoreCase(method, "Delete")) {
			request = Request.Delete(url);

		} else if (StringUtils.equalsIgnoreCase(method, "Head")) {
			request = Request.Head(url);

		} else if (StringUtils.equalsIgnoreCase(method, "Options")) {
			request = Request.Options(url);

		} else if (StringUtils.equalsIgnoreCase(method, "Patch")) {
			request = Request.Patch(url);

		} else if (StringUtils.equalsIgnoreCase(method, "Post")) {
			request = Request.Post(url).bodyString(body, ContentType.WILDCARD);

		} else if (StringUtils.equalsIgnoreCase(method, "Put")) {
			request = Request.Put(url);

		} else if (StringUtils.equalsIgnoreCase(method, "Trace")) {
			request = Request.Trace(url);

		} else {
			request = Request.Get(url);
		}

		try {

			Executor executor = Executor.newInstance();

			if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
				executor.auth(username, password);
			}

			HttpResponse result = executor.execute(request.connectTimeout(1000).socketTimeout(10000).setHeader(HttpHeaders.CONTENT_TYPE, contentType)).returnResponse();

			if (result != null) {
				logManager.saveLog(Log.builder().uri(url).request(body).from("mock").response(EntityUtils.toString(result.getEntity())).status(result.getStatusLine().getStatusCode() == 200 ? LogStatus.OK : LogStatus.ERROR).date(new Timestamp(System.currentTimeMillis())));
			} else {
				logManager.saveLog(Log.builder().uri(url).request(body).from("mock").status(LogStatus.ERROR).date(new Timestamp(System.currentTimeMillis())));
			}

		} catch (IOException e) {
			log.error("", e);
			logManager.saveLog(Log.builder().uri(url).request(body).from("mock").response(e.getMessage()).status(LogStatus.ERROR).date(new Timestamp(System.currentTimeMillis())));
		}
	}
}
