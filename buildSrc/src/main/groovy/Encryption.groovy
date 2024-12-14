import java.nio.charset.StandardCharsets

class Encryption extends Cryption {

    @Override
    protected String updateLine(String line) {
        byte[] cipherText = cipher.doFinal(line.getBytes(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(cipherText)
    }
}