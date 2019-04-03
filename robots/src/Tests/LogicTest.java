package Tests;
import gui.MainApplicationFrame;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import gui.GameVisualizer;

import javax.swing.*;

public class LogicTest {
    private GameVisualizer game = new GameVisualizer();
    @Test
    void defaultTest(){
        game.m_robotPositionX = 150.8;
        game.onModelUpdateEvent();
        assertEquals(game.m_robotPositionX, 150.8);
    }

    @Test
    void runDirectTest(){
        game.m_robotPositionX = 100;
        game.m_robotPositionY = 100;
        game.m_robotDirection = 0;
        game.onModelUpdateEvent();
        assertEquals(Math.round(game.m_robotDirection), 0);
        game.m_robotDirection = 0.6;
        assertEquals(Math.ceil(Math.toDegrees(game.m_robotDirection)), 35);
    }

    @Test
    void runNoDirectTest(){
        game.m_robotDirection = 3.1415926;
        game.m_robotPositionY = 90;
        game.m_robotPositionX = 90;
        game.onModelUpdateEvent();
        assertEquals(game.m_robotPositionX, 89.5);
        assertEquals(Math.ceil(game.m_robotPositionY), 91);
    }

    @Test
    void runBorderTest(){
        game.currentHeight = 50;
        game.currentWidth = 20;
        assertTrue(game.isRobotAbroad(10, 20, 0.1));
    }

    @Test
    void runNoBorderTest(){
        game.currentHeight = 50;
        game.currentWidth = 20;
        assertFalse(game.isRobotAbroad(10, 20, 1.6));
        game.currentWidth = 50;
        assertFalse(game.isRobotAbroad(30, 20, 0.1));
    }
}