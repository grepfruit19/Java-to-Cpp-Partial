package edu.nyu.oop;

import java.util.ArrayList;

/**
 * This class handles data storage in AstParser for classes.
 * Created by willk on 10/26/16.
 */
public class ClassInfo {
    private String packageName;
    private String className;
    private String superClass; //String "null" if no superClass.
    private ArrayList<MethodInfo> listOfMethods = new ArrayList<MethodInfo>();

    ClassInfo(String packageInput, String classInput, String superInput){
        this.packageName = packageInput;
        this.className = classInput;
        this.superClass = superInput;
    }

    public void addMethod(MethodInfo input){
        listOfMethods.add(input);
    }

    public String getPackageName() {return this.packageName; }

    public String getClassName() { return this.className; }

    public String getSuperClass() { return this.superClass; }

    public ArrayList<MethodInfo> getMethods(){
        return this.listOfMethods;
    }

    public void setMethods(ArrayList<MethodInfo> input) { this.listOfMethods = input; }

    //Sets "inherited from" for every method in a class.
    //When does this need to be called? Probably right after class creation.
    public void setMethodInheritances(){
        for (MethodInfo method: this.listOfMethods){
            method.setInheritedFrom(this.className);
        }
        System.out.println(listOfMethods);
    }

    public MethodInfo findMethod(String methodName){
        MethodInfo current=null;
        for (int i=0;i<this.listOfMethods.size();i++){
            current = listOfMethods.get(i);
            if (current.getName()==methodName){
                break;
            }
        }
        return current;
    }

    public String toString(){
        String output = "";
        output += this.packageName + "\n";
        output += this.className + "\n";
        output += this.superClass + "\n";
        for (int i=0;i<listOfMethods.size();i++){
            output += listOfMethods.get(i).getName() + "\n";
        }
        return output;
    }

    public void printClass(){
        System.out.println(this.toString());
    }
}
