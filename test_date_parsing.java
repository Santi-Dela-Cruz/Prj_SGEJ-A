import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class test_date_parsing {
    public static void main(String[] args) {
        // Test SQLite date format parsing
        String dateStr1 = "2025-07-16T01:35:03.768674100";
        String dateStr2 = "2025-07-16 01:35:03";
        
        System.out.println("Testing date parsing fix...");
        
        // Test format 1 - ISO with microseconds
        try {
            LocalDateTime date1 = LocalDateTime.parse(dateStr1);
            System.out.println("✓ ISO format parsed successfully: " + date1);
        } catch (Exception e) {
            System.out.println("✗ ISO format failed: " + e.getMessage());
        }
        
        // Test format 2 - Simple format
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime date2 = LocalDateTime.parse(dateStr2, formatter);
            System.out.println("✓ Simple format parsed successfully: " + date2);
        } catch (Exception e) {
            System.out.println("✗ Simple format failed: " + e.getMessage());
        }
        
        // Test our robust parsing logic
        System.out.println("\nTesting robust parsing logic:");
        testRobustParsing(dateStr1);
        testRobustParsing(dateStr2);
    }
    
    private static void testRobustParsing(String dateStr) {
        try {
            // Try ISO format first
            LocalDateTime result = LocalDateTime.parse(dateStr);
            System.out.println("✓ Robust parsing (ISO): " + result);
        } catch (Exception e) {
            try {
                // Fallback to simple format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime result = LocalDateTime.parse(dateStr, formatter);
                System.out.println("✓ Robust parsing (Simple): " + result);
            } catch (Exception e2) {
                System.out.println("✗ Robust parsing failed: " + e2.getMessage());
            }
        }
    }
}
