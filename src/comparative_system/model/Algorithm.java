package comparative_system.model;

import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;

/**
 * Класс, реализующий алгоритм.
 * @author Gromov Evg.
 */
public class Algorithm {
    /** Индекс алгоритма в БД. */
    private long id;
    /** Имя алгоритма. */
    private String name;
    /** Метод вызова алгоритма. */
    private String mainMethod;
    /** Исходные и сгенерированные коды алгоритма. */
    private ArrayList<Code> codes;
    boolean hasErrors;
    /** Список пар результатов: "id генератора данных" => "лист результатов". */
    private HashMap<Long, ArrayList<Result>> results;
   
    /** Флаг для показа счетчиков в коде для GUI. */
    private boolean showCounters = false;
    
    
    /**
     * Конструктор класса.
     * @param name Имя алгоритма.
     * @param mainMethod Метод вызова алгоритма.
     * @param codes Исходные и сгенерированные коды алгоритма.
     */
    public Algorithm(String name, String mainMethod, ArrayList<Code> codes) {
        this(-1, name, mainMethod, codes);
    }
    
    /**
     * Конструктор класса.
     * @param id Индекс алгоритма в БД.
     * @param name Имя алгоритма.
     * @param mainMethod Метод вызова алгоритма.
     * @param codes Исходные и сгенерированные коды алгоритма.
     */
    public Algorithm(long id, String name, String mainMethod, ArrayList<Code> codes) {
        this.id = id;
        this.name = name;
        this.mainMethod = mainMethod;
        this.codes = codes;
        this.hasErrors = false;
        this.results = new HashMap();
    }

    /**
     * Метод для задания id алгоритма в БД.
     * @param id Индекс алгоритма в БД.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Метод возвращает имя алгоритма.
     * @return Имя алгоритма.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Метод сохраняет имя алгоритма.
     * @param name Новое имя.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Метод возвращает метод вызова алгоритма.
     * @return Метод вызова алгоритма.
     */
    public String getMainMethod() {
        return this.mainMethod;
    }
    
    /**
     * Метод задает имя метода вызова алгоритма.
     * @param mainMethod Имя метода вызова алгоритма.
     */
    public void setMainMethod(String mainMethod) {
        this.mainMethod = mainMethod;
    }

    /**
     * Метод возвращает id алгоритма в БД.
     * @return id алгоритма в БД.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Метод возвращает коды алгоритма.
     * @return Коды алгоритма.
     */
    public ArrayList<Code> getCodes() {
        return this.codes;
    }
    
    /**
     * Метод сохраняет коды алгоритма.
     * @param codes Коды для сохранения.
     */
    public void setCodes(ArrayList<Code> codes) {
        this.codes = codes;
    }
    
    /**
     * Метод возвращает флаг для интерфейса.
     * @return {@code true}, если счетчики показаны, иначе {@code false}.
     */
    public boolean getShowCounters() {
        return this.showCounters;
    }
    
    /**
     * Метод задает флаг для интерфейса.
     * @param show {@code true} для показа счетчиков, иначе {@code false}.
     */
    public void setShowCounters(boolean show) {
        this.showCounters = show;
    }

    public Class getClassWithMainMethod() {
        for (Code c : codes) {
            if (c.getHasMainMethod()) {
                return c.getGeneratedClass();
            }
        }
        return null;
    }

    void setHasErrors(boolean err) {
        this.hasErrors = err;
    }

    public boolean hasErrors() {
        return this.hasErrors;
    }
    
    public void setResults(long gen_index, ArrayList<Result> values) {
        this.results.put(gen_index, Result.calculateMedians(values));
    }
    
    public ArrayList<Result> getResults(long gen_index) {
        return this.results.get(gen_index);
    }
    
    public void removeResults(long gen_index) {
        if (this.results.containsKey(gen_index)) {
            this.results.remove(gen_index);
        }
    }
}










