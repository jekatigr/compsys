package comparative_system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *  Статический класс данных программы.
 * @author TireX
 */
public class Preferences {
    /** Путь к файлу данных. */
    private static final String preferences_file_path = "preferences.dat";
    /** Путь к папке jdk. */
    private static String jdk_path = "";
    /** Количество потоков при подсчетах. */
    private static int threads_count = 4;
    /** Пути к файлам последних открытых проектов (4 максимум). Путь с индексом 0 открывался раньше всего. */
    private static ArrayList<String> projects_files = new ArrayList<>();
    private static String last_path_for_filechooser = "";


    /**
     * Загрузка данных программы из файла preferences.dat, который является json-файлом.
     * @return Возвращает {@code true} в случае успешной загрузки.
     */
    public static boolean loadPreferences() {
        try {
            //читаем данные из файла
            StringBuilder fileContent = new StringBuilder();
            File file = new File(preferences_file_path);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String text;
            while ((text = bufferedReader.readLine()) != null) {
                fileContent.append(text);
            }
            //--читаем данные из файла
            //сохраняем данные в переменные
            JSONParser parcer = new JSONParser();
            JSONObject data = (JSONObject)parcer.parse(fileContent.toString());

            jdk_path = String.valueOf(data.get("jdk_path"));
            threads_count = Integer.parseInt(String.valueOf(data.get("threads_count")));
            JSONArray paths = (JSONArray)data.get("projects_files");
            for(int i = 0; i < paths.size(); i++) {
                projects_files.add(String.valueOf(paths.get(i)));
            }
            last_path_for_filechooser = String.valueOf(data.get("last_path_for_filechooser"));
            //--сохраняем данные в переменные
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);//TODO: сделать нормальный вывод ошибок.
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Сохранение данных программы в файле preferences.dat в папке программы.
     * @return {@code true} в случае успешного сохранения.
     */
    public static boolean savePreferences() {
        FileWriter fw = null;
        try {
            //Сохраняем все данные в JSON-объект
            JSONObject data = new JSONObject();
            data.put("jdk_path", jdk_path);
            data.put("threads_count", threads_count);
            JSONArray projects_files_json = new JSONArray();
            for(int i = 0; i < projects_files.size(); i++) {
                projects_files_json.add(projects_files.get(i));
            }   data.put("projects_files", projects_files_json);
            data.put("last_path_for_filechooser", last_path_for_filechooser);
            //--Сохраняем все данные в JSON-объект
            //Перезаписываем файл данных, либо создаем новый
            File pref_file = new File(preferences_file_path);
            fw = new FileWriter(pref_file);
            fw.write(data.toJSONString());
            //--Перезаписываем файл данных, либо создаем новый
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    /**
     * Метод возвращает файл последнего открытого проекта.
     * @return Файл последнего открытого проекта, {@code null} в случае, когда проекта нет.
     */
    public static File getLastOpenedProject() {
        if (projects_files != null && projects_files.size() > 0) {
            File file = new File(projects_files.get(projects_files.size() - 1));
            if (file.isFile() && file.canRead() && file.exists()) {
                return file;
            } else {
                projects_files.remove(projects_files.size() - 1);
                return getLastOpenedProject();
            }
        }
        return null;
    }

    /**
     * Метод возвращает последний выбранный пользователем путь в окнах выбора или сохранения файлов.
     * @return Путь к каталогу.
     */
    public static String getLastPathForFileChooser() {
        return last_path_for_filechooser;
    }

    /**
     * Добавляет файл последнего открытого проекта к списку последних проектов. В случае, когда этот
     * проект уже есть в списке - он встает на последнее место.
     * @param file Файл проекта.
     */
    public static void addLastOpenedProject(File file) {
        String path = file.getAbsolutePath();
        if (projects_files.contains(path)) {
            projects_files.remove(path);
            projects_files.add(path);
        } else {
            projects_files.add(path);
            while (projects_files.size() >= 4) {
                projects_files.remove(0);
            }
        }
    }

    public static void updateLastPathForFileChooser(String path) {
        last_path_for_filechooser = path;
    }
}
