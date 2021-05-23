package etc;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class RealPath {

    public static String rootPath = "C:\\Users\\kengo\\Downloads\\tmp";

    public static void main(String[] args) throws IOException {

        File file = new File(rootPath, "..\\foo.txt");
        System.out.println(file.getAbsolutePath());
        System.out.println(file.exists()); // true になる

        FileSystem fs = FileSystems.getDefault();
        Path pathObj = fs.getPath(rootPath, "..\\foo.txt");

        System.out.println(pathObj.toRealPath().toString());
    }
}
