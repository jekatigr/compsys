package comparative_system;

/**
 * Класс для подсчета операций в алгоритмах.
 * @author Gromov Evg.
 */
public class Counter {
    /** Количество операций. */
    private static long count_of_operations = 0;
    
    /**
     * Метод для добавления количества подсчитанных операций в список к соответствующему потоку.
     * @param count 
     */
    public static void add(long count) {
        count_of_operations += count;
    }
    
    /**
     * Метод для возврата результата по подсчету операций.
     * @return Количество операций.
     */
    public static long getCountResult() {
        return count_of_operations;
    }

    /**
     * Метод для сброса счетчика операций.
     */
    public static void resetCountResult() {
        count_of_operations = 0;
    }
}
