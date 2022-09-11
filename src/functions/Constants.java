/* Don-CODE */
package functions;

import java.awt.Color;

public class Constants {

    private final Color BLUE;
    private final Color welcome_BLUE;
    private final Color welcome_BLUE_light;
    private final Color RED;
    private final Color welcome_RED;
    private final Color welcome_RED_light;
    private final Color WHITE;
    private final Color GREEN;
    private final Color welcome_GREEN;
    private final Color welcome_GREEN_light;
    private final Color GRAY;
    private final Object[][] MESSAGES;
    private final String SOUND_background;
    private final String SOUND_click;
    private final String SOUND_gameMusic;
    private final String SOUND_loose;
    private final String SOUND_redGreenButton;
    private final String SOUND_win;
    private final String ERROR_FILENAME;
    private final String SETTINGS_FILENAME;
    private final String SCORE_FILENAME;
    private final String KEY;
    private final String MAIN_FILE;

    public Constants() {
        this.BLUE = new Color(51, 153, 255);
        this.welcome_BLUE = new Color(0, 0, 153);
        this.welcome_BLUE_light = new Color(0, 0, 190);
        this.RED = new Color(255, 51, 51);
        this.welcome_RED = new Color(204, 0, 51);
        this.welcome_RED_light = new Color(230, 0, 51);
        this.WHITE = new Color(255, 255, 255);
        this.GREEN = new Color(51, 153, 0);
        this.welcome_GREEN = new Color(0, 153, 0);
        this.welcome_GREEN_light = new Color(0, 176, 0);
        this.GRAY = new Color(102, 102, 102);// 0                                                                         1                                                                   2                                              3                                                      4                                               5                                                         6
        this.MESSAGES = new Object[][]{{"Hey there, let's play TicTacToe...", "pic_gif/happy.gif", "Ok", "Cancel"}, {"Do you want to start first ?", "pic_gif/blank.gif", "Yes", "No"}, {"Are you done ?", "pic_gif/sleep.gif", "Yes"}, {"Ha Ha I win...", "pic_gif/wink.gif", "Ok", "Home"}, {"You win...", "pic_gif/wow.gif", "Ok", "Home"}, {"Let's try again...", "pic_gif/blank.gif", "Yes", "No"}, {"Your move...", "pic_gif/ehh.gif"}};
        this.SOUND_background = "sound/background.wav";
        this.SOUND_click = "sound/click.wav";
        this.SOUND_gameMusic = "sound/gameMusic.wav";
        this.SOUND_loose = "sound/loose.wav";
        this.SOUND_redGreenButton = "sound/redgreenbutton.wav";
        this.SOUND_win = "sound/win.wav";
        this.ERROR_FILENAME = "\\TikTacToe_errorLog.tok";
        this.SETTINGS_FILENAME = "\\TikTacToe_settings.tok";
        this.SCORE_FILENAME = "\\TikTacToe_score.tok";
        this.KEY = "#a$1O3k[B)%=2Dv5";
        this.MAIN_FILE = "\\Documents\\TikTacToe";
    }

    public String getKEY() {
        return this.KEY;
    }

    public String getMAIN_FILE() {
        return System.getProperty("user.home") + this.MAIN_FILE;
    }

    public String getSCORE_FILENAME() {
        return getMAIN_FILE() + this.SCORE_FILENAME;
    }

    public String getSETTINGS_FILENAME() {
        return getMAIN_FILE() + this.SETTINGS_FILENAME;
    }

    public String getERROR_FILENAME() {
        return getMAIN_FILE() + this.ERROR_FILENAME;
    }

    public String getSOUND_background() {
        return this.SOUND_background;
    }

    public String getSOUND_click() {
        return this.SOUND_click;
    }

    public String getSOUND_gameMusic() {
        return this.SOUND_gameMusic;
    }

    public String getSOUND_loose() {
        return this.SOUND_loose;
    }

    public String getSOUND_redGreenButton() {
        return this.SOUND_redGreenButton;
    }

    public String getSOUND_win() {
        return this.SOUND_win;
    }

    public Color getWelcome_GREEN_light() {
        return this.welcome_GREEN_light;
    }

    public Color getWelcome_GREEN() {
        return this.welcome_GREEN;
    }

    public Color getWelcome_RED_light() {
        return this.welcome_RED_light;
    }

    public Color getWelcome_RED() {
        return this.welcome_RED;
    }

    public Color getWelcome_BLUE_light() {
        return this.welcome_BLUE_light;
    }

    public Color getWelcome_BLUE() {
        return this.welcome_BLUE;
    }

    public Color getBlue() {
        return this.BLUE;
    }

    public Color getRed() {
        return this.RED;
    }

    public Color getWhite() {
        return this.WHITE;
    }

    public Color getGreen() {
        return this.GREEN;
    }

    public Color getGray() {
        return this.GRAY;
    }

    public String getMessages(int i, int j) {
        return this.MESSAGES[i][j].toString();
    }

}
