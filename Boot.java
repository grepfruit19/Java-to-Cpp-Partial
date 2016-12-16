package edu.nyu.oop;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import edu.nyu.oop.util.JavaFiveImportParser;
import edu.nyu.oop.util.NodeUtil;
import edu.nyu.oop.util.XtcProps;
import org.slf4j.Logger;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.util.Tool;
import xtc.lang.JavaPrinter;
import xtc.parser.ParseException;

/**
 * This is the entry point to your program. It configures the user interface, defining
 * the set of valid commands for your tool, provides feedback to the user about their inputs
 * and delegates to other classes based on the commands input by the user to classes that know
 * how to handle them. So, for example, do not put translation code in Boot. Remember the
 * Single Responsiblity Principle https://en.wikipedia.org/wiki/Single_responsibility_principle
 */
public class Boot extends Tool {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Override
    public String getName() {
        return XtcProps.get("app.name");
    }

    @Override
    public String getCopy() {
        return XtcProps.get("group.name");
    }

    @Override
    public void init() {
        super.init();
        // Declare command line arguments.
        runtime.
        bool("printJavaAst", "printJavaAst", false, "Print Java Ast.").
        bool("printJavaCode", "printJavaCode", false, "Print Java code.").
        bool("printJavaImportCode", "printJavaImportCode", false, "Print Java code for imports and package source.");
    }

    @Override
    public void prepare() {
        super.prepare();
        // Perform consistency checks on command line arguments.
        // (i.e. are there some commands that cannot be run together?)
        logger.debug("This is a debugging statement."); // Example logging statement, you may delete
    }

    @Override
    public File locate(String name) throws IOException {
        File file = super.locate(name);
        if (Integer.MAX_VALUE < file.length()) {
            throw new IllegalArgumentException("File too large " + file.getName());
        }
        if(!file.getAbsolutePath().startsWith(System.getProperty("user.dir"))) {
            throw new IllegalArgumentException("File must be under project root.");
        }
        return file;
    }

    @Override
    public Node parse(Reader in, File file) throws IOException, ParseException {
        return NodeUtil.parseJavaFile(file);
    }

    @Override
    public void process(Node n) {
        if (runtime.test("printJavaAst")) {
            runtime.console().format(n).pln().flush();
        }

        if (runtime.test("printJavaCode")) {
            new JavaPrinter(runtime.console()).dispatch(n);
            runtime.console().flush();
        }

        if (runtime.test("printJavaImportCode")) {
            List<GNode> nodes = JavaFiveImportParser.parse((GNode) n);
            for(Node node : nodes) {
                runtime.console().pln();
                new JavaPrinter(runtime.console()).dispatch(node);
            }
            runtime.console().flush();
        }

        // if (runtime.test("Your command here.")) { ... don't forget to add it to init()
    }

    /**
     * Run Boot with the specified command line arguments.
     *
     * @param args The command line arguments.
     */
    //Assumes commandline Boot.java inputFileName
    public static void main(String[] args) {
        String inputFileName = args[0];

        SourceAstGenerator SourceAstGenerator = new SourceAstGenerator("");
        AstParser AstParser = new AstParser();
        System.out.println("=====Generating source ASTs=====");
        //Contains the head node for the input file.
        Node sourceAst = SourceAstGenerator.generateAst(false,inputFileName);
        //System.out.println("Source files generated");
        //AstParser.prettyPrintAst(sourceAst);

        System.out.println("=====Parsing Classes from source ASTs=====");
        ArrayList<ClassInfo> classes = AstParser.processAstRoot(sourceAst);

        System.out.println();
        //This line handles all the inheritance mumbo jumbo.
        HeaderAstGenerator.processClassInfos(classes);



        System.out.println("=====Generating header ASTs from parsed information=====");
        //Contains the root nodes for every class in a given source file.
        ArrayList<Node> headerAsts = new ArrayList<>();
        for (ClassInfo current : classes){
            Node astHead = HeaderAstGenerator.createClassAst(current);
            AstParser.prettyPrintAst(astHead);
            headerAsts.add(astHead);
        }

        System.out.println("=====Generating header file (output.h)=====");
        //Generates header files.
        HeaderPrinter.generateHeaderFile(headerAsts);
        System.out.println("=====Header file generated=====");
        HeaderPrinter.generateHeaderFile(headerAsts);

        //ArrayList<Node> allASTs = SourceAstGenerator.generateAllASTs(true);

        //Each AL contains an AL, which corresponds to an entire AST.
        //ArrayList<ArrayList> parsedAsts = AstParser.processAstSet(allASTs);




        //new Boot().run(args);
        phase4 p4 = new phase4();
        phase5 p5 = new phase5();


        Node destAst = p4.createNewAST(sourceAst);



//        ArrayList<Node> outputAsts = new ArrayList<>();
//        for (int i =0;i<6;i++){
//            String fileName= "Test00"+Integer.toString(i);
//            outputAsts.add(p4.createNewAST(SourceAstGenerator.generateAst(false,fileName)));
//
//        }


        // TO SEE THE CPP AST
//        AstParser.prettyPrintAst(destAst);

        
        int testnum = Integer.parseInt(inputFileName.substring(inputFileName.length() - 1));
//        System.out.println(testnum);
        p5.printOutputCpp(destAst, testnum);
        p5.printMainCpp(destAst.getNode(destAst.size()-1),testnum);

//        //phase 5
//        for (int i =0;i<6;i++) {
//            p5.printOutputCpp(outputAsts.get(i), i);
//            p5.printMainCpp(outputAsts.get(i).getNode(outputAsts.get(i).size()-1), i);
//        }
//


    }
}