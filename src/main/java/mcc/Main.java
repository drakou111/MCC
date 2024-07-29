package mcc;

import mcc.compiler.AssemblyHelper;
import mcc.compiler.VariableList;
import mcc.node.java.ProgramNode;
import mcc.parser.Parser;
import mcc.token.Token;
import mcc.token.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        /*if (args.length < 2) {
            System.err.println("Usage: java Main <input.txt> <output.as>");
            return;
        }

        String inputPath = args[0];
        String outputPath = args[1];*/

        String inputPath = "input.txt";
        String outputPath = "output.as";

        String fileContent;

        try {
            fileContent = new String(Files.readAllBytes(Paths.get(inputPath)));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        System.out.println("Tokenizing...");
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.getTokens(fileContent);

        System.out.println("Parsing...");
        Parser parser = new Parser(tokens);
        ProgramNode node = parser.parse();

        System.out.println("Compiling...");
        fileContent = node.compile(new VariableList(), 0, "", "") + AssemblyHelper.halt();

        try {
            Files.writeString(Paths.get(outputPath), fileContent);
            System.out.println("Successfully compiled " + inputPath + " into " + outputPath + ".");
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
}