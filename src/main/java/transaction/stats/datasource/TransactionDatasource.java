package transaction.stats.datasource;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import transaction.stats.model.Statistics;
import transaction.stats.model.Transaction;

/**
 * This Class will be the in-memory store for transactions
 */
@Component
public class TransactionDatasource {


    private static ConcurrentHashMap<Long, Double> store = new ConcurrentHashMap<>();

    protected volatile static double sum = 0.0, min = Double.MAX_VALUE, max = Double.MIN_VALUE, average = 0.0;
    protected volatile static long count = 0;
    
    public Statistics getStatistics() throws Exception{


        Statistics result = new Statistics();
        if(count > 0) {
        	 result.setAvg(average);
             result.setCount(count);
             result.setMax(max);
             result.setMin(min);
             result.setSum(sum);
        }
        else {
        	result.setMax(0);
        	result.setMin(0);
        }
       
        return result;
    }


    public void logTransaction(Transaction transaction) throws Exception{
        store.put(transaction.getTimestamp(),transaction.getAmount());
        TransactionExpirer.isEmpty = false;
        compute(transaction.getAmount());
    }
    
    protected static ConcurrentHashMap<Long , Double> getStore(){
        return store;
    }
    
    synchronized void compute(double amount){
    	sum += amount;
		count+= 1;
		min = TransactionExpirer.getMin(amount, min);
		max =TransactionExpirer.getMax(amount, max);
		average = TransactionExpirer.getAverage(sum, count);
    }
    
}


