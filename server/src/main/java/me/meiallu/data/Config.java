package me.meiallu.data;

import com.esotericsoftware.yamlbeans.YamlReader;
import me.meiallu.Cabba;
import me.meiallu.database.Database;
import me.meiallu.database.DiskDatabase;
import me.meiallu.database.MemoryDatabase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

public class Config {

    public boolean cli;
    public boolean require_pass;

    public int port;
    public String password;

    public String[] allowed_addresses;
    public int save_delay;

    public static Config load() {
        try {
            File folder = new File("cabba");
            folder.mkdir();

            File configFile = new File("cabba/config.yml");
            File dumpFile = new File("cabba/memory.dump");

            if (dumpFile.exists()) {
                HashMap<String, Database> hash = (HashMap<String, Database>) Cabba.readFileToObject("cabba/memory.dump");
                Cabba.setDatabases(hash);
            } else {
                Cabba.setDatabases(new HashMap<>());
                new MemoryDatabase("default");
            }

            for (File file : folder.listFiles())
                if (file.isDirectory())
                    new DiskDatabase(file.getName());

            if (!configFile.exists()) {
                URL configURL = Cabba.class.getResource("/config.yml");
                FileUtils.copyURLToFile(Objects.requireNonNull(configURL), configFile);
            }

            YamlReader yamlReader = new YamlReader(new FileReader("cabba/config.yml"));
            return yamlReader.read(Config.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
