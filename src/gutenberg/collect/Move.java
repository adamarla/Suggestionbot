package gutenberg.collect;

import java.nio.file.Files;
import java.nio.file.Path;

public class Move extends Task {
    
    /**
     * @param path - /bank/suggestiontray/sha1sum.teacher_id.file_type 
     * e.g. /bank/suggestiontray/a1b2c3d4e5.105.01
     * @return /bank/locker/teacher_id/sha1sum/suggestion.file_type
     * e.g. /bank/locker/105/a1b2c3d4e5/suggestion.01
     */
    @Override
    protected Path execute(Path path) throws Exception {
        
        String filename = path.getFileName().toString();        
        String[] tokens = filename.split("\\.");
        String sha1sum = tokens[0];
        String teacherId = tokens[1];
        String fileType = tokens[2];
        
        Path target = null;
        Path root = path.getParent().getParent();
        Path workingDir = root.resolve(LOCKER).resolve("0-" + teacherId).
            resolve(sha1sum);
        if (!Files.exists(workingDir)) {
            Files.createDirectories(workingDir);
            target = workingDir.resolve(SUGGESTION + "." + fileType);
            Files.copy(path, target);
        }
        Files.delete(path);
        return target;
    }
}
