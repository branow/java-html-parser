import java.io.*;

public class FileText {

    private final File file;

    public FileText(File file) {
        this.file = file;
    }

    public FileText(String path) {
        this.file = new File(path);
    }

    public String read() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String s;
            while ((s = br.readLine()) != null)
                sb.append(s).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void write(String text) {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
            br.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void append(String text) {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(file, true))) {
            br.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
