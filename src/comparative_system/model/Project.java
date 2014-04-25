/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.model;

import com.almworks.sqlite4java.SQLiteConnection; 
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import comparative_system.CompSys;
import comparative_system.Proccessor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;

/**
 *
 * @author TireX
 */
public class Project {

    static {
        Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.OFF); //отключаем логи sqlite.
    }

    /** Имя проекта. */
    String name;
    /** Файл проекта. */
    File file;
    /** Генераторы, включающие в себя наборы данных для анализа. **/
    ArrayList<DataGenerator> dg;
    /** Алгоритмы для анализа. */
    ArrayList<Algorithm> algorithms;
    /* Результаты анализа. Элементы в списке соотносятся с соответствующими алгоритмами в списке алгоритмов. */
    //ArrayList<Result> results;
    /** Индекс алгоритма, который в данный момент показывается в GUI. */
    private static int currentGuiAlg = -1;
    /** Индекс генератора, который в данный момент показывается в GUI. */
    private static int currentGuiGen = -1;

    public Project(File file) {
        this.file = file;
        this.name = file.getName();
        dg = new ArrayList<>();
        algorithms = new ArrayList<>();
        //results = new ArrayList();
    }

    /**
     * Метод для добавления алгоритма к проекту из БД. 
     * @param id Индекс алгоритма в БД проекта.
     * @param name Имя алгоритма.
     * @param mainMethod Метод вызова алгоритма.
     * @param counterName Имя счетчика операций.
     * @param codes Исходные и сгенерированные коды алгоритма.
     */
    private void addAlgorithm(int id, String name, String mainMethod, ArrayList<Code> codes, ArrayList<String> classesTabsNames, ArrayList<MethodDeclaration> methods) {
        algorithms.add(new Algorithm(id, name, mainMethod, codes, classesTabsNames, methods));
        Algorithm alg = this.algorithms.get(this.algorithms.size() - 1);
        alg.setFullNamesOfClassesList(Proccessor.getFullNamesOfClasses(alg.getId(), alg.getCodes()));
    }

    /**
     * Метод для добавления нового алгоритма к проекту. Здесь же расставляются 
     * счетчики операций. После этого алгоритм сохраняется в БД и ему сопостовляется id.
     * @param name Имя алгоритма.
     * @param codes Исходные коды алгоритма.
     * @param mainMethod Метод вызова алгоритма.
     */
    public void addAlgorithm(String name, String mainMethod, ArrayList<String> codes) { 
        algorithms.add(new Algorithm(name, mainMethod, Proccessor.putCounters(codes), Proccessor.getClassNames(codes), Proccessor.getAllMethodsFromCodes(codes)));
        this.saveAlgorithmInDB(this.algorithms.size() - 1);
        Algorithm alg = this.algorithms.get(this.algorithms.size() - 1);
        long alg_id = alg.getId();
        Proccessor.putPackagesIfNotExist(alg_id, alg.getCodes());
        alg.setFullNamesOfClassesList(Proccessor.getFullNamesOfClasses(alg_id, alg.getCodes()));
        this.saveCodesOfAlgorithmInDB(this.algorithms.size() - 1);
    }
    
    /**
     * Метод для сохранения алгоритма. Здесь снова расставляются 
     * счетчики операций. После этого алгоритм сохраняется в БД по прежнему id.
     * @param index Индекс алгоритма в списке проекта.
     * @param name Имя алгоритма.
     * @param codes Исходные коды алгоритма.
     * @param mainMethod Метод вызова алгоритма.
     */
    public void saveAlgorithm(int index, String name, String mainMethod, ArrayList<String> codes) { 
        algorithms.get(index).setName(name);
        algorithms.get(index).setMainMethod(mainMethod);
        long alg_id = algorithms.get(index).getId();
        ArrayList<Code> codesG = Proccessor.putCounters(codes);
        algorithms.get(index).setCodes(Proccessor.putPackagesIfNotExist(alg_id, codesG));
        algorithms.get(index).setFullNamesOfClassesList(Proccessor.getFullNamesOfClasses(alg_id, codesG));
        algorithms.get(index).setClassNamesListList(Proccessor.getClassNames(codes));
        algorithms.get(index).setMethodsList(Proccessor.getAllMethodsFromCodes(codes));
        this.saveAlgorithmInDB(index);
        this.saveCodesOfAlgorithmInDB(index);
    }
    
    /**
     * Добавление нового генератора исходных данных к проекту.
     * @param dg Генератор исходных данных.
     */
    public void addDataGenerator(DataGenerator dg) {//TODO: 
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Возвращает количество генераторов исходных данных в проекте.
     * @return Количество генераторов.
     */
    public int getCountOfDataGenerators() {
        return this.dg.size();
    }

    /**
     * Возвращает генератор исходных данных с индексом, равным {@code gen_id}.
     * @param index Индекс геренатора.
     * @return Генератор исходных данных.
     */
    public DataGenerator getDataGenerator(int index) {
        return dg.get(index);
    }

    /**
     * Добавление набора исходных данных к соответствующим генераторам.
     * @param data_items Лист исходных данных.
     */
    private void addAllDataItems(ArrayList<Data> data_items) {
        for(Data d : data_items) {
            int gen_id = getDataGeneratorIndex(d.getDataGeneratorDBId());
            if (gen_id != -1 && this.getDataGenerator(gen_id) != null) {
                this.getDataGenerator(gen_id).addData(d);
            }
        }
    }

    /**
     * Метод возвращает индекс генератора в списке генераторов по id в БД.
     * @param dataGeneratorId id генератора в БД.
     * @return Индекс генератора в списке в проекте, -1, если генератор не найден.
     */
    private int getDataGeneratorIndex(int dataGeneratorId) {
        for (int i = 0; i < dg.size(); i++) {
            if (dg.get(i).getId() == dataGeneratorId) {
                return i;
            }
        }
        return -1;
    }

    public String getAllAlgotithmsAsImports() {
        String res = "";
        for (Algorithm alg : algorithms) {
            for(Name imp : alg.getFullNamesOfClassesList()) {
                res += "import " + imp.toString() + ";\n";
            }
        } 
        return res + " \n";
    }
    
    /**
     * Добавление набора исходных данных к одному из генераторов проекта по id из БД.
     * @param gen_id Индекс генератора в БД.
     * @param d Набор исходных ранных.
     *
    private void a-ddData(int gen_id, Data d) {
        if (this.getDataGenerator(gen_id) != null) {
            this.getDataGenerator(gen_id).addData(d);
        }
    }*/

    /**
     * Метод возвращает количество алгоритмов в проекте.
     * @return Количество алгоритмов.
     */
    public int getCountOfAlgorithms() {
        return algorithms.size();
    }
    
    /**
     * Метод возвращает алгоритм по индексу.
     * @param index Индекст алгоритма в листе.
     * @return Алгоритм типа {@code Algorithm}, {@code null} когда такого алгоритма нет.
     */
    public Algorithm getAlgorithm(int index) {
        if (algorithms != null && index >= 0 && index < algorithms.size())
            return algorithms.get(index);
        return null;
    }
    
    /**
     * Метод для сохранения алгоритма в БД. 
     * Здесь же ему сопоставляется id, если он еще не сохранялся в БД.
     * @param index Индекс в списке, соответствующий алгоритму, 
     * который нужно сохранить в БД.  
     */
    private void saveAlgorithmInDB(int index) {
        try {
            //открываем базу данных проекта
            SQLiteConnection db = new SQLiteConnection(this.file);
            db.open();
            //--открываем базу данных проекта
            //сохраняем данные алгоритма
            Algorithm alg = algorithms.get(index);
            long alg_id = alg.getId();
            if (alg_id == -1) { //сохраняем, как новый
                db.exec("INSERT INTO algorithms (name, main) "
                        + "VALUES ('"+ alg.getName() 
                        + "', '"+ alg.getMainMethod() +"')");
                algorithms.get(index).setId(db.getLastInsertId());
            } else {//обновляем существующий
                db.exec("UPDATE algorithms SET name='" + alg.getName() + "', main='"+ alg.getMainMethod() +"' WHERE id="+ alg_id);
            }
            //--сохраняем данные алгоритма
        } catch (SQLiteException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Метод для сохранения кодов алгоритма в БД. 
     * @param index Индекс в списке, соответствующий алгоритму, 
     * исходные коды которого нужно сохранить в БД.  
     */
    private void saveCodesOfAlgorithmInDB(int index) {
        try {
            //открываем базу данных проекта
            SQLiteConnection db = new SQLiteConnection(this.file);
            db.open();
            //--открываем базу данных проекта
            Algorithm alg = algorithms.get(index);
            long alg_id = alg.getId();
            //сохраняем коды
            db.exec("DELETE FROM codes WHERE alg_id="+ alg_id);
            ArrayList<Code> codes = alg.getCodes();
            for (Code code : codes) {
                db.exec("INSERT INTO codes (alg_id, sourse_code, generated_code) "
                        + "VALUES ("+ alg_id
                        + ", '" + code.getSourceCode() + "', '" + code.getGeneratedCode() + "')");
            }
            //--сохраняем коды 
        } catch (SQLiteException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Метод возвращает путь к файлу проекта.
     * @return Путь к файлу проекта.
     */
    public String getProjectFilePath() {
        return this.file.toString();
    }
    
    
    /**
     * Метод возвращает индекс алгоритма, который показывается в GUI в данный момент.
     * @return Индекс алгоритма в списке проекта.
     */
    public static int getCurrentGuiAlg() {
        return currentGuiAlg;
    }
    
    /**
     * Метод для сохранения индекса алгоритма, который в данный момент показывается в GUI.
     * @param index Индекс алгоритма.
     */
    public static void setCurrentGuiAlg(int index) {
        currentGuiAlg = index;
    }
    
    /**
     * Метод возвращает индекс генератора данных, который показывается в GUI в данный момент.
     * @return Индекс генератора в списке проекта.
     */
    public static int getCurrentGuiGen() {
        return currentGuiGen;
    }
    
    /**
     * Метод для сохранения индекса генератора данных, который в данный момент показывается в GUI.
     * @param index Индекс генератора данных.
     */
    public static void setCurrentGuiGen(int index) {
        currentGuiGen = index;
    }
    
    /**
     * Метод загружает проект из файла.
     * @param file Файл проекта.
     * @return Проект, как объект типа {@link comparative_system.model.Project Project.}
     */
    public static Project openProject(File file) {
        try {
            Project project = new Project(file);
            //открываем базу данных проекта
            SQLiteConnection db = new SQLiteConnection(file);
            db.open();
            //--открываем базу данных проекта
            //делаем запросы и сохраняем данные
            //алгоритмы
            SQLiteStatement st = db.prepare("SELECT * FROM algorithms");
            while(st.step()) {
                int alg_id = st.columnInt(0);
                //коды алгоритмов
                ArrayList<Code> codes = new ArrayList<>();
                ArrayList<String> classesTabNames = new ArrayList<>();
                ArrayList<MethodDeclaration> methods = new ArrayList<>();
                SQLiteStatement st2 = db.prepare("SELECT * FROM codes WHERE alg_id="+ alg_id +"");
                while(st2.step()) {
                    codes.add(new Code(st2.columnInt(0), st2.columnString(2), st2.columnString(3)));
                    classesTabNames.add(Proccessor.getClassName(st2.columnString(2)));
                    methods.addAll(Proccessor.getAllMethodsFromCode(st2.columnString(2)));
                }
                //--коды алгоритмов        
                project.addAlgorithm(alg_id, st.columnString(1), st.columnString(2), codes, classesTabNames, methods);
            }
            //--алгоритмы
            //параметры методов вызова
            st = db.prepare("SELECT * FROM main_params");
            while(st.step()) {
                addMainMethodsParam(st.columnString(1), st.columnString(2));
            }
            //--параметры методов вызова
            //генераторы исходных данных
            st = db.prepare("SELECT * FROM source_gen");
            while(st.step()) {
                project.addDataGenerator(new DataGenerator(st.columnInt(0), st.columnString(1), st.columnString(2)));
            }
            //--генераторы исходных данных
            //исходные данные
            st = db.prepare("SELECT COUNT(name) FROM sqlite_master WHERE name='source_data'"); //проверяем таблицу на существование
            if (st.hasRow()) {
                st = db.prepare("IF EXISTS (SELECT * FROM source_data )"); //делаем запрос сразу на все исх данные, ибо тут обработка будет быстрее, чем если запрашивать отдельно для каждого генератора.
                ArrayList data_params = new ArrayList(); //список нетипизированных данных, соответствующих параметрам методов вызова алгоритмов.
                ArrayList<Data> data_items = new ArrayList<>();
                while(st.step()) {
                    data_params.clear();
                    for (int i = 2; i < DataGenerator.getCountOfMethodsParams(); i++) {
                        data_params.add(st.columnValue(i));
                    }
                    data_items.add(new Data(st.columnInt(0), st.columnInt(1), data_params.toArray()));
                }
                project.addAllDataItems(data_items);
            }
            //--исходные данные
            //--делаем запросы и сохраняем данные
            return project;
        } catch (SQLiteException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);//возможно, повреждена БД
        }
        return null;
    }

    /**
     * Метод создает новый проект. Если переданный в метод файл уже существует, то он перезаписывается.
     * @param file Файл проекта для сохранения.
     */
    public static void createNewProject(File file) {
        try {
            //проверяем существование файла, если надо - очищаем
            if (file.exists() && file.canWrite())
            {
                try {
                    BufferedWriter bf = new BufferedWriter(new FileWriter(file));
                    bf.write("");
                    bf.close();
                } catch (IOException ex) {
                    Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //--проверяем существование файла, если надо - очищаем
            //создаем базу данных проекта
            SQLiteConnection db = new SQLiteConnection(file);
            db.open();
            //--создаем базу данных проекта
            //создаем структуру БД
            db.exec("CREATE TABLE algorithms(id INTEGER PRIMARY KEY, name, main)");
            db.exec("CREATE TABLE codes(id INTEGER PRIMARY KEY, alg_id, sourse_code, generated_code)");
            db.exec("CREATE TABLE source_gen(id INTEGER PRIMARY KEY, name, code)");
            db.exec("CREATE TABLE results(id INTEGER PRIMARY KEY, alg_id, data_id, operations)");
            db.exec("CREATE TABLE exceptions(id INTEGER PRIMARY KEY, exceptions)");
            db.exec("CREATE TABLE main_params(id INTEGER PRIMARY KEY, type, name)");
            //--создаем структуру БД
            db.dispose();
        } catch (SQLiteException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  

    /**
     * Метод добавляет очередной параметр в общий список параметров методов вызова алгоритмов в классе {@link comparative_system.model.DataGenerator DataGenerator.}
     * @param param Параметр метода.
     */
    private static void addMainMethodsParam(String type, String name) {
        DataGenerator.addMainMethodsParam(type, name);
    }

    /**
     * Проверка того, что параметры выбранного в выпадающем 
     * списке метода совпадают по порядку и по типам с уже сохраненными
     * параметрыми в {@code DataGenerator}. 
     * В случае, когда параметры еще не заданы - сохраняются параметры выбранного в данный момент метода.
     * @param parameters Лист параметров метода.
     * @return {@code true}, когда параметры совпадают или были только что сохранены,
     * {@code false}, когда параметры не совпадают.
     */
    public static boolean methodParamsAreCompatible(List parameters) {
        if (DataGenerator.getCountOfMethodsParams() == 0) {
            DataGenerator.saveNewMainMethodParams(parameters);
            return true;
        } else {//сравниваем параметры метода по типу с уже сохраненными
            return DataGenerator.compareListOfParams(parameters);
        }
    }
}
