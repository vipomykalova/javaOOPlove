package Tests;

import com.sun.org.apache.xpath.internal.operations.Bool;
import gui.GameWindow;
import gui.SaveAndLoadGame;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты на сохранение и загрузку состояния игры
 * @author Виктория Помыкалова, Кононский Павел
 * @version 2.0
 * @see gui.GameVisualizer
 * @see gui.SaveAndLoadGame
 */

public class SaveLoadTests {
    /** Экземпляр игрового поля  */
    private GameWindow game = new GameWindow();
    /** Менеджер загрузки и сохранения*/
    private SaveAndLoadGame saveLoadManager = new SaveAndLoadGame();

    /**
     * Проверка корректного сохранения и загрузки состояния игры
     */
    @Test
    void saveAndLoadNormalStateTest() {

        game.getVisualizer().stopTimer();
        ArrayList<String> currentStateForCheck = game.getVisualizer().getGameState();
        String savedState = saveLoadManager.getDataForSave(game); //сохранили текущее состояние

        ArrayList<String> newState = new ArrayList<>();
        newState.add("150");
        newState.add("150");
        newState.add("0");
        newState.add("200");
        newState.add("150");

        game.getVisualizer().setPosition(newState); //поменяли состояние
        saveLoadManager.setLoadData(game, savedState); //вернули прежнее

        assertEquals(game.getVisualizer().getGameState(), currentStateForCheck); //проверили, что всё поменялось
    }

    /**
     * Проверка на то, что программа корректно обработает поврежденные данные для загрузки
     */
    @Test
    void saveAndLoadCorruptedStateTest() {

        game.getVisualizer().stopTimer();
        String savedState = saveLoadManager.getDataForSave(game); //сохранили текущее состояние
        String[] stateToCorrupt = savedState.split(" ");
        stateToCorrupt[0] = "a"; //повредили данные
        String corruptedState = String.join(" ", stateToCorrupt);
        boolean loadResult = saveLoadManager.setLoadData(game, corruptedState); //пытаемся загрузить
        assertFalse(loadResult); //Проверяем что модуль отказался загружать поврежденные данные
    }

    /**
     * Проверка на то, что программа корректно обработает утечку данных для загрузки
     */
    @Test
    void saveAndLoadNotEnoughDataStateTest() {

        game.getVisualizer().stopTimer();
        String savedState = saveLoadManager.getDataForSave(game); //сохранили текущее состояние
        String[] state = savedState.split(" ");
        String stateWithoutSomeData = "";
        for (int i = 0; i < state.length - 1; i++) { //симулируем потерю данных
            stateWithoutSomeData += state[i];
        }
        boolean loadResult = saveLoadManager.setLoadData(game, stateWithoutSomeData); // пытаемся загрузить
        assertFalse(loadResult); 
    }

}
