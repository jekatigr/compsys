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
    
    
    private static final String counterName = "counter123";
//    String code = "public static class A {\n" +
//"    public int res() {\n" +
//"		int result = 0;\n" +
//"		result = 17 + 20;\n" +
//"		return result;\n" +
//"	}\n" +
//"	class B {\n" +
//"		int l;\n" +
//"	}\n" +
//"}";        
    
    public ProccessorTest() {
    }

    /**
     * Test of getGeneratedCode method, of class Proccessor.
     */
    @Test
    public void testGetGeneratedCode() {
        //System.out.println("getGeneratedCode");
        
        String code = "int i; i = 12 + 3;";
        
        String expResult = "";
        String result = "";//Proccessor.getGeneratedCode(counterName, code);
        assertEquals(expResult, result);
        
        fail("Just fail.");
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
     * Test of getCounterName method, of class Proccessor.
     */
    @Test
    public void testGetCounterName() {
        //System.out.println("getCounterName");
        ArrayList<String> codes = null;
        String expResult = "";
        String result = "";//Proccessor.getCounterName(codes);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of putCounters method, of class Proccessor.
     */
    @Test
    public void testPutCounters() {
        //System.out.println("putCounters");
        String counterName = "";
        ArrayList<String> codes = null;
        ArrayList<Code> expResult = null;
        ArrayList<Code> result = null;//Proccessor.putCounters(counterName, codes);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of countOperationsInExpression method, of class Proccessor.
     */
    @Test
    public void testCountOperationsInExpression() {
        //System.out.println("countOperationsInExpression");
        Expression ex = null;
        int expResult = 0;
        int result = 0;//Proccessor.countOperationsInExpression(ex);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    
    
    /**
     * Test of putCountersInCodeFromMap method, of class Proccessor.
     */
    @Test
    public void testPutCountersInCodeFromMap() {
        //System.out.println("putCountersInCodeFromMap");
        
        String counterName = "";
        String code = "";
        String expResult = "";
        String result = "";//Proccessor.putCountersInCodeFromMap(map, counterName, code);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fillNewMap method, of class Proccessor.
     */
    @Test
    public void testFillNewMap() {
        System.out.println("fillNewMap");
        String code = "public class A{\n" +
"    public static int res() {\n" +
"		int result = 0;\n" +
"		for (int i = 1+2; i < 100+1+1+1; i++) {\n" +
"			result -= i;\n" +
"			for (int j = 0; j < 100; j++) \n" +
"							\n" +
"				result %= 10;\n" +
"			\n" +
"		}\n" +
"		return result + 1;\n" +
"	}\n" +
"	public int meth() {\n" +
"		int h = 0;\n" +
"		int start;\n" +
"		h = start = h + res();\n" +
"		System.out.print(h + 10);\n" +
"	}\n" +
"}";
        Proccessor.fillNewMap(code);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals(1, 1);
    }
}
