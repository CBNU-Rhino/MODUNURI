package app.app.TouristApi.Controller;

import app.app.TouristApi.Service.JejuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tourist")
public class JejuController {

    private final JejuService jejuService;  // 인스턴스 변수를 통해 호출

    public JejuController(JejuService jejuService) {
        this.jejuService = jejuService;
    }

    // Endpoint to trigger fetching and saving Jeju's accessibility information
    @PostMapping("/fetch-accessible-jeju")
    public ResponseEntity<String> fetchJejuAccessibleData() {
        try {
            // 인스턴스 메소드로 호출
            jejuService.fetchAndSaveJejuAccessibilityInfo();
            return ResponseEntity.ok("Jeju accessible tourist data saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while saving Jeju accessible tourist data: " + e.getMessage());
        }
    }

    // Endpoint to fetch and save Jeju's tourist information
    @PostMapping("/fetch-jeju-tourist-info")
    public ResponseEntity<String> fetchJejuTouristInfo() {
        try {
            // JejuService의 인스턴스를 통해 fetchAndSaveJejuTouristInfo 호출
            jejuService.fetchAndSaveJejuTouristInfo();
            return ResponseEntity.ok("Jeju tourist data saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while saving Jeju tourist data: " + e.getMessage());
        }
    }
}
