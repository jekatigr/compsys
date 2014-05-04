package comparative_system.model;

/**
 * Класс, реализующий исходный код алгоритма. Каждый объект - компилируемый класс.
 * @author Gromov Evg.
 */
public class Code {
    /** Исходный код. */
    private String sourceCode;
    /** Код со вставленными счетчиками. */
    private String generatedCode;
    /** Имя пакета для кода. */
    private String packageName;
    /** Имя класса. */
    private String className;
    /** Скомпилированный класс, который будет подгружаться в систему. */
    private Class generatedClass;
    /** true, если в данном классе находится метод вызова алгоритма. */
    private boolean hasMainMethod;
    
    private String sourceCodeErrors;
    private String generatedCodeErrors;
    
    /**
     * Конструктор класса.
     * @param sourceCode Исходный код.
     * @param packageName Имя пакета.
     * @param generatedCode Код со вставленными счетчиками.
     * @param className Имя класса.
     */
    public Code(String sourceCode, String generatedCode, String packageName, String className) {
        this.sourceCode = sourceCode;
        this.generatedCode = generatedCode;
        this.packageName = packageName;
        this.className = className;
        this.generatedClass = null;
        this.hasMainMethod = false;
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

    public void setPackageName(String name) {
        this.packageName = name;
    }
    
    public String getPackageName() {
        return this.packageName;
    }
    
    public void setClassName(String name) {
        this.className = name;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public void setGeneratedClass(Class c) {
        this.generatedClass = c;
    }
    
    public Class getGeneratedClass() {
        return this.generatedClass;
    }
    
    public void setSourceCodeErrors(String sourceCodeErrors) {
        this.sourceCodeErrors = sourceCodeErrors;
    }
    
    public String getSourceCodeErrors() {
        return this.sourceCodeErrors;
    }
    
    public void setGeneratedCodeErrors(String generatedCodeErrors) {
        this.generatedCodeErrors = generatedCodeErrors;
    }
    
    public String getGeneratedCodeErrors() {
        return this.generatedCodeErrors;
    }
    /**
     * Метод возвращает путь к файлу будущего класса в пакете для компиляции (без имени файла).
     * @return 
     */
    public String getPathOfFileForCompile() {
        return this.packageName.replaceAll("\\.", "/") + "/";
    }
    
    public void setHasMainMethod(boolean hasMainMethod) {
        this.hasMainMethod = hasMainMethod;
    }

    boolean getHasMainMethod() {
        return this.hasMainMethod;
    }
}








