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
        // pass this description to the function to create the Expr class
        defineAst(outputDir, "Expr", description);
        // generate statement and expression class
        defineAst(outputDir, "Stmt", Arrays.asList(
                "Expression : Expr expression",
                "Print : Expr expression"));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        // start of file
        writer.println("package com.saivishnu.lox;\n");
        writer.println("import java.util.List;\n");
        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println("\n\tabstract <R> R accept(Visitor<R> visitor);");

        // end of file
        writer.println("}");
        writer.close();
    }

    // implementation of Visitor interface (it is a Design Pattern)
    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("\tinterface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("\t\tR visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("\t}");
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

        writer.println("\t\t@Override");
        writer.println("\t\t<R> R accept(Visitor<R> visitor) {");
        writer.println("\t\t\treturn visitor.visit" + className + baseName + "(this);");
        writer.println("\t\t}");

        // create fields
        for (String field : fields)
            writer.println("\t\tfinal " + field + ";");

        // close/end class
        writer.println("\t}");

    }
}
