/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system;

import comparative_system.model.Code;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Test;
import static org.junit.Assert.*;

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
        System.out.println("getAllMethodsFromCodes");
        ArrayList<String> codes = null;
        ArrayList<MethodDeclaration> expResult = null;
        ArrayList<MethodDeclaration> result = Proccessor.getAllMethodsFromCodes(codes);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCounterName method, of class Proccessor.
     */
    @Test
    public void testGetCounterName() {
        System.out.println("getCounterName");
        ArrayList<String> codes = null;
        String expResult = "";
        String result = Proccessor.getCounterName(codes);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of putCounters method, of class Proccessor.
     */
    @Test
    public void testPutCounters() {
        System.out.println("putCounters");
        String counterName = "";
        ArrayList<String> codes = null;
        ArrayList<Code> expResult = null;
        ArrayList<Code> result = Proccessor.putCounters(counterName, codes);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
