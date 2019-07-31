package com.sphereon.da.ledger.mithra.utils.fatd.util;

import java.io.Serializable;

public class Range implements Serializable {
    private int start;
    private int end;

    public Range(final int start, final int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return this.start;
    }

    public Range setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return this.end;
    }

    public Range setEnd(int end) {
        this.end = end;
        return this;
    }
}
