import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ezex.model.FileTreeNode;

import java.io.File;

/**
 * Created by suem on 30.12.16.
 */
public class Test {
    public static void main(String[] args) {
        FileTreeNode ftn =FileTreeNode.ofFolder(new File("."));
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        System.out.println(gson.toJson(ftn));
    }
}
