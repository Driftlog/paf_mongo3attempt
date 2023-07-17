package sg.edu.nus.iss.paf_mongo3attempt.repo;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SetOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class BGRepo {
    
    @Autowired
    private MongoTemplate template;

    // db.game.aggregate([{
    //     $match: { gid: 1}
    // }, 
    // {
    //     $lookup: {
    //         from: 'comments',
    //         foreignField: 'gid',
    //         localField: 'gid',
    //         as: 'reviews'
    //     }
    // },
    //     {$project: {_id : 0}}
    // ]
    // )
    
    public Document getGameReviews(int gameId) {
        
        Document gameDoc = new Document();
        Document gameReviews = new Document();

        // db.game.find( {gid: gameId})
        Criteria criteria1 = Criteria.where("gid").is(gameId);
        Query query = Query.query(criteria1);
        gameDoc = template.findOne(query, Document.class, "game");

        if (gameDoc == null) {
            return null;
        }

        MatchOperation matchToId = Aggregation.match(Criteria.where("gid").is(gameId));
        LookupOperation lookupComments = Aggregation.lookup("comments", "gid", "gid", "reviews");

        Aggregation pipeline = Aggregation.newAggregation(matchToId, lookupComments);

        gameReviews = template.aggregate(pipeline, "game", Document.class).getMappedResults().get(0);

        return gameReviews;
    }

    
// db.comments.aggregate([{
//     $group: {
//         _id: "$gid",
//         rating: {$max: "$rating"},
//         user: {$first: "$user"},
//         comment: {$first: "$c_text"},
//         review_id: {$first: "$c_id"}
//     }
// },
//      {
//          $lookup: {
//              from: 'game',
//              foreignField: 'gid',
//              localField: '_id',
//              as: 'reviews'   
//          }
//      },
//      {
//          $set: { name : '$reviews.name'
//          }   
//      },
//       {
//          $unwind: '$name'
//      },
//      {
//          $project: {
//              name: 1,
//              rating: 1,
//              user: 1,
//              comment: 1,
//              review_id: 1
//          }
//      }
// ])
    public Document getLowestReviews(String sortReq) {

        GroupOperation groupBy = Aggregation.group("gid");

        if (sortReq.equals("lowest")) {
        groupBy = Aggregation.group("$gid")
                        .min("rating").as("rating")
                        .first("$user").as("user")
                        .first("c_text").as("comment")
                        .first("c_id").as("review_id");
        } else {
                groupBy= Aggregation.group("gid")
                    .max("rating").as("rating")
                    .first("user").as("user")
                    .first("c_text").as("comment")
                    .first("c_id").as("review_id");
        }
                                                    
            
        LookupOperation merge = Aggregation.lookup("game", "_id", "gid", "reviews");


        SetOperation setName = SetOperation.builder().set("name").toValue("$reviews.name");

        AggregationOperation unwind = Aggregation.unwind("name");

        ProjectionOperation projectFields = Aggregation.project("name", "rating", "user", "comment", "review_id");

        // LimitOperation limit = new LimitOperation((long) 50); Uncomment for faster query

        Aggregation pipeline = Aggregation.newAggregation(groupBy, merge, setName, unwind, projectFields/*, limit */);

        AggregationResults<Document> gameReviews = template.aggregate(pipeline, "comments", Document.class);


    
        System.out.println(gameReviews.getMappedResults());

        Document lowestReviews = new Document()
                                    .append("lowest", "lowest")
                                    .append("games", gameReviews.getMappedResults())
                                    .append("timestamp", new Date());

        return lowestReviews;
    }


}
