package mcc.compiler;

public class Constants {
    public static int REGISTER_COUNT = 16;
    public static int REGISTER_BIT_COUNT = 8;
    public static int REGISTER_MAX_VALUE = (int)Math.pow(2, REGISTER_BIT_COUNT) - 1;
    public static int CHARACTER_COUNT = 20;


    public static int PIXEL_X_PORT = 240;
    public static int PIXEL_Y_PORT = 241;
    public static int DRAW_PIXEL_PORT = 242;
    public static int CLEAR_PIXEL_PORT = 243;
    public static int LOAD_PIXEL_PORT = 244;
    public static int BUFFER_SCREEN_PORT = 245;
    public static int CLEAR_SCREEN_BUFFER_PORT = 246;
    public static int WRITE_CHAR_PORT = 247;
    public static int BUFFER_CHARS_PORT = 248;
    public static int CLEAR_CHARS_BUFFER_PORT = 249;
    public static int SNOW_NUMBER_PORT = 250;
    public static int CLEAR_NUMBER_PORT = 251;
    public static int SIGNED_MODE_PORT = 252;
    public static int UNSIGNED_MODE_PORT = 253;
    public static int RNG_PORT = 254;
    public static int CONTROLLER_INPUT_PORT = 255;

    public static int LEFT = 1;
    public static int DOWN = 2;
    public static int RIGHT = 4;
    public static int UP = 8;
    public static int B = 16;
    public static int A = 32;
    public static int SELECT = 64;
    public static int START = 128;
}
