package sg.edu.nus.iss.paf_mongo3attempt.controller;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.iss.paf_mongo3attempt.service.BGService;

@RestController
public class BGController {
    
    @Autowired
    private BGService svc;
    
    @GetMapping(path="/game/{game_id}/reviews",
        produces = "application/json")
    public ResponseEntity<Document> getGameReviews(@PathVariable int gameId) {
        Document gameReviews = svc.getGameReviews(gameId);

        if (gameReviews == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().body(gameReviews);
    }

    @GetMapping(path="/game/lowest",
        produces = "application/json")
    public ResponseEntity<Document> getLowestReviews() {
        Document lowestReviews = svc.getReviews("lowest");

         if (lowestReviews == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().body(lowestReviews);
    }

    @GetMapping(path="/game/highest",
        produces = "application/json")
    public ResponseEntity<Document> getHighestReviews() {
        Document lowestReviews = svc.getReviews("highest");

         if (lowestReviews == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok().body(lowestReviews);
    }
}
