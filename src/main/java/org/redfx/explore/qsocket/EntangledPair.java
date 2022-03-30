package org.redfx.explore.qsocket;

import org.redfx.strange.Qubit;

/**
 *
 * @author johan
 */
public class EntangledPair {

    private final long idx;
    private final Qubit a;
    private final Qubit b;

    public EntangledPair (long idx, Qubit a, Qubit b) {
        this.idx = idx;
        this.a = a;
        this.b = b;
    }

    public Qubit getA() {
        return this.a;
    }

    public Qubit getB() {
        return this.b;
    }
    
    public long getId() {
        return this.idx;
    }

}
