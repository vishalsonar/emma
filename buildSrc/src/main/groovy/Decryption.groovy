class Decryption extends Cryption {

    @Override
    protected String updateLine(String line) {
        return new String(cipher.doFinal(Base64.getDecoder().decode(line)))
    }
}