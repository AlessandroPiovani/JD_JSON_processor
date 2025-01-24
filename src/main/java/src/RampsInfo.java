/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author alessandro.piovani@istat.it
 */
public class RampsInfo {
    @JsonProperty ("start")
    private String start;
    
    @JsonProperty ("end")
    private String end;
    
    @JsonProperty ("fixed_coef")
    private double fixed_coef;
        
    public RampsInfo(){}

    public String getEnd() {
        return end;
    }

    public String getStart() {
        return start;
    }

    public double getFixed_coef() {
        return fixed_coef;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setFixed_coef(double fixed_coef) {
        this.fixed_coef = fixed_coef;
    }


}

