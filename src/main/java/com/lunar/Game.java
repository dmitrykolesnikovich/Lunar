package com.lunar;

import com.lunar.input.InputListener;
import com.lunar.input.MouseInput;
import com.lunar.state.GameState;
import com.lunar.window.FramePreferences;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class Game implements Runnable {
    private JFrame frame;
    private int width, height, fps;

    private Thread thread;
    private boolean running = true;

    private Graphics graphics;

    private List<GameState> stack;
    private int maxTPS = 0; // The maximum tick rate
    private int maxFPS = 0; // The maximum frame rate

    private boolean showFPS = false;

    /**
     * Initialize the project.
     *
     * @param title    The string on the window's title bar.
     * @param width    Width of the window
     * @param height   Height of the window
     * @param tickRate Determines how fast the game loop is.
     */
    public Game(String title, int width, int height, int tickRate) {
        this.width = width;
        this.height = height;

        stack = new ArrayList<>();

        this.maxTPS = tickRate;

        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setFocusable(true);

        frame.addKeyListener(new InputListener());
        frame.addMouseListener(new MouseInput());
    }

    /**
     * Initialize the project.
     *
     * @param title    The string on the window's title bar.
     * @param width    Width of the window
     * @param state    A game state (if you have that)
     * @param height   Height of the window
     * @param tickRate Determines how fast the game loop is.
     */
    public Game(String title, int width, int height, GameState state, int tickRate) {
        this(title, width, height, tickRate);

        addToStack(state);
    }

    /**
     * Initialize the game.
     *
     * @param title    The string on the window's title bar.
     * @param width    Width of the window
     * @param height   Height of the window
     * @param pref     The window preferences.
     * @param tickRate Determines how fast the game loop is.
     */
    public Game(String title, int width, int height, FramePreferences pref, int tickRate) {
        this.width = width;
        this.height = height;

        stack = new ArrayList<>();

        maxTPS = tickRate;

        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(pref.getCloseOperation());
        frame.setLocationRelativeTo(pref.getRelativeLocation());
        frame.setResizable(pref.isResizable());
        frame.setFocusable(pref.isFocusable());

        frame.addKeyListener(new InputListener());
        frame.addMouseListener(new MouseInput());
    }

    /**
     * Initialize the game.
     *
     * @param title    The string on the window's title bar.
     * @param width    Width of the window
     * @param state    A game state (if you have that)
     * @param height   Height of the window
     * @param pref     The window preferences.
     * @param tickRate Determines how fast the game loop is.
     */
    public Game(String title, int width, int height, FramePreferences pref, GameState state, int tickRate) {
        this(title, width, height, pref, tickRate);

        stack = new ArrayList<>();
        addToStack(state);
    }

    /**
     * Start the game thread.
     */

    public synchronized void start() {
        frame.setVisible(true);
        frame.createBufferStrategy(3);
        onStart();

        running = true;
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Stop the game thread.
     */

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * The game-loop, handles drawing, updating, etc.
     */

    @Override
    public void run() {
        long lastRunTime = System.nanoTime();
        double TICK_RATE = 1000000000 / maxTPS;
        double tickDelta = 0;

        int frames = 0;
        long frameTime = System.currentTimeMillis();

        while (running) {
            long now = System.nanoTime();
            long tickTime = now - lastRunTime;
            long timeStart = System.currentTimeMillis();

            lastRunTime = now;

            tickDelta += tickTime / TICK_RATE;
            while (tickDelta >= 1) {
                onTick();
                tickDelta--;
            }

            if (frames < maxFPS || maxFPS == 0) {
                onDraw();
                frames++;
            }

            if (System.currentTimeMillis() - frameTime >= 1000) {
                frameTime += 1000;
                fps = frames;
                frames = 0;
            }

            // TODO: Fix later(implement a new game loop), currently locks FPS to whatever the TPS is.
            long time = ((long) TICK_RATE - (System.currentTimeMillis() - timeStart)) / (long) 1e6;
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        stop();
    }

    /**
     * When the game starts.
     */

    private void onStart() {
        // call onStart method to each GameStack.
        stack.forEach(GameState::onStart);
    }

    /**
     * Draw all game objects.
     */

    private void onDraw() {
        BufferStrategy frameStrategy = frame.getBufferStrategy();
        if (frameStrategy == null) {
            // create the buffer strategy, its null.
            frame.createBufferStrategy(3);
            frameStrategy = frame.getBufferStrategy();
        }
        // clear and get our graphics object.
        graphics = frameStrategy.getDrawGraphics();
        graphics.clearRect(0, 0, width, height);

        if (showFPS) {
            graphics.setColor(Color.red);
            graphics.drawString(Integer.toString(fps) + " fps", 50, 50);
        }

        // update stack.
        stack.forEach(state -> state.onDraw(graphics));

        graphics.dispose();
        frameStrategy.show();

    }

    /**
     * Update all game objects.
     */

    private void onTick() {
        stack.forEach(GameState::onTick);
    }

    /**
     * Gets the width of the Game's window.
     *
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the Game's window.
     *
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Shows or hides the current FPS.
     * Mainly for debugging purposes.
     */
    public void setFPSVisibility(boolean showFPS) {
        this.showFPS = showFPS;
    }

    /**
     * Limits the maximum FPS.
     * Set to 0 for unlimited FPS.
     */
    public void setMaxFPS(int frames) {
        this.maxFPS = frames;
    }

    /**
     * Gets the FPS of the Game.
     *
     * @return the frames per second count,
     * which is how many times the game renders in a given second.
     */
    public int getFPS() {
        return fps;
    }

    /**
     * @return the graphics object.
     */
    public Graphics getGraphics() {
        return graphics;
    }

    /**
     * Add a state to the stack.
     */
    public void addToStack(GameState state) {
        stack.add(state);
        stack.sort((state1, state2) -> Integer.compare(state2.getPriority(), state1.getPriority()));
    }

    /**
     * Remove the state from the stack.
     *
     * @param state the state that should be removed from the game stack.
     */
    public void removeFromStack(GameState state) {
        stack.remove(state);
    }

    /**
     * Clear the stack.
     */
    public void clearStack() {
        stack.clear();
    }

    /**
     * Add a key listener
     */
    public void addKeyListener(KeyListener listener) {
        frame.addKeyListener(listener);
    }

    /**
     * Clear all key listeners. NOTE: this method will also remove the default listener.
     */
    public void clearKeyListeners() {
        KeyListener[] listeners = frame.getKeyListeners();
        for (KeyListener listener : listeners) {
            removeKeyListener(listener);
        }
    }

    /**
     * Remove the given key listener
     */
    public void removeKeyListener(KeyListener listener) {
        frame.removeKeyListener(listener);
    }

    /**
     * Add a mouse listener
     */
    public void addMouseListener(MouseListener listener) {
        frame.addMouseListener(listener);
    }

    /**
     * Clear all mouse listeners. NOTE: this method will also remove the default listener.
     */
    public void clearMouseListeners() {
        MouseListener[] listeners = frame.getMouseListeners();
        for (MouseListener listener : listeners) {
            removeMouseListener(listener);
        }
    }

    /**
     * Remove the given mouse listener
     */
    public void removeMouseListener(MouseListener listener) {
        frame.removeMouseListener(listener);
    }
}
