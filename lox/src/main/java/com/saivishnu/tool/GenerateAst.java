package com.saivishnu.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        // description of each type and its fields
        List<String> description = Arrays.asList(
                "Binary : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal: Object value",
                "Unary : Token operator, Expr right");
        // pass this description to the function
        defineAst(outputDir, "Expr", description);
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        // start of file
        writer.println("package com.saivishnu.lox;\n");
        writer.println("import java.util.List;\n");
        writer.println("abstract class " + baseName + " {");

        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        // end of file
        writer.println("}");
        writer.close();
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        // create class
        writer.println("\tstatic class " + className + " extends " + baseName + " {");

        // create constructor
        writer.println("\t\t" + className + "(" + fieldList + ")" + "{");
        // store parameters in fields
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("\t\t\tthis." + name + " = " + name + ";");
        }
        writer.println("\t\t}\n");

        // create fields
        for (String field : fields)
            writer.println("\t\tfinal " + field + ";");

        // close/end class
        writer.println("\t}");

    }
}
