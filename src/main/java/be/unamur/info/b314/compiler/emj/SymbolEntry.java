package be.unamur.info.b314.compiler.emj;


// Classe interne pour représenter une entrée de la table des symboles
public class SymbolEntry {


        private final String symbolId;
        private final String type;
        private final String functionName;

        public SymbolEntry(String symbolId, String type, String functionName) {
            this.symbolId = symbolId;
            this.type = type;
            this.functionName = functionName;
        }

        public String getSymbolId() {
            return symbolId;
        }

        public String getType() {
            return type;
        }

        public String getFunctionName() {
            return functionName;
        }

}
