package comparative_system;

import comparative_system.model.Data;
import comparative_system.model.Result;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Класс для многопоточного проведения вычислений трудоекмкости.
 * @author Gromov Evg.
 */
public class Tester implements Callable {
    
    ArrayList<Data> list;
    
    public Tester(ArrayList<Data> arr) {
        list = arr;
    }

    @Override
    public ArrayList<Result> call() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
