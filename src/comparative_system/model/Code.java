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
    //private long id;
    /** Исходный код. */
    private String sourceCode;
    /** Код со вставленными счетчиками. */
    private String generatedCode;
    
    /**
     * Конструктор класса.
     * @param sourceCode Исходный код.
     * @param generatedCode Код со вставленными счетчиками.
     */
    public Code(String sourceCode, String generatedCode) {
        this(-1, sourceCode, generatedCode);
    }
    
    /**
     * Конструктор класса.
     * @param id id кода в БД проекта.
     * @param sourceCode Исходный код.
     * @param generatedCode Код со вставленными счетчиками.
     */
    public Code(long id, String sourceCode, String generatedCode) {
        //this.id = id;
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

    public void setSourceCode(String code) {
        this.sourceCode = code;
    }

    public void setGeneratedCode(String code) {
        this.generatedCode = code;
    }

    /**
     * Метод возвращает id исходных кодов в БД.
     * @return id исходных кодов.
     */
//    long getId() {
//        return this.id;
//    }

    /**
     * Метод для задания соответствующего БД id исходных кодов.
     * @param id Индекс исходных кодов в БД.
     */
//    void setId(long id) {
//        this.id = id;
//    }
}