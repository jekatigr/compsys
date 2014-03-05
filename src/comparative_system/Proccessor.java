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
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
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
            this.list.add(new MapNode(startPosition, length, expression, countOperations));
        }
    }
    
    
    private static final ASTParser parser = ASTParser.newParser(AST.JLS3);
    
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
     * Метод для определения будущего имени счетчика операций.
     * @param codes Исходные коды алгоритма.
     * @return Будущее имя счетчика.
     */
    public static String getCounterName(ArrayList<String> codes) {//TODO: реализовать метод getCounterName
        return "tempNameOfCounter";
    }

    /**
     * Метод для расстановки счетчиков операций в алгоритма. 
     * @param counterName Имя счетчика.
     * @param codes Исходные коды алгоритма.
     * @return Коды с расставленными счетчиками.
     */
    public static ArrayList<Code> putCounters(String counterName, ArrayList<String> codes) {
        ArrayList<Code> gen_codes = new ArrayList<>();
        for (String code : codes) {
            gen_codes.add(new Code(code, getGeneratedCode(counterName, code)));
        }        
        return gen_codes;
    }   
    
    /**
     * Метод возвращает строку, содержашую код с вставленными счетчиками.
     * @param counterName Имя счетчика для вставки.
     * @param code Исходный код одного public-класса.
     * @return Код с вставленными счетчиками.
     */
    static String getGeneratedCode(String counterName, String code) {
        Map map = buildNewMap(code);
        return putCountersInCodeFromMap(map, counterName, code);
    }

    static Map buildNewMap(String code) {
        final Map map = new Map();
        
        parser.setSource(code.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        for (Object e : cu.types()) {        
            if (e instanceof TypeDeclaration) {//если класс
                for(MethodDeclaration m : ((TypeDeclaration)e).getMethods()) {                
                    for (Object s : m.getBody().statements()) {                    
                        System.out.println(s.toString()+"\n\n");
                        countOperationsInStatement((Statement)s);
                    }
                }
            }
        }
        
//        cu.accept(new ASTVisitor() {
//            public boolean visit(BodyDeclaration node) {// ходим по всем действиям
//                System.out.println();
//                map.addParsedExpression(node.getStartPosition(), node.getLength(), "Expression", countOperations(node));
//                System.out.println();
//                System.out.println();
//                return true;
//            }
//        });
        return map;
    }

    static String putCountersInCodeFromMap(Map map, String counterName, String code) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static int count = 0;
    static int countOperationsInExpression(Expression ex) {
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
                countOperationsInExpression((Expression)e);
            }
        }
        
        if (ex instanceof SuperMethodInvocation) {
            for (Object e : ((SuperMethodInvocation)ex).arguments()) 
            {
                countOperationsInExpression((Expression)e);
            }
        }
        
        if (ex instanceof VariableDeclarationExpression) {
            for (Object e : ((VariableDeclarationExpression)ex).fragments()) 
            {
                countOperationsInExpression(((VariableDeclarationFragment)e).getInitializer());
            }
        }
        
        if (ex instanceof Annotation) {
            if (ex instanceof NormalAnnotation) {
                for(Object m : ((NormalAnnotation)ex).values()) {
                    countOperationsInExpression(((MemberValuePair)m).getValue());
                }
            }
            if (ex instanceof SingleMemberAnnotation) {
                countOperationsInExpression(((SingleMemberAnnotation)ex).getValue());
            }
        }
        
        if (ex instanceof CastExpression) {
            countOperationsInExpression(((CastExpression)ex).getExpression());
        }
        
        if (ex instanceof ConditionalExpression) {
            countOperationsInExpression(((ConditionalExpression)ex).getExpression());
            countOperationsInExpression(((ConditionalExpression)ex).getThenExpression());
            countOperationsInExpression(((ConditionalExpression)ex).getElseExpression());
        }
        
        if (ex instanceof FieldAccess) {
            countOperationsInExpression(((FieldAccess)ex).getExpression());
        }
        
        if (ex instanceof InstanceofExpression) {
            countOperationsInExpression(((InstanceofExpression)ex).getLeftOperand());
        }
        
        if (ex instanceof ParenthesizedExpression) {
            countOperationsInExpression(((ParenthesizedExpression)ex).getExpression());
        }
        
        if (ex instanceof ArrayCreation) {
            countOperationsInExpression(((ArrayCreation)ex).getInitializer());
        }
        
        if (ex instanceof ArrayInitializer) {
            for (Object e : ((ArrayInitializer)ex).expressions()) 
            {
                countOperationsInExpression((Expression)e);
            }
        }
        
        if (ex instanceof ClassInstanceCreation) {
            countOperationsInExpression(((ClassInstanceCreation)ex).getExpression());
            for (Object e : ((ClassInstanceCreation)ex).arguments()) 
            {
                countOperationsInExpression((Expression)e);
            }
            
            for (Object e : ((ClassInstanceCreation)ex).getAnonymousClassDeclaration().bodyDeclarations()) 
            {
                if (e instanceof MethodDeclaration)
                    countOperationsInStatement(((MethodDeclaration)e).getBody());
            }
            
            countOperationsInExpression(((ClassInstanceCreation)ex).getExpression());
        }
        
        
        return 0;
    }
    
    private static int countOperationsInStatement(Statement st) {
        
        if (st == null ||
                st instanceof EmptyStatement || 
                st instanceof ContinueStatement || 
                st instanceof BreakStatement ) {
            return 0;
        }
        
        if (st instanceof ExpressionStatement) {
            countOperationsInExpression(((ExpressionStatement)st).getExpression());
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
            countOperationsInExpression(((ReturnStatement)st).getExpression());
        }
        
        if (st instanceof AssertStatement) {
            countOperationsInExpression(((AssertStatement)st).getExpression());
        }
        
        if (st instanceof ConstructorInvocation) {
            for (Object e : ((ConstructorInvocation)st).arguments()) {
                countOperationsInExpression((Expression)e);
            }
        }
        
        if (st instanceof SuperConstructorInvocation) {
            for (Object e : ((SuperConstructorInvocation)st).arguments()) {
                countOperationsInExpression((Expression)e);
            }
        }
        
        if (st instanceof DoStatement) {
            countOperationsInStatement(((DoStatement)st).getBody());
            countOperationsInExpression(((DoStatement)st).getExpression());            
        }
        
        if (st instanceof ForStatement) {
            for (Object e : ((ForStatement)st).initializers()) {
                countOperationsInExpression((Expression)e);
            }
            
            countOperationsInExpression(((ForStatement)st).getExpression());
            
            for (Object e : ((ForStatement)st).updaters()) {
                countOperationsInExpression((Expression)e);
            }           
        }
        
        if (st instanceof IfStatement) {
            countOperationsInExpression(((IfStatement)st).getExpression());//условие
            countOperationsInStatement(((IfStatement)st).getThenStatement());
            if (((IfStatement)st).getElseStatement() != null) {
                countOperationsInStatement(((IfStatement)st).getElseStatement());
            }
        }
        
        if (st instanceof SwitchStatement) {
            countOperationsInExpression(((SwitchStatement)st).getExpression());
            for (Object e : ((SwitchStatement)st).statements()) {
                countOperationsInStatement((Statement)e);//TODO: при чем тут SwitchCase класс?
            }  
        }
        
        if (st instanceof SynchronizedStatement) {
            countOperationsInExpression(((SynchronizedStatement)st).getExpression());
            countOperationsInStatement(((SynchronizedStatement)st).getBody());
        }
        
        if (st instanceof ThrowStatement) {
            countOperationsInExpression(((ThrowStatement)st).getExpression());
        }
        
        if (st instanceof TryStatement) {
            for (Object e : ((TryStatement)st).resources()) {
                countOperationsInExpression((Expression)e);
            }  
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
            for (Object e : ((VariableDeclarationStatement)st).fragments()) 
            {
                countOperationsInExpression(((VariableDeclarationFragment)e).getInitializer());
            }
        }
        
        if (st instanceof WhileStatement) {
            countOperationsInExpression(((WhileStatement)st).getExpression());            
            countOperationsInStatement(((WhileStatement)st).getBody());
        }
        
        if (st instanceof EnhancedForStatement) {
            countOperationsInExpression(((EnhancedForStatement)st).getExpression());            
            countOperationsInStatement(((EnhancedForStatement)st).getBody());
        }
        
        
        return 0;
    }
}