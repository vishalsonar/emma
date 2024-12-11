import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

abstract class Cryption extends DefaultTask {

    private static final String NEW_LINE = "\n"
    private static final String SALT = "EMMA_SALT"
    private static final String ALGORITHM = "TripleDES"
    private static final String PASSWORD = "EMMA_PASSWORD"
    private static final String TRANSFORMATION = "TripleDES/CBC/PKCS5Padding";

    protected Cipher cipher

    @TaskAction
    protected void doCryption() {
        List<String> filePathList = new ArrayList<>()
        filePathList.add("src/main/java/com/sonar/vishal/emma/algorithm/OneGainAlgorithm.java")

        SecretKeySpec secretKeySpec = new SecretKeySpec(System.getenv(PASSWORD).getBytes(StandardCharsets.UTF_8), ALGORITHM)
        IvParameterSpec ivSpec = new IvParameterSpec(System.getenv(SALT).getBytes(StandardCharsets.UTF_8))
        cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(this instanceof Encryption ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secretKeySpec, ivSpec)
        filePathList.stream().map { filePath -> Path.of(filePath) }.forEach { filePath ->
            {
                if (Files.exists(filePath)) {
                    List<String> fileLines = new ArrayList<>()
                    Files.readAllLines(filePath).forEach { line -> fileLines.add(line) }
                    FileChannel.open(filePath, StandardOpenOption.WRITE).truncate(0).close()
                    fileLines.stream().map { line -> updateLine(line) }.forEach { line ->
                        {
                            Files.writeString(filePath, line + NEW_LINE, StandardOpenOption.APPEND)
                        }
                    }
                }
            }
        }
    }

    protected abstract String updateLine(String line)
}
