package src;

    import com.fasterxml.jackson.annotation.JsonProperty;

    public class DestSpecVarFromFileInfo {
        @JsonProperty ("container")
        private String container;
        @JsonProperty ("start")
        private String start;
        @JsonProperty ("n_var")
        private int numVar;
        
        public DestSpecVarFromFileInfo(){}

        public String getContainer() {
            return container;
        }

        public void setContainer(String container) {
            this.container = container;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public int getNumVar() {
            return numVar;
        }

        public void setNumVar(int numVar) {
            this.numVar = numVar;
        }
        
        
    }    
