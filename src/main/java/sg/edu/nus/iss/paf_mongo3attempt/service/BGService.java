package sg.edu.nus.iss.paf_mongo3attempt.service;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.paf_mongo3attempt.repo.BGRepo;

@Service
public class BGService {
    
    @Autowired
    private BGRepo repo;

    public Document getGameReviews(int gameId) {
        return repo.getGameReviews(gameId);
    }    

    // public Document getHighestReviews() {

    //     // List<Document> gameHighestReviews = repo.getHighestReviews();

    //     Document result = new Document()
    //                         .append("rating", "highest")
    //                         .append("games", gameHighestReviews)
    //                         .append("timestamp", new Date());
                
    //     return result;
    // }

    public Document getReviews(String sortReq) {

        return repo.getLowestReviews(sortReq);
    }



}
