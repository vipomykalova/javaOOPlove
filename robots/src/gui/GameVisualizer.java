package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

public class GameVisualizer extends JPanel {
    private Timer m_timer = initTimer();
    Boolean isEditor = false;

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public volatile double m_robotPositionX;
    public volatile double m_robotPositionY;
    public volatile double m_robotDirection;

    public volatile int m_targetPositionX;
    public volatile int m_targetPositionY;

    public volatile double currentWidth;
    public volatile double currentHeight;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    public GameVisualizer() {
        setStartPosition();
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isEditor) {
                    setTargetPosition(e.getPoint());
                    repaint();
                } else {
                    if (distance(e.getX(), e.getY(), m_robotPositionX, m_robotPositionY) < 10) {
                        if (getMouseMotionListeners().length == 0)
                            addMouseMotionListener(new MouseAdapter() {
                                @Override
                                public void mouseMoved(MouseEvent e) {
                                    m_robotPositionX = e.getX();
                                    m_robotPositionY = e.getY();
                                    setTargetPosition(e.getPoint());
                                    repaint();

                                }
                            });
                        else {
                            removeMouseMotionListener(getMouseMotionListeners()[0]);
                        }
                    }
                }
            }
        });
        setDoubleBuffered(true);
    }


    public void stopTimer() {
        m_timer.cancel();
        m_timer = initTimer();
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
    }

    public void setTimer() {
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);
    }

    public ArrayList<String> getGameState() {
        ArrayList<String> gameState = new ArrayList<String>();
        gameState.add(String.valueOf(m_robotPositionX));
        gameState.add(String.valueOf(m_robotPositionY));
        gameState.add(String.valueOf(m_robotDirection));
        gameState.add(String.valueOf(m_targetPositionX));
        gameState.add(String.valueOf(m_targetPositionY));
        return gameState;

    }

    protected void setStartPosition() {

        m_robotPositionX = 100;
        m_robotPositionY = 100;
        m_robotDirection = 0;
        m_targetPositionX = 150;
        m_targetPositionY = 100;
    }

    public void setPosition(ArrayList<String> gameState) {

        m_robotPositionX = Double.parseDouble(gameState.get(0));
        m_robotPositionY = Double.parseDouble(gameState.get(1));
        m_robotDirection = Double.parseDouble(gameState.get(2));
        m_targetPositionX = Integer.parseInt(gameState.get(3));
        m_targetPositionY = Integer.parseInt(gameState.get(4));
    }

    protected void setTargetPosition(Point p) {
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    public boolean isRobotAbroad(double x, double y, double direction) {

        double distToLeftBoarder = x - Math.abs(Math.cos(direction)) * 15;
        double distToUpBoarder = y - Math.abs(Math.sin(direction)) * 15;
        double distToRightBoarder = x + Math.abs(Math.cos(direction)) * 15;
        double distToDownBoarder = y + Math.abs(Math.sin(direction)) * 15;

        if (currentWidth != 0 && currentHeight != 0) {

            if ((distToLeftBoarder <= 0 || distToLeftBoarder >= currentWidth) ||
                    (distToRightBoarder <= 0 || distToRightBoarder >= currentWidth) ||
                    (distToDownBoarder <= 0 || distToDownBoarder >= currentHeight) ||
                    (distToUpBoarder <= 0 || distToUpBoarder >= currentHeight)) {

                JOptionPane.showConfirmDialog(super.getParent(),
                        "Кликни, если хочешь еще поиграть :)",
                        "RIP жучок", JOptionPane.DEFAULT_OPTION);
                return true;
            }
        }
        return false;
    }

    public void onModelUpdateEvent() {
        currentWidth = super.getSize().width;
        currentHeight = super.getSize().height;
        if (isRobotAbroad(m_robotPositionX, m_robotPositionY, m_robotDirection)) {
            setStartPosition();
        }

        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.7) {
            return;
        }
        double velocity = maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;
        double angleBetweenTargetRobot = asNormalizedRadians(angleToTarget - m_robotDirection);
        if (angleBetweenTargetRobot < Math.PI) {
            angularVelocity = maxAngularVelocity;
        } else {
            angularVelocity = -maxAngularVelocity;
        }

        if (Math.abs(angleToTarget - m_robotDirection) < 0.05) {

            moveRobot(velocity, angularVelocity, 10);
        } else {
            if (distance < 15) {
                moveRobot(0, angularVelocity, 10);
            } else {
                moveRobot(velocity / 2, angularVelocity, 10);
            }
        }
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        double newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        double newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);

        m_robotPositionX = newX;
        m_robotPositionY = newY;
        double newDirection = m_robotDirection + angularVelocity * duration * 4;
        m_robotDirection = asNormalizedRadians(newDirection);
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), m_robotDirection);
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        int robotCenterX = round(m_robotPositionX);
        int robotCenterY = round(m_robotPositionY);
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}
