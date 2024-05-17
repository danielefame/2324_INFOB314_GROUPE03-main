package be.unamur.info.b314.compiler.emj;

import be.unamur.info.b314.compiler.EMJParser;
import be.unamur.info.b314.compiler.main.MicroPythonPrinter;
import be.unamur.info.b314.compiler.EMJLexer;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


/*
Visitor class for the EMJ language, extending the base visitor class generated by ANTLR
@author : Alix Decrop
@version : 1.0
*/
public class EMJVisitor extends be.unamur.info.b314.compiler.EMJParserBaseVisitor<Object> {

    private String currentFunction = "{1F3E0}"; // Initialiser à la fonction main par défaut
    private final EMJErrorLogger errorLogger;
    private final HashMap<String, SymbolEntry> symbolTable;
    private MicroPythonPrinter printer = new MicroPythonPrinter("trs");
    public EMJVisitor() {
        this.errorLogger = new EMJErrorLogger();
        this.symbolTable = new HashMap<>();
    }

    public EMJErrorLogger getErrorLogger() {
        return this.errorLogger;
    }

    private String getCurrentFunction() {
        return currentFunction;
    }

    // Méthode pour ajouter une entrée dans la table des symboles
    private void addSymbolEntry(String symbolId, String type, String functionName) {
        symbolTable.put(symbolId, new SymbolEntry(symbolId, type, functionName));
    }

    // Méthode pour vérifier si un identifiant de symbole existe déjà dans la table des symboles
    private boolean symbolExists(String symbolId) {
        return symbolTable.containsKey(symbolId);
    }

    private boolean FunctionExists(String functionName) {
        for (SymbolEntry entry : symbolTable.values()) {
            if (entry.getFunctionName().equals(functionName)) {
                return true; // La fonction existe déjà
            }
        }
        return false; // La fonction n'existe pas
    }

    // Méthode pour récupérer une entrée dans la table des symboles
    private SymbolEntry getSymbolEntry(String symbolId) {
        return symbolTable.get(symbolId);
    }


    public String getFuncReturnType(EMJParser.FuncreturnContext ctx) {
        // Récupérer le dernier ajout de fonction dans la table des symboles
        String lastAddedFunction = symbolTable.values().stream()
                .filter(entry -> "function".equals(entry.getType()))
                .map(SymbolEntry::getFunctionName)
                .reduce((first, second) -> second) // Obtenez le dernier élément de la liste
                .orElse(null);

        // Si aucune fonction n'a été ajoutée, retournez une chaîne vide
        if (lastAddedFunction == null) {
            return "";
        }

        // Sinon, retournez le nom de la dernière fonction ajoutée
        return lastAddedFunction;
    }
// Méthode pour retirer le \ u des codes unicode
    private String cleanUnicodeType(String unicodeString) {
        String idOK = unicodeString.replaceAll("\\\\u|[\"]", " ");
        return idOK;
    }

    @Override
    public Object visitFuncdecl(EMJParser.FuncdeclContext ctx) {
        // Récupérer l'identifiant et le type de la fonction
        
        String funcId = cleanUnicodeType(ctx.id().emojis().emoji().getText());
        String funcType = cleanUnicodeType(ctx.functype().type().emojitype().getText());
                        
        currentFunction = funcId;
        
        if (ctx.functype() != null && ctx.functype().type() != null) {
            funcType = visitType(ctx.functype().type());
        }
        else if ( ctx.functype().VOID() != null){
            funcType = "Void";
        }

        // Vérifier si la fonction existe déjà dans la table des symboles
        if (FunctionExists(funcId)) {
            // Si la fonction existe déjà, générer une erreur
            this.errorLogger.addError(new EMJError("funcIdAlreadyExists", ctx.getText(), ctx.start.getLine()));
        } else {
            // Ajouter l'entrée dans la table de symboles
            addSymbolEntry(funcId, funcType, funcId);
        }

        // Récupérer le type de retour de la fonction
        String funcReturnType = getFuncReturnType(ctx.funcreturn());

        // Vérifier si le type de retour correspond au type déclaré
        if (!funcReturnType.equals(funcType)) {
            // Si les types ne correspondent pas, générer une erreur
            this.errorLogger.addError(new EMJError("funcReturnTypeMismatch", ctx.getText(), ctx.start.getLine()));
        }

        return null;
    }

    private String determineValueType(String value) {
        try {
            Integer.parseInt(value);
            return "int"; // Si la chaîne peut être convertie en entier, le type est 'int'
        } catch (NumberFormatException e) {
           {
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    return "boolean"; // Si la chaîne est 'true' ou 'false', le type est 'boolean'
                } else {
                    return "string"; // Sinon, considérez-le comme une chaîne de caractères
                }
            }
        }
    }
    @Override
    public Object visitVardecl(EMJParser.VardeclContext ctx) {
        String varId = cleanUnicodeType(ctx.id().emojis().emoji().getText());
        String varType = cleanUnicodeType(ctx.type().emojitype().getText());

        // Vérifier si l'identifiant de la variable existe déjà dans la table des symboles
        if (symbolExists(varId)) {
            // Si la variable existe déjà, vérifier si elle a déjà été déclarée dans cette fonction
            SymbolEntry entry = getSymbolEntry(varId);
            if (entry.getFunctionName().equals(currentFunction)) {
                // Si oui, ajouter une erreur
                this.errorLogger.addError(new EMJError("varIdAlreadyExistsinthisfunction", ctx.getText(), ctx.start.getLine()));
            } else {
                // Sinon ajouter l'entrée dans la table de symboles
                addSymbolEntry(varId, varType, currentFunction);
            }
        } else {
            // Si la variable n'existe pas, ajouter une nouvelle entrée dans la table des symboles
            addSymbolEntry(varId, varType, currentFunction);
        }

        // Exemple: Supposons que la valeur est récupérée de la même manière (cela dépend de votre grammaire ANTLR)
        String assignedValue = cleanUnicodeType(ctx.varvalue().basicvalue().getText()); // Adaptez cela selon la structure réelle de votre arbre syntaxique
        String valueType = cleanUnicodeType(determineValueType(assignedValue)); // Vous devriez avoir une méthode pour déterminer le type de la valeur

        // Vérification du type
        if (!varType.equals(valueType)) {
            this.errorLogger.addError(new EMJError("typeMismatch", "Assigned value type does not match variable type", ctx.start.getLine()));
        }
        printer.printInitialization(varId, assignedValue,0);

        return null;
    }


    // Si la fonction appelée n'est pas déclarée, généré une erreur
    /**
     * Visits the CallfuncContext to handle function calls.
     * Retrieves the identifier of the called function and checks its existence in the symbol table.
     * If the function is not declared, an error is generated.
     * 
     * @param ctx The CallfuncContext to be visited.
     * @return null
     */
    @Override
    public Object visitCallfunc(EMJParser.CallfuncContext ctx) {
        // Retrieve the identifier of the called function
        String funcId = cleanUnicodeType(ctx.id().emojis().emoji().getText());
        
        List<String> params = new ArrayList<>(); // List to store the function parameters as String
        // Print the function call for debugging purposes
        printer.printcallFunction("ghj",params,1);
    
        // Check if the function identifier exists in the symbol table
        if (!symbolExists(funcId)) {
            // Generate an error if the called function is not declared
            this.errorLogger.addError(new EMJError("FunctionNotDeclared", ctx.getText(), ctx.start.getLine()));
        }
    
        return null;
    }



    public void printSymbolTable() {
        System.out.println("Symbol Table:");
        for (SymbolEntry entry : symbolTable.values()) {
            System.out.println("Symbol ID: " + entry.getSymbolId());
            System.out.println("Type: " + entry.getType());
            System.out.println("Function Name: " + entry.getFunctionName());
            System.out.println("--------------------------");
        }
    }


    //Visite le root
    @Override
    public Object visitRoot(EMJParser.RootContext ctx) {
        // Visite du programme ou de la carte
        if (ctx.programme() != null) {
            visitProgramme(ctx.programme());
        } else if (ctx.carte() != null) {
            visitCarte(ctx.carte());
        }
        return null;
    }

    @Override
    public Object visitProgramme(EMJParser.ProgrammeContext ctx) {
        // Visite de l'importation, de la chaîne de caractères et de la fonction principale
        if (ctx.run().funcdecl() != null) {
            visitFuncdecl(ctx.run().funcdecl());
            if (ctx.run().funcdecls() != null){
                // Vérification et visite des autres déclarations de fonction
                for (EMJParser.FuncdeclsContext funcdeclsContext : ctx.run().funcdecls()) {
                    visitFuncdecls(funcdeclsContext);
                }
            }
        }
        return null;
    }

    @Override
    public Object visitCarte(EMJParser.CarteContext ctx) {
        // Visite du nombre de lignes, du nombre de colonnes et de la direction
        int nbLines = Integer.parseInt(ctx.nbline().getText());
        int nbColumns = Integer.parseInt(ctx.nbcolumn().getText());
        visitDirection(ctx.direction());

       /* // Vérification et visite des éléments de la carte
        if (ctx.elements() != null) {
            for (int i = 0; i < ctx.elements().element().size(); i++) {
                EMJParser.ElementContext elementContext = ctx.elements().element(i);
                visitElement(elementContext);
            }
        }
*/
        return null;
    }



    public Object gettypeleftexp(EMJParser.LeftexpContext ctx) {
        // Récupérer l'identifiant de la fonction appelée
        String type = ctx.id().getText();

        return type;
    }


    // Méthode pour vérifier si une variable a été déclarée avant une affectation
    private void checkVarDeclarationBeforeAssignment(EMJParser.LeftexpContext ctx) {
        // Récupérer l'identifiant de la variable
        String varId = cleanUnicodeType(ctx.id().emojis().emoji().getText());

        // Vérifier si la variable a été déclarée
        if (!symbolTable.containsKey(varId)) {
            // Si la variable n'a pas été déclarée, générer une erreur
            this.errorLogger.addError(new EMJError("varNotDeclared", ctx.getText(), ctx.start.getLine()));
        }
    }

    @Override
    public Object visitAffectinstr(EMJParser.AffectinstrContext ctx) {
        // Vérifier si la variable a été déclarée avant l'affectation
        checkVarDeclarationBeforeAssignment(ctx.leftexp());

        // Appeler la méthode pour vérifier la compatibilité des types
        checkValueTypeCompatibility(ctx.leftexp(), ctx.rightexp());

        return null;
    }


    // Méthode pour vérifier la compatibilité des types entre la valeur assignée et le type déclaré de la variable
    private void checkValueTypeCompatibility(EMJParser.LeftexpContext leftExpContext, EMJParser.RightexpContext rightExpContext) {
        // Récupérer l'identifiant de la variable
        String varId = cleanUnicodeType(leftExpContext.id().emojis().emoji().getText());

        // Récupérer le type déclaré de la variable
        String varType = symbolTable.get(varId).getType();

        // Récupérer le type de la valeur à droite
        String rightExpType = rightExpContext.accept(this).toString();

        // Vérifier si les types correspondent
        if (!rightExpType.equals(varType)) {

            // Si les types ne correspondent pas, générer une erreur
            this.errorLogger.addError(new EMJError("typeMismatch", rightExpContext.getText(), rightExpContext.start.getLine()));
        }

    }

    public String visitType(EMJParser.TypeContext ctx){


        return null;
    }


    @Override
    public String visitFuncreturn(EMJParser.FuncreturnContext ctx) {
        // Récupérer le type de la valeur de retour de la fonction
        String returnExprType = "";


        // Récupérer le type de la valeur de retour déclaré dans la fonction
        if (ctx.rightexp() != null) {
            returnExprType = visitRightexp(ctx.rightexp());
        } else if (ctx.VOID() != null) {
            returnExprType = "void";
        }

        return returnExprType;
    }


    // Méthode pour visiter les expressions à droite
    @Override
    public String visitRightexp(EMJParser.RightexpContext ctx) {
        if (ctx.intexp() != null) {
            // Si c'est une expression entière, retourne le type "int"
            return "int";
        }/* else if (ctx.callfunc() != null) {
            // Si c'est un appel de fonction, retourne le type de retour de la fonction
            return visitCallfunc(ctx.callfunc()); // Supposant que la méthode accepte correctement le type de retour
        } */else if (ctx.string() != null) {
            // Si c'est une chaîne de caractères, retourne le type "str"
            return "str";
        } else if (ctx.tuple() != null) {
            // Si c'est un tuple, retourne le type "tuple"
            return "tuple";
        } else if (ctx.boolean1() != null) {
            // Si c'est un booléen, retourne le type "bool"
            return "bool";
        }
        return null;
    }

}

