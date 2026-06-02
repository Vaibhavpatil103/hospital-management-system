package hospital.management.system;

import org.mindrot.jbcrypt.BCrypt;

public class TestHash {
    public static void main(String[] args) {
        String hash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        String password = "admin123";
        boolean match = BCrypt.checkpw(password, hash);
        System.out.println("Match: " + match);
        
        System.out.println("Generated: " + BCrypt.hashpw(password, BCrypt.gensalt()));
    }
}
