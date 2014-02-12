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
    /** id кода в БД проекта. */
    private int id;
    /** Исходный код. */
    private String sourceCode;
    /** Код со вставленными счетчиками. */
    private String generatedCode;
    
    /**
     * Конструктор класса.
     * @param id id кода в БД проекта.
     * @param sourceCode Исходный код.
     * @param generatedCode Код со вставленными счетчиками.
     */
    public Code(int id, String sourceCode, String generatedCode) {
        this.sourceCode = sourceCode;
        this.generatedCode = generatedCode;
    }

    /**
     * Метод возвращает исходный код без изменений.
     * @return Исходный код без изменений.
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * Метод возвращает исходный код со вставленными счетчиками.
     * @return Исходный код со вставленными счетчиками.
     */
    public String getGeneratedCode() {
        return generatedCode;
    }
}
