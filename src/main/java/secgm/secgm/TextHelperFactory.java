package secgm.secgm;

public class TextHelperFactory {
    public static TextHelper getTextHelper() {
        // Replace this with your actual version detection logic
        String minecraftVersion = "1.16"; // Or use a more dynamic approach
        
        if (minecraftVersion.startsWith("1.17")) {
            return new TextHelperV1_17();
        } else {
            return new TextHelperV1_16();
        }
    }
}