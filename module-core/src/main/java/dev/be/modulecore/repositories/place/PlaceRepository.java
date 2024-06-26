package dev.be.modulecore.repositories.place;

import dev.be.modulecore.domain.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    boolean existsByPlaceId(String placeId);

    Optional<Place> findByPlaceId(String placeId);

    @Query("select p from Place p left join fetch p.category c " +
            "where p.region1DepthName = :region1 and p.region2DepthName in :region2s and c.id in :categories")
    List<Place> findByRegion1DepthNameAndRegion2DepthNameAndCategory(@Param("region1") String region1,
                                                                     @Param("region2s") List<String> region2List,
                                                                     @Param("categories") List<String> categories);

    @Query("select p.placeId from Place p where p.region1DepthName = :region1 and p.region2DepthName in :region2s")
    List<String> findPlaceIdByRegion1DepthNameAndRegion2DepthName(@Param("region1") String region1,
                                                                  @Param("region2s") List<String> region2list);

}
