package comparative_system;

import comparative_system.model.Code;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
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
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
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
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

/**
 * Класс для работы с кодом. Здесь Определены функции для расстановки счетчиков, компиляции и прочего.
 * @author Gromov Evg.
 */
public class Proccessor { 
    /** Парсер для разбора исходного кода. */
    private static final ASTParser parser = ASTParser.newParser(AST.JLS4);
    /** Документ с кодом, в который будут расставляться счетчики операций. */
    private static Document codeDoc = null;
    /** Rewriter для внесения изменений в ast-дерево исходного кода алгоритмов. */
    private static ASTRewrite rewriter = null;
    
    /**
     * Метод возвращает лист всех деклараций методов, которые будут найдены в коде {@code codes}.
     * @param codes Коды для поиска методов.
     * @return Лист с декларациями методов типа {@code MethodDeclaration}.
     */
    public static ArrayList<MethodDeclaration> getAllMethodsFromCodes(ArrayList<Code> codes) {
        final ArrayList<MethodDeclaration> methods = new ArrayList<>();
        for(Code code : codes) {
            methods.addAll(getAllMethodsFromCode(code.getSourceCode()));
        }
        return methods;
    }
    
    /**
     * Метод возвращает лист всех деклараций методов, которые будут найдены в коде {@code codes}.
     * @param codes Коды для поиска методов.
     * @return Лист с декларациями методов типа {@code MethodDeclaration}.
     */
    public static ArrayList<MethodDeclaration> getAllMethodsFromCodesStrings(ArrayList<String> codes) {
        final ArrayList<MethodDeclaration> methods = new ArrayList<>();
        for(String code : codes) {
            methods.addAll(getAllMethodsFromCode(code));
        }
        return methods;
    }
    
    /**
     * Метод возвращает лист всех деклараций методов, которые будут найдены в коде {@code code}.
     * @param code Код для поиска методов.
     * @return Лист с декларациями методов типа {@code MethodDeclaration}.
     */
    public static ArrayList<MethodDeclaration> getAllMethodsFromCode(String code) {
        final ArrayList<MethodDeclaration> methods = new ArrayList<>();
        
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
            gen_codes.add(new Code(code, getGeneratedCode(code), Proccessor.getPackage(code), Proccessor.getClassName(code)));
        }        
        return gen_codes;
    }
    
    /**
     * Метод для расстановки счетчиков операций в алгоритма. 
     * @param code Исходные коды алгоритма.
     * @return Коды с расставленными счетчиками.
     */
    public static Code putCounters(String code) {
            return new Code(code, getGeneratedCode(code), Proccessor.getPackage(code), Proccessor.getClassName(code));
    }   
    
    /**
     * Метод возвращает строку, содержашую код с вставленными счетчиками.
     * @param counterName Имя счетчика для вставки.
     * @param code Исходный код одного public-класса.
     * @return Код с вставленными счетчиками.
     */
    static String getGeneratedCode(String code) {
        putCountersInCode(code);
        return codeDoc.get();
    }
    
    /**
     * Метод для расстановки счетчиков в пореданный исходный код.
     * @param code Исходный код.
     */
    private static void putCountersInCode(String code) {
        try {
            codeDoc = new Document(code);
            parser.setSource(code.toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            CompilationUnit cu = (CompilationUnit) parser.createAST(null);

            cu.recordModifications();
            ImportDeclaration counter_import = cu.getAST().newImportDeclaration();
            counter_import.setName(cu.getAST().newName("comparative_system.Counter"));
            cu.imports().add(counter_import);
            TextEdit edits = cu.rewrite(codeDoc, null);
            edits.apply(codeDoc);
            
            parser.setSource(codeDoc.get().toCharArray());
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
            
            edits = rewriter.rewriteAST(codeDoc, null);
            edits.apply(codeDoc);
            
            parser.setSource(codeDoc.get().toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            cu = (CompilationUnit) parser.createAST(null);
            ast = cu.getAST();
            rewriter = ASTRewrite.create(ast);
            
            cu.accept(new ASTVisitor() {
                @Override
                public boolean visit(WhileStatement st) {
                    int c = countOperationsInExpression(st.getExpression());        
                    if (c > 0) { insertIterationsCounterInCycle(st, c); }
                    return true;
                }
                
                @Override
                public boolean visit(DoStatement st) {
                    int c = countOperationsInExpression(st.getExpression());  
                    if (c > 0) { insertIterationsCounterInCycle(st, c); }    
                    return true;
                }
                
                @Override
                public boolean visit(ForStatement st) {
                    int c = 0;
                    for (Object e : st.updaters()) {
                        c += countOperationsInExpression((Expression)e);
                    }     
                    c += countOperationsInExpression(st.getExpression());
                    
                    if (c > 0) { insertIterationsCounterInCycle(st, c); }
                    return true;
                }
                
                @Override
                public boolean visit(EnhancedForStatement st) {
                    int c = countOperationsInExpression(st.getExpression()); 
                    if (c > 0) { insertIterationsCounterInCycle(st, c + 4); } // инкримент счетчика (2), сравнение счетика с размером контейнера (1), и присваивание значения из контейнера (1).
                    return true;
                }
            });
            
            edits = rewriter.rewriteAST(codeDoc, null);
            edits.apply(codeDoc);
        } catch (MalformedTreeException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadLocationException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Метод для подсчета количества операций и расстановки счетчиков в переданой структуре типа {@code Statement}. 
     * Здесь код разбивается на структуры. 
     * Если в структуре может быть какая-либо операция, структура передается в 
     * метод {@code countOperationsInExpression}, где подсчитывается количество операций в структуре.
     * Счетчик с количеством операций вставляется перед структурой.
     * @param st Структура для подсчета количества операций и расстановки счетчиков.
     */
    private static void countOperationsInStatement(Statement st) {
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
        }
        
        if (st instanceof ForStatement) {
            int c = 0;
            for (Object e : ((ForStatement)st).initializers()) {
                c = countOperationsInExpression((Expression)e);
            }
            
            int c2 = countOperationsInExpression(((ForStatement)st).getExpression());
            
            if (c + c2 > 0) { insertCounter(st, c + c2); } //сравнение выполняется 1 раз всегда. Счетчики для обновления "счетчиков" и сравнения вставляются позже.             
            
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
            if (c>0) { insertCounter(st, c + 1); } //только сравнение(1).
                           
            countOperationsInStatement(((EnhancedForStatement)st).getBody());
        }
    }

    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int countOperationsInExpression(Expression ex) {
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
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
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
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessInfixExpression(InfixExpression ex) {
        int c = 1;
        c += countOperationsInExpression(((InfixExpression)ex).getLeftOperand());
        c += countOperationsInExpression(((InfixExpression)ex).getRightOperand());
        for (Object o : ex.extendedOperands()) {
            c += 1;
            c += countOperationsInExpression((Expression)o);
        }
        return c;
    }
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessPostfixExpression(PostfixExpression ex) {
        int c = 2; // постфиксные ++ и --.
        c += 2 * countOperationsInExpression(((PostfixExpression)ex).getOperand());
        return c;
    }
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
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
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
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
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessArrayAccessExpression(ArrayAccess ex) {
        int c = 1; 
        c += countOperationsInExpression(ex.getArray());
        c += countOperationsInExpression(ex.getIndex());
        return c;
    }
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessArrayInitializerExpression(ArrayInitializer ex) {
        int c = 0; 
        for (Object e : ex.expressions()) 
        {
            c += countOperationsInExpression((Expression)e);
        }
        return c;
    }
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessArrayCreationExpression(ArrayCreation ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getInitializer());
        return c;
    } 
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessParenthesizedExpression(ParenthesizedExpression ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getExpression());
        return c;
    }
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessInstanceofExpression(InstanceofExpression ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getLeftOperand());
        return c;
    }
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessFieldAccessExpression(FieldAccess ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getExpression());
        return c;
    } 
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessCastExpression(CastExpression ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getExpression());
        return c;
    }
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessConditionalExpression(ConditionalExpression ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getExpression());
        c += countOperationsInExpression(ex.getThenExpression());
        c += countOperationsInExpression(ex.getElseExpression());
        return c;
    }
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessNormalAnnotationExpression(NormalAnnotation ex) {
        int c = 0; 
        for(Object m : ex.values()) {
            c += countOperationsInExpression(((MemberValuePair)m).getValue());
        }
        return c;
    }
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessSingleMemberAnnotationExpression(SingleMemberAnnotation ex) {
        int c = 0; 
        c += countOperationsInExpression(ex.getValue());
        return c;
    }
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
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
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessSuperMethodInvocationExpression(SuperMethodInvocation ex) {
        int c = 0; 
        for (Object e : ex.arguments()) 
        {
            c += countOperationsInExpression((Expression)e);
        }
        return c;
    }
    
    /**
     * Метод для подсчета количества операций в выражении. Если в выражении содержатся другие выражения, вызывается рекурсия.
     * @param ex Выражение для подсчета количества операций.
     * @return Количество операций в выражении.
     */
    private static int proccessMethodInvocationExpression(MethodInvocation ex) {
        int c = 0; 
        for (Object e : ex.arguments()) 
        {
            c += countOperationsInExpression((Expression)e);
        }
        return c;
    }
    //</editor-fold>

    /**
     * Метод для вставки счеткика операций в исходный код перед переданной структурой. 
     * @param st Структура, перед которой требуется поставить счетчик.
     * @param c Количество операций внутри структуры.
     */
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

    /**
     * Метод для вставки счеткика операций в исходный код перед циклами. 
     * @param st Цикл, перед которым требуется поставить счетчик.
     * @param c Количество операций внутри цикла.
     */
    private static void insertIterationsCounterInCycle(Statement st, int c) {
        try {
            AST ast = st.getAST();
            MethodInvocation counterMethod = ast.newMethodInvocation();
            counterMethod.setExpression(ast.newSimpleName("Counter"));
            counterMethod.setName(ast.newSimpleName("add"));
            ExpressionStatement countExpr = ast.newExpressionStatement(counterMethod);
            ((MethodInvocation)countExpr.getExpression()).arguments().clear();
            ((MethodInvocation)countExpr.getExpression()).arguments().add(st.getAST().newNumberLiteral(""+c));
            
            Block cycleBody = ast.newBlock();
            
            if (st instanceof WhileStatement) {
                cycleBody.statements().add(countExpr);  
                cycleBody.statements().add(rewriter.createCopyTarget(((WhileStatement)st).getBody()));
                rewriter.replace(((WhileStatement)st).getBody(), cycleBody, null);
                return;
            }
            
            if (st instanceof DoStatement) {
                cycleBody.statements().add(rewriter.createCopyTarget(((DoStatement)st).getBody()));
                cycleBody.statements().add(countExpr);  
                rewriter.replace(((DoStatement)st).getBody(), cycleBody, null);
                return;
            }
            
            if (st instanceof ForStatement) {
                cycleBody.statements().add(countExpr);  
                cycleBody.statements().add(rewriter.createCopyTarget(((ForStatement)st).getBody()));
                rewriter.replace(((ForStatement)st).getBody(), cycleBody, null);
                return;
            }
            
            if (st instanceof EnhancedForStatement) {
                cycleBody.statements().add(countExpr);  
                cycleBody.statements().add(rewriter.createCopyTarget(((EnhancedForStatement)st).getBody()));
                rewriter.replace(((EnhancedForStatement)st).getBody(), cycleBody, null);
                return;
            }
            
            throw new IllegalArgumentException("Переданный параметр не является циклом!");
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedTreeException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Метод для получения названия класса из исходного кода.
     * @param code Исходный код.
     * @return Имя класса.
     */
    public static String getClassName(String code) {
        parser.setSource(code.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        for (Object e : cu.types()) {
            if (e instanceof TypeDeclaration) {//если класс
                return ((TypeDeclaration)e).getName().toString();
            }
        }
        return "tab";
    }

    /**
     * Метод для получения названий всех классов из исходных кодов.
     * @param codes Исходные коды.
     * @return Лист с именами классов.
     */
    public static ArrayList<String> getClassNames(ArrayList<String> codes) {
        ArrayList<String> res = new ArrayList<>();
        for (String code : codes) {
            res.add(getClassName(code));
        }
        return res;
    }

    /**
     * Метод для проверки компилируемости алгоритма. Здесь исходные коды 
     * сохраняются в каталогах, соответствующих пакетам и компилируются.
     * @param codes Лист с исходными кодами алгоритма.
     * @return Строка, содержащая ошибки компиляции. Если ошибок нет, то вернет пустую строку.
     */
    public static String checkClassesCompilableInTabs(ArrayList<String> codes) {
        String res = "";
        ArrayList<String> filesForCompile = new ArrayList<>();
        //сохраняем исходники в каталогах согласно пакетам
        for (String code : codes) {
            try {
                parser.setSource(code.toCharArray());
                parser.setKind(ASTParser.K_COMPILATION_UNIT);
                final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
                Name packageForPath = (cu.getPackage() != null) ? cu.getPackage().getName() : null;
                
                FileWriter fileW = null;
                
                //создаем необходимые каталоги для пакета
                String dirsString = "";
                if (packageForPath != null) {
                    while (packageForPath.isQualifiedName()) {
                        dirsString = ((QualifiedName)packageForPath).getName().toString() + "/" + dirsString;
                        packageForPath = ((QualifiedName)packageForPath).getQualifier();
                    }
                    dirsString = ((SimpleName)packageForPath).toString() + "/" + dirsString;
                }
                dirsString = "javatempfiles/" + dirsString;
                File dirs = new File(dirsString);
                dirs.mkdirs();
                
                String fileFullName = dirsString + Proccessor.getClassName(code) + ".java";
                filesForCompile.add(fileFullName);
                File file = new File(fileFullName);
                fileW = new FileWriter(file);
                fileW.write(code);
                fileW.close();
            } catch (IOException ex) {
                Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //компилим
        for (String file : filesForCompile) {
            try {
                ProcessBuilder procBuilder = new ProcessBuilder(Preferences.getJdkPath() + "\\javac", "-sourcepath", "javatempfiles/", file);
                procBuilder.redirectErrorStream(true);
                
                Process process = procBuilder.start();
                
                InputStream stdout = process.getInputStream();
                InputStreamReader isrStdout = new InputStreamReader(stdout);
                BufferedReader brStdout = new BufferedReader(isrStdout);
                
                String line = "";
                while((line = brStdout.readLine()) != null) {
                    res += line + "\n";
                }
                
                int exitVal = process.waitFor();
            } catch (IOException ex) {
                Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return res;
    }
    
    /**
     * Метод для компиляции кода. Еще нигде не используется.
     * @param code Код для компиляции.
     * @return Строка, содержащая ошибки компиляции. Если ошибок нет, то вернет пустую строку.
     */
    private static String compile(String code) {
        parser.setSource(code.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        Name packageForPath = cu.getPackage().getName();
        
        String res = "";
        try {
            FileWriter fileW = null;
            
            //создаем необходимые каталоги для пакета
            String dirsString = "";
            if (packageForPath != null) {
                while (packageForPath.isQualifiedName()) {
                    dirsString = ((QualifiedName)packageForPath).getName().toString() + "/" + dirsString;
                    packageForPath = ((QualifiedName)packageForPath).getQualifier();
                }
                dirsString = ((SimpleName)packageForPath).toString() + "/" + dirsString;
            }
            dirsString = "javatempfiles/" + dirsString;
            File dirs = new File(dirsString);
            dirs.mkdirs();
            
            
            File file = new File(dirsString + Proccessor.getClassName(code) + ".java");
            fileW = new FileWriter(file);
            fileW.write(code);
            fileW.close();

            ProcessBuilder procBuilder = new ProcessBuilder(Preferences.getJdkPath() + "\\javac", "-sourcepath", "javatempfiles/", dirsString + file.getName());
            procBuilder.redirectErrorStream(true);

            Process process = procBuilder.start();

            InputStream stdout = process.getInputStream();
            InputStreamReader isrStdout = new InputStreamReader(stdout);
            BufferedReader brStdout = new BufferedReader(isrStdout);

            String line = "";
            while((line = brStdout.readLine()) != null) {
                res += line;
            }

            int exitVal = process.waitFor();
        } catch (IOException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    /**
     * Метод возвращает список имен классов с пакетами.
     * @param alg_id id алгоритма для имен по умолчанию.
     * @param codes Список исходных кодов.
     * @return Список полных имен.
     */
    public static ArrayList<Name> getFullNamesOfClasses(long alg_id, ArrayList<Code> codes) {
        ArrayList<Name> fullNames = new ArrayList<>();
        for (Code code : codes) {
            parser.setSource(code.getSourceCode().toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
            Name packageForPath = (cu.getPackage() != null) ? cu.getPackage().getName() : null;

            String className = "";
            for (Object e : cu.types()) {
                if (e instanceof TypeDeclaration) {//если класс
                    className = ((TypeDeclaration)e).getName().toString();
                    break;
                }
            } 

            if (packageForPath != null) {
                fullNames.add(cu.getAST().newName(packageForPath.toString() + "." + className));
            }             
        }
        return fullNames;
    }

    /**
     * Метод для удаления директории со всем содержимым.
     * @param dir 
     */
    public static void removeDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String children1 : children) {
                File f = new File(dir, children1);
                removeDirectory(f);
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }
    
    /**
     * Метод для проверки компилируемости генератора исходных данных. 
     * Коды всех алгоритмов также компилируются.
     * @param dataGenCode Код генератора исходных данных.
     * @param algs Лист с алгоритмами.
     * @return Строка, содержащая ошибки компиляции. Если ошибок нет, то вернет пустую строку.
     */
    public static String checkGeneratorCompilable(String dataGenCode) {
        String res = "";
        
        //чистим папку для сохранения
        removeDirectory(new File("data_generator/"));
        new File("data_generator/generator/").mkdirs();
        //--чистим папку для сохранения
        //сохраняем исходники в каталогах согласно пакетам
        FileWriter fileW = null;
        try {
            File file = new File("data_generator/generator/Generator.java");
            fileW = new FileWriter(file);
            fileW.write(dataGenCode);
            fileW.close();
        } catch (IOException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        //компилим
        try {
            ProcessBuilder procBuilder = new ProcessBuilder(Preferences.getJdkPath() + "\\javac", "-classpath", "dist/CompSys.jar;generated_codes/", "data_generator/generator/Generator.java");//TODO: в "поле" этот класспаф не сработает
            procBuilder.redirectErrorStream(true);

            Process process = procBuilder.start();

            InputStream stdout = process.getInputStream();
            InputStreamReader isrStdout = new InputStreamReader(stdout);
            BufferedReader brStdout = new BufferedReader(isrStdout);

            String line = "";
            while((line = brStdout.readLine()) != null) {
                res += line + "\n";
            }

            int exitVal = process.waitFor();
        } catch (IOException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }

    /**
     * Метод для вставки названий пакетов в коды классов алгоритма. Изменяются только те 
     * классы, в которых не указаны пакеты - в качестве имени пакета указывается "algorithm"+"id алгоритма в БД проекта".
     * @param alg_id id алгоритма в БД проекта.
     * @param codes Исходные коды классов алгоритма.
     * @return Лист с классами, в каждом из которых указан пакет.
     */
    public static ArrayList<Code> putPackagesIfNotExist(long alg_id, ArrayList<Code> codes) {
        for (Code c : codes) {
            String codeS = c.getSourceCode();
            String codeG = c.getSourceCode();
            parser.setSource(codeS.toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
            Name packageForPath = (cu.getPackage() != null) ? cu.getPackage().getName() : null;

            if (packageForPath == null) {
                c.setPackageName("algorithm" + alg_id);
                c.setSourceCode("package algorithm" + alg_id + ";\n\n" + codeS);
                c.setGeneratedCode("package algorithm" + alg_id + ";\n\n" + codeG);
            }                
        }
        return codes;
    }

    /**
     * Метод для вставки пакета в класс генератора данных. Если позователь уже указал пакет - он будет перезаписан.
     * @param code Код генератора данных.
     * @return Класс генератора с указанных пакетом.
     */
    public static String setPackageGenerator(String code) {
        Document doc = new Document(code);
        try {
            parser.setSource(doc.get().toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
            AST ast = cu.getAST();
            ASTRewrite rewriterPackage = ASTRewrite.create(ast);
                        
            PackageDeclaration newPackage = ast.newPackageDeclaration();
            newPackage.setName(ast.newName("generator"));
            
            if (cu.getPackage() != null) {
                rewriterPackage.replace(cu.getPackage(), newPackage, null);
            } else {
                return "package generator;\n\n" + code;
            }
            TextEdit edits = rewriterPackage.rewriteAST(doc, null);
            edits.apply(doc);
        } catch (MalformedTreeException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadLocationException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doc.get();
    }

    /**
     * Метод для отделения пользовательских импортов от автоматически генерируемых импортов. 
     * @param code Весь код генератора данных.
     * @param defaultImportsMap Автоматически генерируемые импорты.
     * @return Строка с пользовательскими импортами.
     */
    public static String getUserDefinedImports(String code, HashMap defaultImportsMap) {
        String res = "";

        parser.setSource(code.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        
        List imports = cu.imports();
        for (Object imp : imports) {
            Name name = ((ImportDeclaration)imp).getName();
            String tempValue = name.getFullyQualifiedName();
            if (((ImportDeclaration)imp).isOnDemand()) {//все автоматически генерированные импотры алгоритмов идут в эту ветку
                if (!defaultImportsMap.containsValue(tempValue)) {
                    res += "import " + tempValue + ".*;\n";
                }
            } else {
                if (!tempValue.equals("comparative_system.model.Data") && !tempValue.equals("comparative_system.model.IGenerator") && !tempValue.equals("java.util.ArrayList")) {
                    res += "import " + tempValue + ";\n";
                }
            }
        }  
        
        return res;
    }

    /**
     * Метод для выделения реализации метода {@code generate} из класса генератора.
     * @param code Класс генератора исходных данных.
     * @return Код метода {@code generate}.
     */
    public static String getGenerateImplementation(String code) {
        final StringBuilder res = new StringBuilder("");
        
        parser.setSource(code.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodDeclaration node) {
                if (node.getName().toString().equals("generate")) {
                    res.append(node.getBody());                    
                }
                return false;
            }
        });
       
        return res.toString().substring(1, res.length() - 2);
    }
    
    /** Метод возвращает имя пакета из исходного кода.
     * @param code Исходный код.
     * @return Имя пакета. 
     */
    public static String getPackage(String code) {
        parser.setSource(code.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        return (cu.getPackage() != null) ? cu.getPackage().getName().getFullyQualifiedName() : "";
    }
    
    /**
     * Метод возвращает декларацию метода из кода.
     * @param code Исходный код.
     * @param methodName Имя метода.
     * @return Декларация метода.
     */
    public static MethodDeclaration getMethodDeclarationFromCode(String code, String methodName) {
        ArrayList<MethodDeclaration> methods = getAllMethodsFromCode(code);
        for(MethodDeclaration m : methods) {
            if (m.getName().toString().equals(methodName)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Метод для подготовки кодов алгоритма к работе в программе. 
     * Здесь все коды компилируются, в том числе и сгенерированные, 
     * после чего классы без ошибок компиляции подгружаются в программу. 
     * Здесь также определяется класс с главным методом.
     * @param codes Лист кодов. В кодах должны быть проставлены пакеты!
     * @param mainMethod Имя метода вызова алгоритма.
     * @return Обновленный лист кодов.
     */
    public static ArrayList<Code> prepareCodes(ArrayList<Code> codes, String mainMethod) {
        //сохраняем исходники в каталогах согласно пакетам
        FileWriter fileW;
        for (Code code : codes) {
            try {
                String packagePath = code.getPathOfFileForCompile();
                //создаем необходимые каталоги для пакета              
                (new File("source_codes/" + packagePath)).mkdirs();
                (new File("generated_codes/" + packagePath)).mkdirs();
                //--создаем необходимые каталоги для пакета               

                String fileFullName = packagePath + code.getClassName() + ".java";
                
                //сохраняем коды
                File file = new File("source_codes/" + fileFullName);
                fileW = new FileWriter(file);
                fileW.write(code.getSourceCode());
                fileW.close();
                
                file = new File("generated_codes/" + fileFullName);
                fileW = new FileWriter(file);
                fileW.write(code.getGeneratedCode());
                fileW.close();
                //--сохраняем коды
                
            } catch (IOException ex) {
                Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //компилим
        for (Code code : codes) {
            String errors = "";
            try {
                //исходные коды
                String file = "source_codes/" + code.getPathOfFileForCompile() + code.getClassName() + ".java";
                ProcessBuilder procBuilder = new ProcessBuilder(Preferences.getJdkPath() + "\\javac", "-sourcepath", "source_codes", file);
                procBuilder.redirectErrorStream(true);
                Process process = procBuilder.start();
                InputStream stdout = process.getInputStream();
                InputStreamReader isrStdout = new InputStreamReader(stdout);
                BufferedReader brStdout = new BufferedReader(isrStdout);
                String line;
                while((line = brStdout.readLine()) != null) {
                    errors += line + "\n";
                }
                process.waitFor();
                code.setSourceCodeErrors(errors.trim());
                
                if (!errors.equals("")) { continue; } else { errors = ""; } //если исходные коды с ошибками, то сгенерированные по определению тоже.
                //коды со счетчиками
                file = "generated_codes/" + code.getPathOfFileForCompile() + code.getClassName() + ".java";
                procBuilder = new ProcessBuilder(Preferences.getJdkPath() + "\\javac", "-sourcepath", "generated_codes", "-classpath", "dist/CompSys.jar", file);
                procBuilder.redirectErrorStream(true);
                process = procBuilder.start();
                stdout = process.getInputStream();
                isrStdout = new InputStreamReader(stdout);
                brStdout = new BufferedReader(isrStdout);
                while((line = brStdout.readLine()) != null) {
                    errors += line + "\n";
                }
                process.waitFor();
                code.setGeneratedCodeErrors(errors.trim());
            } catch (IOException ex) {
                Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // подгружаем коды со счетчиками без ошибок в программу и находим главный метод
        try {
            URL url = new File("generated_codes/").toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, CompSys.class.getClassLoader());
            for (Code c : codes) {
                if (c.getSourceCodeErrors().equals("") && c.getGeneratedCodeErrors().equals("")) {
                    c.setGeneratedClass(classLoader.loadClass(c.getPackageName() + "." + c.getClassName()));
                }
                if (Proccessor.getMethodDeclarationFromCode(c.getSourceCode(), mainMethod) != null) {
                    c.setHasMainMethod(true);
                }
            }   
        } catch (MalformedURLException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Proccessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return codes;
    }

    /**
     * Метод возвращает все импорты из исходного кода.
     * @param code Код.
     * @return Список импортов.
     */
    public static List getAllImportsFromCode(Code code) {
        parser.setSource(code.getSourceCode().toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        return cu.imports();
    }
}