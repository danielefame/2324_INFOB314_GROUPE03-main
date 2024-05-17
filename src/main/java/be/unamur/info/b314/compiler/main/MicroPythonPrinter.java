package be.unamur.info.b314.compiler.main;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MicroPythonPrinter {
    private PrintWriter writer;

    public  MicroPythonPrinter(String fileName) {
        try {
            // Créer un objet File pour représenter le fichier Python
            File pythonFile = new File(fileName);

            // Créer un objet FileWriter pour écrire dans le fichier Python, en mode append
            FileWriter fw = new FileWriter(pythonFile, true);

            // Créer un objet PrintWriter pour écrire du texte formatté dans le fichier
            writer = new PrintWriter(fw);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Imports an entire module in MicroPython.
     * This method generates an import statement for a specified module.
     * Example usage: printImport("sys") would produce "import sys".
     *
     * @param module The name of the module to import.
     */
    public void printImport(String module) {
        writer.println("import " + module);
    }
    
    /**
     * Imports specific members from a module in MicroPython.
     * This method generates a from-import statement for specified members of a module.
     * Example usage: printFromImport("math", new String[]{"sin", "cos"}) would produce "from math import sin, cos".
     *
     * @param module The name of the module from which members are imported.
     * @param members An array of strings representing the names of the members to import.
     */
    public void printFromImport(String module, String[] members) {
        writer.println("from " + module + " import " + String.join(", ", members));
    }

    
    /**
     * Prints a function definition in MicroPython syntax.
     * @param name The name of the function.
     * @param params An arrayList of parameters for the function.
     * */
    public void printFunction(String name, ArrayList<String> params) {
        writer.println("def " + name + "(" + String.join(", ", params) + "):\n");
    }
    public void printcallFunction(String name, List<String> params,int identation) {
        writer.println(decalage("\t",identation) + name + "(" + String.join(", ", params) + "):\n");
        
    }

    public void printIf(String condition,int identation) {
        
        writer.println(decalage("\t",identation) +"if ( " + condition + "): \n");
        
    }
    public void printInitialization(String var, String value,int identation) {
        
        writer.println(decalage("\t",identation) + var + " = " + value);
    }
    public void printelse(int identation) {
        
            writer.println(decalage("\t",identation) +"else: \n");
        }
    
    public void printwhile(String condtion, int identation) {
       
            writer.println(decalage("\t",identation) +"while( " + condtion+ " ): \n");
 
        }
        
    
    public void printFor(String iter,int identation) {
        
            writer.println(decalage("\t",identation) +"for( i in range(" + iter+ ")): \n");
        }
    
        public static String decalage(String str,int count) {
            if (count <= 0) {
                return "";
            }
    
            StringBuilder builder = new StringBuilder(str.length() * count);
            for (int i = 0; i < count; i++) {
                builder.append(str);
            }
    
            return builder.toString();
        }
    /**
 * Print a multi-line comment.
 * @param comment The comment text.
 */
public void printMultiLineComment(String comment) {
    writer.println("'''\n" + comment + "\n'''");
}

/**
 * Print a single-line comment.
 * @param comment The comment text.
 */
public void printSingleLineComment(String comment) {
    writer.println("# " + comment);
}
}