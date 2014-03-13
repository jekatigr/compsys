/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system;

import comparative_system.model.Code;
import java.awt.Point;
import java.util.ArrayList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Test;


/**
 *
 * @author TireX
 */
public class ProccessorTest {
  
    public ProccessorTest() {
    }
    /**
     * Test of getAllMethodsFromCodes method, of class Proccessor.
     */
    @Test
    public void testGetAllMethodsFromCodes() {
        //System.out.println("getAllMethodsFromCodes");
        ArrayList<String> codes = null;
        ArrayList<MethodDeclaration> expResult = null;
        ArrayList<MethodDeclaration> result = null;//Proccessor.getAllMethodsFromCodes(codes);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of putCountersInCode method, of class Proccessor.
     */
    @Test
    public void testPutCountersInCodeInfixExpression() {
        System.out.println("putCountersInCodeInfixExpression1");
        String code = "public class A{\n" +
"    public static int[] res(int k) {\n" +
"		String i = 10 + 5 / 2;\n" +
"	}\n" +
"}";
        String exp = "public class A{\n" +
"    public static int[] res(int k) { Counter.add(3);\n" +
"		String i = 10 + 5 / 2;\n" +
"	}\n" +
"}";
        String newCode = Proccessor.getGeneratedCode(code);
        assertEquals(removeEmptyChars(exp), removeEmptyChars(newCode));
    }
    
    /**
     * Test of putCountersInCode method, of class Proccessor.
     */
    @Test
    public void testPutCountersInCodeInfixExpression2() {
        System.out.println("putCountersInCodeInfixExpression2");
        
        String code = "public class A{\n" +
"    public static int[] res(int k) {\n" +
"		Data.getSomething(k * 2 + 1)[k + 1]++;\n" +
"	}\n" +
"}";
        String exp = "public class A{\n" +
"    public static int[] res(int k) { Counter.add(10);\n" +
"		Data.getSomething(k * 2 + 1)[k + 1]++;\n" +
"	}\n" +
"}";
        String newCode = Proccessor.getGeneratedCode(code);
        assertEquals(removeEmptyChars(exp), removeEmptyChars(newCode));
    }
    
    /**
     * Test of putCountersInCode method, of class Proccessor.
     */
    @Test
    public void testPutCountersInCodeInfixExpression3() {
        System.out.println("putCountersInCodeInfixExpression3");
        
        String code = "public class A{\n" +
"    public static int[] res(int k) {\n" +
"		String i = md(\"re\"+\"43\")[10*11]++ + 10*(21+12-4/3);\n" +
"	}\n" +
"}";
        String exp = "public class A{\n" +
"    public static int[] res(int k) {Counter.add(14);\n" +
"		String i = md(\"re\"+\"43\")[10*11]++ + 10*(21+12-4/3);\n" +
"	}\n" +
"}";
        String newCode = Proccessor.getGeneratedCode(code);
        assertEquals(removeEmptyChars(exp), removeEmptyChars(newCode));
    }
    
    /**
     * Test of putCountersInCode method, of class Proccessor.
     */
    @Test
    public void testPutCountersInCodeInfixExpression4() {
        System.out.println("putCountersInCodeInfixExpression4");
        
        String code = "public class A{\n" +
"    public static void res(int k) {\n" +
"                int i = 0;\n" +
"                int k2 = 0;\n" +
"                int k3 = k2 + 13 + i++ + k2--;\n" +
"	}\n" +
"}";
        String exp = "public class A{\n" +
"    public static void res(int k) {Counter.add(1);\n" +
"                int i = 0;\n" +
"				Counter.add(1);\n" +
"                int k2 = 0;\n" +
"				Counter.add(8);\n" +
"                int k3 = k2 + 13 + i++ + k2--;\n" +
"	}\n" +
"}";
        String newCode = Proccessor.getGeneratedCode(code);
        assertEquals(removeEmptyChars(exp), removeEmptyChars(newCode));
    }
    
    /**
     * Test of putCountersInCode method, of class Proccessor.
     */
    @Test
    public void testPutCountersInCodeForSt1() {
        System.out.println("putCountersInCodeForSt1");
        
        String code = "public class A{\n" +
"    public static void res(int k) {\n" +
"        for (int i = 12+5, k = 12 + 12 + 120; i + 15 < (i * i) / 2; i++) {\n" +
"			System.out.print(i);\n" +
"		}\n" +
"	}\n" +
"}";
        String exp = "public class A{\n" +
"    public static void res(int k) {\n" +
"	Counter.add(9);\n" +
"        for (int i = 12+5, k = 12 + 12 + 120; i + 15 < (i * i) / 2; i++) {\n" +
"		Counter.add(6);{\n" +
"			System.out.print(i);\n" +
"			}\n" +
"		}\n" +
"	}\n" +
"}";
        String newCode = Proccessor.getGeneratedCode(code);
        assertEquals(removeEmptyChars(exp), removeEmptyChars(newCode));
    }
    
    /**
     * Test of putCountersInCode method, of class Proccessor.
     */
    @Test
    public void testPutCountersInCodeForSt2() {
        System.out.println("putCountersInCodeForSt2");
        
        String code = "public class A{\n" +
"    public static void res(int k) {\n" +
"        for (;;) {\n" +
"	\n" +
"			System.out.print(k*k*k*k);\n" +
"		}\n" +
"	}\n" +
"}";
        String exp = "public class A{\n" +
"    public static void res(int k) {\n" +
"        for (;;) {\n" +
"			\n" +
"			Counter.add(3);\n" +
"			System.out.print(k*k*k*k);\n" +
"		}\n" +
"	}\n" +
"}";
        String newCode = Proccessor.getGeneratedCode(code);
        assertEquals(removeEmptyChars(exp), removeEmptyChars(newCode));
    }
    
    /**
     * Test of putCountersInCode method, of class Proccessor.
     */
    @Test
    public void testPutCountersInCodeForSt3() {
        System.out.println("putCountersInCodeForSt3");
        
        String code = "public class A{\n" +
"    public static void res(int k) {\n" +
"        for (;;i++) {\n" +
"			\n" +
"		}\n" +
"	}\n" +
"}";
        String exp = "public class A{\n" +
"    public static void res(int k) {\n" +
"        for (;;i++) {\n" +
"			Counter.add(2);{}\n" +
"		}\n" +
"	}\n" +
"}";
        String newCode = Proccessor.getGeneratedCode(code);
        assertEquals(removeEmptyChars(exp), removeEmptyChars(newCode));
    }
    
    
    private static String removeEmptyChars(String str) {
        String res = str.replace("\n", "");
        res = res.replace("\r", "");
        res = res.replace("\t", "");
        res = res.replace(" ", "");
        return res;
    }
}
