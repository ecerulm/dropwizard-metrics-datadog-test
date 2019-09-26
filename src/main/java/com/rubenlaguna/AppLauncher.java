package com.rubenlaguna;

import com.codahale.metrics.*;
import org.coursera.metrics.datadog.DatadogReporter;
import org.coursera.metrics.datadog.transport.UdpTransport;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AppLauncher {
    public static void main(String[] args) {
        final Instant start = Instant.now();
        final Instant end = start.plus(Duration.ofMinutes(5));

        SharedMetricRegistries.setDefault("default");
        MetricRegistry metrics = SharedMetricRegistries.getDefault();


        final UdpTransport udpTransport = new UdpTransport.Builder().build();
        final DatadogReporter reporter = DatadogReporter.forRegistry(metrics)
                .withTransport(udpTransport)
                .build();
        reporter.start(5, TimeUnit.SECONDS);

        final ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        consoleReporter.start(5, TimeUnit.SECONDS);


        final Meter meter = metrics.meter("meter1");
        final Gauge<Long> gauge = metrics.register("gauge1", new Gauge<Long>() {
            private Random r = new Random();

            @Override
            public Long getValue() {
                long leftLimit = 50L;
                long rightLimit = 100L;
                long generatedLong = leftLimit + (r.nextLong() % (rightLimit - leftLimit));
                return generatedLong;
            }
        });
        final Counter counter = metrics.counter("counter1");
        final Histogram histogram = metrics.histogram("histogram1");
        final Timer timer1 = metrics.timer("timer1");

        try {
            Random r = new Random();
            while (Instant.now().isBefore(end)) {
                meter.mark(10);
                counter.inc(r.nextInt(100));
                histogram.update(r.nextInt(100));
                try (final Timer.Context context = timer1.time()) {
                    Thread.sleep(r.nextInt(100));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
