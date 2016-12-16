# Java-to-Cpp-Partial

This repository is for part of a semester-long project for my Object Oriented Programming class. Unfortunately, my group had to disband halfway through the semester because most of my teammates dropped the course, but I decided to upload the work I did as code samples anyway. 

The project involved using Abstract Syntax Trees to translate Java to C++. The included code involved using the AST to convert Java classes into a C++ header. 

The flow of this program is that it takes the input AST (The AST generated from the input Java program) and it parses the AST into ClassInfo and MethodInfo objects. These objects are then used to construct the AST for the header file in HeaderAstGenerator. That AST is then used to generate the header file itself in HeaderPrinter.

For this project, I came up with the idea of using the ClassInfo and MethodInfo objects as an intermediate step between parsing the AST and constructing the header AST. This proved to be useful as it made debugging much easier, and the passing of information between parsing and header AST generating much easier, as using the AST could get fairly clunky at times. 

AstParser, MethodInfo and ClassInfo were written by me, HeaderPrinter was pair programmed with my teammate, HeaderAstGenerator was written by my teammate. Boot.java was a supplied class.
