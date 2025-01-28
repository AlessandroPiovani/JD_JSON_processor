package src;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


/**
 *
 * @author alessandro.piovani@istat.it
 */
public class InterventionVariablesInfo {
    
    @JsonProperty ("delta")
    private double delta;
    
    @JsonProperty ("delta_s")
    private double delta_s;
    
    @JsonProperty ("seq")
    private List<SequenceInfo> seq;

    @JsonProperty ("D1DS")
    private boolean D1DS;
    
    @JsonProperty ("fixed_coef")
    private double fixed_coef;
    
    public void setDelta(double delta) {
        this.delta = delta;
    }

    public void setDelta_s(double delta_s) {
        this.delta_s = delta_s;
    }

    public void setSeq(List<SequenceInfo> seq) {
        this.seq = seq;
    }

    public double getDelta() {
        return delta;
    }

    public double getDelta_s() {
        return delta_s;
    }

    public List<SequenceInfo> getSeq() {
        return seq;
    }
    
    public boolean getD1DS() {
        return D1DS;
    }

    public void setD1DS(boolean D1DS) {
        this.D1DS = D1DS;
    }
 
    public double getFixed_coef() {
        return fixed_coef;
    }

    public void setFixed_coef(double fixed_coef) {
        this.fixed_coef = fixed_coef;
    }
}
