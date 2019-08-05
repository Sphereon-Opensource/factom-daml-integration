package com.sphereon.da.ledger.mithra.utils;

import com.daml.ledger.javaapi.data.Filter;
import com.daml.ledger.javaapi.data.FiltersByParty;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.InclusiveFilter;
import com.daml.ledger.javaapi.data.TransactionFilter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class LedgerUtils {

    public static TransactionFilter filterFor(Set<Identifier> templateIds, String party) {
        InclusiveFilter inclusiveFilter = new InclusiveFilter(templateIds);
        Map<String, Filter> filter = Collections.singletonMap(party, inclusiveFilter);
        return new FiltersByParty(filter);
    }
}
