package org.stadium.repository;

import org.stadium.model.BookingTransaction;
import org.stadium.model.enums.TransactionStatus;
import java.util.List;

public class TransactionRepository extends CsvRepository<BookingTransaction> {
    public TransactionRepository(String filePath) {
        super(filePath);
    }

    @Override
    public BookingTransaction createEntity() {
        return new BookingTransaction();
    }

    @Override
    public String getHeader(){
        return "transactionId,fanId,matchId.totalAmount,ticketCount,transactionDate,status";
    }
    public List<BookingTransaction> findByFanId(String fanId){
        return findByCondition(t -> t.toCsvLine().split(",", -1)[1].equals(fanId));
    }
    public List<BookingTransaction> findByStatus(TransactionStatus status){
        return findByCondition(t -> t.toCsvLine().split(",", -1)[6].equals(status.name()));
    }
}
