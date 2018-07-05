package transaction.stats.datasource;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;


@EnableScheduling
@Component
public class TransactionExpirer {

	protected static volatile boolean isEmpty = true;
	static Object objectLock = new Object();
	@Value("${retention.time}")
	private int retentionTime;
	private static final int tolerance = 10;
	private double sum = 0, min = Double.MAX_VALUE, max = Double.MIN_VALUE;
	long count = 0;
	
	 @Bean
		public TaskScheduler taskScheduler() {
		    return new ConcurrentTaskScheduler(); //single threaded by default
		}
	
	public void purgeExpiredTransaction() {
		while(true) {
			if(!isEmpty) {
				long currentTime = System.currentTimeMillis();
				ConcurrentHashMap<Long, Double> store = TransactionDatasource.getStore();
				if(store.isEmpty())
					isEmpty = true;
				else {
					for(Entry<Long, Double> entry: store.entrySet()) {
						long time = retentionTime*1000;
						if(currentTime - entry.getKey() > time) {
							store.remove(entry.getKey());
						}
					}
					sum = 0.0;
					min = Double.MAX_VALUE;
					max = Double.MIN_VALUE;
					count = 0;
					normalize(store.values());
					
				}
			}
		}
	}

	private void normalize(Collection<Double> values) {
		
		for (double amount: values) {
			sum += amount;
			min = getMin(amount, min);
			max = getMax(amount, max);
		}
		 count = values.size();
		 
	
		TransactionDatasource.sum = sum;
		TransactionDatasource.count = count;
		TransactionDatasource.max = max;
		TransactionDatasource.min = min;
		TransactionDatasource.average = getAverage(sum, count);
		
	}
	
	public static double getMax(double a, double b) {
    	return a>b?a:b;
    }
    
    public static double getMin(double a, double b) {
    	return a<b?a:b;
    }
    
    public static double getAverage(double value, long count) {
    	if(count <= 0)
    		return 0.0;
    	return value/count;
    }

    //Use a single thread to clean up expired data and update the statistics.
	@Scheduled(fixedRate=tolerance)
	public void run() {
		purgeExpiredTransaction();
	}
}
