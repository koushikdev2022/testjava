


import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Tempareture {
    public static class Reading {
        String sensorId;
        long timestamp; 
        double temperature;

        public Reading(String sensorId, long timestamp, double temperature) {
            this.sensorId = sensorId;
            this.timestamp = timestamp;
            this.temperature = temperature;
        }
    }

    public static class SensorState {
        private final Deque<Reading> lastReadings = new LinkedList<>();
        private boolean unhealthy = false;
        private long firstReadingTime = -1;

        public synchronized void processReading(Reading reading) {
            if (firstReadingTime == -1) {
                firstReadingTime = reading.timestamp;
            }
            lastReadings.addLast(reading);
            
            if (lastReadings.size() > 5) {
                lastReadings.removeFirst();
            }
        }
        public synchronized boolean isUnhealthy(long currentTime) {
         
            if (unhealthy) return true; 
            if (lastReadings.isEmpty()) return true;

            double sum = 0;
            for (Reading r : lastReadings) {
                sum += r.temperature;
            }
            double avg = sum / lastReadings.size();
            
            if (avg < 10.0 || avg > 90.0) {
                unhealthy = true;
                return true;
            }

          
            long fiveMinutesInMillis = 5 * 60 * 1000;
            
            if (currentTime - firstReadingTime >= fiveMinutesInMillis) {
                long windowStart = currentTime - fiveMinutesInMillis;
                int readingsInWindow = 0;
                
                for (Reading r : lastReadings) {
                    if (r.timestamp >= windowStart) {
                        readingsInWindow++;
                    }
                }
                if (readingsInWindow <= 2) {
                    unhealthy = true;
                    return true;
                }
            }
            
            return false;
        }

    
    }


    private static final Map<String, SensorState> sensorRegistry = new ConcurrentHashMap<>();
    public static void recordReading(String sensorId, long timestamp, double temperature) {
        sensorRegistry.computeIfAbsent(sensorId, k -> new SensorState())
                    .processReading(new Reading(sensorId, timestamp, temperature));
    }

    public static void monitorHealth(long currentTime) {
        for (Map.Entry<String, SensorState> entry : sensorRegistry.entrySet()) {
            String sensorId = entry.getKey();
            SensorState state = entry.getValue();
            
            if (state.isUnhealthy(currentTime)) {
                System.out.println("Sensor " + sensorId + " is UNHEALTHY.");
            } else {
                System.out.println("Sensor " + sensorId + " is Healthy.");
            }
        }
    }
    
    public static void main(String Args[]){
            long now = System.currentTimeMillis();
            long oneMin = 60 * 1000;

        
            recordReading("S2", now - (4 * oneMin), 25.0);
            recordReading("S2", now - (3 * oneMin), 26.0);
            recordReading("S2", now - (2 * oneMin), 25.5);
            recordReading("S2", now - (1 * oneMin), 24.0);
            recordReading("S2", now, 25.0);

        
            recordReading("S1", now - (2 * oneMin), 95.0);
            recordReading("S1", now - (1 * oneMin), 92.0);
            recordReading("S1", now, 96.0);

        
            recordReading("S3", now, 25.0); 

            monitorHealth(now);
    }

}
