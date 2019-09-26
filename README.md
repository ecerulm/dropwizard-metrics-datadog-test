This is simple app that shows how to use dropwizard-metrics together with metrics-datadog.

The app setups five dropwizard-metrics:
* Gauge: `gauge1`
* Counter: `counter1`
* Histogram: `histogram1`
* Meter: `meter1`
* Timer: `timer1`
 
It setups two dropwizard-metrics reporters
* DatadogReporter
* ConsoleReporter
 
Both reporters report each 5 seconds.
 
The app runs for five minutes in a loop where it updates each metric using random values.
You can see the output of the ConsoleReporter on STDIN and capture the udp traffic towards DogStatsD using wireshark.

Although there is only 5 metrics they get expanded to 33 different metrics. 
* `gauge1` expands to a single 1  metric
* `counter1` expands to a single 1  metric
* `histogram1` expands to 11 metrics (count, min, max, mean, stddev, median, 75th percentile, 95th p, 98th p, 99th p, 999th p)
* `meter1` expands to 5 metrics (count, mean rate, 1-minute rate, 5-minute rate, 15-minute rate)
* `timer1` expands to 15 metrics (count, mean rate, 1-minute rate, 5-minute rate, 15-minute rate, min, max, mean, stddev, median, 75th percentile, 95th p, 98th p, 99th p, 999th p)
 


# DatadogReporter output
It will send 33 metrics in 22 udp packets to the DogStatsD server at localhost:8125 each 5 seconds. 

Each UDP packet may contain 1 or more metrics. 
The payload of each UDP packet is  printable ASCII.
Each line in the payload represents a metric. 

The payload format is in the form:
```
metricname1:value1|g\n
metricname2:value2|g
...
```

Note that the last line (which maybe also the first) on each payload is NOT newline terminated.


If we concatenate the payload of all packets and add newlines to the end of each payload we get:
``` 
gauge1:52|g
counter1:5984|g
histogram1.count:108|g
histogram1.max:99|g
histogram1.mean:49.139518|g
histogram1.min:1|g
histogram1.stddev:30.640078|g
histogram1.median:52|g
histogram1.p75:78|g
histogram1.p95:95|g
histogram1.p98:98|g
histogram1.p99:98|g
histogram1.p999:99|g
meter1.count:1080|g
meter1.1MinuteRate:216|g
meter1.5MinuteRate:216|g
meter1.15MinuteRate:216|g
meter1.meanRate:215.425972|g
timer1.max:99.882951|g
timer1.mean:46.354881|g
timer1.min:0.002138|g
timer1.stddev:30.513799|g
timer1.median:42.902956|g
timer1.p75:76.474535|g
timer1.p95:94.913629|g
timer1.p98:97.872557|g
timer1.p99:98.745396|g
timer1.p999:99.882951|g
timer1.count:107|g
timer1.1MinuteRate:21.4|g
timer1.5MinuteRate:21.4|g
timer1.15MinuteRate:21.4|g
timer1.meanRate:21.367497|g
```


# ConsoleReporter output
Each report period looks like this:
```
9/26/19 10:56:20 PM ============================================================

-- Gauges ----------------------------------------------------------------------
gauge1
             value = 32

-- Counters --------------------------------------------------------------------
counter1
             count = 6041

-- Histograms ------------------------------------------------------------------
histogram1
             count = 120
               min = 0
               max = 97
              mean = 48.69
            stddev = 26.65
            median = 46.00
              75% <= 71.00
              95% <= 92.00
              98% <= 95.00
              99% <= 96.00
            99.9% <= 97.00

-- Meters ----------------------------------------------------------------------
meter1
             count = 1200
         mean rate = 239.40 events/second
     1-minute rate = 240.00 events/second
     5-minute rate = 240.00 events/second
    15-minute rate = 240.00 events/second

-- Timers ----------------------------------------------------------------------
timer1
             count = 119
         mean rate = 23.76 calls/second
     1-minute rate = 23.80 calls/second
     5-minute rate = 23.80 calls/second
    15-minute rate = 23.80 calls/second
               min = 0.00 milliseconds
               max = 97.97 milliseconds
              mean = 41.81 milliseconds
            stddev = 29.26 milliseconds
            median = 33.87 milliseconds
              75% <= 63.72 milliseconds
              95% <= 93.36 milliseconds
              98% <= 95.97 milliseconds
              99% <= 97.18 milliseconds
            99.9% <= 97.97 milliseconds

```