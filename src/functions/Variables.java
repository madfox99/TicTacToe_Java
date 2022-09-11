/* Don-CODE */
package functions;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Collections;
import javax.sound.sampled.Clip;

public class Variables {

    private CardLayout card_main;
    private CardLayout card_msg;
    private boolean gameStart;
    private boolean userFirstPlaying;
    private boolean userWin;
    private boolean gameWindowRedButtonEnable;
    private boolean gameWindowGreenButtonEnable;
    private boolean[] gameButtonCanSelect;
    private int[] tempClick;
    private String[] player;
    private int messageNo;
    private int topBarMousePress_xx;
    private int topBarMousePress_yy;
    private int btn_xValue;
    private int btn_yValue;
    private Thread BlinkingColorTextMakingThread;
    private Thread WinLooseDisplayThread; // Game Win Loose Draw sounds
    private Thread gameMusicThread; // Game music while message displaying
    private Thread keySoundsThread; // Game [X] [O] button sound
    private Thread backgroundMusicThread; // Game Main background music
    private Thread redGreenKeySoundsThread; // Game Red Green dicision buttons sound
    private boolean backgroundMusic;
    private boolean keySounds;
    private boolean gameMusic;
    private boolean alwaysOnTop;
    private engine.Game game;
    private Clip clipBackgroundMusic;
    private Clip clipGameMusic;
    private Clip clipsetWinLooseDisplay;
    private boolean settingsBackgroundMusic;
    private boolean setNewTicTacToetext;
    private boolean startANewGameClicked;
    private ArrayList<String> scoreList;
    private int clickCount;

    public Variables() {
        this.gameStart = false;
        this.userFirstPlaying = false;
        this.userWin = false;
        this.gameWindowRedButtonEnable = false;
        this.gameWindowGreenButtonEnable = false;
        this.gameButtonCanSelect = new boolean[]{true, true, true, true, true, true, true, true, true};
        this.tempClick = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        this.player = new String[]{null, null, null, null, null, null, null, null, null};
        this.messageNo = 0;
        this.backgroundMusic = true;
        this.keySounds = true;
        this.gameMusic = true;
        this.alwaysOnTop = false;
        this.settingsBackgroundMusic = true;
        this.setNewTicTacToetext = false;
        this.startANewGameClicked = false;
        this.BlinkingColorTextMakingThread = null;
        this.WinLooseDisplayThread = null;
        this.gameMusicThread = null;
        this.keySoundsThread = null;
        this.backgroundMusicThread = null;
        this.scoreList = new ArrayList();
        this.clickCount = 0;
    }

    public void setVariablesToDefault() {
        this.gameStart = false;
        this.userFirstPlaying = false;
        this.userWin = false;
        this.gameWindowRedButtonEnable = false;
        this.gameWindowGreenButtonEnable = false;
        this.gameButtonCanSelect = new boolean[]{true, true, true, true, true, true, true, true, true};
        this.tempClick = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        this.player = new String[]{null, null, null, null, null, null, null, null, null};
        this.messageNo = 0;
        this.BlinkingColorTextMakingThread = null;
        this.gameMusicThread = null;
        this.keySoundsThread = null;
        this.redGreenKeySoundsThread = null;
        this.clipGameMusic = null;
        this.setNewTicTacToetext = false;
        this.startANewGameClicked = false;
        this.game = null;
        System.gc();
    }

    public int getClickCount() {
        return this.clickCount;
    }

    public void setClickCount() {
        this.clickCount++;
    }

    public void setClickCountZERO() {
        this.clickCount = 0;
    }

    public void setScoreList(String score) {
        if (this.scoreList.size() > 16) {
            this.scoreList.remove(0);
        }
        this.scoreList.add(score);
    }

    public void setScoreListZERO() {
        for (int i = 0; i < this.scoreList.size(); i++) {
            this.scoreList.remove(i);
        }
    }

    public ArrayList getScoreList() {
        ArrayList<String> NEW = (ArrayList) this.scoreList.clone();
        Collections.reverse(NEW);
        return NEW;
    }

    public ArrayList getScoreListOrigin() {
        return this.scoreList;
    }

    public void setClipsetWinLooseDisplay(Clip clipsetWinLooseDisplay) {
        this.clipsetWinLooseDisplay = clipsetWinLooseDisplay;
    }

    public Clip getClipsetWinLooseDisplay() {
        return this.clipsetWinLooseDisplay;
    }

    public void setStartANewGameClicked(boolean startANewGameClicked) {
        this.startANewGameClicked = startANewGameClicked;
    }

    public boolean getStartANewGameClicked() {
        return this.startANewGameClicked;
    }

    public void setSetNewTicTacToetext(boolean setNewTicTacToetext) {
        this.setNewTicTacToetext = setNewTicTacToetext;
    }

    public boolean getSetNewTicTacToetext() {
        return this.setNewTicTacToetext;
    }

    public void setRedGreenKeySoundsThread(Thread redGreenKeySoundsThread) {
        this.redGreenKeySoundsThread = redGreenKeySoundsThread;
    }

    public Thread getRedGreenKeySoundsThread() {
        return this.redGreenKeySoundsThread;
    }

    public void setClipGameMusic(Clip clipGameMusic) {
        this.clipGameMusic = clipGameMusic;
    }

    public Clip getClipGameMusic() {
        return this.clipGameMusic;
    }

    public void setGameMusicThread(Thread gameMusicThread) {
        this.gameMusicThread = gameMusicThread;
    }

    public Thread getGameMusicThread() {
        return this.gameMusicThread;
    }

    public void setSettingsBackgroundMusic(boolean settingsBackgroundMusic) {
        this.settingsBackgroundMusic = settingsBackgroundMusic;
    }

    public boolean getSettingsBackgroundMusic() {
        return this.settingsBackgroundMusic;
    }

    public void setClipBackgroundMusic(Clip clipBackgroundMusic) {
        this.clipBackgroundMusic = clipBackgroundMusic;
    }

    public Clip getClipBackgroundMusic() {
        return this.clipBackgroundMusic;
    }

    public void setGame(engine.Game game) {
        this.game = game;
    }

    public engine.Game getGame() {
        return this.game;
    }

    public void setBackgroundMusicThread(Thread backgroundMusicThread) {
        this.backgroundMusicThread = backgroundMusicThread;
    }

    public Thread getBackgroundMusicThread() {
        return this.backgroundMusicThread;
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        this.alwaysOnTop = alwaysOnTop;
    }

    public boolean getAlwaysOnTop() {
        return this.alwaysOnTop;
    }

    public void setGameMusic(boolean gameMusic) {
        this.gameMusic = gameMusic;
    }

    public boolean getGameMusic() {
        return this.gameMusic;
    }

    public void setKeySounds(boolean keySounds) {
        this.keySounds = keySounds;
    }

    public boolean getKeySounds() {
        return this.keySounds;
    }

    public void setBackgroundMusic(boolean backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
    }

    public boolean getBackgroundMusic() {
        return this.backgroundMusic;
    }

    public void setPlayer(int no, String value) {
        this.player[no] = value;
    }

    public String getPlayer(int no) {
        return this.player[no];
    }

    public void setKeySoundsThread(Thread keySoundsThread) {
        this.keySoundsThread = keySoundsThread;
    }

    public Thread getKeySoundsThread() {
        return this.keySoundsThread;
    }

    public void setWinLooseDisplayThread(Thread WinLooseDisplayThread) {
        this.WinLooseDisplayThread = WinLooseDisplayThread;
    }

    public Thread getWinLooseDisplayThread() {
        return this.WinLooseDisplayThread;
    }

    public void setBlinkingColorTextMakingThread(Thread BlinkingColorTextMakingThread) {
        this.BlinkingColorTextMakingThread = BlinkingColorTextMakingThread;
    }

    public Thread getBlinkingColorTextMakingThread() {
        return this.BlinkingColorTextMakingThread;
    }

    public void setBtn_xyValue(int btn_xValue, int btn_yValue) {
        this.btn_xValue = btn_xValue;
        this.btn_yValue = btn_yValue;
    }

    public int getBtn_xValue() {
        return this.btn_xValue;
    }

    public int getBtn_yValue() {
        return this.btn_yValue;
    }

    public void setTempClick(int no, int value) {
        this.tempClick[no] = value;
    }

    public int getTempClick(int no) {
        return tempClick[no];
    }

    public void setMessageNo(int messageNo) {
        this.messageNo = messageNo;
    }

    public int getMessageNo() {
        return this.messageNo;
    }

    public void setTopBarMousePress_xxyy(int topBarMousePress_xx, int topBarMousePress_yy) {
        this.topBarMousePress_xx = topBarMousePress_xx;
        this.topBarMousePress_yy = topBarMousePress_yy;
    }

    public int getTopBarMousePress_xx() {
        return this.topBarMousePress_xx;
    }

    public int getTopBarMousePress_yy() {
        return this.topBarMousePress_yy;
    }

    public void setCard_main(CardLayout card_main) {
        this.card_main = card_main;
    }

    public CardLayout getCard_main() {
        return this.card_main;
    }

    public void setCard_msg(CardLayout card_msg) {
        this.card_msg = card_msg;
    }

    public CardLayout getCard_msg() {
        return this.card_msg;
    }

    public void setGameStart(boolean gameStart) {
        this.gameStart = gameStart;
    }

    public boolean getGameStart() {
        return this.gameStart;
    }

    public void setUserFirstPlaying(boolean userFirstPlaying) {
        this.userFirstPlaying = userFirstPlaying;
    }

    public boolean getUserFirstPlaying() {
        return this.userFirstPlaying;
    }

    public void setUserWin(boolean userWin) {
        this.userWin = userWin;
    }

    public boolean getuserWin() {
        return this.userWin;
    }

    public void setGameWindowRedButtonEnable(boolean gameWindowRedButtonEnable) {
        this.gameWindowRedButtonEnable = gameWindowRedButtonEnable;
    }

    public boolean getGameWindowRedButtonEnable() {
        return this.gameWindowRedButtonEnable;
    }

    public void setGameWindowGreenButtonEnable(boolean gameWindowGreenButtonEnable) {
        this.gameWindowGreenButtonEnable = gameWindowGreenButtonEnable;
    }

    public boolean getGameWindowGreenButtonEnable() {
        return this.gameWindowGreenButtonEnable;
    }

    public void setGameButtonCanSelect(int no, boolean value) {
        this.gameButtonCanSelect[no] = value;
    }

    public boolean getGameButtonCanSelect(int no) {
        return this.gameButtonCanSelect[no];
    }

}
