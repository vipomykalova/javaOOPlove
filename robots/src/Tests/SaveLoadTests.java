package Tests;

import gui.GameWindow;
import gui.SaveAndLoadGame;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SaveLoadTests {
    private GameWindow game = new GameWindow();
    private SaveAndLoadGame saveLoadManager = new SaveAndLoadGame();

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
