import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class AqlInterpreter {
    public static void main(String[] args) throws Exception {
        // 1. Create ANTLR input stream from AQL query
        ANTLRInputStream input = new ANTLRFileStream("query.txt"); // Or provide input file/stream

        // 2. Create lexer
        AqlLexer lexer = new AqlLexer(input);

        // 3. Create token stream
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // 4. Create parser
        AqlParser parser = new AqlParser(tokens);

        // 5. Parse the input to generate parse tree
        ParseTree tree = parser.selectQuery();

        // 6. Create a listener to interpret the parse tree
        ParseTreeWalker walker = new ParseTreeWalker();
        AqlToMongoDBConverter converter = new AqlToMongoDBConverter();
        walker.walk(converter, tree);

        // 7. Get the MongoDB query from the converter
        String mongoQuery = converter.getMongoQuery();
        System.out.println("MongoDB Query: \n" + mongoQuery);
    }
}
