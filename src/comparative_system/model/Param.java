/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.model;

/**
 * Класс, реализующий параметр метода.
 * @author TireX
 */
public class Param {
    /** Тип параметра. */
    private String type;
    /** Имя параметра. */
    private String name;
    /**
     * Конструктор класса.
     * @param type Тип параметра.
     * @param name Имя параметра.
     */
    public Param(String type, String name) {
        this.type = type;
        this.name = name;
    }
    
    /**
     * Возвращает тип параметра.
     * @return Тип параметра.
     */
    public String getType() {
        return type;
    }
    
    /**
     * Возвращает имя параметра.
     * @return Имя параметра.
     */
    public String getName() {
        return name;
    }
}
