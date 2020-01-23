package com.example.sse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * EventPublisher
 *
 * @author giho.kwon
 * @since 2020. 01. 24.
 */
@Component
public class EventPublisher {
	private final ApplicationEventPublisher publisher;
	private final Random rnd = new Random();
	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	public EventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	@PostConstruct
	public void startSchedule() {
		this.executorService.schedule(this::probe, 1, TimeUnit.SECONDS);
	}

	private void probe() {
		int randomNo = rnd.nextInt(100);
		publisher.publishEvent(new EventObject(randomNo));

		executorService.schedule(this::probe, rnd.nextInt(3000), TimeUnit.MILLISECONDS);
	}
}
