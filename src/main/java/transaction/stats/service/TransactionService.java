package transaction.stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import transaction.stats.datasource.TransactionDatasource;
import transaction.stats.model.Statistics;
import transaction.stats.model.Transaction;

import java.time.Clock;
import java.time.Instant;

@Component
public class TransactionService {

	@Autowired
	private TransactionDatasource transactionDatasource;

	@Value("${retention.time}")
	private int retentionTime;
	
    public Statistics getStatistics() throws Exception {

        return transactionDatasource.getStatistics();
    }


    public boolean logTransaction(Transaction transaction) throws Exception {
        if(Instant.now(Clock.systemUTC()).toEpochMilli() - transaction.getTimestamp() <= retentionTime*1000){
        	transactionDatasource.logTransaction(transaction);
            return true;
        }

        return  false;


    }


	public TransactionDatasource getTransactionDatasource() {
		return transactionDatasource;
	}


	public void setTransactionDatasource(TransactionDatasource transactionDatasource) {
		this.transactionDatasource = transactionDatasource;
	}

    
}
