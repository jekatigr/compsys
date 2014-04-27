package comparative_system.model;

/**
 * Класс, реализующий параметр метода.
 * @author Gromov Evg.
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
