package com.xypha.onlineBus.api.mapper;

import com.xypha.onlineBus.api.BranchDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BranchMapper {

    @Select("SELECT * FROM branches WHERE id = #{id}")
    BranchDto getBranchById(
            @Param("id") Long id);

    @Select("SELECT * FROM branches ORDER BY id LIMIT #{limit} OFFSET #{offset}")
    @Results({
            @Result(column = "branch_name", property = "branchName")
    })
    List<BranchDto> getAllBranches(
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("SELECT COUNT(*) FROM branches")
    long countBranches();

}
