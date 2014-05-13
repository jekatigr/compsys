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
    /** Описание ошибок в исходном коде. */
    private String sourceCodeErrors;
    /** Описание ошибок в сгенерированном коде. */
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

    /**
     * Метод для задания пакета к этому коду.
     * @param name Имя пакета.
     */
    public void setPackageName(String name) {
        this.packageName = name;
    }
    
    /**
     * Метод возвращает имя пакета этого кода.
     * @return Имя пакета.
     */
    public String getPackageName() {
        return this.packageName;
    }
    
    /**
     * Метод для задания имени класса данного кода.
     * @param name Имя класса.
     */
    public void setClassName(String name) {
        this.className = name;
    }
    
    /**
     * Метод возвращает имя класса данного кода.
     * @return 
     */
    public String getClassName() {
        return this.className;
    }
    
    /**
     * Метод для задания класса с вставленными счетчиками.
     * @param c Скомпилированный класс.
     */
    public void setGeneratedClass(Class c) {
        this.generatedClass = c;
    }
    
    /**
     * Метод возвращает класс этого кода с вставленными счетчиками.
     * @return Скомпилированный класс.
     */
    public Class getGeneratedClass() {
        return this.generatedClass;
    }
    
    /**
     * Метод для задания описания ошибок в исходном коде.
     * @param sourceCodeErrors Описание ошибок в исходном коде.
     */
    public void setSourceCodeErrors(String sourceCodeErrors) {
        this.sourceCodeErrors = sourceCodeErrors;
    }
    
    /**
     * Метод возвращает описание ошибок в исходном коде.
     * @return Описание ошибок в исходном коде.
     */
    public String getSourceCodeErrors() {
        return this.sourceCodeErrors;
    }
    
    /**
     * етод для задания описания ошибок в сгенерированном коде.
     * @param generatedCodeErrors Описание ошибок в сгенерированном коде
     */
    public void setGeneratedCodeErrors(String generatedCodeErrors) {
        this.generatedCodeErrors = generatedCodeErrors;
    }
    
    /**
     * Метод возвращает описание ошибок в сгенерированном коде.
     * @return Описание ошибок в сгенерированном коде.
     */
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
    
    /**
     * Метод задает флаг, есть ли метод вызова алгоритма в данном коде.
     * @param hasMainMethod Флаг, есть ли главный метод в данном коде.
     */
    public void setHasMainMethod(boolean hasMainMethod) {
        this.hasMainMethod = hasMainMethod;
    }

    /**
     * Метод возвращает флаг, есть ли главный метод в данном коде.
     * @return Флаг, есть ли главный метод в данном коде.
     */
    boolean getHasMainMethod() {
        return this.hasMainMethod;
    }
}








