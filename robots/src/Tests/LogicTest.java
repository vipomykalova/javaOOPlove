package Tests;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import gui.GameVisualizer;

public class LogicTest {
    private GameVisualizer game = new GameVisualizer();
    @Test
    void DefaultTest(){
        game.m_robotPositionX = 150.8;
        game.onModelUpdateEvent();
        assertEquals(game.m_robotPositionX, 150.8);
    }

    @Test
    void RunDirectTest(){
        game.m_robotPositionX = 100;
        game.m_robotPositionY = 100;
        game.m_robotDirection = 0;
        game.onModelUpdateEvent();
        assertEquals(Math.round(game.m_robotDirection), 0);
        game.m_robotDirection = 0.6;
        assertEquals(Math.ceil(Math.toDegrees(game.m_robotDirection)), 35);
    }

    @Test
    void RunNoDirectTest(){
        game.m_robotDirection = 3.1415926;
        game.m_robotPositionY = 90;
        game.m_robotPositionX = 90;
        game.onModelUpdateEvent();
        assertEquals(game.m_robotPositionX, 89.5);
        assertEquals(Math.ceil(game.m_robotPositionY), 91);
    }

    @Test
    void RunBorderTest(){
        game.currentHeight = 50;
        game.currentWidth = 20;
        assertTrue(game.isRobotAbroad(10, 20, 0.1));
    }

    @Test
    void RunNoBorderTest(){
        game.currentHeight = 50;
        game.currentWidth = 20;
        assertFalse(game.isRobotAbroad(10, 20, 1.6));
        game.currentWidth = 50;
        assertFalse(game.isRobotAbroad(30, 20, 0.1));
    }
}