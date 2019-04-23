package gui;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Класс с методами, позволяющими сохранять и загружать игру
 * @author Вика Помыкалова
 * @version 1.0
 * @see gui.MainApplicationFrame#saveState(javax.swing.JMenu)
 * @see gui.MainApplicationFrame#loadState(javax.swing.JMenu)
 */

public class SaveAndLoadGame {
    /**
     * Создает из параметров игры строку, которая используется как контейнер для хранения информации
     * @param currentGame текущее состояние игры
     * @return строковое представление текущего состояния игры
     * @see SaveAndLoadGame#getDataForSave(GameWindow)
     */
    private String makeDataForSave(GameWindow currentGame) {

        ArrayList<String> currentState = currentGame.getVisualizer().getGameState();

        String result = "";
        for (int i = 0; i < currentState.size(); i++) {
            result += currentState.get(i) + " ";
        }
        return result;
    }

    /**
     * Публичный метод получения строкового представления состояния игры, который позволяет
     * обращаться к приватному методу
     * @param currentGame текущее состояние игры
     * @return строковое представление текущего состояния игры
     * @see SaveAndLoadGame#makeDataForSave(GameWindow)
     */

    public String getDataForSave(GameWindow currentGame) {

        return makeDataForSave(currentGame);
    }

    /**
     * Сохраняет текущее состояние игры в файл
     * @param data строковый массив, первый элемент - состояние, второй элемент - имя файла для сохранения
     * @throws IOException может возникнуть ошибка при записи в файл
     * @see SaveAndLoadGame#saveData(String...)
     */

    private void saveDataInFile(String[] data) throws IOException {

        String state = data[0];
        String fileName = data[1];
        FileWriter fw = new FileWriter(fileName);
        fw.write(state);
        fw.flush();
        fw.close();
    }

    /**
     * Публичный метод для сохранения текущего состояния игры в файл,
     * который позволяет обращаться к приватному методу
     * @param data строковый массив произвольной длинны, по умолчанию - длины 2,
     *             1-ый элемент - строковое представление состояния игры,
     *             2-ой элемент - название файла для сохранения
     * @throws IOException может возникнуть ошибка при записи в файл
     * @see SaveAndLoadGame#saveDataInFile(String[])
     */

    public void saveData(String... data) throws IOException {

        saveDataInFile(data);
    }

    /**
     * Метод достаёт строковое представление состояния игры из файла
     * @param file файл загрузки
     * @return строковое представление состояния игры
     * @throws FileNotFoundException может возникнуть, если пользователь запросил
     * несуществующий файл с состоянием
     * @throws Exception может возникнуть при чтение поврежденного файла
     * @see SaveAndLoadGame#getDataFromSource(Object)
     */

    private String getDataFromFile(File file) throws FileNotFoundException, Exception {

        FileReader fw = new FileReader(file);
        Scanner scan = new Scanner(fw);

        String state = "";

        while (scan.hasNextLine()) {
            state += scan.nextLine();
        }

        return state;

    }

    /**
     * Публичный метод для того, чтобы доставать состояние игры из какого-то ресурса, в нашем случае - файла
     * @param source объект хранения состояния игры
     * @return строковое представление состояния игры
     * @throws FileNotFoundException может возникнуть, если пользователь запросил
     * несуществующий файл с состоянием
     * @throws Exception может возникнуть при чтение поврежденного файла
     * @see SaveAndLoadGame#getDataFromFile(File)
     */

    public String getDataFromSource(Object source) throws FileNotFoundException, Exception {

        return getDataFromFile((File) source);
    }

    /**
     * Устанавливает загруженное состояние игры
     * @param currentGame текущая игра
     * @param state параметры для загрузки нового состояния игры
     * @return true - если состояние было успешно згружено, false - в противном случае
     * @see SaveAndLoadGame#setLoadData(GameWindow, String)
     */

    private Boolean setGameState(GameWindow currentGame, String state) {

        String[] stateParse = state.split(" ");

        if (stateParse.length != 7) {
            return false;
        } else {
            ArrayList<String> stateInList = new ArrayList<>();
            for (int i = 0; i < stateParse.length - 2; i++) {
                stateInList.add(stateParse[i]);
            }

            try {
                currentGame.setSize(Integer.parseInt(stateParse[5]), Integer.parseInt(stateParse[6]));
                currentGame.getVisualizer().setPosition(stateInList);
            } catch (NumberFormatException e) {
                return false;
            }

            return true;
        }

    }

    /**
     * Публичный метод для загрузки состояния игры, который позволяет обращаться к приватному методу
     * @param currentGame текущая игра
     * @param state параметры для загрузки нового состояния игры
     * @return true - если состояние было успешно згружено, false - в противном случае
     * @see SaveAndLoadGame#setGameState(GameWindow, String)
     */

    public Boolean setLoadData(GameWindow currentGame, String state) {

        return setGameState(currentGame, state);
    }
}
