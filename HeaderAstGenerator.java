package edu.nyu.oop;

import edu.nyu.oop.util.NodeUtil;
import xtc.tree.GNode;
import xtc.tree.Node;

import java.io.*;

import java.util.ArrayList;

//This class creates ASTs for the data layouts and VTables of each header file using static methods.
public class HeaderAstGenerator {



    //Methods used to create subtree for the Object Class, String Class and Class Class
    public static Node createRootNode(String nameOfPackage, String nameOfClass){//This is the root node of the tree
        GNode rootNode = GNode.create(nameOfClass);
        rootNode.addNode(createHeadDecNode(nameOfPackage, nameOfClass ));
        return rootNode;
    }

    public static Node createHeadDecNode(String nameOfPackage, String nameOfClass){
        //Create the root node named "HeaderDeclaration"
        //First Node is the name or package or null
        //Second Node is the name of the Class
        //Third Node is the DataLayout Node
        //Fourth Node is the VTable Node
        //Fifth Node is the extension
        GNode headDecNode = GNode.create("HeaderDeclaration",nameOfPackage,nameOfClass, createDataLayout(), createEmptyNode_vTable(), createExtension());
        return headDecNode;
    }

    public static Node createExtension(){
        GNode extension = GNode.create("Extends");
        extension.addNode(null);
        extension.set(0,"null");
        return extension;
    }

    public static Node createDataLayout(){
        GNode dataLayout = GNode.create("DataLayout");
        dataLayout.addNode(null);//without this line, we can't add Nodes later on.
        return dataLayout;
    }

    public static Node createEmptyNode_FieldDeclaration(String modifiers_str, String vtPointer, String pointerVariable, String declarator_str){
        //DataLayout's child
        GNode modifiers_node = GNode.create("Modifiers");
        if(!modifiers_str.equals("null")){// I want to use null, but I get an NullPointerException
            modifiers_node.add(modifiers_str);
        }
        GNode declarator_node = GNode.create("Declarators");
        if(!declarator_str.equals("null")){
            declarator_node.add(declarator_str);
        }
        GNode fieldDecNode = GNode.create("FieldDeclaration",modifiers_node,vtPointer, pointerVariable, declarator_node);

        return fieldDecNode;
    }

    public static Node createEmptyNode_consrutuctorDeclaration(String nameOfClass, String[] constructorParameters){
        //DataLayout's child
        GNode parameters = GNode.create("Parameters");
        if(constructorParameters.length!=0){
            for (Object p : constructorParameters){
                parameters.add(p);
            }
        }
        GNode constructorDeclaration = GNode.create("ConstructorDeclaration", nameOfClass, parameters);

        return constructorDeclaration;
    }

    public static Node createEmptyNode_dataLayoutMethodDeclaration(String[] modifiers_str, String returnType, String methodName, String classThatItIsFrom, String[] methodParameters){
        //DataLayout's child
        GNode modifiers = GNode.create("Modifiers");
        if(modifiers_str.length!=0){
            for(Object s: modifiers_str) {
                modifiers.add(s);
            }
        }
        GNode parameters = GNode.create("Parameters");
        if(methodParameters.length!=0){
            for (Object p : methodParameters){
                parameters.add(p);
            }
        }
        GNode methodDeclaration = GNode.create("DataLayoutMethodDeclaration", modifiers, returnType, methodName, classThatItIsFrom,parameters );
        return methodDeclaration;
    }

    public static Node createEmptyNode_dataLayoutMethodDeclaration(ArrayList<String> modifiers_str, String returnType, String methodName, String classThatItIsFrom, ArrayList<String> methodParameters){
        //DataLayout's child
        GNode modifiers = GNode.create("Modifiers");
        if(modifiers_str.size()!=0){
            for(Object s: modifiers_str) {
                modifiers.add(s);
            }
        }
        GNode parameters = GNode.create("Parameters");
        if(methodParameters.size()!=0){
            for (Object p : methodParameters){
                parameters.add(p);
            }
        }
        GNode methodDeclaration = GNode.create("DataLayoutMethodDeclaration", modifiers, returnType, methodName, classThatItIsFrom,parameters );
        return methodDeclaration;
    }

    public static Node createEmptyNode_vTable(){
        GNode vTable = GNode.create("VTable");
        vTable.addNode(null);
        return vTable;
    }

    public static Node createEmptyNode_vTableMethodDeclaration(String modifiers_str, String returnType, String methodName, String classThatItIsFrom, String[] methodParameters) {
        //vTable's child
        GNode modifiers = GNode.create("Modifiers");
        if(modifiers_str!="null"){
            modifiers.add(modifiers_str);
        }
        GNode parameters = GNode.create("Parameters");
        if(methodParameters.length!=0){
            for (Object p : methodParameters){
                parameters.add(p);
            }
        }

        GNode vTableMethodDeclaration = GNode.create("vTableMethodDeclaration", modifiers, returnType, methodName, classThatItIsFrom, parameters );
        return vTableMethodDeclaration;
    }

    public static Node createEmptyNode_vTableMethodDeclaration(String modifiers_str, String returnType, String methodName, String classThatItIsFrom, ArrayList<String> methodParameters) {
        //vTable's child
        GNode modifiers = GNode.create("Modifiers");
        if(modifiers_str!="null"){
            modifiers.add(modifiers_str);
        }
        GNode parameters = GNode.create("Parameters");
        if(methodParameters.size()!=0){
            for (Object p : methodParameters){
                parameters.add(p);
            }
        }

        GNode vTableMethodDeclaration = GNode.create("vTableMethodDeclaration", modifiers, returnType, methodName, classThatItIsFrom, parameters );
        return vTableMethodDeclaration;
    }

    //EmptyTree
    public static Node emptyTree() {
        GNode emptyTree= (GNode) createRootNode("null","null");

        //Find DataLayout Node and add subtrees
        /*
        String[] emptyArray_noParameters = new String[0];
        GNode dataLayoutNode = (GNode) NodeUtil.dfs(emptyTree, "DataLayout");
        //System.out.println(dataLayoutNode.getName());
        dataLayoutNode.addNode(createEmptyNode_FieldDeclaration("null", "null","null","null"));// I want to use null, but I get an NullPointerException
        dataLayoutNode.addNode(createEmptyNode_consrutuctorDeclaration("null", emptyArray_noParameters));
        dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(emptyArray_noParameters, "null", "null", "null", emptyArray_noParameters));

        //Find vTable Node and add subtrees
        GNode vTableNode = (GNode) NodeUtil.dfs(emptyTree, "VTable");
        //System.out.println(vTableNode.getName());
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "null", "null","null", emptyArray_noParameters));
        */
        return emptyTree;
    }

    //Determines all of the method inheritances
    public static void processClassInfos(ArrayList<ClassInfo> input){
        for (ClassInfo current: input){
            current.setMethods(findInheritedMethods(current,input));
        }
    }

    /*Input: Takes in a ClassInfo object and all classes within a certain AST
    Processing: Recursively searches through a class's superclass (and that class's superclass) to retrieve a list of
    methods that are inherited.
    Output: ArrayList<MethodInfo containing all methods inherited and whatnot. 
    */
    public static ArrayList<MethodInfo> findInheritedMethods(ClassInfo current, ArrayList<ClassInfo> classList){
        if (current.getSuperClass().equals("null")){
            return current.getMethods();
        }
        else{ //has a superClass
            String superClassName = current.getSuperClass();
            ClassInfo superClass=null;
            for (ClassInfo findingSuper: classList){
                if (findingSuper.getClassName().equals(superClassName)){ superClass = findingSuper; }
            }
            ArrayList<MethodInfo> superClassMethods = findInheritedMethods(superClass, classList);
            ArrayList<MethodInfo> currentClassMethods = current.getMethods();
            //Overwrite superClassMethods with any currentClassMethods available.
            for (MethodInfo currentClassMethod: currentClassMethods){
                String currentMethodName = currentClassMethod.getName();
                //TODO: Handle parameters and overloading?
                for (MethodInfo superClassMethod: superClassMethods){
                    if (currentMethodName.equals(superClassMethod.getName())){
                        superClassMethod = currentClassMethod; //Overwrite the method with the current class'
                    }
                }
            }
            return superClassMethods;
        }
    }

    public static Node createClassAst(ClassInfo input){
        String packageName, className, superClass;
        ArrayList<MethodInfo> methods, methodsVTable;
        packageName = input.getPackageName();
        className = input.getClassName();
        superClass = input.getSuperClass();
        methods = input.getMethods();
        methodsVTable = input.getMethods();

        GNode rootNode= (GNode) createRootNode(packageName,className);
        GNode dataLayoutNode = (GNode) NodeUtil.dfs(rootNode, "DataLayout");
        GNode VTableNode = (GNode) NodeUtil.dfs(rootNode,"VTable");
        GNode extension = (GNode) NodeUtil.dfs(rootNode,"Extends");

        //First FieldDeclaration Node
        dataLayoutNode.set(0, createEmptyNode_FieldDeclaration("null", "__" + className + "_VT*","__vptr","null"));
        dataLayoutNode.addNode(createEmptyNode_FieldDeclaration("static", "__" + className + "_VT","__vtable","null"));// I want to use null, but I get an NullPointerException

        //First DataLayoutMethod Node
        ArrayList<MethodInfo> defaultMethodsDataLayout = addDefaultMethod(className, false);
        for (int i=0;i<defaultMethodsDataLayout.size();i++){
            MethodInfo currentDefault = defaultMethodsDataLayout.get(i);
            //TODO: Handle superclass inheritance that is not Object.
            if (ifMethodOverride(currentDefault.getName(), methods)>=0){
                MethodInfo override = methods.get(ifMethodOverride(currentDefault.getName(), methods));
                methods.remove(ifMethodOverride(currentDefault.getName(), methods));
                dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(override.getModifiers(), override.getReturnType(), override.getName(), className, override.getParameters()));
            } else{
                dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(currentDefault.getModifiers(), currentDefault.getReturnType(), currentDefault.getName(), "Object", currentDefault.getParameters()));
            }
        }
        if (methods.size()>0){ //Adds any non-default methods.
            for (MethodInfo current : methods){
                dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(current.getModifiers(), current.getReturnType(), current.getName(), className, current.getParameters()));
            }
        }

        //Adding nodes to VTable
        ArrayList<MethodInfo> defaultMethodsVtable = addDefaultMethod(className, true);
        for (int i=0;i<defaultMethodsVtable.size();i++){
            MethodInfo currentDefault = defaultMethodsVtable.get(i);
            //This next if statement is a bandage to fix the fact that VTable's first node is set to null.
            if (i==0 && currentDefault.getName().equals("__isa")){
                VTableNode.set(0,createEmptyNode_vTableMethodDeclaration("null", currentDefault.getReturnType(), currentDefault.getName(), "Object", currentDefault.getParameters()));
                continue;
            }
            if (ifMethodOverride(currentDefault.getName(), methodsVTable)>=0) {
                MethodInfo override = methodsVTable.get(ifMethodOverride(currentDefault.getName(), methodsVTable));
                methodsVTable.remove(ifMethodOverride(currentDefault.getName(), methodsVTable));
                VTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", override.getReturnType(), override.getName(), className, override.getParameters()));
            } else{
                VTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", currentDefault.getReturnType(), currentDefault.getName(), "Object", currentDefault.getParameters()));
            }
        }
        if (methodsVTable.size()>0){
            for (MethodInfo current: methods){
                VTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", current.getReturnType(), current.getName(), className, current.getParameters()));
            }
        }

        extension.set(0,superClass);

        return rootNode;
    }

    //Returns index if a method is present in given list, else returns -1.
    private static int ifMethodOverride(String methodName, ArrayList<MethodInfo> list){
        for (int i=0;i<list.size();i++){
            MethodInfo current = list.get(i);
            if (current.getName().equals(methodName)){
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<MethodInfo> addDefaultMethod(String className, boolean VTableFlag){
        ArrayList<MethodInfo> defaultMethods = new ArrayList<MethodInfo>();
        ArrayList<String> staticMods = new ArrayList<>();
        if (VTableFlag){
            ArrayList<String> isAParam = new ArrayList<>();
            MethodInfo isA = new MethodInfo("__isa", "Class", staticMods, isAParam);
            defaultMethods.add(isA);
        } else{
            staticMods.add("static");
        }

        ArrayList<String> hashCodeParam = new ArrayList<>();
        hashCodeParam.add(className);
        MethodInfo hashCode = new MethodInfo("hashCode", "int32_t", staticMods, hashCodeParam);

        ArrayList<String> toStringParam = new ArrayList<>();
        toStringParam.add(className);
        MethodInfo toString = new MethodInfo("toString", "String", staticMods, toStringParam);

        ArrayList<String> equalsParam = new ArrayList<>();
        equalsParam.add(className);
        equalsParam.add("Object");
        MethodInfo equals = new MethodInfo("equals", "bool", staticMods , equalsParam);

        ArrayList<String> getClassParam = new ArrayList<>();
        getClassParam.add(className);
        MethodInfo getClass = new MethodInfo("getClass", "Class", staticMods, getClassParam);

        ArrayList<String> __classParam = new ArrayList<>();
        __classParam.add(className);
        MethodInfo __class = new MethodInfo("__class", "Class", staticMods, __classParam);


        defaultMethods.add(hashCode);
        defaultMethods.add(equals);
        defaultMethods.add(getClass);
        defaultMethods.add(toString);
        defaultMethods.add(__class);

        return  defaultMethods;

    }

    //Object Class and the String Class are very similar
    public static Node createTemplateTreeForObjectAndStringSubtree(String className) {
        //Kind of like the "Empty Tree" for Object Class and String class
        GNode subtree= (GNode) createRootNode("null",className);

        //Find DataLayout Node and add subtrees
        GNode dataLayoutNode = (GNode) NodeUtil.dfs(subtree, "DataLayout");
        //System.out.println(dataLayoutNode.getName());
        //Add Field Declaration Nodes
        dataLayoutNode.addNode(createEmptyNode_FieldDeclaration("null", "__"+className+"_VT*","__vptr","null"));// I want to use null, but I get an NullPointerException
        dataLayoutNode.addNode(createEmptyNode_FieldDeclaration("static", "__"+className+"_VT","vtable", "null"));
        //Add Constructor Declaration Nodes
        String[] emptyArray_noParameters = new String[0];
        dataLayoutNode.addNode(createEmptyNode_consrutuctorDeclaration(className, emptyArray_noParameters));
        //Add Method Declaration Nodes
        String[] methodParameters1 = {"__"+className};
        String[] methodModifiersArray={"static"};
        dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(methodModifiersArray, "String", "toString", className, methodParameters1));
        String[] methodParameters2 = {"__"+className};
        dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(methodModifiersArray, "int32_t", "hashCode", className, methodParameters1));
        String[] methodParameters3 = {"__"+className};
        dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(methodModifiersArray, "Class", "getClass",className, methodParameters1));
        String[] methodParameters4 = {className, className};
        dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(methodModifiersArray, "bool", "equals", className, methodParameters1));
        String[] methodParameters5 = new String[0];
        dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(methodModifiersArray, "Class", "__class", className, methodParameters1));

        //Find vTable Node and add subtrees
        GNode vTableNode = (GNode) NodeUtil.dfs(subtree, "VTable");
        //System.out.println(vTableNode.getName());
        //Add vTableMethodDeclaration Nodes
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "Class", "__isa","Object", emptyArray_noParameters));
        String[] vTableParameters2 = {"__"+className};
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "String", "toString","Object", vTableParameters2));
        String[] vTableParameters3 = {"__"+className};
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "int32_t", "hashCode","Object", vTableParameters3));
        String[] vTableParameters4 = {"__"+className};
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "Class", "getClass","Object", vTableParameters4));
        String[] vTableParameters5 = {className, className};
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "bool", "equals","Object", vTableParameters5));

        return subtree;
    }
    public static GNode createObjectSubtree(){
        String className = "Object";
        GNode object_subtree= (GNode) createTemplateTreeForObjectAndStringSubtree(className);
        object_subtree.addNode(createStringSubtree());
        object_subtree.addNode(createClassSubtree());
        return object_subtree;
    }

    public static GNode createStringSubtree(){
        String className = "String";
        GNode string_subtree= (GNode) createTemplateTreeForObjectAndStringSubtree(className);
        //Modify Parts
        //String has two more DataLayoutMethodDeclaration nodes as children of the DataLayout Node
        //Find DataLayout Node and add subtrees
        GNode dataLayoutNode = (GNode) NodeUtil.dfs(string_subtree, "DataLayout");
        String[] methodParameters6 = {"__"+className};
        String[] methodModifiersArray={"static"};
        dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(methodModifiersArray, "int32_t", "length", className, methodParameters6));
        String[] methodParameters7 = {"__"+className, "int32_t"};
        dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(methodModifiersArray, "int32_t", "charAt", className, methodParameters6));
        //String has two more vTableMethodDeclaration nodes as children of the vTable Node (which corresponds to the two added above in this class
        //Find VTable Node and add subtrees
        GNode vTableNode = (GNode) NodeUtil.dfs(string_subtree, "VTable");
        String[] vTableParameters6 = {"__"+className};
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "int32_t", "length",className, vTableParameters6));
        String[] vTableParameters7 = {"__"+className};
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "int32_t", "charAt",className, vTableParameters6));

        return string_subtree;
    }

    public static GNode createClassSubtree() {
        String className = "Class";
        GNode subtree= (GNode) createRootNode("null",className);

        //Find DataLayout Node and add subtrees
        GNode dataLayoutNode = (GNode) NodeUtil.dfs(subtree, "DataLayout");
        //System.out.println(dataLayoutNode.getName());
        //Add Field Declaration Nodes
        dataLayoutNode.addNode(createEmptyNode_FieldDeclaration("null", "__"+className+"_VT*","__vptr","null"));// I want to use null, but I get an NullPointerException
        dataLayoutNode.addNode(createEmptyNode_FieldDeclaration("null", "String","name","null"));
        dataLayoutNode.addNode(createEmptyNode_FieldDeclaration("null", "Class","parent","null"));
        dataLayoutNode.addNode(createEmptyNode_FieldDeclaration("static", "__"+className+"_VT","vtable", "null"));
        //Add Constructor Declaration Nodes
        String[] constructorParameters = {"name","parent"};
        dataLayoutNode.addNode(createEmptyNode_consrutuctorDeclaration(className, constructorParameters));
        //Add DataLayoutMethod Nodes
        String[] emptyArray_noParameters = new String[0];
        String[] methodModifiersArray1={"static"};
        String[] methodParameters1 = {"__Object"};
        dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(methodModifiersArray1, "String", "toString", className, methodParameters1));
        String[] methodParameters2 = {};
        dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(methodModifiersArray1, "String", "getName", className, methodParameters2));
        String[] methodParameters3 = {};
        dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(methodModifiersArray1, "Class", "getSuperclass", className, methodParameters3));
        String[] methodModifiersArray2={"class", "Object"};
        String[] methodParameters4 = {};
        dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(methodModifiersArray2, "bool", "isInstance", className, methodParameters4));
        String[] methodParameters5 = {};
        dataLayoutNode.addNode(createEmptyNode_dataLayoutMethodDeclaration(methodModifiersArray1, "Class", "__class", className, methodParameters5));


        //Find vTable Node and add subtrees
        GNode vTableNode = (GNode) NodeUtil.dfs(subtree, "VTable");
        //System.out.println(vTableNode.getName());
        //Add vTableMethodDeclaration Nodes
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "Class", "__isa","Object", emptyArray_noParameters));
        String[] vTableMethodParameters1 = {"__Object"};
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "String", "toString","Object", vTableMethodParameters1));
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "int32_t", "hashCode","Object", methodParameters1));
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "Class", "getClass","Object", methodParameters1));
        String[] vTableMethodParameters4 = {"Object","Object"};
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "bool", "equals","Object", vTableMethodParameters4));
        String[] vTableMethodParameters5 = {"__Class"};
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "String", "getName","Class", vTableMethodParameters5));
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "Class", "getSuperclass","Class", vTableMethodParameters5));
        String[] vTableMethodParameters7 = {"__Class","__Object"};
        vTableNode.addNode(createEmptyNode_vTableMethodDeclaration("null", "bool", "isInstance","Class", vTableMethodParameters7));

        return subtree;
    }
}
