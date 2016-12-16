package edu.nyu.oop;

import java.util.ArrayList;

/**
 * This class stores data for a single method defined by a class in phase 2.
 * Created by willk on 10/26/16.
 */
public class MethodInfo {
    private String name;
    private String returnType;
    private String inheritedFrom;
    private ArrayList<String> modifiers;
    private ArrayList<String> parameters;

    MethodInfo(String methodName, String ret, ArrayList<String> mods, ArrayList<String> params){
        this.name = methodName;
        this.returnType = ret;
        this.modifiers = mods;
        this.parameters = params;
    }

    public String getName(){
        return name;
    }
    public String getReturnType(){ return returnType; }
    public ArrayList<String> getModifiers(){
        return modifiers;
    }
    public ArrayList<String> getParameters(){
        return parameters;
    }

    //Sets inherited from to the current class.
    public void setInheritedFrom(String input){ this.inheritedFrom = input; }

    //Returns multiline string with all data on its own line.
    public String toString(){
        String output = "";
        output += this.name + "\n";
        output += this.returnType + "\n";
        output += this.modifiers + "\n";
        for (int i=0;i<parameters.size();i++){
            output += parameters.get(i) + "\n";
        }
        output += inheritedFrom;
        return output;
    }

    //Prints toString.
    public void printMethod(){
        String output = this.toString();
        System.out.print(output);
    }

}
