package comparative_system.model;

import com.almworks.sqlite4java.SQLiteConnection; 
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import comparative_system.Proccessor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;

/**
 * Класс, реализующий проект.
 * @author Gromov Evg.
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
    /** Индекс алгоритма, который в данный момент показывается в GUI. */
    private static int currentGuiAlg = -1;
    /** Индекс генератора, который в данный момент показывается в GUI. */
    private static int currentGuiGen = -1;

    /**
     * Конструктор класса. 
     * @param file Файл БД проекта.
     */
    public Project(File file) {
        this.file = file;
        this.name = file.getName();
        dg = new ArrayList<>();
        algorithms = new ArrayList<>();
    }

    /**
     * Метод для добавления к проекту алгоритма, загруженного из БД. 
     * @param alg Объект алгоритма.
     */
    private void addAlgorithm(Algorithm alg) {
        algorithms.add(alg);
    }

    /**
     * Метод для добавления нового алгоритма к проекту. Здесь же расставляются 
     * счетчики операций. Алгоритм сохраняется в БД и ему сопостовляется id. После этого 
     * классы алгоритма с вставленными счетчиками компилируются и подгружаются в основную программу.
     * @param alg Алгоритм.
     */
    public void addNewAlgorithm(Algorithm alg) { 
        algorithms.add(alg);
        this.saveAlgorithmInDB(this.algorithms.size() - 1);
        long alg_id = alg.getId();
        alg.setCodes(Proccessor.prepareCodes(Proccessor.putPackagesIfNotExist(alg_id, alg.getCodes()), alg.getMainMethod()));
        this.saveCodesOfAlgorithmInDB(this.algorithms.size() - 1);
        
        boolean err = false;
        for(Code c : alg.getCodes()) {
            if (!c.getSourceCodeErrors().isEmpty() || !c.getGeneratedCodeErrors().isEmpty()) {
                err = true;
            }
        }
        alg.setHasErrors(err);
    }
    
    /**
     * Метод для сохранения алгоритма. Здесь снова расставляются 
     * счетчики операций. Алгоритм сохраняется в БД по прежнему id. После этого 
     * классы алгоритма с вставленными счетчиками заново компилируются и подгружаются в основную программу.
     * @param index Индекс алгоритма в списке проекта.
     * @param name Имя алгоритма.
     * @param codes Исходные коды алгоритма.
     * @param mainMethod Метод вызова алгоритма.
     */
    public void saveAlgorithm(int index, String name, String mainMethod, ArrayList<Code> codes) { 
        algorithms.get(index).setName(name);
        algorithms.get(index).setMainMethod(mainMethod);
        long alg_id = algorithms.get(index).getId();
        codes = Proccessor.prepareCodes(Proccessor.putPackagesIfNotExist(alg_id, codes), mainMethod);
        algorithms.get(index).setCodes(codes);
        this.saveAlgorithmInDB(index);
        this.saveCodesOfAlgorithmInDB(index);
        
        boolean err = false;
        for(Code c : codes) {
            if (!c.getSourceCodeErrors().isEmpty() || !c.getGeneratedCodeErrors().isEmpty()) {
                err = true;
            }
        }
        algorithms.get(index).setHasErrors(err);
    }
    
    /**
     * Добавление к проекту генератора исходных данных, загруженного из БД. 
     * @param id Индекс генератора в БД.
     * @param name Имя генератора.
     * @param imports Импорты, добваленные пользователем.
     * @param generateImplementation Код генератора.
     */
    public void addDataGenerator(long id, String name, String imports, String generateImplementation) {
        dg.add(new DataGenerator(id, name, imports, generateImplementation));
    }
    
    /**
     * Добавление нового генератора исходных данных к проекту. Здесь же генератор
     * сохраняется в БД и ему сопостовляется id.
     * @param name Имя генератора.
     * @param imports Импорты, добваленные пользователем.
     * @param generateImplementation Код генератора.
     */
    public void addNewDataGenerator(String name, String imports, String generateImplementation) {
        dg.add(new DataGenerator(name, imports, generateImplementation));
        this.saveDataGeneratorInDB(this.dg.size() - 1);
    }

    /**
     * Метод для сохранения генератора. Генератор сохраняется в БД по прежнему id.
     * @param index Индекс генератора в списке проекта.
     * @param name Имя генератора.
     * @param imports Импорты, добваленные пользователем.
     * @param generateImplementation Код генератора.
     */
    public void saveDataGenerator(int index, String name, String imports, String generateImplementation) { 
        dg.get(index).setName(name);
        dg.get(index).setImports(imports);
        dg.get(index).setGenerateImplementation(generateImplementation);
        this.saveDataGeneratorInDB(index);
    }
    
    /**
     * Возвращает количество генераторов исходных данных в проекте.
     * @return Количество генераторов.
     */
    public int getCountOfDataGenerators() {
        return this.dg.size();
    }

    /**
     * Метод возвращает лист генераторов данных проекта.
     * @return Лист генераторов данных.
     */
    public ArrayList<DataGenerator> getDataGenerators() {
        return this.dg;
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
    private int getDataGeneratorIndex(long dataGeneratorId) {
        for (int i = 0; i < dg.size(); i++) {
            if (dg.get(i).getId() == dataGeneratorId) {
                return i;
            }
        }
        return -1;
    }
    
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
     * Метод возвращает лист всех алгоритмов проекта.
     * @return Лист алгоритмов.
     */
    public ArrayList<Algorithm> getAlgorithms() {
        return this.algorithms;
    }

    /**
     * Метод возвращает список пакетов для импорта, которые ссылаются на все алгоритмы проекта.
     * @return Список пакетов.
     */
    public HashMap getAllAlgotithmsAsImportsMap() {
        HashMap map = new HashMap();
        String res = "";
        for (Algorithm alg : algorithms) {
            for(Code c : alg.getCodes()) {
                String key = c.getClassName();
                String value = c.getPackageName();
                if (!map.containsValue(value)) {
                    map.put(key, value);
                }
            }
        } 
        return map;
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
     * Метод для удаления алгоритма из БД. 
     * @param index Индекс в списке, соответствующий алгоритму, 
     * который нужно удалить из БД.  
     */
    private void removeAlgorithmFromDB(int index) {
        try {
            //открываем базу данных проекта
            SQLiteConnection db = new SQLiteConnection(this.file);
            db.open();
            //--открываем базу данных проекта
            //сохраняем данные алгоритма
            Algorithm alg = algorithms.get(index);
            long alg_id = alg.getId();
            if (alg_id == -1) { 
                db.exec("DELETE * FROM algorithms WHERE id="+ alg_id);
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
     * Метод для сохранения генератора данных в БД. 
     * Здесь же ему сопоставляется id, если он еще не сохранялся в БД.
     * @param index Индекс в списке, соответствующий генератору данных, 
     * который нужно сохранить в БД.  
     */
    private void saveDataGeneratorInDB(int index) {
        try {
            //открываем базу данных проекта
            SQLiteConnection db = new SQLiteConnection(this.file);
            db.open();
            //--открываем базу данных проекта
            //сохраняем данные алгоритма
            DataGenerator gen = dg.get(index);
            long gen_id = gen.getId();
            if (gen_id == -1) { //сохраняем, как новый
                db.exec("INSERT INTO data_generators (name, imports, generate_implementation) "
                        + "VALUES ('"+ gen.getName() 
                        + "', '"+ gen.getImports() 
                        + "', '"+ gen.getGenerateImplementation() +"')");
                dg.get(index).setId(db.getLastInsertId());
            } else {//обновляем существующий
                db.exec("UPDATE data_generators SET name='" + gen.getName() + "', imports='"+ gen.getImports() +"', generate_implementation='"+ gen.getGenerateImplementation() +"' WHERE id="+ gen_id);
            }
            //--сохраняем данные алгоритма
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
    public static Task<Project> openProject(final File file) {
        Task<Project> t = new Task<Project>() {
            @Override
            protected Project call() throws Exception {
                try {
                    Project project = new Project(file);
                    //открываем базу данных проекта
                    this.updateTitle("Открытие проекта...");
                    this.updateMessage("Открытие базы данных проекта " + file.getName() + "...");
                    SQLiteConnection db = new SQLiteConnection(file);
                    db.open();
                    this.updateProgress(1, 4);
                    //--открываем базу данных проекта
                    //делаем запросы и сохраняем данные
                    //алгоритмы
                    this.updateTitle("Загрузка алгоритмов:");
                    SQLiteStatement st = db.prepare("SELECT * FROM algorithms");
                    while(st.step()) {
                        String alg_name = st.columnString(1);
                        this.updateMessage("Компиляция кодов \"" + alg_name + "\"");
                        long alg_id = st.columnLong(0);
                        //коды алгоритмов
                        ArrayList<Code> codes = new ArrayList<>();

                        //ArrayList<MethodDeclaration> methods = new ArrayList<>();

                        SQLiteStatement st2 = db.prepare("SELECT * FROM codes WHERE alg_id="+ alg_id +"");
                        while(st2.step()) {
                            String sc = st2.columnString(2);
                            codes.add(new Code(sc, st2.columnString(3), Proccessor.getPackage(sc), Proccessor.getClassName(sc)));

                            //methods.addAll(Proccessor.getAllMethodsFromCode(st2.columnString(2)));
                        }
                        codes = Proccessor.prepareCodes(codes, st.columnString(2));
                        //--коды алгоритмов        
                        Algorithm alg = new Algorithm(alg_id, alg_name, st.columnString(2), codes);
                        boolean err = false;
                        for(Code c : codes) {
                            if (!c.getSourceCodeErrors().isEmpty() || !c.getGeneratedCodeErrors().isEmpty()) {
                                err = true;
                            }
                        }
                        alg.setHasErrors(err);
                        project.addAlgorithm(alg);
                    }
                    this.updateProgress(2, 4);
                    //--алгоритмы
                    //параметры методов вызова
                    this.updateTitle("Открытие проекта...");
                    this.updateMessage("Загрузка генераторов данных...");
                    st = db.prepare("SELECT * FROM main_params");
                    while(st.step()) {
                        addMainMethodsParam(st.columnString(1), st.columnString(2));
                    }
                    //--параметры методов вызова
                    //генераторы исходных данных
                    st = db.prepare("SELECT * FROM data_generators");
                    while(st.step()) {
                        project.addDataGenerator(st.columnLong(0), st.columnString(1), st.columnString(2), st.columnString(3));
                    }
                    this.updateProgress(3, 4);
                    this.updateMessage("Генерация данных...");
                    ArrayList<DataGenerator> dg = project.getDataGenerators();
                    for (DataGenerator gen : dg) {
                        gen.generateData(project.getAllAlgotithmsAsImportsMap());
                    }
                    //--генераторы исходных данных
                    //исходные данные
//                    this.updateTitle("Загрузка исходных данных...");
//                    st = db.prepare("SELECT COUNT(name) FROM sqlite_master WHERE name='source_data'"); //проверяем таблицу на существование
//                    if (st.hasRow()) {
//                        st = db.prepare("IF EXISTS (SELECT * FROM source_data )"); //делаем запрос сразу на все исх данные, ибо тут обработка будет быстрее, чем если запрашивать отдельно для каждого генератора.
//                        ArrayList data_params = new ArrayList(); //список нетипизированных данных, соответствующих параметрам методов вызова алгоритмов.
//                        ArrayList<Data> data_items = new ArrayList<>();
//                        while(st.step()) {
//                            data_params.clear();
//                            for (int i = 2; i < DataGenerator.getCountOfMethodsParams(); i++) {
//                                data_params.add(st.columnValue(i));
//                            }
//                            data_items.add(new Data(st.columnLong(0), st.columnLong(1), data_params.toArray()));
//                        }
//                        project.addAllDataItems(data_items);
//                    }
                    this.updateProgress(4, 4);
                    //--исходные данные
                    //--делаем запросы и сохраняем данные
                    return project;
                } catch (SQLiteException ex) {
                    Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);//возможно, повреждена БД
                }
                return null;
            }
        };
        return t;
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
            db.exec("CREATE TABLE data_generators(id INTEGER PRIMARY KEY, name, imports, generate_implementation)");
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

    public void removeAlgorithm(int index) {
        algorithms.remove(index);
        removeAlgorithmFromDB(index);
    }

    public void removeDataGenerator(int index) {
        for(Algorithm alg : algorithms) {
            alg.removeResults(index);
        }
        dg.remove(index);
    }
}
