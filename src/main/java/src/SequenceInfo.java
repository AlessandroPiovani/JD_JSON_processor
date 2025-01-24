package src;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author alessandro.piovani@istat.it
 */
public class SequenceInfo {
    @JsonProperty ("start")
    private String start;
    
    @JsonProperty ("end")
    private String end;

        public String getEnd() {
        return end;
    }

    public String getStart() {
        return start;
    }


    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
