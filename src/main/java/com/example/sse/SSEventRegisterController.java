package com.example.sse;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * SSEventRegisterController
 *
 * @author giho.kwon
 * @since 2020. 01. 24.
 */
@RestController
public class SSEventRegisterController {
	private final Set<SseEmitter> clients = new CopyOnWriteArraySet();

	@GetMapping(value = "/connect-stream")
	public SseEmitter events() {
		SseEmitter emitter = new SseEmitter();
		clients.add(emitter);

		emitter.onTimeout(() -> clients.remove(emitter));
		emitter.onCompletion(() -> clients.remove(emitter));
		return emitter;
	}

	@Async
	@EventListener
	public void handleMessage(EventObject event) {
		clients.parallelStream()
			.map(each -> {
				try {
					each.send(event, MediaType.APPLICATION_JSON);
					return null;
				} catch (IOException ioe) {
					return each;
				}
			}).filter(Objects::nonNull)
			.forEach(clients::remove);
	}

}
