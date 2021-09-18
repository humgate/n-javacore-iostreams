import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    static final String INSTALL_DIR = "D:\\JProjects\\netology\\javacore\\Games";
    static final String FOLDER_CR_MSG = "Создан каталог ";
    static final String FILE_CR_MSG = "Создан файл ";
    // лог
    static StringBuilder log = new StringBuilder();
    //форматтер для таймстемпа в логе
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:ss");

    public static void main(String[] args) {
        /*
         * Задача1. Инсталляция - создание каталогов и файлов.
         */

        // src
        createFolder(new File(INSTALL_DIR + "\\src"), FOLDER_CR_MSG);
        createFolder(new File(INSTALL_DIR + "\\src\\main"), FOLDER_CR_MSG);
        createFile(new File(INSTALL_DIR + "\\src\\main\\Main.java"), FILE_CR_MSG);
        createFile(new File(INSTALL_DIR + "\\src\\main\\Utils.java"), FILE_CR_MSG);
        createFolder(new File(INSTALL_DIR + "\\src\\test"), FOLDER_CR_MSG);

        // res
        createFolder(new File(INSTALL_DIR+"\\res"), FOLDER_CR_MSG);
        createFolder(new File(INSTALL_DIR+"\\res\\drawables"), FOLDER_CR_MSG);
        createFolder(new File(INSTALL_DIR+"\\res\\vectors"), FOLDER_CR_MSG);
        createFolder(new File(INSTALL_DIR+"\\res\\icons"), FOLDER_CR_MSG);

        // savegames
        createFolder(new File(INSTALL_DIR+"\\savegames"), FOLDER_CR_MSG);

        // temp
        createFolder(new File(INSTALL_DIR + "\\temp"), FOLDER_CR_MSG);
        File temp = new File(INSTALL_DIR + "\\temp\\temp.txt");
        createFile(temp, FILE_CR_MSG);
        writeLogFile(temp);

        /*
         * Задача 2. Сохранение - создание трех снапшотов игры и сохранение их на диск
         */
        GameProgress game1 = new GameProgress(100, 2, 1, 34.51d);
        saveGame(INSTALL_DIR + "\\savegames\\game1.dat", game1);
        List<String> savedFiles = new ArrayList<>(); //список созданных фалов сохранений
        savedFiles.add(INSTALL_DIR + "\\savegames\\game1.dat");

        GameProgress game2 = new GameProgress(90, 5, 2, 734.53d);
        saveGame(INSTALL_DIR + "\\savegames\\game2.dat", game2);
        savedFiles.add(INSTALL_DIR + "\\savegames\\game2.dat");

        GameProgress game3 = new GameProgress(97, 6, 4, 2434.42d);
        saveGame(INSTALL_DIR + "\\savegames\\game3.dat", game3);
        savedFiles.add(INSTALL_DIR + "\\savegames\\game3.dat");

        //сбрасываем лог в файл
        writeLogFile(temp);

        //архивируем сохраненные игры
        zipFiles(INSTALL_DIR + "\\savegames\\gamesaves.zip", savedFiles);

        //сбрасываем лог в файл
        writeLogFile(temp);

        //удаляем неархивированные
        deleteUnarchivedFiles(savedFiles);

        //сбрасываем лог в файл
        writeLogFile(temp);

        /*
         * Задача 3. Загрузка игр
         */

        //разархивируем файлы сохранения игры из архива
        openZip(INSTALL_DIR + "\\savegames\\gamesaves.zip", INSTALL_DIR + "\\savegames");

        //сбрасываем лог в файл
        writeLogFile(temp);

        //десериализуем объект игры из файла INSTALL_DIR + "\\savegames\\game3.dat" и выводим на экран
        System.out.println(openProgress(INSTALL_DIR + "\\savegames\\game3.dat"));

        //сбрасываем лог в файл
        writeLogFile(temp);

    }

    /**
     * Создает подкаталог и если успешно записывает мессадж в лог
     *
     * @param dir    - каталог для создания
     * @param logMsg - текст мессаджа при успешном создании
     * @return true если каталог создался, иначе false
     */
    public static boolean createFolder(File dir, String logMsg) {
        if (dir.mkdir()) {
            log.append("\n" +
                    LocalDateTime.now().format(formatter) + " " +
                    logMsg + " " + dir.getPath());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Создает пустой файл
     *
     * @param file   полный путь до файла
     * @param logMsg текст мессаджа при успешном создании
     * @return true если файл создался, иначе false
     */
    public static boolean createFile(File file, String logMsg) {
        try {
            if (file.createNewFile()) {
                log.append("\n" +
                        LocalDateTime.now().format(formatter) + " " +
                        logMsg + " " + file.getPath());
                return true;
            } else {
                return false;
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            log.append("\n" +
                    LocalDateTime.now().format(formatter) + " " +
                    " Ошибка при создании файла " + file.getPath() + " " +
                    ex.getMessage());
            return false;
        }
    }

    /**
     * Пишет лог в файл. После успешной записи очищает log.
     *
     * @param logFile - файл дла записи лога
     */
    public static void writeLogFile(File logFile) {
        try (FileWriter writer = new FileWriter(logFile.getPath(),  true)) {
            writer.write(log.toString());
            writer.flush();
            log.delete(0,log.length());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Сохраняет игру на диск
     *
     * @param path полный путь к файлу сохранения
     * @param gameProgress состояние игры для сохранения
     */
    public static void saveGame(String path, GameProgress gameProgress) {
        // откроем выходной поток для записи в файл
        try (FileOutputStream fos = new FileOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // запишем экземпляр класса в файл
            oos.writeObject(gameProgress);
            //залогируем
            log.append("\n"+
                    LocalDateTime.now().format(formatter) + " " +
                    "Игра с hash: " + gameProgress.hashCode() +
                    " успешно сохранена в " + path);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            log.append("\n"+
                    LocalDateTime.now().format(formatter) + " " +
                    "Ошибка сохранения игры с hash: " + gameProgress.hashCode() +
                    ". " + ex.getMessage());
        }
    }

    /**
     * Переносит список файлов в из файловой системы в архив
     *
     * @param archivePath - zip архив
     * @param fileList    - списко файлыор для переноса
     */
    public static void zipFiles(String archivePath, List<String> fileList) {
        //поток вывода в Zip
        ZipOutputStream zout = null;

        //список потоков чтения из файлов, нужен чтобы в блоке finally их все закрыть
        Deque<FileInputStream> fisList = new LinkedList<>();

        // используем обычный try без ресурсов, закроем все в finally
        try {
            zout = new ZipOutputStream(new FileOutputStream(archivePath));

            for (String file : fileList) {
                fisList.add(new FileInputStream(file));
                ZipEntry entry = new ZipEntry(file);
                zout.putNextEntry(entry);

                // считываем содержимое файла в массив byte
                byte[] buffer = new byte[fisList.peekLast().available()];
                fisList.peekLast().read(buffer);

                // добавляем содержимое к архиву
                zout.write(buffer);

                // закрываем текущую запись для новой записи
                zout.closeEntry();

                // логгируем
                log.append("\n" +
                        LocalDateTime.now().format(formatter) + " " +
                        "Файл " + file + " добавлен в архив " + archivePath);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            //закрываем все открытые потоки чтения
            try {
                for (FileInputStream fis : fisList) {
                    if (fis != null) fis.close();
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                log.append("\n" +
                        LocalDateTime.now().format(formatter) +
                        " Ошибка при попытке закрыть поток(и) чтения из файлов сохранения игры" +
                        ex.getMessage());
            }

            //закрываем поток вывода в zip
            try {
                if (zout != null) zout.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                log.append("\n" +
                        LocalDateTime.now().format(formatter) +
                        " Ошибка при попытке закрыть поток выводв zip архив" +
                        ex.getMessage());
            }
        }
    }

    /**
     * Удаляет с диска файлы из списка
     *
     * @param fileList - список файлов (полныцх путей)_
     */
    public static void deleteUnarchivedFiles(List<String> fileList) {
        for (String file : fileList) {
            if (new File(file).delete()) {
                log.append("\n" +
                        LocalDateTime.now().format(formatter) + " " +
                        "Файл " + file + " удален");
            } else {
                log.append("\n" +
                        LocalDateTime.now().format(formatter) + " " +
                        "Не удалось удалить файл " + file);
            }
        }
    }

    /**
     * Разархивирует zip в указанный фолдер. Извлекает из архива только файлы, игнорируя сохраненный
     * в архиве путь к файлам
     *
     * @param zipFile   архив
     * @param targetDir фолдер куда будут разархивированы файлы
     */
    public static void openZip(String zipFile, String targetDir) {
        try (ZipInputStream zin = new ZipInputStream(
                new FileInputStream(zipFile))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                // получим название файла
                name = entry.getName();
                //отбросим записанный путь к файлу и сохраним тольк оимя файла
                String [] temp = name.split("\\\\");
                name = temp[temp.length-1];
                //составим полное имя файла начинающееся с targetDir
                name = targetDir + "\\" + name;

                // распаковка
                FileOutputStream fout = new FileOutputStream(name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                log.append("\n" +
                        LocalDateTime.now().format(formatter) + " " +
                        "Файл " + name + " восстановлен из архива");
                zin.closeEntry();
                fout.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Дсериализует объект состояния игры из сохраненного на диске файла игра
     * @param savedGameFile полный путь к файлу игры
     * @return объект состояния игры
     */
    public static GameProgress openProgress(String savedGameFile) {
        GameProgress gameProgress = null;
        // откроем входной поток для чтения файла
        try (FileInputStream fis = new FileInputStream(savedGameFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // десериализуем объект и скастим его в класс
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            log.append("\n" +
                    LocalDateTime.now().format(formatter) + " " +
                    "Ошибка при чтении игры из файла " + savedGameFile);
        }
        log.append("\n" +
                LocalDateTime.now().format(formatter) + " " +
                "Игра с hash " + gameProgress.hashCode() +
                "загружена из файла сохранения " + savedGameFile);
        return gameProgress;
    }
}
