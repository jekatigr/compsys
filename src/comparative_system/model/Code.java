package comparative_system.model;

/**
 * Класс, реализующий исходный код алгоритма.
 * @author Gromov Evg.
 */
public class Code {
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

    /**
     * Метод задает исходный код без изменений.
     * @param code Исходный код без изменений.
     */
    public void setSourceCode(String code) {
        this.sourceCode = code;
    }

    /**
     * Метод задает исходный код со вставленными счетчиками.
     * @param code Исходный код со вставленными счетчиками.
     */
    public void setGeneratedCode(String code) {
        this.generatedCode = code;
    }
}