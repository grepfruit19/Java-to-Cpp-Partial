package edu.nyu.oop;


import edu.nyu.oop.util.NodeUtil;
import edu.nyu.oop.util.RecursiveVisitor;


import xtc.tree.Node;
import xtc.util.Runtime;
import xtc.lang.JavaEntities;

import java.io.*;
import java.util.*;

//This code handles the pulling of all data from each AST
public class AstParser extends RecursiveVisitor{//extends Visitor extends Annotation {

    public static Runtime newRuntime() { // code copied from xtc-demo/XtcTestUtils.java
        Runtime runtime = new Runtime();
        runtime.initDefaultValues();
        runtime.dir("in", Runtime.INPUT_DIRECTORY, true, "");
        runtime.setValue(Runtime.INPUT_DIRECTORY, JavaEntities.TEMP_DIR);
        return runtime;
    }

    public static void prettyPrintAst(Node node) { // code copied from xtc-demo/XtcTestUtils.java
        newRuntime().console().format(node).pln().flush();
    }

    /*Assumes an input of an ArrayList of AST roots.
    * Contains AL of AL, each element in the outer AL corresponds to an AST.
    * Each of those ALs contains ClassInfos for each class in the AST.
    */
    public ArrayList<ArrayList> processAstSet(ArrayList<Node> allASTs){
        System.out.println(allASTs.size());
        //astInfo stores the classes for each AST
        ArrayList<ArrayList> astInfo = new ArrayList<>();
        //iterate through each AST
        for(int astCounter=0;astCounter<allASTs.size();astCounter++){
            System.out.println("iterating through AST " + astCounter);
            Node astRoot = allASTs.get(astCounter);
            ArrayList<ClassInfo> astClasses = this.processAstRoot(astRoot);
            astInfo.add(astClasses);
        }
        return astInfo;
    }

    public ArrayList<ClassInfo> processAstRoot(Node astRoot){
        ArrayList<ClassInfo> astClasses = new ArrayList<>();

        Node packageNode = NodeUtil.dfs(astRoot, "QualifiedIdentifier");
        String packageName = packageNode.getString(0) + "_" + packageNode.getString(1);

        List<Node> classRoots = NodeUtil.dfsAll(astRoot, "ClassDeclaration");
        //iterate through ClassDeclarations
        for (int classCounter = 0;classCounter<classRoots.size();classCounter++){
            Node classRoot = classRoots.get(classCounter);

            ClassInfo thisClass = processClassDec(classRoot, packageName);
            astClasses.add(thisClass);
        }
        return astClasses;
    }

    //Takes in a ClassDeclaration node, parses its data into ClassInfo
    private ClassInfo processClassDec(Node classRoot, String packageName){
        String className = classRoot.getString(1);

        String superClass;
        Node extensionNode = classRoot.getNode(3);
        if (extensionNode!=null){
            //currentNode = Type
            Node currentNode = extensionNode.getNode(0);
            //currentNode = QualifiedIdentifier
            currentNode = currentNode.getNode(0);
            superClass = currentNode.getString(0);
        } else{
            superClass = "null";
        }

        ClassInfo thisClass = new ClassInfo(packageName, className, superClass);

        //Contains all MethodDeclaration nodes within a particular class.
        List<Node> allMethods = NodeUtil.dfsAll(classRoot, "MethodDeclaration");

        //Iterates through each MethodDeclaration node within a particular class.
        for (int methodCounter = 0; methodCounter < allMethods.size(); methodCounter++) {
            //methodHead equivalent to MethodDeclaration
            Node methodHead = allMethods.get(methodCounter);

            MethodInfo currentMethod = this.processMethodDec(methodHead);
            thisClass.addMethod(currentMethod);
        }
        thisClass.setMethodInheritances();

        return thisClass;
    }


    public static List<Node> searchMethodsInAClass_andMakeVTableMethodDeclarationNode(List<Node> find_ClassDeclaration){
        //go to ClassBody Node
        for(Node n: find_ClassDeclaration) {
            Node classBodyNode = (Node) n.get(5);
            List<Node> methodNodes = NodeUtil.dfsAll(classBodyNode, "MethodDeclaration");
            System.out.println(methodNodes.size());
        }
        return null;
    }

    //Takes in a MethodDeclaration node and parses its info into a MethodInfo
    private MethodInfo processMethodDec(Node methodHead){

        ArrayList<String> modifiers = new ArrayList<>();
        List<Node> allModifiers = NodeUtil.dfsAll(methodHead, "Modifier");
        for (Node mod : allModifiers) {
            modifiers.add(mod.getString(0));
        }

        String returnType = "";
        Node returnHead = methodHead.getNode(2);
        //System.out.println(returnHead.getName());
        if (returnHead.getName() == "Type"){
            Node typeIdentifier = returnHead.getNode(0);
            returnType = typeIdentifier.getString(0);
        }
        else if (returnHead.getName() == "VoidType"){
            returnType = "void";
        }

        String methodName = methodHead.getString(3);

        //TODO: Inherited From?

        //Parameters will be stored as a list of Strings
        Node parametersHead = methodHead.getNode(4);
        ArrayList<String> parameterList = new ArrayList<>();
        //Not to be confused with FormalParameters node, which is the parent to FormalParameter
        List<Node> allFormalParameter = NodeUtil.dfsAll(parametersHead, "FormalParameter");

        for (int parameterCounter=0;parameterCounter<allFormalParameter.size();parameterCounter++){
            //currentParameter = FormalParameter
            Node currentParameter = allFormalParameter.get(parameterCounter);
            //currentParameter = Type
            currentParameter = currentParameter.getNode(1);
            //currentParameter = QualifiedIdentifier
            currentParameter = currentParameter.getNode(0);
            String theParameter = currentParameter.getString(0);
            parameterList.add(theParameter);
        }

        MethodInfo currentMethod = new MethodInfo(methodName, returnType, modifiers, parameterList);
        return currentMethod;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        System.out.println("-----phase 1 START------");
        SourceAstGenerator SourceAstGenerator = new SourceAstGenerator("");
        //allASTs stores the head node for every AST.
        ArrayList<Node> allASTs = SourceAstGenerator.generateAllASTs(true);

        System.out.println("-----phase 2 START------");
        AstParser p2 = new AstParser();

        Node test01 = allASTs.get(1);//root node for test001
        ArrayList<ClassInfo> testClasses = p2.processAstRoot(test01);

        //Test ClassInfo
        //ClassInfo test_001_Class_A = new ClassInfo("inputs_test001","A", "Object");
        //Node test_001_A = HeaderAstGenerator.createClassAst(test_001_Class_A);
        //p2.prettyPrintAst(test_001_A);

/*
        //Test Method Info
        ClassInfo test_001_Class = new ClassInfo("inputs_test001","A", "Object");
        System.out.println("-----------test Class------------");

        ArrayList<String> testMods = new ArrayList<>();
        ArrayList<String> testParams = new ArrayList<>();
        MethodInfo testMethod = new MethodInfo("toString", "String", testMods, testParams);
        test_001_Class.addMethod(testMethod);


        Node test_001 = HeaderAstGenerator.createClassAst(test_001_Class);
        prettyPrintAst(test_001);

        ArrayList<Node> headerTest = new ArrayList<>();
        headerTest.add(test_001);
        HeaderPrinter.generateHeaderFile(headerTest);
*/
        //Test with the actual AST generated from phase 1
        ArrayList<Node> cppAST = new ArrayList<Node>();
        for(Node a : allASTs) {
            Node object_subtree = HeaderAstGenerator.createObjectSubtree();

            for (ClassInfo i : p2.processAstRoot(a)) {
                /*ArrayList<MethodInfo> methods = i.getMethods();

                //System.out.println("Method Size");
                //System.out.println(methods.size());
                int methodsSize = methods.size();

                for (int m = 0; m < methodsSize; m++) {
                    //System.out.println("for loop");
                    //j.printMethod();
                    i.addMethod(methods.get(m));
                }*/

                Node createNode = HeaderAstGenerator.createClassAst(i);
                object_subtree.addNode(createNode);//Adds the A Node and test001 Node

            }
            cppAST.add(object_subtree);
        }
        //p2.prettyPrintAst(cppAST.get(1));

        //phase3
        ArrayList<Node> headerTest = new ArrayList<>();
        headerTest.add(cppAST.get(1));
        //cppAST.get(1) is rootNode(Object Node)
        //cppAST.get(1).getNode(3) is class A
        HeaderPrinter.generateHeaderFile(headerTest);


        /*
        System.out.println("-----------Empty Tree------------");
        Node empty_tree = HeaderAstGenerator.emptyTree();
        p2.prettyPrintAst(empty_tree);
        */

        /*System.out.println("-----------Object SubTree------------");
        Node object_subtree = HeaderAstGenerator.createObjectSubtree();
        p2.prettyPrintAst(object_subtree);

        System.out.println("-----------String SubTree------------");
        Node stringSubtree = HeaderAstGenerator.createStringSubtree();
        p2.prettyPrintAst(stringSubtree);

        System.out.println("-----------Class SubTree------------");
        Node classSubtree = HeaderAstGenerator.createClassSubtree();
        p2.prettyPrintAst(classSubtree);
        */



    }


}
