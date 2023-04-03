package org.baylist.db.repository;

import org.baylist.db.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {

    Item findDistinctByNameContains(String likeName);

    Item findByBodyContains(String likeBody);

}
