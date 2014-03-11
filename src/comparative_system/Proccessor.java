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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
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
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
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
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

/**
 * 
 * @author TireX
 */
public class Proccessor { 
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

    private static ASTRewrite rewriter = null;
    private static CompilationUnit cu = null;
    private static Document codeDoc = null;
    public static void fillNewMap(String code) {
        try {
            codeDoc = new Document(code);
            parser.setSource(code.toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            cu = (CompilationUnit) parser.createAST(null);
            AST ast = cu.getAST();
            rewriter = ASTRewrite.create(ast);
            
            for (Object e : cu.types()) {
                if (e instanceof TypeDeclaration) {//если класс
                    for(MethodDeclaration m : ((TypeDeclaration)e).getMethods()) {
                        for (Object s : m.getBody().statements()) {
                            countOperationsInStatement((Statement)s);
                        }
                    }
                }
            }
            
            TextEdit edits = rewriter.rewriteAST(codeDoc, null);
            edits.apply(codeDoc);
            System.out.println("\n\n\n"+codeDoc.get()+"\n\n");
            
//        cu.accept(new ASTVisitor() {
//            public boolean visit(BodyDeclaration node) {// ходим по всем действиям
//                System.out.println();
//                //map.addParsedExpression(node.getStartPosition(), node.getLength(), "Expression", countOperations(node));
//                System.out.println();
//                System.out.println();
//                return true;
//            }
//        });
        } catch (MalformedTreeException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadLocationException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static String putCountersInCodeFromMap(String code) {
        String res = "";
        
        return res;   
    }
    
    
    private static void countOperationsInStatement(Statement st) {
        //if (astRewrite == null) return;
        if (st == null ||
                st instanceof EmptyStatement || 
                st instanceof ContinueStatement || 
                st instanceof BreakStatement ) {
            return;
        }
        
        if (st instanceof ExpressionStatement) {
            int c = countOperationsInExpression(((ExpressionStatement)st).getExpression());
            if (c>0) { insertCounter(st, c); }
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
            if (c>0) { insertCounter(st, c); }
        }
        
        if (st instanceof AssertStatement) {
            int c = countOperationsInExpression(((AssertStatement)st).getExpression());
            if (c>0) { insertCounter(st, c); }
        }
        
        if (st instanceof ConstructorInvocation) {
            int c = 0;
            for (Object e : ((ConstructorInvocation)st).arguments()) {
                c += countOperationsInExpression((Expression)e);
            }
            if (c>0) { insertCounter(st, c); }
        }
        
        if (st instanceof SuperConstructorInvocation) {
            int c = 0;
            for (Object e : ((SuperConstructorInvocation)st).arguments()) {
                c += countOperationsInExpression((Expression)e);
            }
            if (c>0) { insertCounter(st, c); }
        }
        
        if (st instanceof DoStatement) {
            countOperationsInStatement(((DoStatement)st).getBody());
            int c = countOperationsInExpression(((DoStatement)st).getExpression());  
            if (c>0) { insertCounter(st, c); }    
        }
        
        if (st instanceof ForStatement) {
            int c = 0;
            for (Object e : ((ForStatement)st).initializers()) {
                c = countOperationsInExpression((Expression)e);
            }
            if (c>0) { insertCounter(st, c); }                
            
            int c2 = countOperationsInExpression(((ForStatement)st).getExpression());
            
            for (Object e : ((ForStatement)st).updaters()) {
                c2 += countOperationsInExpression((Expression)e);
            }     
            if (c > 0) {
                insertCounter(((ForStatement)st).getBody(), c);
            }
            
            countOperationsInStatement(((ForStatement)st).getBody());
        }
        
        if (st instanceof IfStatement) {
            int c = countOperationsInExpression(((IfStatement)st).getExpression());//условие
            if (c>0) { insertCounter(st, c); }
            
            countOperationsInStatement(((IfStatement)st).getThenStatement());
            if (((IfStatement)st).getElseStatement() != null) {
                countOperationsInStatement(((IfStatement)st).getElseStatement());
            }
        }
        
        if (st instanceof SwitchStatement) {
            int c = countOperationsInExpression(((SwitchStatement)st).getExpression());
            if (c>0) { insertCounter(st, c); } 
            
            for (Object e : ((SwitchStatement)st).statements()) {
                countOperationsInStatement((Statement)e);//TODO: при чем тут SwitchCase класс?
            }  
        }
        
        if (st instanceof SynchronizedStatement) {
            int c = countOperationsInExpression(((SynchronizedStatement)st).getExpression());
            if (c>0) {
                if (c>0) { insertCounter(st, c); }
            }
            
            countOperationsInStatement(((SynchronizedStatement)st).getBody());
        }
        
        if (st instanceof ThrowStatement) {
            int c = countOperationsInExpression(((ThrowStatement)st).getExpression());
            if (c>0) { insertCounter(st, c); }
        }
        
        if (st instanceof TryStatement) {
            int c = 0;
            for (Object e : ((TryStatement)st).resources()) {
                c += countOperationsInExpression((Expression)e);
            }  
            if (c>0) { insertCounter(st, c); }
            
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
                if (((VariableDeclarationFragment)e).getInitializer() != null)
                {
                    c += countOperationsInExpression(((VariableDeclarationFragment)e).getInitializer());
                    c += 1;
                }
            }
            if (c>0) { insertCounter(st, c); }
        }
        
        if (st instanceof WhileStatement) {
            int c = countOperationsInExpression(((WhileStatement)st).getExpression());        
            if (c>0) { insertCounter(st, c); }
                        
            countOperationsInStatement(((WhileStatement)st).getBody());
        }
        
        if (st instanceof EnhancedForStatement) {
            int c = countOperationsInExpression(((EnhancedForStatement)st).getExpression()); 
            if (c>0) { insertCounter(st, c + 4); } // инкримент счетчика (2), сравнение счетика с размером контейнера (1), и присваивание значения из контейнера (1).
                           
            countOperationsInStatement(((EnhancedForStatement)st).getBody());
        }
    }

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
        
        if (ex instanceof Annotation) {
            if (ex instanceof NormalAnnotation)       { return proccessNormalAnnotationExpression((NormalAnnotation)ex); }
            if (ex instanceof SingleMemberAnnotation) { return proccessSingleMemberAnnotationExpression((SingleMemberAnnotation)ex); }
        }
        
        if (ex instanceof MethodInvocation)              { return proccessMethodInvocationExpression((MethodInvocation)ex); }
        if (ex instanceof SuperMethodInvocation)         { return proccessSuperMethodInvocationExpression((SuperMethodInvocation)ex); }
        if (ex instanceof VariableDeclarationExpression) { return proccessVariableDeclarationExpression((VariableDeclarationExpression)ex); }
        if (ex instanceof CastExpression)                { return proccessCastExpression((CastExpression)ex); }
        if (ex instanceof ConditionalExpression)         { return proccessConditionalExpression((ConditionalExpression)ex); }        
        if (ex instanceof FieldAccess)                   { return proccessFieldAccessExpression((FieldAccess)ex); }              
        if (ex instanceof InstanceofExpression)          { return proccessInstanceofExpression((InstanceofExpression)ex); }           
        if (ex instanceof ParenthesizedExpression)       { return proccessParenthesizedExpression((ParenthesizedExpression)ex); }            
        if (ex instanceof ArrayCreation)                 { return proccessArrayCreationExpression((ArrayCreation)ex); }              
        if (ex instanceof ArrayInitializer)              { return proccessArrayInitializerExpression((ArrayInitializer)ex); }             
        if (ex instanceof ArrayAccess)                   { return proccessArrayAccessExpression((ArrayAccess)ex); }      
        if (ex instanceof ClassInstanceCreation)         { return proccessClassInstanceCreationExpression((ClassInstanceCreation)ex); }             
        if (ex instanceof PrefixExpression)              { return proccessPrefixExpression((PrefixExpression)ex); }             
        if (ex instanceof PostfixExpression)             { return proccessPostfixExpression((PostfixExpression)ex); }           
        if (ex instanceof InfixExpression)               { return proccessInfixExpression((InfixExpression)ex); }          
        if (ex instanceof Assignment)                    { return proccessAssignmentExpression((Assignment)ex); }
             
        return 0;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Методы разбора типов-потомков Expression.">
    private static int proccessAssignmentExpression(Assignment ex) {
        int c = 0;
        int c2 = countOperationsInExpression(((Assignment)ex).getLeftHandSide());
        if (ex.getOperator().equals(Assignment.Operator.ASSIGN)) {// из-за result[i]+=1 -> result[i]=result[i] + 1, а не result[i]=result+1;
            c = c2 + 1;
        } else {
            c = c2 * 2 + 2;
        }
        
        c += countOperationsInExpression(((Assignment)ex).getRightHandSide());
        return c;
    }
    private static int proccessInfixExpression(InfixExpression ex) {
        int c = 1;
        c += countOperationsInExpression(((InfixExpression)ex).getLeftOperand());
        c += countOperationsInExpression(((InfixExpression)ex).getRightOperand());
        return c;
    }
    private static int proccessPostfixExpression(PostfixExpression ex) {
        int c = 2; // постфиксные ++ и --.
        c += 2 * countOperationsInExpression(((PostfixExpression)ex).getOperand());
        return c;
    }
    private static int proccessPrefixExpression(PrefixExpression ex) {
        int c = 0, c2; // префиксные ++, --, +, -, ~ и !.
        if (ex.getOperator().equals(PrefixExpression.Operator.INCREMENT) || ex.getOperator().equals(PrefixExpression.Operator.DECREMENT)) {
            c2 = 2 + 2 * countOperationsInExpression(ex.getOperand());
        } else {
            c2 = 1 + countOperationsInExpression(ex.getOperand());
        }
        c += c2;
        return c;
    } 
    private static int proccessClassInstanceCreationExpression(ClassInstanceCreation ex) {
        int c = 0;
        
        c += countOperationsInExpression(ex.getExpression());
        for (Object e : ex.arguments()) 
        {
            c += countOperationsInExpression((Expression)e);
        }

        if (ex.getAnonymousClassDeclaration() != null) {
            for (Object e : ex.getAnonymousClassDeclaration().bodyDeclarations()) 
            {
                if (e instanceof MethodDeclaration)
                    countOperationsInStatement(((MethodDeclaration)e).getBody());
            }
        }

        c += countOperationsInExpression(ex.getExpression());
        
        return c;
    }
    private static int proccessArrayAccessExpression(ArrayAccess ex) {
        int c = 1; 
        c += countOperationsInExpression(ex.getArray());
        c += countOperationsInExpression(ex.getIndex());
        return c;
    }
    private static int proccessArrayInitializerExpression(ArrayInitializer ex) {
        int c = 0; 
        for (Object e : ex.expressions()) 
        {
            c += countOperationsInExpression((Expression)e);
        }
        return c;
    }
    private static int proccessArrayCreationExpression(ArrayCreation ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getInitializer());
        return c;
    } 
    private static int proccessParenthesizedExpression(ParenthesizedExpression ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getExpression());
        return c;
    } 
    private static int proccessInstanceofExpression(InstanceofExpression ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getLeftOperand());
        return c;
    }
    private static int proccessFieldAccessExpression(FieldAccess ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getExpression());
        return c;
    } 
    private static int proccessCastExpression(CastExpression ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getExpression());
        return c;
    }
    private static int proccessConditionalExpression(ConditionalExpression ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getExpression());
        c += countOperationsInExpression(ex.getThenExpression());
        c += countOperationsInExpression(ex.getElseExpression());
        return c;
    }
    private static int proccessNormalAnnotationExpression(NormalAnnotation ex) {
        int c = 0; 
        for(Object m : ex.values()) {
            c += countOperationsInExpression(((MemberValuePair)m).getValue());
        }
        return c;
    }
    private static int proccessSingleMemberAnnotationExpression(SingleMemberAnnotation ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getValue());
        return c;
    }
    private static int proccessVariableDeclarationExpression(VariableDeclarationExpression ex) {
        int c = 0; 
        for (Object e : ex.fragments()) 
        {
            if (((VariableDeclarationFragment)e).getInitializer() != null) {
                c += countOperationsInExpression(((VariableDeclarationFragment)e).getInitializer());
                c += 1;
            }
        }
        return c;
    }
    private static int proccessSuperMethodInvocationExpression(SuperMethodInvocation ex) {
        int c = 0; 
        for (Object e : ex.arguments()) 
        {
            c += countOperationsInExpression((Expression)e);
        }
        return c;
    }
    private static int proccessMethodInvocationExpression(MethodInvocation ex) {
        int c = 0; 
        for (Object e : ex.arguments()) 
        {
            c += countOperationsInExpression((Expression)e);
        }
        return c;
    }
    //</editor-fold>

    private static void insertCounter(Statement st, int c) {
        try {
            AST ast = st.getAST();
            MethodInvocation counterMethod = ast.newMethodInvocation();
            counterMethod.setExpression(ast.newSimpleName("Counter"));
            counterMethod.setName(ast.newSimpleName("add"));
            ExpressionStatement countExpr = ast.newExpressionStatement(counterMethod);
            ((MethodInvocation)countExpr.getExpression()).arguments().clear();
            ((MethodInvocation)countExpr.getExpression()).arguments().add(st.getAST().newNumberLiteral(""+c));
            
            if (st.getParent() instanceof Block) {
                ListRewrite lrw = rewriter.getListRewrite(st.getParent(), Block.STATEMENTS_PROPERTY);
                lrw.insertBefore(countExpr, st, null);
            } else {
                Block block = ast.newBlock();
                block.statements().add(countExpr);
                block.statements().add(rewriter.createCopyTarget(st));
                rewriter.replace(st, block, null);
            }            
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedTreeException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}