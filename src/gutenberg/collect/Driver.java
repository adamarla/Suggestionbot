package gutenberg.collect;

import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class Driver {

    /**
     * @param args - any arg will make it run in Debug mode
     * @throws IOException 
     */
    public static void main(String[] args) throws Exception {
        
        Path bank = FileSystems.getDefault().getPath("/opt/gutenberg/bank");
        Path semaphore = bank.resolve("suggestionbot");
        try {
            if (!Files.exists(semaphore))
                Files.createFile(semaphore);
            else
                return;
            
            Task[] tasks = Task.getTasks();            
            DirectoryStream<Path> suggestions = 
                Files.newDirectoryStream(bank.resolve(Task.SUGGESTION_TRAY), "*.0*");
            for (Path suggestion : suggestions) {
                System.out.println("Processing... " + suggestion.getFileName().toString());
                for (Task task: tasks) {
                    suggestion = task.execute(suggestion);
                    if (suggestion == null) break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Files.deleteIfExists(semaphore);
        }
    }
}
