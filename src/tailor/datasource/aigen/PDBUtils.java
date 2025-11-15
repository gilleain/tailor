package tailor.datasource.aigen;

import java.nio.file.Paths;

class PDBUtils {
    public static String getPDBIDFromPath(String path) {
        String filename = Paths.get(path).getFileName().toString();
        int dotIndex = filename.lastIndexOf('.');
        String nameWithoutExt = (dotIndex > 0) ? filename.substring(0, dotIndex) : filename;
        return nameWithoutExt.substring(0, Math.min(4, nameWithoutExt.length()));
    }
}
