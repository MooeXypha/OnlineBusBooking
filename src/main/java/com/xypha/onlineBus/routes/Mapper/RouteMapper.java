package com.xypha.onlineBus.routes.Mapper;

import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.routes.Entity.Route;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RouteMapper {

    @Insert("""
    INSERT INTO route (source_city_id, destination_city_id, distance, created_at, updated_at)
    VALUES (#{sourceCityId}, #{destinationCityId}, #{distance}, NOW(), NOW())
""")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertRoute(Route route);

    @Select("""
        SELECT r.id, r.source_city_id, r.destination_city_id, r.distance, r.created_at, r.updated_at
        FROM route r
        WHERE r.id = #{id}
    """)
    Route getRouteById(Long id);

    @Update("""
        UPDATE route
        SET source_city_id = #{sourceCityId}, destination_city_id = #{destinationCityId},
            distance = #{distance}, updated_at = NOW()
        WHERE id = #{id}
    """)
    void updateRoute(Route route);

    @Delete("DELETE FROM route WHERE id = #{id}")
    int deleteRoute(Long id);

    @Select("""
        SELECT COUNT(*) FROM route
        WHERE source_city_id = #{sourceCityId} AND destination_city_id = #{destinationCityId}
    """)
    int countDuplicateRoute(@Param("sourceCityId") Long sourceCityId,
                            @Param("destinationCityId") Long destinationCityId);

    @Select("""
        SELECT COUNT(*) FROM route
        WHERE id != #{id} AND source_city_id = #{sourceCityId} AND destination_city_id = #{destinationCityId}
    """)
    int countDuplicateRouteExcludingId(@Param("id") Long id,
                                       @Param("sourceCityId") Long sourceCityId,
                                       @Param("destinationCityId") Long destinationCityId);

    @Select("""
        SELECT r.id, r.source_city_id, r.destination_city_id, r.distance, r.created_at, r.updated_at
        FROM route r
        ORDER BY r.id DESC
        LIMIT #{limit} OFFSET #{offset}
    """)
    List<Route> getAllPaginated(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM route")
    int countRoutes();

    // For search by city names, join city table
    @Select("""
        SELECT r.id, r.source_city_id, r.destination_city_id, r.distance, r.created_at, r.updated_at
        FROM route r
        JOIN city s ON r.source_city_id = s.id
        JOIN city d ON r.destination_city_id = d.id
        WHERE (:source IS NULL OR UPPER(s.name) LIKE CONCAT('%', UPPER(:source), '%'))
          AND (:destination IS NULL OR UPPER(d.name) LIKE CONCAT('%', UPPER(:destination), '%'))
        ORDER BY r.id ASC
        LIMIT :limit OFFSET :offset
    """)
    List<Route> searchRoutes(@Param("source") String source,
                             @Param("destination") String destination,
                             @Param("limit") int limit,
                             @Param("offset") int offset);

    @Select("""
        SELECT COUNT(*)
        FROM route r
        JOIN city s ON r.source_city_id = s.id
        JOIN city d ON r.destination_city_id = d.id
        WHERE (:source IS NULL OR UPPER(s.name) LIKE CONCAT('%', UPPER(:source), '%'))
          AND (:destination IS NULL OR UPPER(d.name) LIKE CONCAT('%', UPPER(:destination), '%'))
    """)
    int countSearchRoutes(@Param("source") String source,
                          @Param("destination") String destination);
}
