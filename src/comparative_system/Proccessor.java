/*
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
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * 
 * @author TireX
 */
public class Proccessor {
    private static AST AST;
    
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
    
    static int countOperationsInExpression(Expression ex) {
        return 0;       
    }

    static Map buildNewMap(String code) {
        final Map map = new Map();
        
        parser.setSource(code.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        cu.accept(new ASTVisitor() {
            public boolean visit(ExpressionStatement expression) {// ходим по всем действиям
                map.addParsedExpression(expression.getStartPosition(), expression.getLength(), "Expression", countOperations(expression.getExpression()));
                return true;
            }
        });
        return map;
    }

    static String putCountersInCodeFromMap(Map map, String counterName, String code) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static int count = 0;
    static int countOperations(Expression ex) {
        count = 0;
        ex.accept(new ASTVisitor() {
            public boolean visit(Assignment ass) {// ходим по всем выражениям с оператором присваивания
                System.out.println("--" + ass.toString());
                count++;
                return false; // если вернуть true, то (h = s = 11) пройдет дважды с (s = 11)
            }
        });
        return count;
    }
}