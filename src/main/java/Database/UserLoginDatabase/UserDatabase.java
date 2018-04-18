package Database.UserLoginDatabase;

import Database.CSVReader;
import Database.CSVWriter;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class UserDatabase {
    private Connection connection;
    private Statement statement;
    private SecureRandom random;

    public static String currentUser;

    public static final int MAXUSERNAMELENGTH = 16;
    public static String currentUsername;

    // DO NOT EDIT WHILE PASSWORDS ARE BEING STORED
    private static final int COST = 16;
    private static final int SIZE = 128;
    private static final int HASHSIZE = SIZE * 3 / 8;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    // END DO NOT EDIT

    // Classes and methods for creating simpleton
    private static class Helper {
        private static final UserDatabase db = new UserDatabase("UserDB");
    }
    /**
     * Creates a database if it has not been created, otherwise returns the existing database
     * @return The UserDatabase for the project
     */
    public static UserDatabase getUserDatabase() {
        UserDatabase db = Helper.db;
        return db;
    }

    public String getCurrentUsername(){
        return this.currentUsername;
    }

    /**
     * Creates a database with the given name. Adds edges and nodes tables.
     * To be used only by the class Helper, instantiating the UserDatabase
     * should be done with getUserDatabase()
     *
     * @param name Name of database in filesystem
     */
    private UserDatabase(String name) {
        // Check for DB Driver
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Java DB Driver not found", e);
        }

        // Open connection
        try {
            connection = DriverManager.getConnection("jdbc:derby:"+name+";create=true");
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new IllegalStateException("Connection to database failed", e);
        }

        this.random = new SecureRandom();
        this.reset();
    }

    /**
     * Resets the database, removing and re-adding the user table
     */
    public void reset() {
        try {
            statement.execute("DROP TABLE USERS");
        } catch (Exception e) {}

        // Create table
        try {
            String sql1 = "CREATE TABLE USERS" +
                    "(username varchar("+MAXUSERNAMELENGTH+") PRIMARY KEY, " +
                    "password varchar("+HASHSIZE+"))";
            statement.executeUpdate(sql1);

        } catch  (SQLException e) {
            throw new IllegalStateException("Unable to initialize database", e);
        }

        try {
            CSVReader reader = new CSVReader(new FileReader("CSVs/logins.csv"), ",");
            ArrayList<String[]> lines = reader.readCSV();
            for (String[] s : lines) {
                statement.execute("INSERT INTO USERS VALUES ('"+s[0]+"','"+s[1]+"')");
            }
        } catch (IOException e) {
            System.out.println("Unable to read file");
        } catch (SQLException e) {
            System.out.println("Unable to insert into database");
        }
    }

    /**
     * Exports all current users to a csv
     */
    public void export() {
        try {
            CSVWriter writer = new CSVWriter("src/main/CSVs/logins.csv");
            ResultSet rs = statement.executeQuery("SELECT * FROM USERS");
            writer.writeRS(rs, 2);
        } catch (SQLException e) {
            System.out.println("Unable to export to csv");
        }
    }

    /**
     * Checks whether a user is in the database
     * @param username Username of user to be checked
     * @return True if the user is in the database, false if not
     */
    public boolean contains(String username) {
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM USERS " +
                    "WHERE username='" + username + "'");
            if (rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Could not access database");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a user to the database
     *
     * @param username New user to be added
     * @param password New user's password
     * @return true if successful, false if not
     */
    public boolean addUser(String username, String password) {
        if (username.length() > MAXUSERNAMELENGTH) {
            System.out.println("Username too long");
            return false;
        }

        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM USERS " +
                    "WHERE username='" + username + "'");
            if (rs.next()) {
                System.out.println("Username already exists");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Could not access database");
            e.printStackTrace();
            return false;
        }

        try {
            String stmt = "INSERT INTO USERS VALUES ('"+
                    username + "','" +
                    getHash(password.toCharArray()) + "')";
            statement.execute(stmt);
            return true;
        } catch (Exception e) {
            System.out.println("Could not create new user");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks whether a user entered the correct password
     *
     * @param username User entered username
     * @param password User entered password
     * @return Staff object if login successful, null if not
     */
    public boolean checkPassword(String username, String password) {
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM USERS " +
                    "WHERE username='" + username + "'");

            currentUsername = username;
            if (!rs.next()) {
                System.out.println("User does not exist");
                return false;
            }
            if (!authenticate(password.toCharArray(), rs.getString(2))) {
                System.out.println("Invalid password");
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hashes a password
     *
     * @param password password to be hashed
     * @return hashed password
     */
    private String getHash(char[] password) {
        byte[] salt = new byte[SIZE/8];
        random.nextBytes(salt);
        byte[] dk = pbkdf2(password, salt, 1<<COST);
        byte[] hash = new byte[salt.length + dk.length];
        System.arraycopy(salt, 0, hash, 0, salt.length);
        System.arraycopy(dk, 0, hash, salt.length, dk.length);
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        return enc.encodeToString(hash);
    }

    /**
     * Checks if a password is correct
     *
     * @param password hashed password
     * @param token unhashed password
     * @return true if authentic login, false if not
     */
    private boolean authenticate(char[] password, String token) {
        byte[] hash = Base64.getUrlDecoder().decode(token);
        byte[] salt = Arrays.copyOfRange(hash, 0, SIZE/8);
        byte[] check = pbkdf2(password, salt, 1<<COST);
        int zero = 0;
        for (int i = 0; i < check.length; i++) {
            zero |= hash[salt.length+i] ^ check[i];
        }
        return zero == 0;
    }

    /**
     * Runs a hash algorithm on the given password and salt
     *
     * @param password password to be hashed
     * @param salt password's salt
     * @param iterations number of hash iterations
     * @return hashed password without salt
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
        KeySpec spec = new PBEKeySpec(password, salt, iterations, SIZE);
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
            return f.generateSecret(spec).getEncoded();
        }
        catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Missing algorithm", ex);
        }
        catch (InvalidKeySpecException ex) {
            throw new IllegalStateException("Invalid SecretKeyFactory", ex);
        }
    }
}