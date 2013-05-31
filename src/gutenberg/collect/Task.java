package gutenberg.collect;

import java.nio.file.Path;

public abstract class Task {
    
    public static Task[] getTasks() {
        Task[] tasks = new Task[3];
        tasks[0] = new Move();
        tasks[1] = new Explode();
        tasks[2] = new Update();
        return tasks;
    }
        
    protected abstract Path execute(Path file) throws Exception;    
    
    public static final String SUGGESTION_TRAY = "suggestiontray",
        LOCKER = "locker", SUGGESTION = "suggestion";
}
