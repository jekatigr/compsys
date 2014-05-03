package comparative_system.model;

/**
 * Класс, реализующий результат вычислений трудоемкости для алгоритма.
 * @author Gromov Evg.
 */
public class Result {
    /** id набора данных в БД проекта. */
    long data_id;
    /** Количество операций. */
    int count;
    
    /**
     * Конструктор класса.
     * @param data_id id набора данных.
     * @param count Количество операций.
     */
    public Result(long data_id, int count) {
        this.data_id = data_id;
        this.count = count;
    }
}
