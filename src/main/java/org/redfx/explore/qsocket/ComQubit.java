/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.redfx.explore.qsocket;

import java.io.IOException;
import org.redfx.strange.Qubit;

/**
 *
 * @author johan
 */
public class ComQubit {
    
    long id;
    private final EntanglerService entanglerService;
    
    public ComQubit(EntanglerService es) {
        super();
        this.entanglerService = es;
    }
    
    public int measure() throws IOException {
        return this.entanglerService.measureComQubit(this);
    }

    void applyXGate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void entangleWith(ComQubit qubit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setId(long id) {
        this.id = id;
    }
}
