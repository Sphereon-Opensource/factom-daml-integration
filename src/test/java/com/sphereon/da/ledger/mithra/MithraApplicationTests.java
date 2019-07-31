package com.sphereon.da.ledger.mithra;

import com.sphereon.da.ledger.mithra.dto.FatTransaction;
import com.sphereon.da.ledger.mithra.services.TransactionService;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MithraApplicationTests {
    @Autowired
    TransactionService transactionService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void transactionTest(){
        try {
            String from = "from";
            String to = "to";
            long amount = 100;
            String tx = transactionService.createTransactionHex(to, from, amount);
            String tx2 = String.format("{\"inputs\":{\"%s\":%d},\"outputs\":{\"%s\":%d}}",
                    from, amount, to, amount);
            System.out.println(tx);
            System.out.println(Hex.encodeHexString(tx2.getBytes()));
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
