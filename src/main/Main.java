/* Don-CODE */
package main;

import engine.Game;
import functions.Constants;
import functions.Variables;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.table.DefaultTableModel;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Main extends javax.swing.JFrame {

    private functions.Constants constants;
    public static functions.Variables variables;

    public Main() {
        createMainObjects(); // Create Create dynamic & constant variable objects
        initComponents(); // Import and display all components    
        mainThings(); // Main functions in JFrame
        checkFiles(); // Check saves files
        assignCardPanels(); // Assigning card layouts
    }
    
    /* [##] Set cursor to hand */
    private void handCursor() {
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /* [##] Set cursor to default */
    private void defaultCursor() {
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /* [##] Data encryption (simple caesarCipherEncryption) */
    private String dataEncryption(String text) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException {
        try {
            String b64encoded = Base64.getEncoder().encodeToString(text.getBytes());
            // Reverse the string
            String reverse = new StringBuffer(b64encoded).reverse().toString();
            StringBuilder tmp = new StringBuilder();
            final int OFFSET = 4;
            for (int i = 0; i < reverse.length(); i++) {
                tmp.append((char) (reverse.charAt(i) + OFFSET));
            }
            return tmp.toString();
        } catch (Exception ex) {
        }
        return null;
    }

    /* [##] Data decryption (simple caesarCipherDecryption) */
    private String dataDecryption(String encryptedText) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        try {
            StringBuilder tmp = new StringBuilder();
            final int OFFSET = 4;
            for (int i = 0; i < encryptedText.length(); i++) {
                tmp.append((char) (encryptedText.charAt(i) - OFFSET));
            }
            String reversed = new StringBuffer(tmp.toString()).reverse().toString();
            return new String(Base64.getDecoder().decode(reversed));
        } catch (Exception ex) {
        }
        return null;
    }

    /* [##] Function for writing Error messages to text file */
    private void writeErrorMessages(int errorNo, String customText, String systemErrorMessage) {
        JOptionPane.showMessageDialog(null, "Error code " + errorNo, "ERROR", JOptionPane.ERROR_MESSAGE);
        try {
            String sentence = getDate() + "\t" + Integer.toString(errorNo) + "\t" + customText + "\t" + systemErrorMessage;
            writeToFile(dataEncryption(sentence), constants.getERROR_FILENAME());
        } catch (Exception ex) {
        }
    }

    /* [##] Write given text to a file */
    private void writeToFile(String sentence, String fileName) throws IOException {
        createMainFolder();
        createAFile(fileName);
        FileWriter myWriter = new FileWriter(fileName, true);
        myWriter.write(sentence + "\n");
        myWriter.flush();
        myWriter.close();
    }

    /* (10200) Play sound when user click Red or Green buttons */
    private void functionRedGreenKeyButtonClickSound() {
        variables.setRedGreenKeySoundsThread(new Thread(new Runnable() {
            public void run() {
                try {
                    Clip sound = AudioSystem.getClip();
                    sound.open(AudioSystem.getAudioInputStream(Main.class.getClassLoader().getResource(constants.getSOUND_redGreenButton())));
                    sound.start();
                    TimeUnit.SECONDS.sleep(1);
                    sound.stop();
                    variables.getRedGreenKeySoundsThread().stop();
                    sound = null;
                    variables.setRedGreenKeySoundsThread(null);
                    System.gc();
                } catch (Exception ex) {
                    writeErrorMessages(10200, "Error in 'functionRedGreenKeyButtonClickSound' method", ex.getMessage().toString());
                }
            }
        }));
    }

    /* (10201) PLay game entering music */
    private void functionGameMusic() {
        try {
            if (variables.getGameMusic()) {
                variables.setGameMusicThread(null);
                System.gc();
                functionGameMusicThread();
                variables.getGameMusicThread().start();
            }
        } catch (Exception ex) {
            writeErrorMessages(10201, "Error in 'functionGameMusic' method", ex.getMessage().toString());
        }
    }

    /* (10202) Thread about pLaying game entering music */
    private void functionGameMusicThread() {
        variables.setGameMusicThread(new Thread(new Runnable() {
            public void run() {
                try {
                    if (variables.getGameMusic()) {
                        variables.setClipGameMusic(AudioSystem.getClip());
                        variables.getClipGameMusic().open(AudioSystem.getAudioInputStream(Main.class.getClassLoader().getResource(constants.getSOUND_gameMusic())));
                        variables.getClipGameMusic().start();
                        variables.getClipGameMusic().loop(variables.getClipGameMusic().LOOP_CONTINUOUSLY);
                        if (variables.getGameMusic()) {
                            boolean x = true;
                            while (true) {
                                colorChange(x);
                                if (x) {
                                    x = false;
                                } else {
                                    x = true;
                                }
                                Thread.sleep(100);
                            }
                        }
                    }
                } catch (Exception ex) {
                    writeErrorMessages(10202, "Error in 'functionGameMusicThread' method", ex.getMessage().toString());
                }
            }
        }));
    }

    /* (10203) Play main background music */
    private void functionBackgroundMusic() {
        try {
            variables.setBackgroundMusicThread(null);
            System.gc();
            if (variables.getBackgroundMusic()) {
                functionBackgroundMusicThread();
                variables.getBackgroundMusicThread().start();
            }
        } catch (Exception ex) {
            writeErrorMessages(10203, "Error in 'functionBackgroundMusic' method", ex.getMessage().toString());
        }
    }

    /* (10204) Thread about Playing main background music */
    private void functionBackgroundMusicThread() {
        variables.setBackgroundMusicThread(new Thread(new Runnable() {
            public void run() {
                try {
                    if (variables.getBackgroundMusic()) {
                        variables.setClipBackgroundMusic(AudioSystem.getClip());
                        variables.getClipBackgroundMusic().open(AudioSystem.getAudioInputStream(Main.class.getClassLoader().getResource(constants.getSOUND_background())));
                        variables.getClipBackgroundMusic().start();
                        variables.getClipBackgroundMusic().loop(variables.getClipBackgroundMusic().LOOP_CONTINUOUSLY);
                    }
                } catch (Exception ex) {
                    writeErrorMessages(10204, "Error in 'functionBackgroundMusicThread' method", ex.getMessage().toString());
                }
            }
        }));
    }

    /* (10205) Main things in class */
    private void mainThings() {
        try {
            /* Main functions in JFrame */
            ImageIcon img = new ImageIcon(Main.class.getClassLoader().getResource("pic_normal/icons8_abacus_25px_1.png"));
            this.setIconImage(img.getImage());
            setLocationRelativeTo(null); // Set location to Center
            this.setBackground(new Color(0, 0, 0, 0)); // Make JFrame transparent
            msg_panel.setVisible(false); // Hide message panel
            /* High score table colors */
            highScoreTable.getTableHeader().setOpaque(false);
            highScoreTable.getTableHeader().setBackground(new Color(0, 0, 0));
            highScoreTable.getTableHeader().setForeground(new Color(255, 255, 255));
            highScoreTable.getTableHeader().setBorder(new LineBorder(Color.white));
            /* End */
            this.addWindowStateListener(new WindowAdapter() {
                @Override
                public void windowStateChanged(WindowEvent we) {
                    if (we.getNewState() == Frame.ICONIFIED) {
                        if (variables.getBackgroundMusic() && variables.getSettingsBackgroundMusic()) {
                            variables.getClipBackgroundMusic().stop();
                            variables.getBackgroundMusicThread().suspend();
                        }
                        if (variables.getStartANewGameClicked() && variables.getBlinkingColorTextMakingThread().isAlive()) {
                            if (variables.getGameMusic()) {
                                variables.getClipGameMusic().stop();
                                variables.getGameMusicThread().suspend();
                            }
                            variables.getBlinkingColorTextMakingThread().suspend();
                        }
                        if (variables.getStartANewGameClicked() && variables.getGameMusic() && variables.getGameMusicThread().isAlive()) {
                            variables.getClipGameMusic().stop();
                            variables.getGameMusicThread().suspend();
                        }
                        System.gc();
                    }
                    if (we.getNewState() == Frame.NORMAL) {
                        if (variables.getBackgroundMusic() && variables.getSettingsBackgroundMusic() && !variables.getBackgroundMusicThread().isAlive()) {
                            variables.getClipBackgroundMusic().start();
                            variables.getBackgroundMusicThread().resume();
                        }
                        if (variables.getStartANewGameClicked() && variables.getBlinkingColorTextMakingThread().isAlive()) {
                            variables.getBlinkingColorTextMakingThread().resume();
                        }
                        if (variables.getStartANewGameClicked() && variables.getGameMusic() && variables.getGameMusicThread().isAlive()) {
                            variables.getGameMusicThread().resume();
                            variables.getClipGameMusic().start();
                        }
                    }
                }
            });
        } catch (Exception ex) {
            writeErrorMessages(10205, "Error in 'mainThings' method", ex.getMessage().toString());
        }
    }

    /* (10206) Create main objects for this class */
    private void createMainObjects() {
        try {
            /* Create dynamic & constant variable objects */
            this.constants = new Constants(); // Assign constant variables object
            this.variables = new Variables(); // Assign dynamic variable object
        } catch (Exception ex) {
            writeErrorMessages(10206, "Error in 'createMainObjects' method", ex.getMessage().toString());
        }
    }

    /* (10207) Assign card panels */
    private void assignCardPanels() {
        try {
            /* Assigning card layouts */
            variables.setCard_main((CardLayout) mainPanel.getLayout()); // Assigning Main panels
            variables.setCard_msg((CardLayout) msg_panel.getLayout()); // Assigning Message panels
        } catch (Exception ex) {
            writeErrorMessages(10207, "Error in 'assignCardPanels' method", ex.getMessage().toString());
        }
    }

    /* (10208) Function of "start a new game" button */
    private void function_WelcomeScreen_StartANewGame() {
        try {
            /* Function of [Start A new game] button */
            if (variables.getBackgroundMusic()) {
                variables.getClipBackgroundMusic().stop();
                variables.getBackgroundMusicThread().stop();
                variables.setClipBackgroundMusic(null);
                variables.setBackgroundMusicThread(null);
                System.gc();
                variables.setBackgroundMusic(false);
            }
            variables.setStartANewGameClicked(true);
            msg_panel.setVisible(true); // Set visible the main message panel
            variables.getCard_main().show(mainPanel, "panelTwo"); // Set visible the game playing panel
            functionGameMusic();
            function_GameMessageScreen_TwoButton_Green_SetText(constants.getMessages(0, 2)); // Set green button text to "Ok"
            function_GameMessageScreen_TwoButton_Red_SetText(constants.getMessages(0, 3)); // Set red button text to "Cancel"
            BlinkingColourTextMakingThread(constants.getMessages(0, 0), true, true, constants.getMessages(0, 1), "msg_icon"); // Display "Hey there, let's play TicTacToe..." message
            variables.getBlinkingColorTextMakingThread().start(); // Start displaying message & blinking thread
            variables.setMessageNo(1); // Set message number to 1
        } catch (Exception ex) {
            writeErrorMessages(10208, "Error in 'function_WelcomeScreen_StartANewGame' method", ex.getMessage().toString());
        }
    }

    /* (10209) Function of "High score" button */
    private void function_WelcomeScreen_HighScore() {
        try {
            /* Function of [High score] button */
            variables.getCard_main().show(mainPanel, "panelThree"); // Show high score panel
            msg_panel.hide(); // Hide the main message panel
        } catch (Exception ex) {
            writeErrorMessages(10209, "Error in 'function_WelcomeScreen_HighScore' method", ex.getMessage().toString());
        }
    }

    /* (10210) Function of "Settings" button */
    private void function_WelcomeScreen_Settings() {
        try {
            /* Function of [Settings] button */
            variables.getCard_main().show(mainPanel, "panelFour");
            msg_panel.hide(); // Hide the main message panel
        } catch (Exception ex) {
            writeErrorMessages(10210, "Error in 'function_WelcomeScreen_Settings' method", ex.getMessage().toString());
        }
    }

    /* (10211) Exit function*/
    private void function_WelcomeScreen_CloseButton() {
        try {
            saveSettings();
            System.exit(0);
        } catch (Exception ex) {
            writeErrorMessages(10211, "Error in 'function_WelcomeScreen_CloseButton' method", ex.getMessage().toString());
        }
    }

    /* (10212) Game [X] [O] button clicking sound */
    private void X_O_KeyButtonSound() {
        variables.setKeySoundsThread(new Thread(new Runnable() {
            public void run() {
                try {
                    Clip clipKeySounds = AudioSystem.getClip();
                    clipKeySounds.open(AudioSystem.getAudioInputStream(Main.class.getClassLoader().getResource(constants.getSOUND_click())));
                    clipKeySounds.start();
                    TimeUnit.SECONDS.sleep(1);
                    clipKeySounds.stop();
                    variables.getKeySoundsThread().stop();
                    clipKeySounds = null;
                    variables.setKeySoundsThread(null);
                    System.gc();
                } catch (Exception ex) {
                    writeErrorMessages(10212, "Error in 'X_O_KeyButtonSound' method", ex.getMessage().toString());
                }
            }
        }));
    }

    /* (10213) [X][O] Game button function */
    private void function_GameScreen_TicTacToeButton(int btnNo) {
        try {
            if (variables.getGameStart() && variables.getGameButtonCanSelect(btnNo) && variables.getGame().board.isRunning()) {
                if (variables.getKeySounds()) {
                    X_O_KeyButtonSound();
                    variables.getKeySoundsThread().start();
                }
                if (variables.getBlinkingColorTextMakingThread().isAlive()) {
                    variables.getBlinkingColorTextMakingThread().stop(); // Stop the BlinkingColorTextMakingThread, if it's alive
                }
                function_GameMessageScreen_TwoButton_Green_Enable();
                setCoordinates(btnNo); // Set clicked button values
                BlinkingColourTextMakingThread(constants.getMessages(2, 0), false, false, constants.getMessages(2, 1), "msg_icon1"); // Display message "Are you done ?"
                variables.getBlinkingColorTextMakingThread().start(); // Starting thread
                if (variables.getTempClick(btnNo) == 0) {
                    btnSetText(btnNo, "O", "blue");
                    function_GameMessageScreen_TwoButton_Green_Enable();
                    variables.setTempClick(btnNo, 1);
                    setNotSelectingsToBlank(btnNo);
                } else if (variables.getTempClick(btnNo) == 1) {
                    btnSetText(btnNo, "", "blue");
                    function_GameMessageScreen_TwoButton_Green_Disable();
                    variables.setTempClick(btnNo, 0);
                }
            }
        } catch (Exception ex) {
            writeErrorMessages(10213, "Error in 'function_GameScreen_TicTacToeButton' method", ex.getMessage().toString());
        }
    }

    /* (10214) Disable function for green message button in two & one button message window */
    private void function_GameMessageScreen_TwoButton_Green_Disable() {
        try {
            variables.setGameWindowGreenButtonEnable(false);
            panel_MsgInOtherOkGreen.setBackground(constants.getGray()); // Two button message window green button
            panel_MsgInWelcomeOkGreen.setBackground(constants.getGray()); // One button message window green button
        } catch (Exception ex) {
            writeErrorMessages(10214, "Error in 'function_GameMessageScreen_TwoButton_Green_Disable' method", ex.getMessage().toString());
        }
    }

    /* (10215) Enable function for green message button in two & one button message window */
    private void function_GameMessageScreen_TwoButton_Green_Enable() {
        try {
            variables.setGameWindowGreenButtonEnable(true);
            panel_MsgInOtherOkGreen.setBackground(constants.getGreen()); // Two button message window green button
            panel_MsgInWelcomeOkGreen.setBackground(constants.getGreen()); // One button message window green button
        } catch (Exception ex) {
            writeErrorMessages(10215, "Error in 'function_GameMessageScreen_TwoButton_Green_Enable' method", ex.getMessage().toString());
        }
    }

    /* (10216) Disable function for red message button in two button message window */
    private void function_GameMessageScreen_TwoButton_Red_Disable() {
        try {
            variables.setGameWindowRedButtonEnable(false);
            panel_MsgInOtherHomeRed.setBackground(constants.getGray()); // Two button message window red button
        } catch (Exception ex) {
            writeErrorMessages(10216, "Error in 'function_GameMessageScreen_TwoButton_Red_Disable' method", ex.getMessage().toString());
        }

    }

    /* (10217) Enable function for red message button in two button message window */
    private void function_GameMessageScreen_TwoButton_Red_Enable() {
        try {
            variables.setGameWindowRedButtonEnable(true);
            panel_MsgInOtherHomeRed.setBackground(constants.getRed()); // Two button message window red button
        } catch (Exception ex) {
            writeErrorMessages(10217, "Error in 'function_GameMessageScreen_TwoButton_Red_Enable' method", ex.getMessage().toString());
        }
    }

    /* (10218) Set icone in message panels */
    private void setIconInAllMessagePanels(String image_btnName, String image_name) {
        try {
            /* Set icons & display message panel */
            switch (image_btnName) {
                case "msg_icon":
                    variables.getCard_msg().show(msg_panel, "msg_in_other");
                    label_MsgInOtherGif.setIcon(new ImageIcon(Main.class.getClassLoader().getResource(image_name)));
                    break;
                case "msg_icon1":
                    variables.getCard_msg().show(msg_panel, "msg_in_welcome");
                    label_MsgInWelcomeGif.setIcon(new ImageIcon(Main.class.getClassLoader().getResource(image_name)));
                    break;
                case "msg_icon2":
                    variables.getCard_msg().show(msg_panel, "msg_textOnly");
                    label_TextOnlyGif.setIcon(new ImageIcon(Main.class.getClassLoader().getResource(image_name)));
                    break;
            }
        } catch (Exception ex) {
            writeErrorMessages(10218, "Error in 'setIconInAllMessagePanels' method", ex.getMessage().toString());
        }

    }

    /* (10219) Set text in message panels */
    private void setTextInAllMessagePanels(String image_btnName, String text, int i) {
        try {
            /* Set text in message panel */
            switch (image_btnName) {
                case "msg_icon":
                    label_MsgInOtherText.setText(text.substring(0, i));
                    break;
                case "msg_icon1":
                    label_MsgInWelcomeText.setText(text.substring(0, i));
                    break;
                case "msg_icon2":
                    labelTextOnlyText.setText(text.substring(0, i));
                    break;
            }
        } catch (Exception ex) {
            writeErrorMessages(10219, "Error in 'setTextInAllMessagePanels' method", ex.getMessage().toString());
        }
    }

    /* (10220) Thread about set blinking 'TICTACTOE' & messages */
    private void BlinkingColourTextMakingThread(String text, boolean Block_colorChange, boolean buttonDisable, String image_name, String image_btnName) {
        try {
            if (buttonDisable) {
                function_GameMessageScreen_TwoButton_Green_Disable();
                function_GameMessageScreen_TwoButton_Red_Disable();
            }
            setIconInAllMessagePanels(image_btnName, image_name);
            variables.setBlinkingColorTextMakingThread(new Thread(new Runnable() {
                public void run() {
                    try {
                        boolean x = true;
                        for (int i = 0; i <= text.length(); i++) {
                            if (Block_colorChange && !variables.getGameMusic()) {
                                colorChange(x);
                                if (x) {
                                    x = false;
                                } else {
                                    x = true;
                                }
                            }
                            setTextInAllMessagePanels(image_btnName, text, i);
                            Thread.sleep(100);
                        }
                        if (buttonDisable) {
                            function_GameMessageScreen_TwoButton_Green_Enable();
                            function_GameMessageScreen_TwoButton_Red_Enable();
                        }
                        variables.getBlinkingColorTextMakingThread().stop();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }));
        } catch (Exception ex) {
            writeErrorMessages(10220, "Error in 'BlinkingColourTextMakingThread' method", ex.getMessage().toString());
        }
    }

    /* (10221) Function of red button of two button message screen */
    private void function_GameMessageScreen_TwoButton_Red() {
        try {
            if (variables.getGameWindowRedButtonEnable()) {
                if (variables.getBlinkingColorTextMakingThread().isAlive()) {
                    variables.getBlinkingColorTextMakingThread().stop();
                }
                switch (variables.getMessageNo()) {
                    case 1: // ("Cancel") in "Hey there, let's play TicTacToe..." message ---- |[Cancel][Ok]| ----
                        if (!variables.getBackgroundMusic() && variables.getSettingsBackgroundMusic()) {
                            variables.setBackgroundMusic(true);
                            functionBackgroundMusic();
                        }
                        if (variables.getGameMusic()) {
                            variables.getClipGameMusic().stop();
                            variables.getGameMusicThread().stop();
                        }
                        variables.setStartANewGameClicked(false);
                        variables.getCard_main().show(mainPanel, "panelOne"); // Show main panel
                        msg_panel.setVisible(false); // Set message panel invisible
                        variables.setVariablesToDefault();
                        break;
                    case 2: // ("No") in "Do you want to start first ?" message ---- |[No][Yes]| ----
                        setTextBlank();
                        if (variables.getGameMusic()) {
                            variables.getClipGameMusic().stop();
                            variables.getGameMusicThread().stop();
                        }
                        variables.setUserFirstPlaying(false); // "No" to "Are you done ?" message
                        variables.setGameStart(true); // Set game start to "true"
                        variables.setGame(new Game()); // Assign a new game
                        BlinkingColourTextMakingThread(constants.getMessages(6, 0), false, true, constants.getMessages(6, 1), "msg_icon2");
                        variables.getBlinkingColorTextMakingThread().start();
                        break;
                    case 5:
                        if (variables.getWinLooseDisplayThread().isAlive()) {
                            if (variables.getGameMusic()) {
                                variables.getClipsetWinLooseDisplay().stop();
                                variables.setClipsetWinLooseDisplay(null);
                            }
                            variables.getWinLooseDisplayThread().stop();
                            variables.setWinLooseDisplayThread(null);
                            System.gc();
                        }
                        if (!variables.getBackgroundMusic() && variables.getSettingsBackgroundMusic()) {
                            variables.setBackgroundMusic(true);
                            functionBackgroundMusic();
                        }
                        if (variables.getGameMusic()) {
                            variables.getClipGameMusic().stop();
                            variables.getGameMusicThread().stop();
                        }
                        variables.setStartANewGameClicked(false);
                        variables.getCard_main().show(mainPanel, "panelOne"); // Show main panel
                        msg_panel.setVisible(false); // Set message panel invisible
                        variables.setVariablesToDefault();
                        break;
                }
            }
        } catch (Exception ex) {
            writeErrorMessages(10221, "Error in 'function_GameMessageScreen_TwoButton_Red' method", ex.getMessage().toString());
        }
    }

    /* (10222) Function of green button of two button message screen */
    private void greenButtonFromTwoButtonsInMessagePanel() {
        try {
            if (variables.getGameWindowGreenButtonEnable()) {
                if (variables.getBlinkingColorTextMakingThread().isAlive()) {
                    variables.getBlinkingColorTextMakingThread().stop();
                }
                switch (variables.getMessageNo()) {
                    case 1: // ("Ok") in "Hey there, let's play TicTacToe..." message ---- |[Cancel][Ok]| ----
                        BlinkingColourTextMakingThread(constants.getMessages(1, 0), true, true, constants.getMessages(1, 1), "msg_icon"); // Set text to "Do you want to start first ?"
                        variables.getBlinkingColorTextMakingThread().start(); // Start changing color thread
                        function_GameMessageScreen_TwoButton_Green_SetText(constants.getMessages(1, 2)); // Set green button text to ("Yes")
                        function_GameMessageScreen_TwoButton_Red_SetText(constants.getMessages(1, 3)); //Set red button text to ("No")
                        variables.setMessageNo(2);
                        break;
                    case 2: // ("Yes") in "Do you want to start first ?" message ---- |[No][Yes]| ----
                        setTextBlank(); // Set all play buttons to blank
                        if (variables.getGameMusic()) {
                            variables.getClipGameMusic().stop();
                            variables.getGameMusicThread().stop();
                        }
                        variables.setUserFirstPlaying(true); // "Yes" to "Are you done ?" message
                        variables.setGameStart(true); // Set game start to "true"
                        variables.setGame(new Game()); // Assign a new game
                        BlinkingColourTextMakingThread(constants.getMessages(6, 0), true, true, constants.getMessages(6, 1), "msg_icon2"); // Display the "Your move..." message
                        variables.getBlinkingColorTextMakingThread().start();// ''
                        break;
                    case 5:
                        System.gc();
                        variables.setVariablesToDefault();
                        if (variables.getBackgroundMusic()) {
                            variables.getClipBackgroundMusic().stop();
                            variables.getBackgroundMusicThread().stop();
                            variables.setClipBackgroundMusic(null);
                            variables.setBackgroundMusicThread(null);
                            System.gc();
                            variables.setBackgroundMusic(false);
                        }
                        if (variables.getWinLooseDisplayThread().isAlive()) {
                            if (variables.getGameMusic()) {
                                variables.getClipsetWinLooseDisplay().stop();
                                variables.setClipsetWinLooseDisplay(null);
                            }
                            variables.getWinLooseDisplayThread().stop();
                            variables.setWinLooseDisplayThread(null);
                            System.gc();
                        }
                        variables.setStartANewGameClicked(true);
                        msg_panel.setVisible(true); // Set visible the main message panel
                        variables.getCard_main().show(mainPanel, "panelTwo"); // Set visible the game playing panel
                        functionGameMusic();
                        function_GameMessageScreen_TwoButton_Green_SetText(constants.getMessages(1, 2)); // Set green button text to ("Yes")
                        function_GameMessageScreen_TwoButton_Red_SetText(constants.getMessages(1, 3)); //Set red button text to ("No")
                        BlinkingColourTextMakingThread(constants.getMessages(1, 0), true, true, constants.getMessages(1, 1), "msg_icon"); // Set text to "Do you want to start first ?"
                        variables.getBlinkingColorTextMakingThread().start(); // Start displaying message & blinking thread
                        variables.setMessageNo(2);
                        break;
                }
            }
        } catch (Exception ex) {
            writeErrorMessages(10222, "Error in 'greenButtonFromTwoButtonsInMessagePanel' method", ex.getMessage().toString());
        }
    }

    /* (10223) Back to home greenbuttin*/
    private void goBackToHome() {
        try {
            variables.getCard_main().show(mainPanel, "panelOne");
            msg_panel.hide();

        } catch (Exception ex) {
            writeErrorMessages(10223, "Error in 'goBackToHome' method", ex.getMessage().toString());
        }
    }

    /* (10224) Set [X][O] button values */
    private void setCoordinates(int btnNo) {
        try {
            if (variables.getGameButtonCanSelect(btnNo)) {
                switch (btnNo) {
                    case 0:
                        variables.setBtn_xyValue(0, 0);
                        break;
                    case 1:
                        variables.setBtn_xyValue(0, 1);
                        break;
                    case 2:
                        variables.setBtn_xyValue(0, 2);
                        break;
                    case 3:
                        variables.setBtn_xyValue(1, 0);
                        break;
                    case 4:
                        variables.setBtn_xyValue(1, 1);
                        break;
                    case 5:
                        variables.setBtn_xyValue(1, 2);
                        break;
                    case 6:
                        variables.setBtn_xyValue(2, 0);
                        break;
                    case 7:
                        variables.setBtn_xyValue(2, 1);
                        break;
                    case 8:
                        variables.setBtn_xyValue(2, 2);
                        break;
                }
            }
        } catch (Exception ex) {
            writeErrorMessages(10224, "Error in 'setCoordinates' method", ex.getMessage().toString());
        }
    }

    /* (10225) Set not selected [X][O] buttons to balank */
    private void setNotSelectingsToBlank(int num) {
        try {
            if (variables.getGameButtonCanSelect(num)) {
                for (int i = 0; i < 9; i++) {
                    if (i == num) {
                        continue;
                    } else {
                        if (variables.getTempClick(i) == 1) {
                            btnSetText(i, "", "blue");
                            variables.setTempClick(i, 0);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            writeErrorMessages(10225, "Error in 'setNotSelectingsToBlank' method", ex.getMessage().toString());
        }
    }

    /* (10226) Selecting colors */
    private Color setColours(String textColor) {
        try {
            switch (textColor) {
                case "blue":
                    return constants.getBlue();
                case "red":
                    return constants.getRed();
                case "white":
                    return constants.getWhite();
            }
        } catch (Exception ex) {
            writeErrorMessages(10226, "Error in 'setColours' method", ex.getMessage().toString());
        }
        return null;
    }

    /* (10227) Set text in [X][O] buttons */
    private void btnSetText(int btnNumber, String value, String textColor) {
        try {
            Color btnTextColor = setColours(textColor);
            if (variables.getGameButtonCanSelect(btnNumber) || variables.getSetNewTicTacToetext()) {
                switch (btnNumber) {
                    case 0:
                        label_00.setForeground(btnTextColor);
                        label_00.setText(value);
                        break;
                    case 1:
                        label_01.setForeground(btnTextColor);
                        label_01.setText(value);
                        break;
                    case 2:
                        label_02.setForeground(btnTextColor);
                        label_02.setText(value);
                        break;
                    case 3:
                        label_10.setForeground(btnTextColor);
                        label_10.setText(value);
                        break;
                    case 4:
                        label_11.setForeground(btnTextColor);
                        label_11.setText(value);
                        break;
                    case 5:
                        label_12.setForeground(btnTextColor);
                        label_12.setText(value);
                        break;
                    case 6:
                        label_20.setForeground(btnTextColor);
                        label_20.setText(value);
                        break;
                    case 7:
                        label_21.setForeground(btnTextColor);
                        label_21.setText(value);
                        break;
                    case 8:
                        label_22.setForeground(btnTextColor);
                        label_22.setText(value);
                        break;
                }
            }
        } catch (Exception ex) {
            writeErrorMessages(10227, "Error in 'btnSetText' method", ex.getMessage().toString());
        }
    }

    /* (10228) Function of Reset button*/
    private void resetButton() {
        try {
            JOptionPane pane = new JOptionPane("Are you sure?", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

            JDialog dialog = pane.createDialog("Reset high score");
            if (variables.getAlwaysOnTop()) {
                dialog.setAlwaysOnTop(true);
            }
            ImageIcon img = new ImageIcon(Main.class.getClassLoader().getResource("pic_normal/icons8_abacus_25px_1.png"));
            dialog.setIconImage(img.getImage());
            dialog.setLocation(this.getX() + 35, this.getY() + 120);
            dialog.setVisible(true);
            dialog.dispose();
            try {
                if ((Integer) pane.getValue() == 0) {  // User click "Ok" button
                    DefaultTableModel model = (DefaultTableModel) highScoreTable.getModel();
                    // Delete all rows in jtable
                    int tableRowCount = highScoreTable.getRowCount();
                    for (int i = 0; i < tableRowCount; i++) {
                        model.removeRow(0);
                    }
                    // Set all array values to null & add new 17 rows to table
                    ArrayList<String> list;
                    for (int i = 0; i < 17; i++) {
                        list = new ArrayList<String>();
                        list.add(null);
                        list.add(null);
                        list.add(null);
                        model.addRow(list.toArray());
                        highScoreTable.setModel(model);
                    }
                    variables.setScoreListZERO();
                    model = null;
                    list = null;
                    // Delete Score File
                    File file = new File(constants.getSCORE_FILENAME());
                    file.delete();
                    //
                    System.gc();
                }
            } catch (NullPointerException e) {
                // User Click [x] button
            }
        } catch (Exception ex) {
            writeErrorMessages(10228, "Error in 'resetButton' method", ex.getMessage().toString());
        }
    }

    /* (10229) Function for two button message panel green button text */
    private void function_GameMessageScreen_TwoButton_Green_SetText(String messsage) {
        try {
            label_MsgInOtherOkGreen.setText(messsage);
        } catch (Exception ex) {
            writeErrorMessages(10229, "Error in 'function_GameMessageScreen_TwoButton_Green_SetText' method", ex.getMessage().toString());
        }
    }

    /* (10230) Function for two button message panel red button text */
    private void function_GameMessageScreen_TwoButton_Red_SetText(String message) {
        try {
            label_MsgInOtherHomeRed.setText(message);
        } catch (Exception ex) {
            writeErrorMessages(10230, "Error in 'function_GameMessageScreen_TwoButton_Red_SetText' method", ex.getMessage().toString());
        }
    }

    /* (10231) Function for minimize button */
    private void MiniMize() {
        try {
            this.setExtendedState(this.ICONIFIED);
        } catch (Exception ex) {
            writeErrorMessages(10231, "Error in 'MiniMize' method", ex.getMessage().toString());
        }
    }

    /* (10232) Set [X][O] button text to blank */
    private void setTextBlank() {
        try {
            for (int i = 0; i < 9; i++) {
                btnSetText(i, "", "blue");
            }
        } catch (Exception ex) {
            writeErrorMessages(10232, "Error in 'setTextBlank' method", ex.getMessage().toString());
        }
    }

    /* (10233) Function about [X][O] button colour change */
    private void colorChange(boolean x) {
        try {
            if (x) {
                label_00.setForeground(constants.getBlue());
                label_01.setForeground(constants.getRed());
                label_02.setForeground(constants.getRed());
                label_10.setForeground(constants.getRed());
                label_11.setForeground(constants.getBlue());
                label_12.setForeground(constants.getRed());
                label_20.setForeground(constants.getBlue());
                label_21.setForeground(constants.getRed());
                label_22.setForeground(constants.getBlue());
            } else {
                label_00.setForeground(constants.getRed());
                label_01.setForeground(constants.getBlue());
                label_02.setForeground(constants.getBlue());
                label_10.setForeground(constants.getBlue());
                label_11.setForeground(constants.getRed());
                label_12.setForeground(constants.getBlue());
                label_20.setForeground(constants.getRed());
                label_21.setForeground(constants.getBlue());
                label_22.setForeground(constants.getRed());
            }
        } catch (Exception ex) {
            writeErrorMessages(10233, "Error in 'colorChange' method", ex.getMessage().toString());
        }
    }

    /* (10234) Set [X][O] button disable function */
    private void setBtnDisable() {
        try {
            String btnNo = Integer.toString(variables.getBtn_xValue()) + Integer.toString(variables.getBtn_yValue());
            switch (btnNo) {
                case "00":
                    variables.setGameButtonCanSelect(0, false);
                    break;
                case "01":
                    variables.setGameButtonCanSelect(1, false);
                    break;
                case "02":
                    variables.setGameButtonCanSelect(2, false);
                    break;
                case "10":
                    variables.setGameButtonCanSelect(3, false);
                    break;
                case "11":
                    variables.setGameButtonCanSelect(4, false);
                    break;
                case "12":
                    variables.setGameButtonCanSelect(5, false);
                    break;
                case "20":
                    variables.setGameButtonCanSelect(6, false);
                    break;
                case "21":
                    variables.setGameButtonCanSelect(7, false);
                    break;
                case "22":
                    variables.setGameButtonCanSelect(8, false);
                    break;
            }
        } catch (Exception ex) {
            writeErrorMessages(10234, "Error in 'setBtnDisable' method", ex.getMessage().toString());
        }
    }

    /* (10235) Message panel one green button function */
    private void oneGreenBtn() {
        try {
            if (variables.getGameWindowGreenButtonEnable() && variables.getGame().board.isRunning()) {
                variables.setClickCount();
                if (variables.getBlinkingColorTextMakingThread().isAlive()) {
                    variables.getBlinkingColorTextMakingThread().stop();
                }
                BlinkingColourTextMakingThread(constants.getMessages(6, 0), false, true, constants.getMessages(6, 1), "msg_icon2"); // Display the "Your move..." message
                variables.getBlinkingColorTextMakingThread().start();
                setBtnDisable();
                variables.getGame().playGame();
                if (!variables.getGame().board.isRunning() && !variables.getGameStart()) {
                    if (variables.getBlinkingColorTextMakingThread().isAlive()) {
                        variables.getBlinkingColorTextMakingThread().stop();
                    }
                    variables.setMessageNo(5);
                    function_GameMessageScreen_TwoButton_Green_SetText(constants.getMessages(5, 2)); // Set green button text to "Ok"
                    function_GameMessageScreen_TwoButton_Red_SetText(constants.getMessages(5, 3)); // Set red button text to "Cancel"
                    BlinkingColourTextMakingThread(constants.getMessages(5, 0), false, true, constants.getMessages(5, 1), "msg_icon"); // Display the "Let's try again..." message
                    variables.getBlinkingColorTextMakingThread().start();

                    setScoreInTable();

                    setTICTACTOEtext();
                }
            }
        } catch (Exception ex) {
            writeErrorMessages(10235, "Error in 'oneGreenBtn' method", ex.getMessage().toString());
        }
    }

    /* (10236) Set "TICTACTOE" in [X][O] buttons */
    private void setTICTACTOEtext() {
        try {
            variables.setSetNewTicTacToetext(true);
            btnSetText(0, "T", "blue");
            btnSetText(1, "I", "red");
            btnSetText(2, "C", "red");
            btnSetText(3, "T", "red");
            btnSetText(4, "A", "blue");
            btnSetText(5, "C", "red");
            btnSetText(6, "T", "blue");
            btnSetText(7, "O", "red");
            btnSetText(8, "E", "blue");
        } catch (Exception ex) {
            writeErrorMessages(10236, "Error in 'setTICTACTOEtext' method", ex.getMessage().toString());
        }
    }

    /* (10237) Key sound button in settings */
    private void settingsFunctionKeySounds() {
        try {
            if (variables.getKeySounds()) { //true
                variables.setKeySounds(false);
                label_SettingsKeySound.setBackground(Color.RED);
                label_SettingsKeySound.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
                label_SettingsKeySoundTic.setIcon(new ImageIcon(Main.class.getClassLoader().getResource("pic_normal/icons8_unchecked_checkbox_30px.png")));
            } else {  // false
                variables.setKeySounds(true);
                label_SettingsKeySound.setBackground(Color.GREEN);
                label_SettingsKeySound.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
                label_SettingsKeySoundTic.setIcon(new ImageIcon(Main.class.getClassLoader().getResource("pic_normal/icons8_tick_box_30px.png")));
            }
        } catch (Exception ex) {
            writeErrorMessages(10237, "Error in 'settingsFunctionKeySounds' method", ex.getMessage().toString());
        }
    }

    /* (10238) Game music button in settings */
    private void settingsFunctionGameMusic() {
        try {
            if (variables.getGameMusic()) { // true
                variables.setGameMusic(false);
                label_SettingsGameMusic.setBackground(Color.RED);
                label_SettingsGameMusic.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
                label_SettingsGameMusicTic.setIcon(new ImageIcon(Main.class.getClassLoader().getResource("pic_normal/icons8_unchecked_checkbox_30px.png")));
            } else { // false
                variables.setGameMusic(true);
                label_SettingsGameMusic.setBackground(Color.GREEN);
                label_SettingsGameMusic.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
                label_SettingsGameMusicTic.setIcon(new ImageIcon(Main.class.getClassLoader().getResource("pic_normal/icons8_tick_box_30px.png")));
            }
        } catch (Exception ex) {
            writeErrorMessages(10238, "Error in 'settingsFunctionGameMusic' method", ex.getMessage().toString());
        }
    }

    /* (10239) Background music button in settings */
    private void settingsFunctionBackgroundMusic() {
        try {
            if (variables.getBackgroundMusic()) { // true
                variables.setBackgroundMusic(false);
                variables.setSettingsBackgroundMusic(false);
                variables.getClipBackgroundMusic().stop();
                variables.getBackgroundMusicThread().stop();
                variables.setClipBackgroundMusic(null);
                variables.setBackgroundMusicThread(null);
                System.gc();
                label_SettingsBackgroundMusic.setBackground(Color.RED);
                label_SettingsBackgroundMusic.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
                label_SettingsBackgroundMusicTic.setIcon(new ImageIcon(Main.class.getClassLoader().getResource("pic_normal/icons8_unchecked_checkbox_30px.png")));
            } else { // false
                variables.setBackgroundMusic(true);
                variables.setSettingsBackgroundMusic(true);
                functionBackgroundMusic();
                label_SettingsBackgroundMusic.setBackground(Color.GREEN);
                label_SettingsBackgroundMusic.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
                label_SettingsBackgroundMusicTic.setIcon(new ImageIcon(Main.class.getClassLoader().getResource("pic_normal/icons8_tick_box_30px.png")));
            }
        } catch (Exception ex) {
            writeErrorMessages(10239, "Error in 'settingsFunctionBackgroundMusic' method", ex.getMessage().toString());
        }
    }

    /* (10240) Always on top button in settings */
    private void settingsFunctionAlwaysOnTop() {
        try {
            if (variables.getAlwaysOnTop()) { //true
                this.setAlwaysOnTop(false);
                variables.setAlwaysOnTop(false);
                label_SettingsAlwaysOnTop.setBackground(Color.RED);
                label_SettingsAlwaysOnTop.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
                label_SettingsAlwaysOnTopTic.setIcon(new ImageIcon(Main.class.getClassLoader().getResource("pic_normal/icons8_unchecked_checkbox_30px.png")));
            } else { //false
                this.setAlwaysOnTop(true);
                variables.setAlwaysOnTop(true);
                label_SettingsAlwaysOnTop.setBackground(Color.GREEN);
                label_SettingsAlwaysOnTop.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
                label_SettingsAlwaysOnTopTic.setIcon(new ImageIcon(Main.class.getClassLoader().getResource("pic_normal/icons8_tick_box_30px.png")));
            }
        } catch (Exception ex) {
            writeErrorMessages(10240, "Error in 'settingsFunctionAlwaysOnTop' method", ex.getMessage().toString());
        }
    }

    /* (10241) Check game files exists */
    private void checkFiles() {
        File file;
        try {
            try {
                checkFiles_scoreFile();
            } catch (NullPointerException ex) {
                file = new File(constants.getSCORE_FILENAME());
                file.delete();
                writeErrorMessages(10241, "Error inside 'checkFiles (Score)' method", ex.getMessage().toString());
            }
            try {
                checkFiles_settingsFile();
            } catch (NullPointerException ex) {
                file = new File(constants.getSETTINGS_FILENAME());
                file.delete();
                writeErrorMessages(10241, "Error inside 'checkFiles (Settings)' method", "Settings file error");
            }
            try {
                file = new File(constants.getERROR_FILENAME());
                if (file.exists()) {
                    errorLogTextArea.setText(readFile(constants.getERROR_FILENAME()));
                }
            } catch (NullPointerException ex) {
                file = new File(constants.getERROR_FILENAME());
                file.delete();
                writeErrorMessages(10241, "Error inside 'checkFiles (Error)' method", ex.getMessage().toString());
            }
        } catch (Exception ex) {
            writeErrorMessages(10241, "Error in 'checkFiles' method", ex.getMessage().toString());
        }
    }

    /* (10242) Save settings to a file */
    private void saveSettings() {
        try {
            createMainFolder();
            boolean settings[] = {variables.getAlwaysOnTop(), variables.getBackgroundMusic(), variables.getGameMusic(), variables.getKeySounds()};
            ArrayList list = new ArrayList();
            for (int i = 0; i < settings.length; i++) {
                list.add(settings[i]);
            }
            FileOutputStream fos = new FileOutputStream(constants.getSETTINGS_FILENAME());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(dataEncryption(list.toString()));
            oos.flush();
            oos.close();
            fos.flush();
            fos.close();
        } catch (Exception ex) {
            writeErrorMessages(10242, "Error in 'saveSettings' method", ex.getMessage().toString());
        }
    }

    /* (10243) Checking saved settings file */
    private void checkFiles_settingsFile() {
        try {
            File file = new File(constants.getSETTINGS_FILENAME());
            if (file.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(constants.getSETTINGS_FILENAME());
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    String al = dataDecryption(ois.readObject().toString());
                    al = al.replace(" ", "").replace("[", "").replace("]", "");
                    String savedValues[] = al.split(",");
                    for (int i = 0; i < savedValues.length; i++) {
                        assignSavedSettings(i, savedValues[i]);
                    }
                } catch (NullPointerException ex) {
                    file.delete();
                    functionBackgroundMusic();
                    writeErrorMessages(10243, "Error inside 'checkFiles_settingsFile' method", ex.getMessage().toString());
                }

            } else {
                functionBackgroundMusic();
            }
        } catch (Exception ex) {
            File file = new File(constants.getSETTINGS_FILENAME());
            file.delete();
            variables.setBackgroundMusic(true);
            functionBackgroundMusic();
            writeErrorMessages(10243, "Error in 'checkFiles_settingsFile' method", ex.getMessage().toString());
        }
    }

    /* (10244) Get current data & time */
    private String getDate() {
        try {
            java.text.SimpleDateFormat todaysDate = new java.text.SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            return todaysDate.format((java.util.Calendar.getInstance()).getTime());
        } catch (Exception ex) {
            writeErrorMessages(10244, "Error in 'getDate' method", ex.getMessage().toString());
        }
        return null;
    }

    /* (10245) Read file data */
    private String readFile(String filename) {
        try {
            FileInputStream fstream = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            String wholeText = "";
            while ((strLine = br.readLine()) != null) {
                wholeText += dataDecryption(strLine) + "\n";
            }
            fstream.close();
            return wholeText;
        } catch (Exception ex) {
            writeErrorMessages(10245, "Error in 'readFile' method", ex.getMessage().toString());
        }
        return null;
    }

    /* (10246) Checking saved score file */
    private void checkFiles_scoreFile() {
        try {
            File file = new File(constants.getSCORE_FILENAME());
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(constants.getSCORE_FILENAME());
                ObjectInputStream ois = new ObjectInputStream(fis);
                try {
                    String al = dataDecryption(ois.readObject().toString());
                    al = al.replace("[", "").replace("]", "");

                    String gg[] = al.split(",");
                    for (int t = 0; t < gg.length; t++) {
                        String jj[] = gg[t].split("@");
                        if (jj[0].toCharArray()[0] == " ".toCharArray()[0]) {
                            variables.setScoreList(jj[0].replaceFirst(" ", "") + "@" + jj[1] + "@" + jj[2]);
                        } else {
                            variables.setScoreList(jj[0] + "@" + jj[1] + "@" + jj[2]);
                        }
                    }
                    setScoreInTable();
                } catch (Exception ex) {
                    ois.close();
                    fis.close();
                    file.delete();
                    writeErrorMessages(10246, "Error inside 'checkFiles_scoreFile' method", "checkFiles_scoreFile NULL error");
                }
            }
        } catch (Exception ex) {
            writeErrorMessages(10246, "Error in 'checkFiles_scoreFile' method", ex.getMessage().toString());
        }
    }

    /* (10247) Create a main folder for all game files in 'Documents' folder */
    private void createMainFolder() throws IOException {
        try {
            Files.createDirectories(Paths.get(constants.getSUPER_MAIN_FILE())); // Create "Document" folder if not exists
            Files.createDirectories(Paths.get(constants.getMAIN_FILE())); // Create "Document/TicTacToe" folder if not exists
        } catch (Exception ex) {
            writeErrorMessages(10247, "Error in 'createMainFolder' method", ex.getMessage().toString());
        }
    }

    /* (10248) Create a new file if not exists */
    private void createAFile(String file) throws IOException {
        try {
            File myObj = new File(file);
            myObj.createNewFile();
            myObj = null;
            System.gc();
        } catch (Exception ex) {
            writeErrorMessages(10248, "Error in 'createAFile' method", ex.getMessage().toString());
        }
    }

    /* (10249) Get saved settings from file and assigned them */
    private void assignSavedSettings(int no, String value) {
        try {
            switch (no) {
                case 0:
                    variables.setAlwaysOnTop(!Boolean.valueOf(value));
                    settingsFunctionAlwaysOnTop();
                    break;
                case 1:
                    functionBackgroundMusic();
                    variables.setBackgroundMusic(Boolean.valueOf(value));
                    variables.setSettingsBackgroundMusic(Boolean.valueOf(value));
                    if (!Boolean.valueOf(value)) {
                        label_SettingsBackgroundMusic.setBackground(Color.RED);
                        label_SettingsBackgroundMusic.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
                        label_SettingsBackgroundMusicTic.setIcon(new ImageIcon(Main.class.getClassLoader().getResource("pic_normal/icons8_unchecked_checkbox_30px.png")));
                    }
                    break;
                case 2:
                    variables.setGameMusic(!Boolean.valueOf(value));
                    settingsFunctionGameMusic();
                    break;
                case 3:
                    variables.setKeySounds(!Boolean.valueOf(value));
                    settingsFunctionKeySounds();
                    break;
            }
        } catch (Exception ex) {
            writeErrorMessages(10249, "Error in 'assignSavedSettings' method", ex.getMessage().toString());
        }
    }

    /* (10250) Set score details in table */
    private void setScoreInTable() throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        try {
            DefaultTableModel model = (DefaultTableModel) highScoreTable.getModel();
            // Delete all rows in jtable
            int tableRowCount = model.getRowCount();
            for (int i = 0; i < tableRowCount; i++) {
                model.removeRow(0);
            }
            // Set all array values to null & add new rows to table
            ArrayList<String> list = null;
            String list_k[] = null;
            for (int k = 0; k < variables.getScoreList().size(); k++) {
                list = new ArrayList<String>();
                list_k = variables.getScoreList().get(k).toString().split("@");
                list.add(list_k[0]);
                list.add(list_k[1]);
                list.add(list_k[2]);
                model.addRow(list.toArray());
                highScoreTable.setModel(model);
            }
            // Save score to a file
            FileOutputStream fos = new FileOutputStream(constants.getSCORE_FILENAME());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(dataEncryption(variables.getScoreListOrigin().toString()));
            oos.flush();
            oos.close();
            fos.flush();
            fos.close();
            //
            list = null;
            list_k = null;
            model = null;
            System.gc();
            variables.setClickCountZERO();
        } catch (Exception ex) {
            writeErrorMessages(10250, "Error in 'setScoreInTable' method", ex.getMessage().toString());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        panelOne = new javax.swing.JPanel();
        panel_PanelOne = new javax.swing.JPanel();
        label_WelcomeToTictactoe = new javax.swing.JLabel();
        panel_WelcomeScreenStartANewGameGreen = new javax.swing.JPanel();
        label_WelcomeScreenStartANewGameGreen = new javax.swing.JLabel();
        label_WelcomeScreenVersion = new javax.swing.JLabel();
        label_WelcomeScreenHelp = new javax.swing.JLabel();
        panel_WelcomeScreenScoreHistoryBlue = new javax.swing.JPanel();
        label_WelcomeScreenScoreHistoryBlue = new javax.swing.JLabel();
        panel_WelcomeScreenSettingsRed = new javax.swing.JPanel();
        label_WelcomeScreenSettingsRed = new javax.swing.JLabel();
        label_WelcomeScreenGifOneBear = new javax.swing.JLabel();
        label_WelcomeScreenGifTwoBear = new javax.swing.JLabel();
        panelTwo = new javax.swing.JPanel();
        btn_00 = new javax.swing.JPanel();
        label_00 = new javax.swing.JLabel();
        btn_01 = new javax.swing.JPanel();
        label_01 = new javax.swing.JLabel();
        btn_02 = new javax.swing.JPanel();
        label_02 = new javax.swing.JLabel();
        btn_10 = new javax.swing.JPanel();
        label_10 = new javax.swing.JLabel();
        btn_11 = new javax.swing.JPanel();
        label_11 = new javax.swing.JLabel();
        btn_12 = new javax.swing.JPanel();
        label_12 = new javax.swing.JLabel();
        btn_20 = new javax.swing.JPanel();
        label_20 = new javax.swing.JLabel();
        btn_21 = new javax.swing.JPanel();
        label_21 = new javax.swing.JLabel();
        btn_22 = new javax.swing.JPanel();
        label_22 = new javax.swing.JLabel();
        panelThree = new javax.swing.JPanel();
        panel_ScoreHistoryHomeBlue = new javax.swing.JPanel();
        label_ScoreHistoryHomeBlue = new javax.swing.JLabel();
        panel_ScoreHistoryResetRed = new javax.swing.JPanel();
        label_ScoreHistoryResetRed = new javax.swing.JLabel();
        scrollPane_ScoreHistory = new javax.swing.JScrollPane();
        highScoreTable = new javax.swing.JTable();
        panelFour = new javax.swing.JPanel();
        panel_SettingsHomeGreen = new javax.swing.JPanel();
        label_SettingsHomeGreen = new javax.swing.JLabel();
        panel_Settings = new javax.swing.JPanel();
        label_SettingsBackgroundMusicTic = new javax.swing.JLabel();
        label_SettingsAlwaysOnTopTic = new javax.swing.JLabel();
        label_SettingsAlwaysOnTop = new javax.swing.JLabel();
        label_SettingsBackgroundMusic = new javax.swing.JLabel();
        label_SettingsGameMusic = new javax.swing.JLabel();
        label_SettingsGameMusicTic = new javax.swing.JLabel();
        label_SettingsKeySoundTic = new javax.swing.JLabel();
        label_SettingsKeySound = new javax.swing.JLabel();
        panelFive = new javax.swing.JPanel();
        label_WinLooseDrawDisplay = new javax.swing.JLabel();
        panelSix = new javax.swing.JPanel();
        tabbedPane_Help = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        errorLogTextArea = new javax.swing.JTextArea();
        panel_HelpHomeGreen = new javax.swing.JPanel();
        label_HelpHomeGreen = new javax.swing.JLabel();
        msg_panel = new javax.swing.JPanel();
        msg_in_other = new javax.swing.JPanel();
        panel_Msg_in_other = new javax.swing.JPanel();
        label_MsgInOtherGif = new javax.swing.JLabel();
        label_MsgInOtherText = new javax.swing.JLabel();
        panel_MsgInOtherOkGreen = new javax.swing.JPanel();
        label_MsgInOtherOkGreen = new javax.swing.JLabel();
        panel_MsgInOtherHomeRed = new javax.swing.JPanel();
        label_MsgInOtherHomeRed = new javax.swing.JLabel();
        msg_in_welcome = new javax.swing.JPanel();
        panel_Msg_in_welcome = new javax.swing.JPanel();
        label_MsgInWelcomeGif = new javax.swing.JLabel();
        label_MsgInWelcomeText = new javax.swing.JLabel();
        panel_MsgInWelcomeOkGreen = new javax.swing.JPanel();
        label_MsgInWelcomeOkGreen = new javax.swing.JLabel();
        msg_textOnly = new javax.swing.JPanel();
        panel_TextOnly = new javax.swing.JPanel();
        label_TextOnlyGif = new javax.swing.JLabel();
        labelTextOnlyText = new javax.swing.JLabel();
        panel_WelcomeScreenTopBarGreen = new javax.swing.JPanel();
        panel_WelcomeScreenTopBarGreenCloseButtonRed = new javax.swing.JPanel();
        label_WelcomeScreenTopBarGreenCloseButtonRed = new javax.swing.JLabel();
        panel_WelcomeScreenTopBarGreenMinimizeButtonGray = new javax.swing.JPanel();
        label_WelcomeScreenTopBarGreenMinimizeButtonGray = new javax.swing.JLabel();
        label_WelcomeScreenTopBarGreenAppName = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TicTacToe");
        setIconImages(null);
        setUndecorated(true);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        mainPanel.setBackground(new java.awt.Color(0, 0, 0));
        mainPanel.setLayout(new java.awt.CardLayout());

        panelOne.setBackground(new java.awt.Color(0, 0, 0));

        panel_PanelOne.setBackground(new java.awt.Color(0, 0, 0));
        panel_PanelOne.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        label_WelcomeToTictactoe.setFont(new java.awt.Font("Gabriola", 1, 30)); // NOI18N
        label_WelcomeToTictactoe.setForeground(new java.awt.Color(255, 255, 255));
        label_WelcomeToTictactoe.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_WelcomeToTictactoe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic_gif/2ElJtFY9.gif"))); // NOI18N

        panel_WelcomeScreenStartANewGameGreen.setBackground(new java.awt.Color(0, 153, 0));
        panel_WelcomeScreenStartANewGameGreen.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_WelcomeScreenStartANewGameGreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenStartANewGameGreenMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenStartANewGameGreenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenStartANewGameGreenMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenStartANewGameGreenMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenStartANewGameGreenMouseReleased(evt);
            }
        });

        label_WelcomeScreenStartANewGameGreen.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label_WelcomeScreenStartANewGameGreen.setForeground(new java.awt.Color(255, 255, 255));
        label_WelcomeScreenStartANewGameGreen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_WelcomeScreenStartANewGameGreen.setText("Start a new game");
        label_WelcomeScreenStartANewGameGreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenStartANewGameGreenMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenStartANewGameGreenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenStartANewGameGreenMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenStartANewGameGreenMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenStartANewGameGreenMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panel_WelcomeScreenStartANewGameGreenLayout = new javax.swing.GroupLayout(panel_WelcomeScreenStartANewGameGreen);
        panel_WelcomeScreenStartANewGameGreen.setLayout(panel_WelcomeScreenStartANewGameGreenLayout);
        panel_WelcomeScreenStartANewGameGreenLayout.setHorizontalGroup(
            panel_WelcomeScreenStartANewGameGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_WelcomeScreenStartANewGameGreen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panel_WelcomeScreenStartANewGameGreenLayout.setVerticalGroup(
            panel_WelcomeScreenStartANewGameGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_WelcomeScreenStartANewGameGreen, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        label_WelcomeScreenVersion.setForeground(new java.awt.Color(255, 255, 255));
        label_WelcomeScreenVersion.setText("V 0.0.1");
        label_WelcomeScreenVersion.setToolTipText("Version 0.0.1");

        label_WelcomeScreenHelp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_WelcomeScreenHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic_normal/icons8_help_20px.png"))); // NOI18N
        label_WelcomeScreenHelp.setToolTipText("About");
        label_WelcomeScreenHelp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenHelpMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenHelpMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenHelpMouseExited(evt);
            }
        });

        panel_WelcomeScreenScoreHistoryBlue.setBackground(new java.awt.Color(0, 0, 153));
        panel_WelcomeScreenScoreHistoryBlue.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_WelcomeScreenScoreHistoryBlue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenScoreHistoryBlueMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenScoreHistoryBlueMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenScoreHistoryBlueMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenScoreHistoryBlueMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenScoreHistoryBlueMouseReleased(evt);
            }
        });

        label_WelcomeScreenScoreHistoryBlue.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label_WelcomeScreenScoreHistoryBlue.setForeground(new java.awt.Color(255, 255, 255));
        label_WelcomeScreenScoreHistoryBlue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_WelcomeScreenScoreHistoryBlue.setText("Scores history");
        label_WelcomeScreenScoreHistoryBlue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenScoreHistoryBlueMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenScoreHistoryBlueMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenScoreHistoryBlueMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenScoreHistoryBlueMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenScoreHistoryBlueMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panel_WelcomeScreenScoreHistoryBlueLayout = new javax.swing.GroupLayout(panel_WelcomeScreenScoreHistoryBlue);
        panel_WelcomeScreenScoreHistoryBlue.setLayout(panel_WelcomeScreenScoreHistoryBlueLayout);
        panel_WelcomeScreenScoreHistoryBlueLayout.setHorizontalGroup(
            panel_WelcomeScreenScoreHistoryBlueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_WelcomeScreenScoreHistoryBlue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panel_WelcomeScreenScoreHistoryBlueLayout.setVerticalGroup(
            panel_WelcomeScreenScoreHistoryBlueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_WelcomeScreenScoreHistoryBlue, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        panel_WelcomeScreenSettingsRed.setBackground(new java.awt.Color(204, 0, 51));
        panel_WelcomeScreenSettingsRed.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_WelcomeScreenSettingsRed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenSettingsRedMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenSettingsRedMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenSettingsRedMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenSettingsRedMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenSettingsRedMouseReleased(evt);
            }
        });

        label_WelcomeScreenSettingsRed.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label_WelcomeScreenSettingsRed.setForeground(new java.awt.Color(255, 255, 255));
        label_WelcomeScreenSettingsRed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_WelcomeScreenSettingsRed.setText("Settings");
        label_WelcomeScreenSettingsRed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenSettingsRedMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenSettingsRedMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenSettingsRedMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenSettingsRedMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenSettingsRedMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panel_WelcomeScreenSettingsRedLayout = new javax.swing.GroupLayout(panel_WelcomeScreenSettingsRed);
        panel_WelcomeScreenSettingsRed.setLayout(panel_WelcomeScreenSettingsRedLayout);
        panel_WelcomeScreenSettingsRedLayout.setHorizontalGroup(
            panel_WelcomeScreenSettingsRedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_WelcomeScreenSettingsRed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panel_WelcomeScreenSettingsRedLayout.setVerticalGroup(
            panel_WelcomeScreenSettingsRedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_WelcomeScreenSettingsRed, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        label_WelcomeScreenGifOneBear.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_WelcomeScreenGifOneBear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic_gif/bear-brown-bear(mody50).gif"))); // NOI18N

        label_WelcomeScreenGifTwoBear.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_WelcomeScreenGifTwoBear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic_gif/milk-and-mocha-cute(mody50).gif"))); // NOI18N

        javax.swing.GroupLayout panel_PanelOneLayout = new javax.swing.GroupLayout(panel_PanelOne);
        panel_PanelOne.setLayout(panel_PanelOneLayout);
        panel_PanelOneLayout.setHorizontalGroup(
            panel_PanelOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_PanelOneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_PanelOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_PanelOneLayout.createSequentialGroup()
                        .addGroup(panel_PanelOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_PanelOneLayout.createSequentialGroup()
                                .addComponent(label_WelcomeScreenVersion)
                                .addGap(63, 63, 63)
                                .addComponent(label_WelcomeScreenGifOneBear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_WelcomeScreenGifTwoBear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                                .addComponent(label_WelcomeScreenHelp))
                            .addComponent(panel_WelcomeScreenStartANewGameGreen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panel_WelcomeScreenScoreHistoryBlue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panel_WelcomeScreenSettingsRed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addComponent(label_WelcomeToTictactoe, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        panel_PanelOneLayout.setVerticalGroup(
            panel_PanelOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_PanelOneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_WelcomeToTictactoe, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(panel_WelcomeScreenStartANewGameGreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_WelcomeScreenScoreHistoryBlue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_WelcomeScreenSettingsRed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(panel_PanelOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_PanelOneLayout.createSequentialGroup()
                        .addGroup(panel_PanelOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_WelcomeScreenVersion, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_WelcomeScreenHelp, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_PanelOneLayout.createSequentialGroup()
                        .addGroup(panel_PanelOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_WelcomeScreenGifTwoBear)
                            .addComponent(label_WelcomeScreenGifOneBear))
                        .addGap(21, 21, 21))))
        );

        javax.swing.GroupLayout panelOneLayout = new javax.swing.GroupLayout(panelOne);
        panelOne.setLayout(panelOneLayout);
        panelOneLayout.setHorizontalGroup(
            panelOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_PanelOne, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelOneLayout.setVerticalGroup(
            panelOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_PanelOne, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainPanel.add(panelOne, "panelOne");

        panelTwo.setBackground(new java.awt.Color(0, 0, 0));

        btn_00.setBackground(new java.awt.Color(0, 0, 0));
        btn_00.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        label_00.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        label_00.setForeground(new java.awt.Color(51, 153, 255));
        label_00.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_00.setText("T");
        label_00.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_00MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_00MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_00MouseExited(evt);
            }
        });

        javax.swing.GroupLayout btn_00Layout = new javax.swing.GroupLayout(btn_00);
        btn_00.setLayout(btn_00Layout);
        btn_00Layout.setHorizontalGroup(
            btn_00Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_00, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btn_00Layout.setVerticalGroup(
            btn_00Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_00, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        btn_01.setBackground(new java.awt.Color(0, 0, 0));
        btn_01.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        label_01.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        label_01.setForeground(new java.awt.Color(255, 51, 51));
        label_01.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_01.setText("I");
        label_01.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_01MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_01MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_01MouseExited(evt);
            }
        });

        javax.swing.GroupLayout btn_01Layout = new javax.swing.GroupLayout(btn_01);
        btn_01.setLayout(btn_01Layout);
        btn_01Layout.setHorizontalGroup(
            btn_01Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_01, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btn_01Layout.setVerticalGroup(
            btn_01Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_01, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        btn_02.setBackground(new java.awt.Color(0, 0, 0));
        btn_02.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        label_02.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        label_02.setForeground(new java.awt.Color(255, 51, 51));
        label_02.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_02.setText("C");
        label_02.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_02MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_02MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_02MouseExited(evt);
            }
        });

        javax.swing.GroupLayout btn_02Layout = new javax.swing.GroupLayout(btn_02);
        btn_02.setLayout(btn_02Layout);
        btn_02Layout.setHorizontalGroup(
            btn_02Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_02, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btn_02Layout.setVerticalGroup(
            btn_02Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_02, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        btn_10.setBackground(new java.awt.Color(0, 0, 0));
        btn_10.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        label_10.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        label_10.setForeground(new java.awt.Color(255, 51, 51));
        label_10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_10.setText("T");
        label_10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_10MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_10MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_10MouseExited(evt);
            }
        });

        javax.swing.GroupLayout btn_10Layout = new javax.swing.GroupLayout(btn_10);
        btn_10.setLayout(btn_10Layout);
        btn_10Layout.setHorizontalGroup(
            btn_10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_10, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btn_10Layout.setVerticalGroup(
            btn_10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_10, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        btn_11.setBackground(new java.awt.Color(0, 0, 0));
        btn_11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        label_11.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        label_11.setForeground(new java.awt.Color(51, 153, 255));
        label_11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_11.setText("A");
        label_11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_11MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_11MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_11MouseExited(evt);
            }
        });

        javax.swing.GroupLayout btn_11Layout = new javax.swing.GroupLayout(btn_11);
        btn_11.setLayout(btn_11Layout);
        btn_11Layout.setHorizontalGroup(
            btn_11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_11, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btn_11Layout.setVerticalGroup(
            btn_11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_11, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        btn_12.setBackground(new java.awt.Color(0, 0, 0));
        btn_12.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        label_12.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        label_12.setForeground(new java.awt.Color(255, 51, 51));
        label_12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_12.setText("C");
        label_12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_12MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_12MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_12MouseExited(evt);
            }
        });

        javax.swing.GroupLayout btn_12Layout = new javax.swing.GroupLayout(btn_12);
        btn_12.setLayout(btn_12Layout);
        btn_12Layout.setHorizontalGroup(
            btn_12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_12, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btn_12Layout.setVerticalGroup(
            btn_12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_12, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        btn_20.setBackground(new java.awt.Color(0, 0, 0));
        btn_20.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        label_20.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        label_20.setForeground(new java.awt.Color(51, 153, 255));
        label_20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_20.setText("T");
        label_20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_20MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_20MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_20MouseExited(evt);
            }
        });

        javax.swing.GroupLayout btn_20Layout = new javax.swing.GroupLayout(btn_20);
        btn_20.setLayout(btn_20Layout);
        btn_20Layout.setHorizontalGroup(
            btn_20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_20, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btn_20Layout.setVerticalGroup(
            btn_20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_20, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        btn_21.setBackground(new java.awt.Color(0, 0, 0));
        btn_21.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        label_21.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        label_21.setForeground(new java.awt.Color(255, 51, 51));
        label_21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_21.setText("O");
        label_21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_21MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_21MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_21MouseExited(evt);
            }
        });

        javax.swing.GroupLayout btn_21Layout = new javax.swing.GroupLayout(btn_21);
        btn_21.setLayout(btn_21Layout);
        btn_21Layout.setHorizontalGroup(
            btn_21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_21, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btn_21Layout.setVerticalGroup(
            btn_21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_21, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        btn_22.setBackground(new java.awt.Color(0, 0, 0));
        btn_22.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        label_22.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        label_22.setForeground(new java.awt.Color(51, 153, 255));
        label_22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_22.setText("E");
        label_22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_22MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_22MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_22MouseExited(evt);
            }
        });

        javax.swing.GroupLayout btn_22Layout = new javax.swing.GroupLayout(btn_22);
        btn_22.setLayout(btn_22Layout);
        btn_22Layout.setHorizontalGroup(
            btn_22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_22, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        btn_22Layout.setVerticalGroup(
            btn_22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_22, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelTwoLayout = new javax.swing.GroupLayout(panelTwo);
        panelTwo.setLayout(panelTwoLayout);
        panelTwoLayout.setHorizontalGroup(
            panelTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTwoLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTwoLayout.createSequentialGroup()
                        .addComponent(btn_00, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_01, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_02, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelTwoLayout.createSequentialGroup()
                        .addComponent(btn_10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelTwoLayout.createSequentialGroup()
                        .addComponent(btn_20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTwoLayout.setVerticalGroup(
            panelTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTwoLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_02, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_01, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_00, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(panelTwo, "panelTwo");

        panelThree.setBackground(new java.awt.Color(0, 0, 0));

        panel_ScoreHistoryHomeBlue.setBackground(new java.awt.Color(0, 0, 153));
        panel_ScoreHistoryHomeBlue.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_ScoreHistoryHomeBlue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_ScoreHistoryHomeBlueMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_ScoreHistoryHomeBlueMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_ScoreHistoryHomeBlueMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_ScoreHistoryHomeBlueMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panel_ScoreHistoryHomeBlueMouseReleased(evt);
            }
        });

        label_ScoreHistoryHomeBlue.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        label_ScoreHistoryHomeBlue.setForeground(new java.awt.Color(255, 255, 255));
        label_ScoreHistoryHomeBlue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_ScoreHistoryHomeBlue.setText("Home");
        label_ScoreHistoryHomeBlue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_ScoreHistoryHomeBlueMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_ScoreHistoryHomeBlueMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_ScoreHistoryHomeBlueMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                label_ScoreHistoryHomeBlueMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                label_ScoreHistoryHomeBlueMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panel_ScoreHistoryHomeBlueLayout = new javax.swing.GroupLayout(panel_ScoreHistoryHomeBlue);
        panel_ScoreHistoryHomeBlue.setLayout(panel_ScoreHistoryHomeBlueLayout);
        panel_ScoreHistoryHomeBlueLayout.setHorizontalGroup(
            panel_ScoreHistoryHomeBlueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_ScoreHistoryHomeBlue, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );
        panel_ScoreHistoryHomeBlueLayout.setVerticalGroup(
            panel_ScoreHistoryHomeBlueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_ScoreHistoryHomeBlue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panel_ScoreHistoryResetRed.setBackground(new java.awt.Color(204, 0, 51));
        panel_ScoreHistoryResetRed.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_ScoreHistoryResetRed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_ScoreHistoryResetRedMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_ScoreHistoryResetRedMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_ScoreHistoryResetRedMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_ScoreHistoryResetRedMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panel_ScoreHistoryResetRedMouseReleased(evt);
            }
        });

        label_ScoreHistoryResetRed.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        label_ScoreHistoryResetRed.setForeground(new java.awt.Color(255, 255, 255));
        label_ScoreHistoryResetRed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_ScoreHistoryResetRed.setText("Reset");
        label_ScoreHistoryResetRed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_ScoreHistoryResetRedMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_ScoreHistoryResetRedMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_ScoreHistoryResetRedMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                label_ScoreHistoryResetRedMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                label_ScoreHistoryResetRedMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panel_ScoreHistoryResetRedLayout = new javax.swing.GroupLayout(panel_ScoreHistoryResetRed);
        panel_ScoreHistoryResetRed.setLayout(panel_ScoreHistoryResetRedLayout);
        panel_ScoreHistoryResetRedLayout.setHorizontalGroup(
            panel_ScoreHistoryResetRedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_ScoreHistoryResetRed, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );
        panel_ScoreHistoryResetRedLayout.setVerticalGroup(
            panel_ScoreHistoryResetRedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_ScoreHistoryResetRed, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
        );

        highScoreTable.setBackground(new java.awt.Color(0, 0, 0));
        highScoreTable.setForeground(new java.awt.Color(255, 255, 255));
        highScoreTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Date-Time", "Moves", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        highScoreTable.setAutoscrolls(false);
        highScoreTable.setOpaque(false);
        highScoreTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        highScoreTable.getTableHeader().setReorderingAllowed(false);
        scrollPane_ScoreHistory.setViewportView(highScoreTable);
        if (highScoreTable.getColumnModel().getColumnCount() > 0) {
            highScoreTable.getColumnModel().getColumn(0).setResizable(false);
            highScoreTable.getColumnModel().getColumn(1).setResizable(false);
            highScoreTable.getColumnModel().getColumn(2).setResizable(false);
        }

        javax.swing.GroupLayout panelThreeLayout = new javax.swing.GroupLayout(panelThree);
        panelThree.setLayout(panelThreeLayout);
        panelThreeLayout.setHorizontalGroup(
            panelThreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThreeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelThreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelThreeLayout.createSequentialGroup()
                        .addGap(0, 213, Short.MAX_VALUE)
                        .addComponent(panel_ScoreHistoryResetRed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel_ScoreHistoryHomeBlue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrollPane_ScoreHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelThreeLayout.setVerticalGroup(
            panelThreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThreeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane_ScoreHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelThreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_ScoreHistoryResetRed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_ScoreHistoryHomeBlue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        mainPanel.add(panelThree, "panelThree");

        panelFour.setBackground(new java.awt.Color(0, 0, 0));

        panel_SettingsHomeGreen.setBackground(new java.awt.Color(0, 153, 0));
        panel_SettingsHomeGreen.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_SettingsHomeGreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_SettingsHomeGreenMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_SettingsHomeGreenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_SettingsHomeGreenMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_SettingsHomeGreenMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panel_SettingsHomeGreenMouseReleased(evt);
            }
        });

        label_SettingsHomeGreen.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        label_SettingsHomeGreen.setForeground(new java.awt.Color(255, 255, 255));
        label_SettingsHomeGreen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_SettingsHomeGreen.setText("Home");
        label_SettingsHomeGreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_SettingsHomeGreenMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_SettingsHomeGreenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_SettingsHomeGreenMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                label_SettingsHomeGreenMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                label_SettingsHomeGreenMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panel_SettingsHomeGreenLayout = new javax.swing.GroupLayout(panel_SettingsHomeGreen);
        panel_SettingsHomeGreen.setLayout(panel_SettingsHomeGreenLayout);
        panel_SettingsHomeGreenLayout.setHorizontalGroup(
            panel_SettingsHomeGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_SettingsHomeGreen, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );
        panel_SettingsHomeGreenLayout.setVerticalGroup(
            panel_SettingsHomeGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_SettingsHomeGreen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panel_Settings.setBackground(new java.awt.Color(0, 0, 0));
        panel_Settings.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        label_SettingsBackgroundMusicTic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_SettingsBackgroundMusicTic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic_normal/icons8_tick_box_30px.png"))); // NOI18N
        label_SettingsBackgroundMusicTic.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_SettingsBackgroundMusicTicMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_SettingsBackgroundMusicTicMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_SettingsBackgroundMusicTicMouseExited(evt);
            }
        });

        label_SettingsAlwaysOnTopTic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_SettingsAlwaysOnTopTic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic_normal/icons8_unchecked_checkbox_30px.png"))); // NOI18N
        label_SettingsAlwaysOnTopTic.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_SettingsAlwaysOnTopTicMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_SettingsAlwaysOnTopTicMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_SettingsAlwaysOnTopTicMouseExited(evt);
            }
        });

        label_SettingsAlwaysOnTop.setBackground(new java.awt.Color(255, 0, 0));
        label_SettingsAlwaysOnTop.setFont(new java.awt.Font("Segoe Print", 1, 14)); // NOI18N
        label_SettingsAlwaysOnTop.setForeground(new java.awt.Color(255, 255, 255));
        label_SettingsAlwaysOnTop.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_SettingsAlwaysOnTop.setText("Always on top");
        label_SettingsAlwaysOnTop.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        label_SettingsAlwaysOnTop.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                label_SettingsAlwaysOnTopMouseMoved(evt);
            }
        });
        label_SettingsAlwaysOnTop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_SettingsAlwaysOnTopMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_SettingsAlwaysOnTopMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_SettingsAlwaysOnTopMouseExited(evt);
            }
        });

        label_SettingsBackgroundMusic.setBackground(new java.awt.Color(0, 255, 0));
        label_SettingsBackgroundMusic.setFont(new java.awt.Font("Segoe Print", 1, 14)); // NOI18N
        label_SettingsBackgroundMusic.setForeground(new java.awt.Color(255, 255, 255));
        label_SettingsBackgroundMusic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_SettingsBackgroundMusic.setText("Background music");
        label_SettingsBackgroundMusic.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        label_SettingsBackgroundMusic.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_SettingsBackgroundMusicMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_SettingsBackgroundMusicMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_SettingsBackgroundMusicMouseExited(evt);
            }
        });

        label_SettingsGameMusic.setBackground(new java.awt.Color(0, 255, 0));
        label_SettingsGameMusic.setFont(new java.awt.Font("Segoe Print", 1, 14)); // NOI18N
        label_SettingsGameMusic.setForeground(new java.awt.Color(255, 255, 255));
        label_SettingsGameMusic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_SettingsGameMusic.setText("Game music");
        label_SettingsGameMusic.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        label_SettingsGameMusic.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_SettingsGameMusicMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_SettingsGameMusicMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_SettingsGameMusicMouseExited(evt);
            }
        });

        label_SettingsGameMusicTic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_SettingsGameMusicTic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic_normal/icons8_tick_box_30px.png"))); // NOI18N
        label_SettingsGameMusicTic.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_SettingsGameMusicTicMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_SettingsGameMusicTicMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_SettingsGameMusicTicMouseExited(evt);
            }
        });

        label_SettingsKeySoundTic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_SettingsKeySoundTic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic_normal/icons8_tick_box_30px.png"))); // NOI18N
        label_SettingsKeySoundTic.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_SettingsKeySoundTicMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_SettingsKeySoundTicMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_SettingsKeySoundTicMouseExited(evt);
            }
        });

        label_SettingsKeySound.setBackground(new java.awt.Color(0, 255, 0));
        label_SettingsKeySound.setFont(new java.awt.Font("Segoe Print", 1, 14)); // NOI18N
        label_SettingsKeySound.setForeground(new java.awt.Color(255, 255, 255));
        label_SettingsKeySound.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_SettingsKeySound.setText("Key sounds");
        label_SettingsKeySound.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        label_SettingsKeySound.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_SettingsKeySoundMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_SettingsKeySoundMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_SettingsKeySoundMouseExited(evt);
            }
        });

        javax.swing.GroupLayout panel_SettingsLayout = new javax.swing.GroupLayout(panel_Settings);
        panel_Settings.setLayout(panel_SettingsLayout);
        panel_SettingsLayout.setHorizontalGroup(
            panel_SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_SettingsLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addGroup(panel_SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_SettingsLayout.createSequentialGroup()
                        .addComponent(label_SettingsKeySoundTic)
                        .addGap(18, 18, 18)
                        .addComponent(label_SettingsKeySound, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel_SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(panel_SettingsLayout.createSequentialGroup()
                            .addComponent(label_SettingsGameMusicTic)
                            .addGap(18, 18, 18)
                            .addComponent(label_SettingsGameMusic, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panel_SettingsLayout.createSequentialGroup()
                            .addGroup(panel_SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(label_SettingsAlwaysOnTopTic, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(label_SettingsBackgroundMusicTic, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addGap(18, 18, 18)
                            .addGroup(panel_SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(label_SettingsAlwaysOnTop, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(label_SettingsBackgroundMusic, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_SettingsLayout.setVerticalGroup(
            panel_SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_SettingsLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addGroup(panel_SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_SettingsAlwaysOnTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panel_SettingsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_SettingsAlwaysOnTopTic)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_SettingsBackgroundMusic)
                    .addComponent(label_SettingsBackgroundMusicTic))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_SettingsGameMusic)
                    .addComponent(label_SettingsGameMusicTic))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(label_SettingsKeySound)
                    .addComponent(label_SettingsKeySoundTic))
                .addGap(80, 80, 80))
        );

        javax.swing.GroupLayout panelFourLayout = new javax.swing.GroupLayout(panelFour);
        panelFour.setLayout(panelFourLayout);
        panelFourLayout.setHorizontalGroup(
            panelFourLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFourLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFourLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFourLayout.createSequentialGroup()
                        .addGap(0, 270, Short.MAX_VALUE)
                        .addComponent(panel_SettingsHomeGreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panel_Settings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelFourLayout.setVerticalGroup(
            panelFourLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFourLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_Settings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_SettingsHomeGreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mainPanel.add(panelFour, "panelFour");

        panelFive.setBackground(new java.awt.Color(0, 0, 0));

        label_WinLooseDrawDisplay.setFont(new java.awt.Font("Tw Cen MT Condensed", 0, 36)); // NOI18N
        label_WinLooseDrawDisplay.setForeground(new java.awt.Color(255, 255, 255));
        label_WinLooseDrawDisplay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_WinLooseDrawDisplay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        javax.swing.GroupLayout panelFiveLayout = new javax.swing.GroupLayout(panelFive);
        panelFive.setLayout(panelFiveLayout);
        panelFiveLayout.setHorizontalGroup(
            panelFiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiveLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_WinLooseDrawDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelFiveLayout.setVerticalGroup(
            panelFiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiveLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_WinLooseDrawDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainPanel.add(panelFive, "panelFive");

        panelSix.setBackground(new java.awt.Color(0, 0, 0));

        tabbedPane_Help.setBackground(new java.awt.Color(0, 0, 0));
        tabbedPane_Help.setAlignmentX(0.0F);
        tabbedPane_Help.setAlignmentY(0.0F);
        tabbedPane_Help.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabbedPane_Help.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jPanel7.setBackground(new java.awt.Color(0, 0, 0));
        jPanel7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Author :");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Version :");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Language :");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("JAVA");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("Don");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("0.0.1");

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("E-mail :");

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("mrdoncode@gmail.com");

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic_normal/icons8_abacus_35px.png"))); // NOI18N
        jLabel22.setText("ABACUS games");
        jLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel22.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        tabbedPane_Help.addTab("About", jPanel7);

        jPanel6.setBackground(new java.awt.Color(0, 0, 0));
        jPanel6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        errorLogTextArea.setBackground(new java.awt.Color(0, 0, 0));
        errorLogTextArea.setColumns(20);
        errorLogTextArea.setFont(new java.awt.Font("Monospaced", 0, 10)); // NOI18N
        errorLogTextArea.setForeground(new java.awt.Color(255, 255, 255));
        errorLogTextArea.setRows(5);
        errorLogTextArea.setAlignmentX(0.0F);
        errorLogTextArea.setAlignmentY(0.0F);
        errorLogTextArea.setBorder(null);
        errorLogTextArea.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        errorLogTextArea.setEnabled(false);
        jScrollPane2.setViewportView(errorLogTextArea);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane_Help.addTab("Error log", jPanel6);

        panel_HelpHomeGreen.setBackground(new java.awt.Color(0, 153, 0));
        panel_HelpHomeGreen.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_HelpHomeGreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_HelpHomeGreenMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_HelpHomeGreenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_HelpHomeGreenMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_HelpHomeGreenMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panel_HelpHomeGreenMouseReleased(evt);
            }
        });

        label_HelpHomeGreen.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        label_HelpHomeGreen.setForeground(new java.awt.Color(255, 255, 255));
        label_HelpHomeGreen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_HelpHomeGreen.setText("Home");
        label_HelpHomeGreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_HelpHomeGreenMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_HelpHomeGreenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_HelpHomeGreenMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                label_HelpHomeGreenMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                label_HelpHomeGreenMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panel_HelpHomeGreenLayout = new javax.swing.GroupLayout(panel_HelpHomeGreen);
        panel_HelpHomeGreen.setLayout(panel_HelpHomeGreenLayout);
        panel_HelpHomeGreenLayout.setHorizontalGroup(
            panel_HelpHomeGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_HelpHomeGreen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );
        panel_HelpHomeGreenLayout.setVerticalGroup(
            panel_HelpHomeGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_HelpHomeGreen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelSixLayout = new javax.swing.GroupLayout(panelSix);
        panelSix.setLayout(panelSixLayout);
        panelSixLayout.setHorizontalGroup(
            panelSixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSixLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPane_Help)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSixLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel_HelpHomeGreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelSixLayout.setVerticalGroup(
            panelSixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSixLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane_Help)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_HelpHomeGreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mainPanel.add(panelSix, "panelSix");

        msg_panel.setBackground(new java.awt.Color(255, 0, 0));
        msg_panel.setLayout(new java.awt.CardLayout());

        msg_in_other.setBackground(new java.awt.Color(0, 0, 255));

        panel_Msg_in_other.setBackground(new java.awt.Color(0, 0, 255));
        panel_Msg_in_other.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        label_MsgInOtherGif.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_MsgInOtherGif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic_gif/happy.gif"))); // NOI18N

        label_MsgInOtherText.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label_MsgInOtherText.setForeground(new java.awt.Color(255, 255, 255));
        label_MsgInOtherText.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_MsgInOtherText.setText("Hey there, let's play TicTacToe...");
        label_MsgInOtherText.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        panel_MsgInOtherOkGreen.setBackground(new java.awt.Color(51, 153, 0));
        panel_MsgInOtherOkGreen.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_MsgInOtherOkGreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_MsgInOtherOkGreenMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_MsgInOtherOkGreenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_MsgInOtherOkGreenMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_MsgInOtherOkGreenMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panel_MsgInOtherOkGreenMouseReleased(evt);
            }
        });

        label_MsgInOtherOkGreen.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        label_MsgInOtherOkGreen.setForeground(new java.awt.Color(255, 255, 255));
        label_MsgInOtherOkGreen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_MsgInOtherOkGreen.setText("Ok");
        label_MsgInOtherOkGreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_MsgInOtherOkGreenMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_MsgInOtherOkGreenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_MsgInOtherOkGreenMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                label_MsgInOtherOkGreenMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                label_MsgInOtherOkGreenMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panel_MsgInOtherOkGreenLayout = new javax.swing.GroupLayout(panel_MsgInOtherOkGreen);
        panel_MsgInOtherOkGreen.setLayout(panel_MsgInOtherOkGreenLayout);
        panel_MsgInOtherOkGreenLayout.setHorizontalGroup(
            panel_MsgInOtherOkGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_MsgInOtherOkGreen, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );
        panel_MsgInOtherOkGreenLayout.setVerticalGroup(
            panel_MsgInOtherOkGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_MsgInOtherOkGreen, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
        );

        panel_MsgInOtherHomeRed.setBackground(new java.awt.Color(204, 0, 51));
        panel_MsgInOtherHomeRed.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_MsgInOtherHomeRed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_MsgInOtherHomeRedMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_MsgInOtherHomeRedMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_MsgInOtherHomeRedMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_MsgInOtherHomeRedMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panel_MsgInOtherHomeRedMouseReleased(evt);
            }
        });

        label_MsgInOtherHomeRed.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        label_MsgInOtherHomeRed.setForeground(new java.awt.Color(255, 255, 255));
        label_MsgInOtherHomeRed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_MsgInOtherHomeRed.setText("Home");
        label_MsgInOtherHomeRed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_MsgInOtherHomeRedMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_MsgInOtherHomeRedMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_MsgInOtherHomeRedMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                label_MsgInOtherHomeRedMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                label_MsgInOtherHomeRedMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panel_MsgInOtherHomeRedLayout = new javax.swing.GroupLayout(panel_MsgInOtherHomeRed);
        panel_MsgInOtherHomeRed.setLayout(panel_MsgInOtherHomeRedLayout);
        panel_MsgInOtherHomeRedLayout.setHorizontalGroup(
            panel_MsgInOtherHomeRedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_MsgInOtherHomeRed, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );
        panel_MsgInOtherHomeRedLayout.setVerticalGroup(
            panel_MsgInOtherHomeRedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_MsgInOtherHomeRed, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panel_Msg_in_otherLayout = new javax.swing.GroupLayout(panel_Msg_in_other);
        panel_Msg_in_other.setLayout(panel_Msg_in_otherLayout);
        panel_Msg_in_otherLayout.setHorizontalGroup(
            panel_Msg_in_otherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_Msg_in_otherLayout.createSequentialGroup()
                .addComponent(label_MsgInOtherGif, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(panel_Msg_in_otherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_Msg_in_otherLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_MsgInOtherText, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panel_Msg_in_otherLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel_MsgInOtherHomeRed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel_MsgInOtherOkGreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        panel_Msg_in_otherLayout.setVerticalGroup(
            panel_Msg_in_otherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_Msg_in_otherLayout.createSequentialGroup()
                .addComponent(label_MsgInOtherText, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel_Msg_in_otherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel_MsgInOtherOkGreen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_MsgInOtherHomeRed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(label_MsgInOtherGif, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout msg_in_otherLayout = new javax.swing.GroupLayout(msg_in_other);
        msg_in_other.setLayout(msg_in_otherLayout);
        msg_in_otherLayout.setHorizontalGroup(
            msg_in_otherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(msg_in_otherLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_Msg_in_other, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        msg_in_otherLayout.setVerticalGroup(
            msg_in_otherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, msg_in_otherLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_Msg_in_other, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        msg_panel.add(msg_in_other, "msg_in_other");

        msg_in_welcome.setBackground(new java.awt.Color(0, 0, 255));

        panel_Msg_in_welcome.setBackground(new java.awt.Color(0, 0, 255));
        panel_Msg_in_welcome.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        label_MsgInWelcomeGif.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_MsgInWelcomeGif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic_gif/ehh.gif"))); // NOI18N

        label_MsgInWelcomeText.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label_MsgInWelcomeText.setForeground(new java.awt.Color(255, 255, 255));
        label_MsgInWelcomeText.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label_MsgInWelcomeText.setText("Are you done ?");
        label_MsgInWelcomeText.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        panel_MsgInWelcomeOkGreen.setBackground(new java.awt.Color(51, 153, 0));
        panel_MsgInWelcomeOkGreen.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_MsgInWelcomeOkGreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_MsgInWelcomeOkGreenMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_MsgInWelcomeOkGreenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_MsgInWelcomeOkGreenMouseExited(evt);
            }
        });

        label_MsgInWelcomeOkGreen.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        label_MsgInWelcomeOkGreen.setForeground(new java.awt.Color(255, 255, 255));
        label_MsgInWelcomeOkGreen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_MsgInWelcomeOkGreen.setText("Ok");
        label_MsgInWelcomeOkGreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_MsgInWelcomeOkGreenMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label_MsgInWelcomeOkGreenMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label_MsgInWelcomeOkGreenMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                label_MsgInWelcomeOkGreenMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                label_MsgInWelcomeOkGreenMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panel_MsgInWelcomeOkGreenLayout = new javax.swing.GroupLayout(panel_MsgInWelcomeOkGreen);
        panel_MsgInWelcomeOkGreen.setLayout(panel_MsgInWelcomeOkGreenLayout);
        panel_MsgInWelcomeOkGreenLayout.setHorizontalGroup(
            panel_MsgInWelcomeOkGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_MsgInWelcomeOkGreen, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );
        panel_MsgInWelcomeOkGreenLayout.setVerticalGroup(
            panel_MsgInWelcomeOkGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_MsgInWelcomeOkGreen, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panel_Msg_in_welcomeLayout = new javax.swing.GroupLayout(panel_Msg_in_welcome);
        panel_Msg_in_welcome.setLayout(panel_Msg_in_welcomeLayout);
        panel_Msg_in_welcomeLayout.setHorizontalGroup(
            panel_Msg_in_welcomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_Msg_in_welcomeLayout.createSequentialGroup()
                .addComponent(label_MsgInWelcomeGif, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(panel_Msg_in_welcomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_Msg_in_welcomeLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_MsgInWelcomeText, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panel_Msg_in_welcomeLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel_MsgInWelcomeOkGreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        panel_Msg_in_welcomeLayout.setVerticalGroup(
            panel_Msg_in_welcomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_Msg_in_welcomeLayout.createSequentialGroup()
                .addComponent(label_MsgInWelcomeText, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_MsgInWelcomeOkGreen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(label_MsgInWelcomeGif, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout msg_in_welcomeLayout = new javax.swing.GroupLayout(msg_in_welcome);
        msg_in_welcome.setLayout(msg_in_welcomeLayout);
        msg_in_welcomeLayout.setHorizontalGroup(
            msg_in_welcomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(msg_in_welcomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_Msg_in_welcome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        msg_in_welcomeLayout.setVerticalGroup(
            msg_in_welcomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, msg_in_welcomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_Msg_in_welcome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        msg_panel.add(msg_in_welcome, "msg_in_welcome");

        msg_textOnly.setBackground(new java.awt.Color(0, 0, 255));

        panel_TextOnly.setBackground(new java.awt.Color(0, 0, 255));
        panel_TextOnly.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));

        label_TextOnlyGif.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_TextOnlyGif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pic_gif/sleep.gif"))); // NOI18N

        labelTextOnlyText.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        labelTextOnlyText.setForeground(new java.awt.Color(255, 255, 255));
        labelTextOnlyText.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTextOnlyText.setText("Your move...");
        labelTextOnlyText.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout panel_TextOnlyLayout = new javax.swing.GroupLayout(panel_TextOnly);
        panel_TextOnly.setLayout(panel_TextOnlyLayout);
        panel_TextOnlyLayout.setHorizontalGroup(
            panel_TextOnlyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_TextOnlyLayout.createSequentialGroup()
                .addComponent(label_TextOnlyGif, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTextOnlyText, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panel_TextOnlyLayout.setVerticalGroup(
            panel_TextOnlyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_TextOnlyLayout.createSequentialGroup()
                .addComponent(labelTextOnlyText, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
            .addComponent(label_TextOnlyGif, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout msg_textOnlyLayout = new javax.swing.GroupLayout(msg_textOnly);
        msg_textOnly.setLayout(msg_textOnlyLayout);
        msg_textOnlyLayout.setHorizontalGroup(
            msg_textOnlyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(msg_textOnlyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_TextOnly, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        msg_textOnlyLayout.setVerticalGroup(
            msg_textOnlyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, msg_textOnlyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_TextOnly, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        msg_panel.add(msg_textOnly, "msg_textOnly");

        panel_WelcomeScreenTopBarGreen.setBackground(new java.awt.Color(0, 102, 51));
        panel_WelcomeScreenTopBarGreen.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenTopBarGreenMouseDragged(evt);
            }
        });
        panel_WelcomeScreenTopBarGreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenTopBarGreenMousePressed(evt);
            }
        });

        panel_WelcomeScreenTopBarGreenCloseButtonRed.setBackground(new java.awt.Color(255, 0, 0));
        panel_WelcomeScreenTopBarGreenCloseButtonRed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenTopBarGreenCloseButtonRedMouseClicked(evt);
            }
        });

        label_WelcomeScreenTopBarGreenCloseButtonRed.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label_WelcomeScreenTopBarGreenCloseButtonRed.setForeground(new java.awt.Color(255, 255, 255));
        label_WelcomeScreenTopBarGreenCloseButtonRed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_WelcomeScreenTopBarGreenCloseButtonRed.setText("X");
        label_WelcomeScreenTopBarGreenCloseButtonRed.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        label_WelcomeScreenTopBarGreenCloseButtonRed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenTopBarGreenCloseButtonRedMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenTopBarGreenCloseButtonRedMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenTopBarGreenCloseButtonRedMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panel_WelcomeScreenTopBarGreenCloseButtonRedLayout = new javax.swing.GroupLayout(panel_WelcomeScreenTopBarGreenCloseButtonRed);
        panel_WelcomeScreenTopBarGreenCloseButtonRed.setLayout(panel_WelcomeScreenTopBarGreenCloseButtonRedLayout);
        panel_WelcomeScreenTopBarGreenCloseButtonRedLayout.setHorizontalGroup(
            panel_WelcomeScreenTopBarGreenCloseButtonRedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_WelcomeScreenTopBarGreenCloseButtonRedLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(label_WelcomeScreenTopBarGreenCloseButtonRed, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panel_WelcomeScreenTopBarGreenCloseButtonRedLayout.setVerticalGroup(
            panel_WelcomeScreenTopBarGreenCloseButtonRedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_WelcomeScreenTopBarGreenCloseButtonRedLayout.createSequentialGroup()
                .addComponent(label_WelcomeScreenTopBarGreenCloseButtonRed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        panel_WelcomeScreenTopBarGreenMinimizeButtonGray.setBackground(new java.awt.Color(153, 153, 153));
        panel_WelcomeScreenTopBarGreenMinimizeButtonGray.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_WelcomeScreenTopBarGreenMinimizeButtonGrayMouseClicked(evt);
            }
        });

        label_WelcomeScreenTopBarGreenMinimizeButtonGray.setBackground(new java.awt.Color(153, 153, 153));
        label_WelcomeScreenTopBarGreenMinimizeButtonGray.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label_WelcomeScreenTopBarGreenMinimizeButtonGray.setForeground(new java.awt.Color(255, 255, 255));
        label_WelcomeScreenTopBarGreenMinimizeButtonGray.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_WelcomeScreenTopBarGreenMinimizeButtonGray.setText("-");
        label_WelcomeScreenTopBarGreenMinimizeButtonGray.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        label_WelcomeScreenTopBarGreenMinimizeButtonGray.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenTopBarGreenMinimizeButtonGrayMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenTopBarGreenMinimizeButtonGrayMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                label_WelcomeScreenTopBarGreenMinimizeButtonGrayMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panel_WelcomeScreenTopBarGreenMinimizeButtonGrayLayout = new javax.swing.GroupLayout(panel_WelcomeScreenTopBarGreenMinimizeButtonGray);
        panel_WelcomeScreenTopBarGreenMinimizeButtonGray.setLayout(panel_WelcomeScreenTopBarGreenMinimizeButtonGrayLayout);
        panel_WelcomeScreenTopBarGreenMinimizeButtonGrayLayout.setHorizontalGroup(
            panel_WelcomeScreenTopBarGreenMinimizeButtonGrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_WelcomeScreenTopBarGreenMinimizeButtonGray, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );
        panel_WelcomeScreenTopBarGreenMinimizeButtonGrayLayout.setVerticalGroup(
            panel_WelcomeScreenTopBarGreenMinimizeButtonGrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label_WelcomeScreenTopBarGreenMinimizeButtonGray, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        label_WelcomeScreenTopBarGreenAppName.setForeground(new java.awt.Color(255, 255, 255));
        label_WelcomeScreenTopBarGreenAppName.setText("TicTacToe");

        javax.swing.GroupLayout panel_WelcomeScreenTopBarGreenLayout = new javax.swing.GroupLayout(panel_WelcomeScreenTopBarGreen);
        panel_WelcomeScreenTopBarGreen.setLayout(panel_WelcomeScreenTopBarGreenLayout);
        panel_WelcomeScreenTopBarGreenLayout.setHorizontalGroup(
            panel_WelcomeScreenTopBarGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_WelcomeScreenTopBarGreenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_WelcomeScreenTopBarGreenAppName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panel_WelcomeScreenTopBarGreenMinimizeButtonGray, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panel_WelcomeScreenTopBarGreenCloseButtonRed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panel_WelcomeScreenTopBarGreenLayout.setVerticalGroup(
            panel_WelcomeScreenTopBarGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_WelcomeScreenTopBarGreenCloseButtonRed, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(panel_WelcomeScreenTopBarGreenMinimizeButtonGray, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(label_WelcomeScreenTopBarGreenAppName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(panel_WelcomeScreenTopBarGreen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(msg_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_WelcomeScreenTopBarGreen, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(msg_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void panel_WelcomeScreenTopBarGreenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenTopBarGreenMousePressed
        variables.setTopBarMousePress_xxyy(evt.getX(), evt.getY());
    }//GEN-LAST:event_panel_WelcomeScreenTopBarGreenMousePressed

    private void panel_WelcomeScreenTopBarGreenMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenTopBarGreenMouseDragged
        this.setLocation(evt.getXOnScreen() - variables.getTopBarMousePress_xx(), evt.getYOnScreen() - variables.getTopBarMousePress_yy());
    }//GEN-LAST:event_panel_WelcomeScreenTopBarGreenMouseDragged

    private void panel_WelcomeScreenTopBarGreenCloseButtonRedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenTopBarGreenCloseButtonRedMouseClicked
        function_WelcomeScreen_CloseButton();
    }//GEN-LAST:event_panel_WelcomeScreenTopBarGreenCloseButtonRedMouseClicked

    private void label_WelcomeScreenTopBarGreenMinimizeButtonGrayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenTopBarGreenMinimizeButtonGrayMouseClicked
        MiniMize();
    }//GEN-LAST:event_label_WelcomeScreenTopBarGreenMinimizeButtonGrayMouseClicked

    private void panel_WelcomeScreenStartANewGameGreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenStartANewGameGreenMouseClicked
        function_WelcomeScreen_StartANewGame();
    }//GEN-LAST:event_panel_WelcomeScreenStartANewGameGreenMouseClicked

    private void panel_ScoreHistoryHomeBlueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_ScoreHistoryHomeBlueMouseClicked
        goBackToHome();
    }//GEN-LAST:event_panel_ScoreHistoryHomeBlueMouseClicked

    private void label_ScoreHistoryResetRedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_ScoreHistoryResetRedMouseClicked
        resetButton();
    }//GEN-LAST:event_label_ScoreHistoryResetRedMouseClicked

    private void label_MsgInOtherHomeRedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInOtherHomeRedMouseClicked
        function_GameMessageScreen_TwoButton_Red();
    }//GEN-LAST:event_label_MsgInOtherHomeRedMouseClicked

    private void panel_MsgInOtherHomeRedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_MsgInOtherHomeRedMouseClicked
        function_GameMessageScreen_TwoButton_Red();
    }//GEN-LAST:event_panel_MsgInOtherHomeRedMouseClicked

    private void label_MsgInOtherOkGreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInOtherOkGreenMouseClicked
        greenButtonFromTwoButtonsInMessagePanel();
    }//GEN-LAST:event_label_MsgInOtherOkGreenMouseClicked

    private void panel_WelcomeScreenScoreHistoryBlueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenScoreHistoryBlueMouseClicked
        function_WelcomeScreen_HighScore();
    }//GEN-LAST:event_panel_WelcomeScreenScoreHistoryBlueMouseClicked

    private void panel_MsgInOtherOkGreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_MsgInOtherOkGreenMouseClicked
        greenButtonFromTwoButtonsInMessagePanel();
    }//GEN-LAST:event_panel_MsgInOtherOkGreenMouseClicked

    private void panel_WelcomeScreenSettingsRedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenSettingsRedMouseClicked
        function_WelcomeScreen_Settings();
    }//GEN-LAST:event_panel_WelcomeScreenSettingsRedMouseClicked

    private void label_SettingsHomeGreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsHomeGreenMouseClicked
        saveSettings();
        goBackToHome();
    }//GEN-LAST:event_label_SettingsHomeGreenMouseClicked

    private void panel_SettingsHomeGreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_SettingsHomeGreenMouseClicked
        saveSettings();
        goBackToHome();
    }//GEN-LAST:event_panel_SettingsHomeGreenMouseClicked

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated

    }//GEN-LAST:event_formWindowActivated

    private void label_MsgInWelcomeOkGreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInWelcomeOkGreenMouseClicked
        if (variables.getGameWindowGreenButtonEnable()) {
            if (variables.getKeySounds()) {
                functionRedGreenKeyButtonClickSound();
                variables.getRedGreenKeySoundsThread().start();
            }
            oneGreenBtn();
        }
    }//GEN-LAST:event_label_MsgInWelcomeOkGreenMouseClicked

    private void panel_MsgInWelcomeOkGreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_MsgInWelcomeOkGreenMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_MsgInWelcomeOkGreenMouseClicked


    private void label_00MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_00MouseClicked
        function_GameScreen_TicTacToeButton(0);
    }//GEN-LAST:event_label_00MouseClicked

    private void label_01MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_01MouseClicked
        function_GameScreen_TicTacToeButton(1);
    }//GEN-LAST:event_label_01MouseClicked

    private void label_02MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_02MouseClicked
        function_GameScreen_TicTacToeButton(2);
    }//GEN-LAST:event_label_02MouseClicked

    private void label_10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_10MouseClicked
        function_GameScreen_TicTacToeButton(3);
    }//GEN-LAST:event_label_10MouseClicked

    private void label_11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_11MouseClicked
        function_GameScreen_TicTacToeButton(4);
    }//GEN-LAST:event_label_11MouseClicked

    private void label_12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_12MouseClicked
        function_GameScreen_TicTacToeButton(5);
    }//GEN-LAST:event_label_12MouseClicked

    private void label_20MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_20MouseClicked
        function_GameScreen_TicTacToeButton(6);
    }//GEN-LAST:event_label_20MouseClicked

    private void label_21MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_21MouseClicked
        function_GameScreen_TicTacToeButton(7);
    }//GEN-LAST:event_label_21MouseClicked

    private void label_22MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_22MouseClicked
        function_GameScreen_TicTacToeButton(8);
    }//GEN-LAST:event_label_22MouseClicked


    private void panel_ScoreHistoryResetRedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_ScoreHistoryResetRedMouseClicked
        resetButton();
    }//GEN-LAST:event_panel_ScoreHistoryResetRedMouseClicked

    private void label_ScoreHistoryHomeBlueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_ScoreHistoryHomeBlueMouseClicked
        goBackToHome();
    }//GEN-LAST:event_label_ScoreHistoryHomeBlueMouseClicked

    private void label_WelcomeScreenStartANewGameGreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenStartANewGameGreenMouseClicked
        function_WelcomeScreen_StartANewGame();
    }//GEN-LAST:event_label_WelcomeScreenStartANewGameGreenMouseClicked

    private void label_WelcomeScreenScoreHistoryBlueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenScoreHistoryBlueMouseClicked
        function_WelcomeScreen_HighScore();
    }//GEN-LAST:event_label_WelcomeScreenScoreHistoryBlueMouseClicked

    private void label_WelcomeScreenSettingsRedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenSettingsRedMouseClicked
        function_WelcomeScreen_Settings();
    }//GEN-LAST:event_label_WelcomeScreenSettingsRedMouseClicked

    private void label_SettingsKeySoundMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsKeySoundMouseClicked
        settingsFunctionKeySounds();
    }//GEN-LAST:event_label_SettingsKeySoundMouseClicked

    private void label_SettingsGameMusicMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsGameMusicMouseClicked
        settingsFunctionGameMusic();
    }//GEN-LAST:event_label_SettingsGameMusicMouseClicked

    private void label_SettingsBackgroundMusicMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsBackgroundMusicMouseClicked
        settingsFunctionBackgroundMusic();
    }//GEN-LAST:event_label_SettingsBackgroundMusicMouseClicked

    private void label_SettingsAlwaysOnTopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsAlwaysOnTopMouseClicked
        settingsFunctionAlwaysOnTop();
    }//GEN-LAST:event_label_SettingsAlwaysOnTopMouseClicked

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus

    }//GEN-LAST:event_formWindowGainedFocus

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

    }//GEN-LAST:event_formWindowOpened

    private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified

    }//GEN-LAST:event_formWindowIconified

    private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus

    }//GEN-LAST:event_formWindowLostFocus

    private void panel_WelcomeScreenStartANewGameGreenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenStartANewGameGreenMousePressed
        panel_WelcomeScreenStartANewGameGreen.setBackground(constants.getWelcome_GREEN_light());
        panel_WelcomeScreenStartANewGameGreen.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_panel_WelcomeScreenStartANewGameGreenMousePressed

    private void panel_WelcomeScreenStartANewGameGreenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenStartANewGameGreenMouseReleased
        panel_WelcomeScreenStartANewGameGreen.setBackground(constants.getWelcome_GREEN());
        panel_WelcomeScreenStartANewGameGreen.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_panel_WelcomeScreenStartANewGameGreenMouseReleased

    private void label_WelcomeScreenStartANewGameGreenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenStartANewGameGreenMousePressed
        panel_WelcomeScreenStartANewGameGreen.setBackground(constants.getWelcome_GREEN_light());
        panel_WelcomeScreenStartANewGameGreen.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_label_WelcomeScreenStartANewGameGreenMousePressed

    private void label_WelcomeScreenStartANewGameGreenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenStartANewGameGreenMouseReleased
        panel_WelcomeScreenStartANewGameGreen.setBackground(constants.getWelcome_GREEN());
        panel_WelcomeScreenStartANewGameGreen.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_label_WelcomeScreenStartANewGameGreenMouseReleased

    private void panel_WelcomeScreenScoreHistoryBlueMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenScoreHistoryBlueMousePressed
        panel_WelcomeScreenScoreHistoryBlue.setBackground(constants.getWelcome_BLUE_light());
        panel_WelcomeScreenScoreHistoryBlue.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_panel_WelcomeScreenScoreHistoryBlueMousePressed

    private void panel_WelcomeScreenScoreHistoryBlueMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenScoreHistoryBlueMouseReleased
        panel_WelcomeScreenScoreHistoryBlue.setBackground(constants.getWelcome_BLUE());
        panel_WelcomeScreenScoreHistoryBlue.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_panel_WelcomeScreenScoreHistoryBlueMouseReleased

    private void label_WelcomeScreenScoreHistoryBlueMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenScoreHistoryBlueMousePressed
        panel_WelcomeScreenScoreHistoryBlue.setBackground(constants.getWelcome_BLUE_light());
        panel_WelcomeScreenScoreHistoryBlue.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_label_WelcomeScreenScoreHistoryBlueMousePressed

    private void label_WelcomeScreenScoreHistoryBlueMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenScoreHistoryBlueMouseReleased
        panel_WelcomeScreenScoreHistoryBlue.setBackground(constants.getWelcome_BLUE());
        panel_WelcomeScreenScoreHistoryBlue.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_label_WelcomeScreenScoreHistoryBlueMouseReleased

    private void panel_WelcomeScreenSettingsRedMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenSettingsRedMousePressed
        panel_WelcomeScreenSettingsRed.setBackground(constants.getWelcome_RED_light());
        panel_WelcomeScreenSettingsRed.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_panel_WelcomeScreenSettingsRedMousePressed

    private void panel_WelcomeScreenSettingsRedMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenSettingsRedMouseReleased
        panel_WelcomeScreenSettingsRed.setBackground(constants.getWelcome_RED());
        panel_WelcomeScreenSettingsRed.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_panel_WelcomeScreenSettingsRedMouseReleased

    private void label_WelcomeScreenSettingsRedMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenSettingsRedMousePressed
        panel_WelcomeScreenSettingsRed.setBackground(constants.getWelcome_RED_light());
        panel_WelcomeScreenSettingsRed.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_label_WelcomeScreenSettingsRedMousePressed

    private void label_WelcomeScreenSettingsRedMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenSettingsRedMouseReleased
        panel_WelcomeScreenSettingsRed.setBackground(constants.getWelcome_RED());
        panel_WelcomeScreenSettingsRed.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_label_WelcomeScreenSettingsRedMouseReleased

    private void panel_MsgInOtherOkGreenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_MsgInOtherOkGreenMousePressed
        if (variables.getGameWindowGreenButtonEnable()) {
            panel_MsgInOtherOkGreen.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        }
    }//GEN-LAST:event_panel_MsgInOtherOkGreenMousePressed

    private void panel_MsgInOtherOkGreenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_MsgInOtherOkGreenMouseReleased
        if (variables.getGameWindowGreenButtonEnable()) {
            panel_MsgInOtherOkGreen.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
        }
    }//GEN-LAST:event_panel_MsgInOtherOkGreenMouseReleased

    private void label_MsgInOtherOkGreenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInOtherOkGreenMousePressed
        if (variables.getGameWindowGreenButtonEnable()) {
            panel_MsgInOtherOkGreen.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        }
    }//GEN-LAST:event_label_MsgInOtherOkGreenMousePressed

    private void label_MsgInOtherOkGreenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInOtherOkGreenMouseReleased
        if (variables.getGameWindowGreenButtonEnable()) {
            panel_MsgInOtherOkGreen.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
        }
    }//GEN-LAST:event_label_MsgInOtherOkGreenMouseReleased

    private void panel_MsgInOtherHomeRedMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_MsgInOtherHomeRedMousePressed
        if (variables.getGameWindowRedButtonEnable()) {
            panel_MsgInOtherHomeRed.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        }
    }//GEN-LAST:event_panel_MsgInOtherHomeRedMousePressed

    private void panel_MsgInOtherHomeRedMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_MsgInOtherHomeRedMouseReleased
        if (variables.getGameWindowRedButtonEnable()) {
            panel_MsgInOtherHomeRed.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
        }
    }//GEN-LAST:event_panel_MsgInOtherHomeRedMouseReleased

    private void label_MsgInOtherHomeRedMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInOtherHomeRedMousePressed
        if (variables.getGameWindowRedButtonEnable()) {
            panel_MsgInOtherHomeRed.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        }
    }//GEN-LAST:event_label_MsgInOtherHomeRedMousePressed

    private void label_MsgInOtherHomeRedMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInOtherHomeRedMouseReleased
        if (variables.getGameWindowRedButtonEnable()) {
            panel_MsgInOtherHomeRed.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
        }
    }//GEN-LAST:event_label_MsgInOtherHomeRedMouseReleased

    private void label_MsgInWelcomeOkGreenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInWelcomeOkGreenMousePressed
        if (variables.getGameWindowGreenButtonEnable()) {
            panel_MsgInWelcomeOkGreen.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        }
    }//GEN-LAST:event_label_MsgInWelcomeOkGreenMousePressed

    private void label_MsgInWelcomeOkGreenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInWelcomeOkGreenMouseReleased
        if (variables.getGameWindowGreenButtonEnable()) {
            panel_MsgInWelcomeOkGreen.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
        }
    }//GEN-LAST:event_label_MsgInWelcomeOkGreenMouseReleased

    private void panel_ScoreHistoryHomeBlueMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_ScoreHistoryHomeBlueMousePressed
        panel_ScoreHistoryHomeBlue.setBackground(constants.getWelcome_BLUE_light());
        panel_ScoreHistoryHomeBlue.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_panel_ScoreHistoryHomeBlueMousePressed

    private void panel_ScoreHistoryHomeBlueMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_ScoreHistoryHomeBlueMouseReleased
        panel_ScoreHistoryHomeBlue.setBackground(constants.getWelcome_BLUE());
        panel_ScoreHistoryHomeBlue.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_panel_ScoreHistoryHomeBlueMouseReleased

    private void label_ScoreHistoryHomeBlueMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_ScoreHistoryHomeBlueMousePressed
        panel_ScoreHistoryHomeBlue.setBackground(constants.getWelcome_BLUE_light());
        panel_ScoreHistoryHomeBlue.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_label_ScoreHistoryHomeBlueMousePressed

    private void label_ScoreHistoryHomeBlueMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_ScoreHistoryHomeBlueMouseReleased
        panel_ScoreHistoryHomeBlue.setBackground(constants.getWelcome_BLUE());
        panel_ScoreHistoryHomeBlue.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_label_ScoreHistoryHomeBlueMouseReleased

    private void panel_ScoreHistoryResetRedMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_ScoreHistoryResetRedMousePressed
        panel_ScoreHistoryResetRed.setBackground(constants.getWelcome_RED_light());
        panel_ScoreHistoryResetRed.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_panel_ScoreHistoryResetRedMousePressed

    private void panel_ScoreHistoryResetRedMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_ScoreHistoryResetRedMouseReleased
        panel_ScoreHistoryResetRed.setBackground(constants.getWelcome_RED());
        panel_ScoreHistoryResetRed.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_panel_ScoreHistoryResetRedMouseReleased

    private void label_ScoreHistoryResetRedMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_ScoreHistoryResetRedMousePressed
        panel_ScoreHistoryResetRed.setBackground(constants.getWelcome_RED_light());
        panel_ScoreHistoryResetRed.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_label_ScoreHistoryResetRedMousePressed

    private void label_ScoreHistoryResetRedMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_ScoreHistoryResetRedMouseReleased
        panel_ScoreHistoryResetRed.setBackground(constants.getWelcome_RED());
        panel_ScoreHistoryResetRed.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_label_ScoreHistoryResetRedMouseReleased

    private void label_WelcomeScreenTopBarGreenCloseButtonRedMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenTopBarGreenCloseButtonRedMousePressed
        label_WelcomeScreenTopBarGreenCloseButtonRed.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_label_WelcomeScreenTopBarGreenCloseButtonRedMousePressed

    private void label_WelcomeScreenTopBarGreenCloseButtonRedMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenTopBarGreenCloseButtonRedMouseReleased
        label_WelcomeScreenTopBarGreenCloseButtonRed.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_label_WelcomeScreenTopBarGreenCloseButtonRedMouseReleased

    private void label_WelcomeScreenTopBarGreenCloseButtonRedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenTopBarGreenCloseButtonRedMouseClicked
        function_WelcomeScreen_CloseButton();
    }//GEN-LAST:event_label_WelcomeScreenTopBarGreenCloseButtonRedMouseClicked

    private void label_WelcomeScreenTopBarGreenMinimizeButtonGrayMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenTopBarGreenMinimizeButtonGrayMousePressed
        label_WelcomeScreenTopBarGreenMinimizeButtonGray.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_label_WelcomeScreenTopBarGreenMinimizeButtonGrayMousePressed

    private void label_WelcomeScreenTopBarGreenMinimizeButtonGrayMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenTopBarGreenMinimizeButtonGrayMouseReleased
        label_WelcomeScreenTopBarGreenMinimizeButtonGray.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_label_WelcomeScreenTopBarGreenMinimizeButtonGrayMouseReleased

    private void panel_WelcomeScreenTopBarGreenMinimizeButtonGrayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenTopBarGreenMinimizeButtonGrayMouseClicked
        MiniMize();
    }//GEN-LAST:event_panel_WelcomeScreenTopBarGreenMinimizeButtonGrayMouseClicked

    private void label_SettingsHomeGreenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsHomeGreenMousePressed
        panel_SettingsHomeGreen.setBackground(constants.getWelcome_GREEN_light());
        panel_SettingsHomeGreen.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_label_SettingsHomeGreenMousePressed

    private void panel_SettingsHomeGreenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_SettingsHomeGreenMousePressed
        panel_SettingsHomeGreen.setBackground(constants.getWelcome_GREEN_light());
        panel_SettingsHomeGreen.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_panel_SettingsHomeGreenMousePressed

    private void panel_SettingsHomeGreenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_SettingsHomeGreenMouseReleased
        panel_SettingsHomeGreen.setBackground(constants.getWelcome_GREEN());
        panel_SettingsHomeGreen.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_panel_SettingsHomeGreenMouseReleased

    private void label_SettingsHomeGreenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsHomeGreenMouseReleased
        panel_SettingsHomeGreen.setBackground(constants.getWelcome_GREEN());
        panel_SettingsHomeGreen.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_label_SettingsHomeGreenMouseReleased

    private void label_SettingsAlwaysOnTopTicMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsAlwaysOnTopTicMouseClicked
        settingsFunctionAlwaysOnTop();
    }//GEN-LAST:event_label_SettingsAlwaysOnTopTicMouseClicked

    private void label_SettingsAlwaysOnTopMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsAlwaysOnTopMouseMoved

    }//GEN-LAST:event_label_SettingsAlwaysOnTopMouseMoved

    private void label_SettingsBackgroundMusicTicMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsBackgroundMusicTicMouseClicked
        settingsFunctionBackgroundMusic();
    }//GEN-LAST:event_label_SettingsBackgroundMusicTicMouseClicked

    private void label_SettingsGameMusicTicMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsGameMusicTicMouseClicked
        settingsFunctionGameMusic();
    }//GEN-LAST:event_label_SettingsGameMusicTicMouseClicked

    private void label_SettingsKeySoundTicMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsKeySoundTicMouseClicked
        settingsFunctionKeySounds();
    }//GEN-LAST:event_label_SettingsKeySoundTicMouseClicked

    private void label_SettingsAlwaysOnTopMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsAlwaysOnTopMouseEntered
        handCursor();
    }//GEN-LAST:event_label_SettingsAlwaysOnTopMouseEntered

    private void label_SettingsAlwaysOnTopMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsAlwaysOnTopMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_SettingsAlwaysOnTopMouseExited

    private void label_SettingsBackgroundMusicMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsBackgroundMusicMouseEntered
        handCursor();
    }//GEN-LAST:event_label_SettingsBackgroundMusicMouseEntered

    private void label_SettingsBackgroundMusicMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsBackgroundMusicMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_SettingsBackgroundMusicMouseExited

    private void label_SettingsGameMusicMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsGameMusicMouseEntered
        handCursor();
    }//GEN-LAST:event_label_SettingsGameMusicMouseEntered

    private void label_SettingsGameMusicMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsGameMusicMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_SettingsGameMusicMouseExited

    private void label_SettingsKeySoundMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsKeySoundMouseEntered
        handCursor();
    }//GEN-LAST:event_label_SettingsKeySoundMouseEntered

    private void label_SettingsKeySoundMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsKeySoundMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_SettingsKeySoundMouseExited

    private void label_SettingsAlwaysOnTopTicMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsAlwaysOnTopTicMouseEntered
        handCursor();
    }//GEN-LAST:event_label_SettingsAlwaysOnTopTicMouseEntered

    private void label_SettingsAlwaysOnTopTicMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsAlwaysOnTopTicMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_SettingsAlwaysOnTopTicMouseExited

    private void label_SettingsBackgroundMusicTicMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsBackgroundMusicTicMouseEntered
        handCursor();
    }//GEN-LAST:event_label_SettingsBackgroundMusicTicMouseEntered

    private void label_SettingsBackgroundMusicTicMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsBackgroundMusicTicMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_SettingsBackgroundMusicTicMouseExited

    private void label_SettingsGameMusicTicMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsGameMusicTicMouseEntered
        handCursor();
    }//GEN-LAST:event_label_SettingsGameMusicTicMouseEntered

    private void label_SettingsGameMusicTicMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsGameMusicTicMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_SettingsGameMusicTicMouseExited

    private void label_SettingsKeySoundTicMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsKeySoundTicMouseEntered
        handCursor();
    }//GEN-LAST:event_label_SettingsKeySoundTicMouseEntered

    private void label_SettingsKeySoundTicMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsKeySoundTicMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_SettingsKeySoundTicMouseExited

    private void label_MsgInOtherOkGreenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInOtherOkGreenMouseEntered
        handCursor();
    }//GEN-LAST:event_label_MsgInOtherOkGreenMouseEntered

    private void label_MsgInOtherOkGreenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInOtherOkGreenMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_MsgInOtherOkGreenMouseExited

    private void label_MsgInOtherHomeRedMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInOtherHomeRedMouseEntered
        handCursor();
    }//GEN-LAST:event_label_MsgInOtherHomeRedMouseEntered

    private void label_MsgInOtherHomeRedMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInOtherHomeRedMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_MsgInOtherHomeRedMouseExited

    private void label_MsgInWelcomeOkGreenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInWelcomeOkGreenMouseEntered
        handCursor();
    }//GEN-LAST:event_label_MsgInWelcomeOkGreenMouseEntered

    private void label_MsgInWelcomeOkGreenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_MsgInWelcomeOkGreenMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_MsgInWelcomeOkGreenMouseExited

    private void panel_MsgInWelcomeOkGreenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_MsgInWelcomeOkGreenMouseEntered
        handCursor();
    }//GEN-LAST:event_panel_MsgInWelcomeOkGreenMouseEntered

    private void panel_MsgInWelcomeOkGreenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_MsgInWelcomeOkGreenMouseExited
        defaultCursor();
    }//GEN-LAST:event_panel_MsgInWelcomeOkGreenMouseExited

    private void panel_MsgInOtherOkGreenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_MsgInOtherOkGreenMouseEntered
        handCursor();
    }//GEN-LAST:event_panel_MsgInOtherOkGreenMouseEntered

    private void panel_MsgInOtherOkGreenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_MsgInOtherOkGreenMouseExited
        defaultCursor();
    }//GEN-LAST:event_panel_MsgInOtherOkGreenMouseExited

    private void panel_MsgInOtherHomeRedMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_MsgInOtherHomeRedMouseEntered
        handCursor();
    }//GEN-LAST:event_panel_MsgInOtherHomeRedMouseEntered

    private void panel_MsgInOtherHomeRedMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_MsgInOtherHomeRedMouseExited
        defaultCursor();
    }//GEN-LAST:event_panel_MsgInOtherHomeRedMouseExited

    private void label_WelcomeScreenStartANewGameGreenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenStartANewGameGreenMouseEntered
        handCursor();
    }//GEN-LAST:event_label_WelcomeScreenStartANewGameGreenMouseEntered

    private void label_WelcomeScreenStartANewGameGreenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenStartANewGameGreenMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_WelcomeScreenStartANewGameGreenMouseExited

    private void panel_WelcomeScreenStartANewGameGreenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenStartANewGameGreenMouseEntered
        handCursor();
    }//GEN-LAST:event_panel_WelcomeScreenStartANewGameGreenMouseEntered

    private void panel_WelcomeScreenStartANewGameGreenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenStartANewGameGreenMouseExited
        defaultCursor();
    }//GEN-LAST:event_panel_WelcomeScreenStartANewGameGreenMouseExited

    private void label_WelcomeScreenScoreHistoryBlueMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenScoreHistoryBlueMouseEntered
        handCursor();
    }//GEN-LAST:event_label_WelcomeScreenScoreHistoryBlueMouseEntered

    private void label_WelcomeScreenScoreHistoryBlueMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenScoreHistoryBlueMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_WelcomeScreenScoreHistoryBlueMouseExited

    private void panel_WelcomeScreenScoreHistoryBlueMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenScoreHistoryBlueMouseEntered
        handCursor();
    }//GEN-LAST:event_panel_WelcomeScreenScoreHistoryBlueMouseEntered

    private void panel_WelcomeScreenScoreHistoryBlueMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenScoreHistoryBlueMouseExited
        defaultCursor();
    }//GEN-LAST:event_panel_WelcomeScreenScoreHistoryBlueMouseExited

    private void label_WelcomeScreenSettingsRedMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenSettingsRedMouseEntered
        handCursor();
    }//GEN-LAST:event_label_WelcomeScreenSettingsRedMouseEntered

    private void label_WelcomeScreenSettingsRedMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenSettingsRedMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_WelcomeScreenSettingsRedMouseExited

    private void panel_WelcomeScreenSettingsRedMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenSettingsRedMouseEntered
        handCursor();
    }//GEN-LAST:event_panel_WelcomeScreenSettingsRedMouseEntered

    private void panel_WelcomeScreenSettingsRedMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_WelcomeScreenSettingsRedMouseExited
        defaultCursor();
    }//GEN-LAST:event_panel_WelcomeScreenSettingsRedMouseExited

    private void label_HelpHomeGreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_HelpHomeGreenMouseClicked
        goBackToHome();
    }//GEN-LAST:event_label_HelpHomeGreenMouseClicked

    private void label_HelpHomeGreenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_HelpHomeGreenMousePressed
        panel_HelpHomeGreen.setBackground(constants.getWelcome_GREEN_light());
        panel_HelpHomeGreen.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_label_HelpHomeGreenMousePressed

    private void label_HelpHomeGreenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_HelpHomeGreenMouseReleased
        panel_HelpHomeGreen.setBackground(constants.getWelcome_GREEN());
        panel_HelpHomeGreen.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_label_HelpHomeGreenMouseReleased

    private void panel_HelpHomeGreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_HelpHomeGreenMouseClicked
        goBackToHome();
    }//GEN-LAST:event_panel_HelpHomeGreenMouseClicked

    private void panel_HelpHomeGreenMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_HelpHomeGreenMousePressed
        panel_HelpHomeGreen.setBackground(constants.getWelcome_GREEN_light());
        panel_HelpHomeGreen.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
    }//GEN-LAST:event_panel_HelpHomeGreenMousePressed

    private void panel_HelpHomeGreenMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_HelpHomeGreenMouseReleased
        panel_HelpHomeGreen.setBackground(constants.getWelcome_GREEN());
        panel_HelpHomeGreen.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
    }//GEN-LAST:event_panel_HelpHomeGreenMouseReleased

    private void label_WelcomeScreenHelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenHelpMouseClicked
        msg_panel.setVisible(false); // Set invisible the main message panel
        variables.getCard_main().show(mainPanel, "panelSix"); // Set visible the game playing panel
    }//GEN-LAST:event_label_WelcomeScreenHelpMouseClicked

    private void label_WelcomeScreenHelpMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenHelpMouseEntered
        handCursor();
    }//GEN-LAST:event_label_WelcomeScreenHelpMouseEntered

    private void label_WelcomeScreenHelpMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_WelcomeScreenHelpMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_WelcomeScreenHelpMouseExited

    private void panel_HelpHomeGreenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_HelpHomeGreenMouseEntered
        handCursor();
    }//GEN-LAST:event_panel_HelpHomeGreenMouseEntered

    private void panel_HelpHomeGreenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_HelpHomeGreenMouseExited
        defaultCursor();
    }//GEN-LAST:event_panel_HelpHomeGreenMouseExited

    private void label_SettingsHomeGreenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsHomeGreenMouseEntered
        handCursor();
    }//GEN-LAST:event_label_SettingsHomeGreenMouseEntered

    private void label_SettingsHomeGreenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_SettingsHomeGreenMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_SettingsHomeGreenMouseExited

    private void panel_SettingsHomeGreenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_SettingsHomeGreenMouseEntered
        handCursor();
    }//GEN-LAST:event_panel_SettingsHomeGreenMouseEntered

    private void panel_SettingsHomeGreenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_SettingsHomeGreenMouseExited
        defaultCursor();
    }//GEN-LAST:event_panel_SettingsHomeGreenMouseExited

    private void label_HelpHomeGreenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_HelpHomeGreenMouseEntered
        handCursor();
    }//GEN-LAST:event_label_HelpHomeGreenMouseEntered

    private void label_HelpHomeGreenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_HelpHomeGreenMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_HelpHomeGreenMouseExited

    private void label_ScoreHistoryHomeBlueMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_ScoreHistoryHomeBlueMouseEntered
        handCursor();
    }//GEN-LAST:event_label_ScoreHistoryHomeBlueMouseEntered

    private void label_ScoreHistoryHomeBlueMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_ScoreHistoryHomeBlueMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_ScoreHistoryHomeBlueMouseExited

    private void panel_ScoreHistoryHomeBlueMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_ScoreHistoryHomeBlueMouseEntered
        handCursor();
    }//GEN-LAST:event_panel_ScoreHistoryHomeBlueMouseEntered

    private void panel_ScoreHistoryHomeBlueMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_ScoreHistoryHomeBlueMouseExited
        defaultCursor();
    }//GEN-LAST:event_panel_ScoreHistoryHomeBlueMouseExited

    private void label_ScoreHistoryResetRedMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_ScoreHistoryResetRedMouseEntered
        handCursor();
    }//GEN-LAST:event_label_ScoreHistoryResetRedMouseEntered

    private void label_ScoreHistoryResetRedMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_ScoreHistoryResetRedMouseExited
        defaultCursor();
    }//GEN-LAST:event_label_ScoreHistoryResetRedMouseExited

    private void panel_ScoreHistoryResetRedMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_ScoreHistoryResetRedMouseEntered
        handCursor();
    }//GEN-LAST:event_panel_ScoreHistoryResetRedMouseEntered

    private void panel_ScoreHistoryResetRedMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_ScoreHistoryResetRedMouseExited
        defaultCursor();
    }//GEN-LAST:event_panel_ScoreHistoryResetRedMouseExited

    private void label_00MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_00MouseEntered
        if (variables.getGameStart()) {
            handCursor();
        }
    }//GEN-LAST:event_label_00MouseEntered

    private void label_01MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_01MouseEntered
        if (variables.getGameStart()) {
            handCursor();
        }
    }//GEN-LAST:event_label_01MouseEntered

    private void label_02MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_02MouseEntered
        if (variables.getGameStart()) {
            handCursor();
        }
    }//GEN-LAST:event_label_02MouseEntered

    private void label_10MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_10MouseEntered
        if (variables.getGameStart()) {
            handCursor();
        }
    }//GEN-LAST:event_label_10MouseEntered

    private void label_11MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_11MouseEntered
        if (variables.getGameStart()) {
            handCursor();
        }
    }//GEN-LAST:event_label_11MouseEntered

    private void label_12MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_12MouseEntered
        if (variables.getGameStart()) {
            handCursor();
        }
    }//GEN-LAST:event_label_12MouseEntered

    private void label_20MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_20MouseEntered
        if (variables.getGameStart()) {
            handCursor();
        }
    }//GEN-LAST:event_label_20MouseEntered

    private void label_21MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_21MouseEntered
        if (variables.getGameStart()) {
            handCursor();
        }
    }//GEN-LAST:event_label_21MouseEntered

    private void label_22MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_22MouseEntered
        if (variables.getGameStart()) {
            handCursor();
        }
    }//GEN-LAST:event_label_22MouseEntered

    private void label_00MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_00MouseExited
        defaultCursor();
    }//GEN-LAST:event_label_00MouseExited

    private void label_01MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_01MouseExited
        defaultCursor();
    }//GEN-LAST:event_label_01MouseExited

    private void label_02MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_02MouseExited
        defaultCursor();
    }//GEN-LAST:event_label_02MouseExited

    private void label_10MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_10MouseExited
        defaultCursor();
    }//GEN-LAST:event_label_10MouseExited

    private void label_11MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_11MouseExited
        defaultCursor();
    }//GEN-LAST:event_label_11MouseExited

    private void label_12MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_12MouseExited
        defaultCursor();
    }//GEN-LAST:event_label_12MouseExited

    private void label_20MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_20MouseExited
        defaultCursor();
    }//GEN-LAST:event_label_20MouseExited

    private void label_21MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_21MouseExited
        defaultCursor();
    }//GEN-LAST:event_label_21MouseExited

    private void label_22MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_22MouseExited
        defaultCursor();
    }//GEN-LAST:event_label_22MouseExited

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel btn_00;
    private javax.swing.JPanel btn_01;
    private javax.swing.JPanel btn_02;
    private javax.swing.JPanel btn_10;
    private javax.swing.JPanel btn_11;
    private javax.swing.JPanel btn_12;
    private javax.swing.JPanel btn_20;
    private javax.swing.JPanel btn_21;
    private javax.swing.JPanel btn_22;
    private javax.swing.JTextArea errorLogTextArea;
    private javax.swing.JTable highScoreTable;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelTextOnlyText;
    public static javax.swing.JLabel label_00;
    public static javax.swing.JLabel label_01;
    public static javax.swing.JLabel label_02;
    public static javax.swing.JLabel label_10;
    public static javax.swing.JLabel label_11;
    public static javax.swing.JLabel label_12;
    public static javax.swing.JLabel label_20;
    public static javax.swing.JLabel label_21;
    public static javax.swing.JLabel label_22;
    private javax.swing.JLabel label_HelpHomeGreen;
    private javax.swing.JLabel label_MsgInOtherGif;
    private javax.swing.JLabel label_MsgInOtherHomeRed;
    private javax.swing.JLabel label_MsgInOtherOkGreen;
    private javax.swing.JLabel label_MsgInOtherText;
    private javax.swing.JLabel label_MsgInWelcomeGif;
    private javax.swing.JLabel label_MsgInWelcomeOkGreen;
    private javax.swing.JLabel label_MsgInWelcomeText;
    private javax.swing.JLabel label_ScoreHistoryHomeBlue;
    private javax.swing.JLabel label_ScoreHistoryResetRed;
    private javax.swing.JLabel label_SettingsAlwaysOnTop;
    private javax.swing.JLabel label_SettingsAlwaysOnTopTic;
    private javax.swing.JLabel label_SettingsBackgroundMusic;
    private javax.swing.JLabel label_SettingsBackgroundMusicTic;
    private javax.swing.JLabel label_SettingsGameMusic;
    private javax.swing.JLabel label_SettingsGameMusicTic;
    private javax.swing.JLabel label_SettingsHomeGreen;
    private javax.swing.JLabel label_SettingsKeySound;
    private javax.swing.JLabel label_SettingsKeySoundTic;
    private javax.swing.JLabel label_TextOnlyGif;
    private javax.swing.JLabel label_WelcomeScreenGifOneBear;
    private javax.swing.JLabel label_WelcomeScreenGifTwoBear;
    private javax.swing.JLabel label_WelcomeScreenHelp;
    private javax.swing.JLabel label_WelcomeScreenScoreHistoryBlue;
    private javax.swing.JLabel label_WelcomeScreenSettingsRed;
    private javax.swing.JLabel label_WelcomeScreenStartANewGameGreen;
    private javax.swing.JLabel label_WelcomeScreenTopBarGreenAppName;
    private javax.swing.JLabel label_WelcomeScreenTopBarGreenCloseButtonRed;
    private javax.swing.JLabel label_WelcomeScreenTopBarGreenMinimizeButtonGray;
    private javax.swing.JLabel label_WelcomeScreenVersion;
    private javax.swing.JLabel label_WelcomeToTictactoe;
    public static javax.swing.JLabel label_WinLooseDrawDisplay;
    public static javax.swing.JPanel mainPanel;
    private javax.swing.JPanel msg_in_other;
    private javax.swing.JPanel msg_in_welcome;
    private javax.swing.JPanel msg_panel;
    private javax.swing.JPanel msg_textOnly;
    private javax.swing.JPanel panelFive;
    private javax.swing.JPanel panelFour;
    private javax.swing.JPanel panelOne;
    private javax.swing.JPanel panelSix;
    private javax.swing.JPanel panelThree;
    private javax.swing.JPanel panelTwo;
    private javax.swing.JPanel panel_HelpHomeGreen;
    private javax.swing.JPanel panel_MsgInOtherHomeRed;
    private javax.swing.JPanel panel_MsgInOtherOkGreen;
    public static javax.swing.JPanel panel_MsgInWelcomeOkGreen;
    private javax.swing.JPanel panel_Msg_in_other;
    private javax.swing.JPanel panel_Msg_in_welcome;
    private javax.swing.JPanel panel_PanelOne;
    private javax.swing.JPanel panel_ScoreHistoryHomeBlue;
    private javax.swing.JPanel panel_ScoreHistoryResetRed;
    private javax.swing.JPanel panel_Settings;
    private javax.swing.JPanel panel_SettingsHomeGreen;
    private javax.swing.JPanel panel_TextOnly;
    private javax.swing.JPanel panel_WelcomeScreenScoreHistoryBlue;
    private javax.swing.JPanel panel_WelcomeScreenSettingsRed;
    private javax.swing.JPanel panel_WelcomeScreenStartANewGameGreen;
    private javax.swing.JPanel panel_WelcomeScreenTopBarGreen;
    private javax.swing.JPanel panel_WelcomeScreenTopBarGreenCloseButtonRed;
    private javax.swing.JPanel panel_WelcomeScreenTopBarGreenMinimizeButtonGray;
    private javax.swing.JScrollPane scrollPane_ScoreHistory;
    private javax.swing.JTabbedPane tabbedPane_Help;
    // End of variables declaration//GEN-END:variables
}
