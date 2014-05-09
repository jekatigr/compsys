package comparative_system.model;

/**
 * Класс, реализующий результат вычислений трудоемкости для алгоритма.
 * @author Gromov Evg.
 */
public class Result {
    /** index набора данных в проекте. */
    long data_index;
    /** Количество операций. */
    long count;
    
    /**
     * Конструктор класса.
     * @param data_index index набора данных в проекте.
     * @param count Количество операций.
     */
    public Result(long data_index, long count) {
        this.data_index = data_index;
        this.count = count;
    }
}
