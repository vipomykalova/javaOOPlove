package gui;


import javax.swing.table.TableRowSorter;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SaveAndLoadGame {

    private String makeDataForSave(GameWindow currentGame) {

        ArrayList<String> currentState = currentGame.getVisualizer().getGameState();

        String result = "";
        for (int i = 0; i < currentState.size(); i++) {
            result += currentState.get(i) + " ";
        }
        return result;
    }

    public String getDataForSave(GameWindow currentGame) {

        return makeDataForSave(currentGame);
    }

    private void saveDataInFile(String[] data) throws IOException {

        String state = data[0];
        String fileName = data[1];
        FileWriter fw = new FileWriter(fileName);
        fw.write(state);
        fw.flush();
        fw.close();
    }

    public void saveData(String... data) throws IOException {

        saveDataInFile(data);
    }

    private String getDataFromFile(File file) throws FileNotFoundException, Exception {

        FileReader fw = new FileReader(file);
        Scanner scan = new Scanner(fw);

        String state = "";

        while (scan.hasNextLine()) {
            state += scan.nextLine();
        }

        return state;

    }

    public String getDataFromSource(Object source) throws FileNotFoundException, Exception {

        return getDataFromFile((File) source);
    }

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

    public Boolean setLoadData(GameWindow currentGame, String state) {

        return setGameState(currentGame, state);
    }
}
