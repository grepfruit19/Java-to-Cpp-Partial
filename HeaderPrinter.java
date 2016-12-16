package edu.nyu.oop;

import edu.nyu.oop.util.NodeUtil;
import xtc.tree.Node;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by willk on 10/31/16.
 */

//This class handles the actual printing of header files from headerASTs.
public class HeaderPrinter {

    //Assumes an input of head Nodes for each header AST.
    public static void generateHeaderFile (ArrayList<Node> inputNodes){

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("output/output.h"), "utf-8"))) {
            writer.write("#pragma once\n\n");
            writer.write("#include <stdint.h>\n#include <string>\n\n");
            writer.write("#include \"java_lang.h\"\n\n");

            String packageName = getPackageName(inputNodes.get(0));
            writer.write("namespace " + packageName + "{\n\n");

            printStructDecs(writer, inputNodes);//Forward Declarations.

            for (int i=0;i<inputNodes.size()-1;i++){
                //write datalayout Node
                Node test_dataLayoutNode = inputNodes.get(i).getNode(0).getNode(2);
                String className = getClassName(inputNodes.get(i));
                printDataLayout(writer, test_dataLayoutNode, className);
                //write VTable Node
                Node test_VTableNode = inputNodes.get(i).getNode(0).getNode(3);
                printVTable(writer,test_VTableNode, className);
                writer.write("\n}");
            }


        } catch (Exception e){
            System.err.println("Issue creating header file");
        }

    }

    private static void printDataLayout(Writer writer, Node datalayoutNode, String struct_className)throws IOException {
        //System.out.println(datalayoutNode.size());
        writer.write("\t\t//DataLayout\n");
        writer.write("\t\tstruct __"+ struct_className +"\n\t\t{\n");
        //Write constructor
        writer.write("\t\t\t//Constructor\n");
        writer.write("\t\t\t__"+struct_className+"();\n\n");
        //writer.write("\t\t\t");

        for(int i=0; i<datalayoutNode.size();i++){
            Node nodeInDataLayout = datalayoutNode.getNode(i);

            if (nodeInDataLayout.getName().equals("FieldDeclaration")){
                //handles modifiers
                boolean modExist = false;
                for (int mods=0;mods<nodeInDataLayout.getNode(0).size();mods++){
                    writer.write("\t\t\t"+nodeInDataLayout.getNode(0).getString(mods) + " ");
                    modExist=true;
                }
                if (modExist==false){
                    writer.write("\t\t\t");
                }
                writer.write(nodeInDataLayout.getString(1) + " ");
                writer.write(nodeInDataLayout.getString(2) + ";\n\n");
                //Declarators Node - parameters
                if (nodeInDataLayout.getNode(3).size()>0){
                    writer.write(" (");
                    for(int d=0; d<nodeInDataLayout.getNode(3).size();d++){
                        writer.write(nodeInDataLayout.getNode(3).getString(d)+ " ");
                        if(d!=nodeInDataLayout.getNode(3).size()-1){
                            writer.write(", ");
                        }
                    }
                    writer.write(" )");
                }
            }
            else if (nodeInDataLayout.getName().equals("DataLayoutMethodDeclaration")){
                //Checks if the method not a default method.
                if ((nodeInDataLayout.getString(3).equals(struct_className))) {
                    //handles modifiers
                    Node modifiers = nodeInDataLayout.getNode(0);
                    //System.out.println(modifiers.getName() + modifiers.size());
                    for (int mods = 0; mods < modifiers.size(); mods++) {
                        writer.write("\t\t\t" + modifiers.getString(mods) + " ");
                    }
                    writer.write(nodeInDataLayout.getString(1) + " "); //return type
                    writer.write(nodeInDataLayout.getString(2) + " "); //methodName
                    if (nodeInDataLayout.getNode(4).size() > 0) {//parameters exist
                        writer.write(" (");
                        for (int d = 0; d < nodeInDataLayout.getNode(4).size(); d++) {
                            writer.write(nodeInDataLayout.getNode(4).getString(d) + "");
                            if (d != nodeInDataLayout.getNode(4).size() - 1) {
                                writer.write(", ");
                            }
                        }
                        writer.write(" );\n\n");
                    } else {//no Parameters
                        writer.write("();\n\n");
                    }
                }
            }

        }
        writer.write("\t\t};\n\n");
    }

    private static void printVTable(Writer writer, Node vTableNode, String struct_className)throws IOException {
        writer.write("\t\t//VTable Layout\n");
        writer.write("\t\tstruct __"+ struct_className +" _VT\n\t\t{\n");
        //For each node in VTable, generate methods.
        for(int i=0; i<vTableNode.size();i++){
            //VTableMethodDeclaration
            Node vTableMethodDeclaration = vTableNode.getNode(i);

            //Modifiers Node
            if (vTableMethodDeclaration.getNode(0).size()>0){
                for(int m=0; m<vTableMethodDeclaration.getNode(0).size();m++){
                    writer.write("\t\t\t"+vTableMethodDeclaration.getNode(0).getString(m)+ " ");
                }
            }
            writer.write("\t\t\t"+ vTableMethodDeclaration.getString(1) + " "); //Return type
            writer.write(" (*"+vTableMethodDeclaration.getString(2) + ") ");//Method name

            //Parameters Node
            if(vTableMethodDeclaration.getNode(4).size()>0){
                writer.write("(");
                for(int m=0; m<vTableMethodDeclaration.getNode(4).size();m++){
                    writer.write(vTableMethodDeclaration.getNode(4).getString(m));
                    if(m!=vTableMethodDeclaration.getNode(4).size()-1){
                        writer.write(", ");
                    }
                }
                writer.write(");");
            }
            writer.write("\n");
        }
        //initializer list
        writer.write("\n\n\t\t\t__"+struct_className+"_VT()\n\t\t\t\t:");
        for(int i=0; i<vTableNode.size();i++) {
            Node nodeInVTableNode = vTableNode.getNode(i);
            writer.write("\n\t\t\t\t\t"+nodeInVTableNode.getString(2)+"(("+nodeInVTableNode.getString(1)+" (*) ");
            //parameters
            writer.write("(");
            if(nodeInVTableNode.getNode(4).size()>0){//if there are parameters
                //writer.write("param exists");
                for(int p=0; p<nodeInVTableNode.getNode(4).size();p++){
                    writer.write(nodeInVTableNode.getNode(4).getString(p));
                    if(p!=nodeInVTableNode.getNode(4).size()-1){
                        writer.write(", ");
                    }
                }
            }//Finish writing parameters
            //Handles inheritance.

            writer.write(")) &__"+ struct_className+"::"+ nodeInVTableNode.getString(2)+")");
        }
        writer.write("\n\t\t\t{\n\t\t\t}\n\t\t};");
    }

    private static void printStructDecs(Writer writer, ArrayList<Node> inputNodes) throws IOException {
        writer.write("\n\t\t//Forward Declarations.\n");
        for (Node current: inputNodes){
            String className = current.getNode(0).getString(1);
            if (className.contains("Test")){
                continue;
            }
            writer.write("\t\tstruct __" + className + ";\n");
            writer.write("\t\tstruct __" + className + "_VT;\n\n");
        }
        for (Node current: inputNodes){
            String className = current.getNode(0).getString(1);
            if (className.contains("Test")){
                continue;
            }
            writer.write("\t\ttypedef __" + className + " *" + className + ";\n");
        }
        writer.write("\n");
    }

    private static String getPackageName(Node head){
        String output = head.getNode(0).getString(0);
        return output;
    }

    private static String getClassName(Node head){
        String output = head.getNode(0).getString(1);
        return output;
    }
}
