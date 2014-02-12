/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.model;

/**
 *
 * @author TireX
 */
public class Code {
    private int id;
    private String sourceCode;
    private String generatedCode;
    public Code(int id, String sourceCode, String generatedCode) {
        this.sourceCode = sourceCode;
        this.generatedCode = generatedCode;
    }
}
