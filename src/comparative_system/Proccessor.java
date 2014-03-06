/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system;

import comparative_system.model.Code;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * 
 * @author TireX
 */
public class Proccessor {
    
    static class Map {
        private static class MapNode {
            private int codeStart;
            private int codeLength;
            private String type;
            private int countOfOperations;
            public MapNode(int codeStart, int codeLength, String type, int countOfOperations) {
                this.codeStart = codeStart;
                this.codeLength = codeLength;
                this.type = type;
                this.countOfOperations = countOfOperations;
            }
        }
        
        ArrayList<MapNode> list; 
        
        public Map() {
            list = new ArrayList<>();
        }
        
        private void addParsedExpression(int startPosition, int length, String expression, int countOperations) {
            if (countOperations > 0) {
                System.out.println(startPosition + " - " + length + " - " + expression + " - " + countOperations);
                this.list.add(new MapNode(startPosition, length, expression, countOperations));
            }
        }
    }
    
    
    private static final ASTParser parser = ASTParser.newParser(AST.JLS4);
    
    public static ArrayList<MethodDeclaration> getAllMethodsFromCodes(ArrayList<String> codes) {
        final ArrayList<MethodDeclaration> methods = new ArrayList<>();
        
        for(String code : codes) {
            parser.setSource(code.toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);

            final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

            cu.accept(new ASTVisitor() {
                    @Override
                    public boolean visit(MethodDeclaration node) {
                            if (!node.isConstructor()) {
                                methods.add(node);
                            }
                            return true;
                    }
            });
        }
        return methods;
    }

    /**
     * Метод для расстановки счетчиков операций в алгоритма. 
     * @param codes Исходные коды алгоритма.
     * @return Коды с расставленными счетчиками.
     */
    public static ArrayList<Code> putCounters(ArrayList<String> codes) {
        ArrayList<Code> gen_codes = new ArrayList<>();
        for (String code : codes) {
            gen_codes.add(new Code(code, getGeneratedCode(code)));
        }        
        return gen_codes;
    }   
    
    /**
     * Метод возвращает строку, содержашую код с вставленными счетчиками.
     * @param counterName Имя счетчика для вставки.
     * @param code Исходный код одного public-класса.
     * @return Код с вставленными счетчиками.
     */
    static String getGeneratedCode(String code) {
        fillNewMap(code);
        return putCountersInCodeFromMap(code);
    }

    private static Map map;
    public static void fillNewMap(String code) {
        map = new Map();
        
        parser.setSource(code.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        for (Object e : cu.types()) {        
            if (e instanceof TypeDeclaration) {//если класс
                for(MethodDeclaration m : ((TypeDeclaration)e).getMethods()) {                
                    for (Object s : m.getBody().statements()) {                    
                        countOperationsInStatement((Statement)s);
                    }
                }
            }
        }
        
//        cu.accept(new ASTVisitor() {
//            public boolean visit(BodyDeclaration node) {// ходим по всем действиям
//                System.out.println();
//                //map.addParsedExpression(node.getStartPosition(), node.getLength(), "Expression", countOperations(node));
//                System.out.println();
//                System.out.println();
//                return true;
//            }
//        });
    }

    static String putCountersInCodeFromMap(String code) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    static int countOperationsInExpression(Expression ex) {
        int c = 0;
        if (ex == null ||
                ex instanceof BooleanLiteral || 
                ex instanceof CharacterLiteral || 
                ex instanceof NullLiteral || 
                ex instanceof NumberLiteral || 
                ex instanceof StringLiteral || 
                ex instanceof TypeLiteral ||
                ex instanceof Name ||
                ex instanceof SuperFieldAccess ||
                ex instanceof ThisExpression) {
            return 0;
        }
        
        if (ex instanceof MethodInvocation) {
            for (Object e : ((MethodInvocation)ex).arguments()) 
            {
                c += countOperationsInExpression((Expression)e);
            }
        }
        
        if (ex instanceof SuperMethodInvocation) {
            for (Object e : ((SuperMethodInvocation)ex).arguments()) 
            {
                c += countOperationsInExpression((Expression)e);
            }
        }
        
        if (ex instanceof VariableDeclarationExpression) {
            for (Object e : ((VariableDeclarationExpression)ex).fragments()) 
            {
                c += countOperationsInExpression(((VariableDeclarationFragment)e).getInitializer());
            }
        }
        
        if (ex instanceof Annotation) {
            if (ex instanceof NormalAnnotation) {
                for(Object m : ((NormalAnnotation)ex).values()) {
                    c += countOperationsInExpression(((MemberValuePair)m).getValue());
                }
            }
            if (ex instanceof SingleMemberAnnotation) {
                c += countOperationsInExpression(((SingleMemberAnnotation)ex).getValue());
            }
        }
        
        if (ex instanceof CastExpression) {
            c += countOperationsInExpression(((CastExpression)ex).getExpression());
        }
        
        if (ex instanceof ConditionalExpression) {
            c += countOperationsInExpression(((ConditionalExpression)ex).getExpression());
            c += countOperationsInExpression(((ConditionalExpression)ex).getThenExpression());
            c += countOperationsInExpression(((ConditionalExpression)ex).getElseExpression());
        }
        
        if (ex instanceof FieldAccess) {
            c += countOperationsInExpression(((FieldAccess)ex).getExpression());
        }
        
        if (ex instanceof InstanceofExpression) {
            c += countOperationsInExpression(((InstanceofExpression)ex).getLeftOperand());
        }
        
        if (ex instanceof ParenthesizedExpression) {
            c += countOperationsInExpression(((ParenthesizedExpression)ex).getExpression());
        }
        
        if (ex instanceof ArrayCreation) {
            c += countOperationsInExpression(((ArrayCreation)ex).getInitializer());
        }
        
        if (ex instanceof ArrayInitializer) {
            for (Object e : ((ArrayInitializer)ex).expressions()) 
            {
                c += countOperationsInExpression((Expression)e);
            }
        }
        
        if (ex instanceof ArrayAccess) {
            c += countOperationsInExpression(((ArrayAccess)ex).getArray());
            c += countOperationsInExpression(((ArrayAccess)ex).getIndex());
        }
        
        if (ex instanceof ClassInstanceCreation) {
            c += countOperationsInExpression(((ClassInstanceCreation)ex).getExpression());
            for (Object e : ((ClassInstanceCreation)ex).arguments()) 
            {
                c += countOperationsInExpression((Expression)e);
            }
            
            for (Object e : ((ClassInstanceCreation)ex).getAnonymousClassDeclaration().bodyDeclarations()) 
            {
                if (e instanceof MethodDeclaration)
                    countOperationsInStatement(((MethodDeclaration)e).getBody());
            }
            
            c += countOperationsInExpression(((ClassInstanceCreation)ex).getExpression());
        }
        
        if (ex instanceof PrefixExpression) {
            c += 1; // префиксные ++, --, +, -, ~ и !.
            c += countOperationsInExpression(((PrefixExpression)ex).getOperand());
        }
        
        if (ex instanceof PostfixExpression) {
            c += 1; // постфиксные ++ и --.
            c += countOperationsInExpression(((PostfixExpression)ex).getOperand());
        }
        
        if (ex instanceof InfixExpression) {
            c += 1;
            c += countOperationsInExpression(((InfixExpression)ex).getLeftOperand());
            c += countOperationsInExpression(((InfixExpression)ex).getRightOperand());
            if (((InfixExpression)ex).hasExtendedOperands()) System.out.println("\n\nДополнительные элементы!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        
        if (ex instanceof  Assignment) {
            c += 1;
            c += countOperationsInExpression(((Assignment)ex).getLeftHandSide());
            c += countOperationsInExpression(((Assignment)ex).getRightHandSide());
        }
              
        
        
        return c;
    }
    
    private static void countOperationsInStatement(Statement st) {
        
        if (st == null ||
                st instanceof EmptyStatement || 
                st instanceof ContinueStatement || 
                st instanceof BreakStatement ) {
            return;
        }
        
        if (st instanceof ExpressionStatement) {
            int c = countOperationsInExpression(((ExpressionStatement)st).getExpression());
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "ExpressionStatement", c);
        }
        
        if (st instanceof LabeledStatement) {
            countOperationsInStatement(((LabeledStatement)st).getBody());
        }
        
        if (st instanceof Block) {
            for (Object s : ((Block)st).statements()) {
                countOperationsInStatement((Statement)s);
            }
        }
        
        if (st instanceof ReturnStatement) {
            int c = countOperationsInExpression(((ReturnStatement)st).getExpression());
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "ReturnStatement", c);
        }
        
        if (st instanceof AssertStatement) {
            int c = countOperationsInExpression(((AssertStatement)st).getExpression());
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "AssertStatement", c);
        }
        
        if (st instanceof ConstructorInvocation) {
            int c = 0;
            for (Object e : ((ConstructorInvocation)st).arguments()) {
                c += countOperationsInExpression((Expression)e);
            }
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "ConstructorInvocation", c);
        }
        
        if (st instanceof SuperConstructorInvocation) {
            int c = 0;
            for (Object e : ((SuperConstructorInvocation)st).arguments()) {
                c += countOperationsInExpression((Expression)e);
            }
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "SuperConstructorInvocation", c);
        }
        
        if (st instanceof DoStatement) {
            countOperationsInStatement(((DoStatement)st).getBody());
            int c = countOperationsInExpression(((DoStatement)st).getExpression());  
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "DoStatement", c);          
        }
        
        if (st instanceof ForStatement) {
            int c = 0;
            for (Object e : ((ForStatement)st).initializers()) {
                c = countOperationsInExpression((Expression)e);
            }
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "ForStatement", c); 
            
            int c2 = countOperationsInExpression(((ForStatement)st).getExpression());
            
            for (Object e : ((ForStatement)st).updaters()) {
                c2 += countOperationsInExpression((Expression)e);
            }           
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "ForStatementIterators", c2);  
            
            countOperationsInStatement(((ForStatement)st).getBody());
        }
        
        if (st instanceof IfStatement) {
            int c = countOperationsInExpression(((IfStatement)st).getExpression());//условие
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "IfStatement", c);  
            
            countOperationsInStatement(((IfStatement)st).getThenStatement());
            if (((IfStatement)st).getElseStatement() != null) {
                countOperationsInStatement(((IfStatement)st).getElseStatement());
            }
        }
        
        if (st instanceof SwitchStatement) {
            int c = countOperationsInExpression(((SwitchStatement)st).getExpression());
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "SwitchStatement", c);  
            
            for (Object e : ((SwitchStatement)st).statements()) {
                countOperationsInStatement((Statement)e);//TODO: при чем тут SwitchCase класс?
            }  
        }
        
        if (st instanceof SynchronizedStatement) {
            int c = countOperationsInExpression(((SynchronizedStatement)st).getExpression());
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "SynchronizedStatement", c);  
            
            countOperationsInStatement(((SynchronizedStatement)st).getBody());
        }
        
        if (st instanceof ThrowStatement) {
            int c = countOperationsInExpression(((ThrowStatement)st).getExpression());
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "ThrowStatement", c);  
        }
        
        if (st instanceof TryStatement) {
            int c = 0;
            for (Object e : ((TryStatement)st).resources()) {
                c += countOperationsInExpression((Expression)e);
            }  
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "TryStatement", c);  
            
            countOperationsInStatement(((TryStatement)st).getBody());
        }
        
        if (st instanceof TypeDeclarationStatement) {
            for(MethodDeclaration m : ((TypeDeclaration)((TypeDeclarationStatement)st).getDeclaration()).getMethods()) {                
                for (Object s : m.getBody().statements()) {         
                    countOperationsInStatement((Statement)s);
                }
            }
        }
        
        if (st instanceof VariableDeclarationStatement) {
            int c = 0;
            for (Object e : ((VariableDeclarationStatement)st).fragments()) 
            {
                c += countOperationsInExpression(((VariableDeclarationFragment)e).getInitializer());
                c += 1;
            }
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "VariableDeclarationStatement", c);
        }
        
        if (st instanceof WhileStatement) {
            int c = countOperationsInExpression(((WhileStatement)st).getExpression());        
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "WhileStatement", c);
                        
            countOperationsInStatement(((WhileStatement)st).getBody());
        }
        
        if (st instanceof EnhancedForStatement) {
            int c = countOperationsInExpression(((EnhancedForStatement)st).getExpression()); 
            map.addParsedExpression(st.getStartPosition(), st.getLength(), "EnhancedForStatement", c + 4); // инкримент счетчика (2), сравнение счетика с размером контейнера (1), и присваивание значения из контейнера (1).
            
            countOperationsInStatement(((EnhancedForStatement)st).getBody());
        }
        
        
        return;
    }
}