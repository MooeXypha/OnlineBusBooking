package com.xypha.onlineBus.routes.Mapper;

import com.xypha.onlineBus.routes.Dto.RouteResponse;
import com.xypha.onlineBus.routes.Dto.RouteWithCity;
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
        SELECT
            r.id,
            r.distance,
            r.created_at AS createdAt,
            r.updated_at AS updatedAt,
            sc.id AS source_id,
            sc.name AS sourceName,
            dc.id AS destination_id,
            dc.name AS destinationName
        FROM route r
        JOIN city sc ON r.source_city_id = sc.id
        JOIN city dc ON r.destination_city_id = dc.id
        ORDER BY r.id DESC
        LIMIT #{limit} OFFSET #{offset}
    """)
    List<RouteWithCity> getAllPaginated(@Param("offset") int offset,
                                        @Param("limit") int limit);

    @Select("""
        SELECT
            r.id,
            r.source_city_id,
            r.destination_city_id,
            r.distance,
            r.created_at AS createdAt,
            r.updated_at AS updatedAt,
            sc.name AS sourceName,
            dc.name AS destinationName
        FROM route r
        JOIN city sc ON sc.id = r.source_city_id
        JOIN city dc ON dc.id = r.destination_city_id
        WHERE r.id = #{id}
    """)
    RouteWithCity getRouteWithCityById(@Param("id") Long id);


    @Select("""
        SELECT
            r.id,
            r.source_city_id,
            r.destination_city_id,
            r.distance,
             r.created_at AS createdAt,
            r.updated_at AS updatedAt,
            sc.name AS sourceName,
            dc.name AS destinationName
        FROM route r
        JOIN city sc ON sc.id = r.source_city_id
        JOIN city dc ON dc.id = r.destination_city_id
        WHERE
            (#{source} IS NULL OR LOWER(sc.name) LIKE LOWER(CONCAT('%', #{source}, '%')))
        AND
            (#{destination} IS NULL OR LOWER(dc.name) LIKE LOWER(CONCAT('%', #{destination}, '%')))
        ORDER BY r.id DESC
        LIMIT #{limit} OFFSET #{offset}
    """)
    List<RouteWithCity> searchRoutesWithCity(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("SELECT COUNT(*) FROM route")
    int countRoutes();



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
