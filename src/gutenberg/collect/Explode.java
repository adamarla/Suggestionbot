package gutenberg.collect;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Explode extends Task {
    
    /**
     * @param - path /bank/locker/0-teacher_id/sha1sum/suggestion.file_type
     * e.g. /bank/suggestiontray/0-105/a1b2c3d4e5/suggestion.01
     * @return /bank/locker/0-teacher_id/sha1sum
     * e.g. /bank/suggestiontray/0-105/a1b2c3d4e5
     */
    @Override
    protected Path execute(Path file) throws Exception {
        
        String filename = file.getFileName().toString();        
        String[] tokens = filename.split("\\.");
        String fileType = tokens[1];
        
        Path workingDirPath = file.getParent();
        Path working = workingDirPath.resolve(SUGGESTION);
        Files.copy(file, working);
        
        // {*.doc, *.docx, *.txt} => *.pdf
        if (fileType.equals("01")) {
            if (execute(workingDirPath, 
                String.format(libreOfficeCmd, SUGGESTION)) == 0)
                //output file of libreoffice includes .pdf
                Files.move(workingDirPath.resolve(SUGGESTION + ".pdf"), 
                    working, StandardCopyOption.REPLACE_EXISTING);
            else
                throw new Exception("libreoffice returned non-zero");
        }

        // {*.pdf, *.tiff, *.png ....} => *.jpg       
        if (execute(workingDirPath, String.format(convertCmd, SUGGESTION)) != 0)
            workingDirPath = null;
        
        Files.delete(working);        
        return workingDirPath;
    }    
    
    private int execute(Path workingDirPath, String command) throws Exception {
        
        String[] tokens = command.split(" ");
        System.out.print("[Locker]: execute");
        for (String token : tokens) {
            System.out.print(" " + token);
        }
        System.out.println();
        
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(tokens);

        pb.directory(workingDirPath.toFile());
        pb.redirectErrorStream(true);

        Process build = pb.start();
        BufferedReader messages = new 
                BufferedReader(new InputStreamReader(build.getInputStream()));

        String line = null;
        while ((line = messages.readLine()) != null) {
            System.out.println(line);
        }
        return build.waitFor();
    }
    
    private final String libreOfficeCmd = "libreoffice --headless --convert-to pdf %s";
    private final String convertCmd = "convert %s -resize 600x800 -scene 1 jpg:page-%%01d.jpeg";

}
